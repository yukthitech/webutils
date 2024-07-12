package com.yukthitech.webutils.autox;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.utils.ZipUtils;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.IWebUtilsActionConstants;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.controllers.BaseCrudController;
import com.yukthitech.webutils.repository.file.FileEntity;
import com.yukthitech.webutils.services.FileService;
import com.yukthitech.webutils.utils.WebAttachmentUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controller for autox reports.
 * TODO: Add authorization using app specific roles
 * @author akiran
 */
@RestController
@RequestMapping("/autox")
@ActionName("autox")
public class AutoxReportController extends BaseCrudController<AutoxReportModel, AutoxReportService, IAutoxReportController<MultipartHttpServletRequest>> implements IAutoxReportController<MultipartHttpServletRequest>
{
	/**
	 * Used to fetch report files.
	 */
	@Autowired
	private FileService fileService;
	
	/**
	 * Current http servlet response.
	 */
	@Autowired
	private HttpServletResponse response;
	
	/**
	 * Data folder which will be used to unzip report files.
	 */
	@Value("${webutils.data.folder:}")
	private File dataFolder;

	/* (non-Javadoc)
	 * @see com.recruitment.autox.IAutoxReportController#uploadReport(com.recruitment.autox.AutoxReportModel, java.lang.Object)
	 */
	@AttachmentsExpected
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_SAVE)
	@RequestMapping(value = "/saveReport", method = RequestMethod.POST)
	@Override
	public BasicSaveResponse uploadReport(@RequestPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART) @Valid AutoxReportModel autoxReport, MultipartHttpServletRequest request)
	{
		if(autoxReport.getReportFile() == null)
		{
			throw new InvalidRequestException("No report file specified in request.");
		}
		
		return super.save(autoxReport);
	}
	
	/**
	 * Gets the report file and unzips the content into this folder and returns the same.
	 * @param reportId id of report for which folder needs to be fetched
	 * @return report unzipped folder.
	 */
	private File getReportFolder(long reportId)
	{
		File reportFolder = new File(dataFolder, "autox" + File.separator + reportId);
		
		if(reportFolder.exists())
		{
			return reportFolder;
		}
		
		Long fileId = fileService.fetchFileIdByOwner(AutoxReportEntity.class, "reportFile", reportId);
		
		//when file id is not found set invalid id,
		// 	so that file controller will sent error to client
		if(fileId == null)
		{
			throw new InvalidRequestException("No report available with specified id: " + reportId);
		}
		
		FileEntity fileEntity = fileService.getFileEntity(fileId);
		ZipUtils.unzip(fileEntity.getFile(), reportFolder);
		return reportFolder;
	}

	/**
	 * Fetches specified file from report file content.
	 * @param reportId if of report from which file needs to be fetched.
	 * @param filePath relative path of file to be fetched.
	 */
	@ActionName("fetchFile")
	@RequestMapping(value = "/reports/{reportId}", method = RequestMethod.GET)
	public void getReportFile(@PathVariable("reportId") long reportId, @RequestParam("file") String filePath) throws Exception
	{
		File reportFolder = getReportFolder(reportId);
		
		String subpath = filePath.startsWith("./") ? filePath.substring(2) : filePath;
		subpath = subpath.replace("//", File.separator);
		
		File file = new File(reportFolder, subpath);
		
		if(!file.exists())
		{
			throw new InvalidRequestException("File {} does not exist under report files of report: {} [Coverted path: {}]", filePath, reportId, subpath);
		}
		
		FileInfo fileInfo = new FileInfo(file.getName(), file, null);
		WebAttachmentUtils.sendFile(response, fileInfo, false, false);
	}

	@ActionName("bulkDelete")
	@RequestMapping(value = "/bulkDelete", method = RequestMethod.POST)
	@Override
	public BaseResponse bulkDelete(@RequestBody @Valid BulkReportDeleteRequest request)
	{
		for(Long id : request.getReportIds())
		{
			super.getService().deleteById(id);
		}
		
		return new BaseResponse();
	}
	
	@Override
	public AutoxReportController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
