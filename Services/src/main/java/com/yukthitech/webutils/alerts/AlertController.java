package com.yukthitech.webutils.alerts;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.controllers.BaseController;

import jakarta.validation.Valid;

/**
 * Controller for alerts.
 * @author akiran
 */
@RestController
@RequestMapping("/alerts")
@ActionName("alerts")
public class AlertController extends BaseController implements IAlertController<MultipartHttpServletRequest>
{
	/**
	 * Service to access pull alerts.
	 */
	@Autowired
	private PullAlertService pullAlertService;
	
	/**
	 * Alert engine to send alerts.
	 */
	@Autowired(required = false)
	private AlertEngine alertEngine;
	
	@AttachmentsExpected
	@ActionName("sendAlert")
	@RequestMapping(value = "/sendAlert", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BaseResponse sendAlert(@RequestPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART) @Valid AlertDetails alert, MultipartHttpServletRequest request)
	{
		alertEngine.sendAlert(alert);
		return new BaseResponse();
	}

	@ActionName("fetchAlerts")
	@RequestMapping(value = "/fetchAlerts", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BasicReadListResponse<AlertDetails> fetchAlerts(@RequestParam("target") String target)
	{
		List<AlertDetails> alertDetails = pullAlertService.fetchAlerts(target);
		
		if(alertDetails == null)
		{
			alertDetails = Collections.emptyList();
		}
		
		return new BasicReadListResponse<>(alertDetails);
	}

	@ActionName("markProcessed")
	@RequestMapping(value = "/markProcessed/{alertId}", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BaseResponse markProcessed(@PathVariable("alertId") long id, @RequestBody @Valid AlertProcessedDetails alertProcessedDetails)
	{
		AlertDetails alert = pullAlertService.fetchFullModel(id, AlertDetails.class);
		
		if(alert == null)
		{
			throw new InvalidRequestException("No pull alert found with id: " + id);
		}
		
		if(alert.getStatus() != PullAlertStatus.NOT_PROCESSED)
		{
			throw new InvalidRequestException("Specified alert is already processed.");
		}
		
		pullAlertService.updateStatus(id, PullAlertStatus.PROCESSED, alertProcessedDetails);
		
		//Send confirmation alert if needed
		if(alert.isConfirmationRequired())
		{
			alert.setAlertProcessedDetails(alertProcessedDetails);
			alertEngine.sendConfirmationAlert(alert);
		}
		
		return new BaseResponse();
	}

	@Override
	public AlertController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
