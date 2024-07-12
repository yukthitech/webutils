package com.yukthitech.webutils.client.helpers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.ZipUtils;
import com.yukthitech.webutils.autox.AutoxReportModel;
import com.yukthitech.webutils.autox.IAutoxReportController;
import com.yukthitech.webutils.client.ClientContext;
import com.yukthitech.webutils.client.ClientControllerFactory;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;
import com.yukthitech.webutils.common.alerts.IAlertType;
import com.yukthitech.webutils.common.lov.ValueLabel;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

/**
 * Utility methods useful for different agents.
 * @author akiran
 */
public class AgentHelper
{
	private static Logger logger = LogManager.getLogger(AgentHelper.class);

	private static final SimpleDateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yy");
	
	/**
	 * Client controller factory for creating controller instances.
	 */
	private ClientControllerFactory clientControllerFactory;
	
	/**
	 * Alert controller for sending alerts.
	 */
	private IAlertController<Object> alertController;
	
	/**
	 * Controller to upload reports.
	 */
	private IAutoxReportController<Object> autoxReportController;
	
	/**
	 * Name of the agent.
	 */
	private String agentName;
	
	/**
	 * Application ui base url.
	 */
	private String appUiBaseUrl;
	
	public AgentHelper(String agentName, String propFile)
	{
		this(agentName, loadProperties(propFile));
	}
	
	@SuppressWarnings("unchecked")
	public AgentHelper(String agentName, Properties prop)
	{
		this.agentName = agentName;
		this.appUiBaseUrl = prop.getProperty(IAgentConstants.APP_UI_BASE_URL);
		
		if(!this.appUiBaseUrl.endsWith("/"))
		{
			this.appUiBaseUrl = this.appUiBaseUrl + "/";
		}
		
		String baseUrl = prop.getProperty(IAgentConstants.APP_BASE_URL);
		String user = prop.getProperty(IAgentConstants.APP_USER);
		String password = prop.getProperty(IAgentConstants.APP_PASSWORD);
		
		ClientContext clientContext = new ClientContext(baseUrl);
		clientContext.authenticate(user, password);
		
		clientControllerFactory = new ClientControllerFactory(clientContext);
		alertController = clientControllerFactory.getController(IAlertController.class);
		autoxReportController = clientControllerFactory.getController(IAutoxReportController.class);
	}

	/**
	 * Gets the client controller factory for creating controller instances.
	 *
	 * @return the client controller factory for creating controller instances
	 */
	public ClientControllerFactory getClientControllerFactory()
	{
		return clientControllerFactory;
	}
	
	/**
	 * Load properties from specified file.
	 *
	 * @param propFile the prop file
	 * @return the properties
	 */
	private static Properties loadProperties(String propFile)
	{
		try
		{
			logger.debug("Loading properties file: " + propFile);
			
			FileInputStream fis = new FileInputStream(propFile);
	
			final Properties prop = new Properties();
			prop.load(fis);
			fis.close();
			
			return prop;
		}catch(Exception ex)
		{
			throw new IllegalStateException("Failed to load properties file: " + propFile, ex);
		}
	}
	
	/**
	 * Send alert with specified details.
	 *
	 * @param title the title
	 * @param message the message
	 * @param alertType the alert type
	 */
	public void sendAlert(String title, String message, IAlertType alertType)
	{
		AlertDetails alert = new AlertDetails();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setSource(agentName);
		
		alert.setAlertType(alertType);
		
		alertController.sendAlert(alert, null);
	}
	
	/**
	 * Uploads the autox reports to recruit server for analysis.
	 */
	public ValueLabel uploadAutoxReports(String actionName, String autoxOutputFolder)
	{
		//compute the report name
		Date now = new Date();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		
		long secDiff = (now.getTime() - today.getTime()) / 1000;
		
		String reportName = String.format("cand-srch_%s_%s_%s", actionName, REPORT_DATE_FORMAT.format(today), Long.toHexString(secDiff));
		
		AutoxReportModel autoxReport = new AutoxReportModel();
		autoxReport.setSource(agentName);
		autoxReport.setName(reportName);
		
		//create zip file with all files required to uploaded
		File reportFolder = new File(autoxOutputFolder);
		File actionReportFolder = new File(reportFolder, actionName);
		
		final HashMap<String, File> filesToSend = new HashMap<>();
		
		new File(actionReportFolder, "logs").listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				String lowerName = pathname.getName().toLowerCase();
				
				if(lowerName.endsWith(".js") || lowerName.endsWith(".log") || lowerName.endsWith(".png"))
				{
					filesToSend.put("logs/" + pathname.getName(), pathname);
				}
				
				return false;
			}
		});
		
		filesToSend.put("test-results.json", new File(actionReportFolder, "test-results.json"));

		File zipFile = ZipUtils.zipFiles(filesToSend);
		
		//upload the report with zip file
		autoxReport.setReportFile(new FileInfo("autox-report.zip", zipFile, null));
		
		BasicSaveResponse saveResp = autoxReportController.uploadReport(autoxReport, null);
		return new ValueLabel("" + saveResp.getId(), reportName);
	}

	/**
	 * Creates anchor link with specified label and uri.
	 * @param label label to be used for link
	 * @param uri uri to be used in link along with app base url
	 * @return final anchor link html code
	 */
	public String createUiLink(String label, String uri)
	{
		return "<a href=\"" + appUiBaseUrl + uri + "\">" + label + "</a>";
	}
	
	/**
	 * Fetches autox uri for specified report id.
	 * @param reportId report id for which url needs to be composed
	 * @return final uri
	 */
	public String getAutoxUri(String reportId)
	{
		return "autox/autox-index.html?reportId=" + reportId; 
	}
}
