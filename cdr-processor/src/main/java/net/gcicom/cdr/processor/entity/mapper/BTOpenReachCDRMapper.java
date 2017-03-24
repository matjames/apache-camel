package net.gcicom.cdr.processor.entity.mapper;

import static net.gcicom.cdr.processor.common.AppConstants.CDR_PROCESSOR_USER;
import static net.gcicom.cdr.processor.common.AppConstants.CDRMapperConstants.NA;
import static net.gcicom.cdr.processor.util.DateTimeUtil.convertLocalDateTimeToDate;
import static net.gcicom.cdr.processor.util.DateTimeUtil.formatYYYYMM;
import static net.gcicom.cdr.processor.util.DateTimeUtil.getDurationInSeconds;
import static net.gcicom.cdr.processor.util.DateTimeUtil.getWeekDayFlag;
import static net.gcicom.cdr.processor.util.EventRecordKeyGenerator.getEventRecordHash;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.gcicom.cdr.processor.entity.input.BTOpenReachCDR;
import net.gcicom.cdr.processor.service.GCICDRService;
import net.gcicom.cdr.processor.service.ValidationFailedException;
import net.gcicom.domain.imported.events.ImportedEvent;
import net.gcicom.domain.rating.NumberRangeMap;

@Component
public class BTOpenReachCDRMapper implements CDRMapper<BTOpenReachCDR> {

	private static final Logger LOG = LoggerFactory.getLogger(BTOpenReachCDRMapper.class);

	private static final String DUMMY = "DUMMY";
	private static final Long L_DUMMY = 1L;

	@Autowired
	private CDRMapperHelper h;

	public List<ImportedEvent> convertToGCICDR(final List<BTOpenReachCDR> input, final Long eventFileId, final String fileName) throws Exception {

		List<ImportedEvent> cdrs = new ArrayList<>();

		for (BTOpenReachCDR source : input) {

			LOG.debug("Converting a {} to GCICDR with eventfileid {}" , source.toString(), eventFileId);
			ImportedEvent cdr = new ImportedEvent();
			Date eventTime = convertLocalDateTimeToDate(getDateTime(source.getEventTime()));

			// populate billing reference details
			cdr = h.populateBillingReferenceDetails(cdr, eventTime, source.getOriginatingNumber());

			cdr.setAccountingPeriod(formatYYYYMM(getDateTime(source.getEventTime())));
			// cdr.setAccountNumber(DUMMY);
			cdr.setCountry(NA);
			// cdr.setCustomerID(L_DUMMY);
			cdr.setDialledCLI(source.getDialedNumber());
			cdr.setEventDurationSecs(getDurationInSeconds(source.getDuration()));
			cdr.setEventFileID(eventFileId);
			cdr.setEventReference(source.getOriginatingNumber());
			// cdr.setEventReferenceID(L_DUMMY);
			cdr.setEventTime(eventTime);
			cdr.setEventTypeID(L_DUMMY);
			
			
			// populates number range details from RatingDB.NumberRangeMap table
			cdr = h.populateNumberRangDetails(cdr, source.getDialedNumber(), eventTime);
			
			
			cdr.setOriginatingCLI(source.getOriginatingNumber());
			cdr.setPreRatedEventFlag(DUMMY);
			cdr.setPresentationCLI(source.getDialedNumber());
			cdr.setSupplierAccountNumber(source.getAccountNumber());
			cdr.setSupplierCost(source.getWholesalePrice());
			cdr.setSupplierID(h.getSupplierId(fileName));
			cdr.setSupplierNumberRangeMap(source.getPhoneBookCode());
			cdr.setSupplierRatingPattern(source.getPhoneBookCode() + "_" + source.getReRateIndicator());
			cdr.setSupplierRecordReference(source.getDunsId());
			cdr.setSupplierServiceType(source.getEventType() + "_" + source.getBearerTypeGroup());
			cdr.setTerminatingCLI(source.getDialedNumber());
			cdr.setTimePeriod(DUMMY);
			cdr.setWeekDayFlag(getWeekDayFlag(getDateTime(source.getEventTime())));
			cdr.setCreatedBy(CDR_PROCESSOR_USER);

			// generate only after populating all the field in cdrs
			cdr.setEventRecordKey(getEventRecordHash(cdr));

			LOG.debug("Converted cdr " + cdr.toString());
			cdrs.add(cdr);
		}

		return cdrs;

	}





	/**
	 * Date time is in CCYYMMDDhhmmsstt format
	 * 
	 * @param dateTime
	 * @return
	 * @throws ValidationFailedException
	 */
	private LocalDateTime getDateTime(String dateTime) throws ValidationFailedException {

		// strip trailing 00 character
		if (!StringUtils.isEmpty(dateTime) && dateTime.length() > 14) {

			String dt = dateTime.substring(0, 14);
			DateTimeFormatter ft = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

			return LocalDateTime.parse(dt, ft);

		} else {

			throw new ValidationFailedException(String.format("Datetime %s is not required format", dateTime));
		}

	}

}
