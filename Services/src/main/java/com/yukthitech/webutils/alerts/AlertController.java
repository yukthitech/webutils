package com.yukthitech.webutils.alerts;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertController;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.controllers.BaseController;

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
	@Autowired
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
	@RequestMapping(value = "/fetchAlerts/{target}", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BasicReadListResponse<AlertDetails> fetchAlerts(@PathVariable("target") String target)
	{
		List<AlertDetails> alertDetails = pullAlertService.fetchAlerts(target);
		return new BasicReadListResponse<>(alertDetails);
	}

	@ActionName("markProcessed")
	@RequestMapping(value = "/markProcessed/{alertId}", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public BaseResponse markProcessed(@PathVariable("alertId") long id)
	{
		pullAlertService.updateStatus(id, PullAlertStatus.PROCESSED);
		return new BaseResponse();
	}

	@Override
	public AlertController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
