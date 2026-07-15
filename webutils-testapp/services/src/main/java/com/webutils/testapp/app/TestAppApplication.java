package com.webutils.testapp.app;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.webutils.common.repo.EnableRepositories;
import com.yukthitech.persistence.rdbms.RdbmsDataStore;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.Encryptor;

/**
 * Spring Boot entry point for the WebUtils widget test harness.
 */
// Single base package: com.webutils.testapp is under com.webutils; listing both
// would register app repositories twice via RepositoryRegistrar.
@SpringBootApplication(scanBasePackages = {"com.webutils"})
@EnableRepositories(basePackages = {"com.webutils"})
@EnableScheduling
public class TestAppApplication
{
	private static final Logger logger = LogManager.getLogger(TestAppApplication.class);

	@Value("${app.db.url}")
	private String dbUrl;

	@Value("${app.db.username}")
	private String dbUsername;

	@Value("${app.db.password}")
	private String dbPassword;

	@Value("${app.db.driver}")
	private String dbDriver;

	@Value("${app.db.type}")
	private String dbType;

	@Value("${app.encryptor.keystore}")
	private String keyStoreFile;

	@Value("${app.encryptor.alias}")
	private String keyStoreAlias;

	@Value("${app.encryptor.password}")
	private String keyStorePassword;

	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(TestAppApplication.class);
		ConfigurableApplicationContext applicationContext = app.run(args);
		applicationContext.start();
		logger.info("WebUtils testapp started successfully");
	}

	@Bean
	public RepositoryFactory buildRepositoryFactory()
	{
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		dataSource.setDriverClassName(dbDriver);

		RdbmsDataStore dataStore = new RdbmsDataStore(dbType);
		dataStore.setDataSource(dataSource);

		RepositoryFactory factory = new RepositoryFactory();
		factory.setDataStore(dataStore);
		return factory;
	}

	@Bean
	public Encryptor buildEncryptor()
	{
		return new Encryptor(keyStoreFile, keyStoreAlias, keyStorePassword);
	}
}
