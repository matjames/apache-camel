package net.gcicom.order.processor.config;

import static net.gcicom.order.processor.config.AppProperties.DRIVER_CLASS_NAME;
import static net.gcicom.order.processor.config.AppProperties.IMPORTED_EVENT_DB_PASSWORD_KEY;
import static net.gcicom.order.processor.config.AppProperties.IMPORTED_EVENT_DB_URL_KEY;
import static net.gcicom.order.processor.config.AppProperties.IMPORTED_EVENT_DB_USER_KEY;
import static net.gcicom.order.processor.config.AppProperties.RATING_DB_PASSWORD_KEY;
import static net.gcicom.order.processor.config.AppProperties.RATING_DB_URL_KEY;
import static net.gcicom.order.processor.config.AppProperties.RATING_DB_USER_KEY;
import static net.gcicom.order.processor.config.AppProperties.ALL_SPARK_DB_PASSWORD_KEY;
import static net.gcicom.order.processor.config.AppProperties.ALL_SPARK_DB_URL_KEY;
import static net.gcicom.order.processor.config.AppProperties.ALL_SPARK_DB_USER_KEY;

import java.util.Properties;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:application.properties"})
public class DataSourceConfiguration {

	
	@Autowired
	Environment env;
	
   
    
    
    @Bean(name = "allsparkEntityMF")
    public LocalContainerEntityManagerFactoryBean allsparkEntityMF() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(allsparkDS());
        em.setPersistenceUnitName("allsparkEntityMF");
        em.setPackagesToScan(new String[] { "net.gcicom.domain.allspark" });
       // em.setPersistenceProvider(new HibernatePersistenceProvider());

        HibernateJpaVendorAdapter a = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(a);
        Properties p = hibernateSpecificProperties();
        p.setProperty("hibernate.ejb.entitymanager_factory_name", "allsparkEntityMF");
        em.setJpaProperties(p);
        return em;
    }

    @Bean(name = "allsparkDS")
    public DataSource allsparkDS() {
    	
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty(DRIVER_CLASS_NAME));
        dataSource.setUrl(env.getProperty(ALL_SPARK_DB_URL_KEY));
        dataSource.setUsername(env.getProperty(ALL_SPARK_DB_USER_KEY));
        dataSource.setPassword(env.getProperty(ALL_SPARK_DB_PASSWORD_KEY));

        return dataSource;
    }
    
    @Bean
    public Properties hibernateSpecificProperties(){
    	
    	final Properties p = new Properties();
    	p.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        p.setProperty("hibernate.dialect", env.getProperty("spring.jpa.hibernate.dialect"));
        p.setProperty("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        p.setProperty("hibernate.cache.use_second_level_cache", env.getProperty("spring.jpa.hibernate.cache.use_second_level_cache"));
        p.setProperty("hibernate.cache.use_query_cache", env.getProperty("spring.jpa.hibernate.cache.use_query_cache"));
        
    	return p;
    	
    }
    
    @Bean(name = "defaultTm")
    public PlatformTransactionManager transactionManager() {
    	
    	JpaTransactionManager txManager = new JpaTransactionManager();
    	txManager.setEntityManagerFactory(allsparkEntityMF().getObject());
    	return txManager;
    }

}
