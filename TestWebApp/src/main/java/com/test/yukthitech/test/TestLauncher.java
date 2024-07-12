/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.yukthitech.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TestLauncher
{
	private static Logger logger = LogManager.getLogger(TestLauncher.class);
	
	/**
	 * Main method to launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) throws Exception
	{
		SpringApplication app = new SpringApplication(TestLauncher.class);
		
		ConfigurableApplicationContext applicationContext = app.run(args);
		
		//invoking start will invoke the application start event methods
		applicationContext.start();

		logger.info("Started Test server successfully...");
	}

}