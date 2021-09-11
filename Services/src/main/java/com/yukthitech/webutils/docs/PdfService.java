package com.yukthitech.webutils.docs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;

/**
 * Pdf file related util methods.
 * 
 * @author akiran
 */
@Service
public class PdfService
{
	/**
	 * User agent to fetch class path resources as streams.
	 * @author akiran
	 */
	private static class UserAgent extends ITextUserAgent
	{
		
		/**
		 * Instantiates a new user agent.
		 *
		 * @param outputDevice the output device
		 * @param sharedContext the shared context
		 */
		public UserAgent(ITextOutputDevice outputDevice, SharedContext sharedContext) 
		{
			super(outputDevice);
			setSharedContext(sharedContext);
	    }

		@Override
		public String resolveURI(String uri)
		{
			return uri;
		}

		@Override
		protected InputStream resolveAndOpenStream(String uri)
		{
			return PdfService.class.getResourceAsStream(uri);
		}
	}

	/**
	 * Used to replace expressions in pdf content.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;

	/**
	 * Generates pdf file from specified html template.
	 * 
	 * @param htmlTemplate
	 *            html template to use to generate pdf.
	 * @param context
	 *            context to use to process hteml template
	 * @param name
	 *            Prefix for file name being generated.
	 * @return Generated pdf file
	 */
	public File processHtmlTemplate(InputStream htmlTemplate, Object context, String name)
	{
		try
		{
			File tempFile = File.createTempFile(name, ".pdf");
			String content = IOUtils.toString(htmlTemplate);

			content = freeMarkerService.processTemplate(name, content, context);

			OutputStream outputStream = new FileOutputStream(tempFile);
			ITextRenderer renderer = new ITextRenderer();
			renderer.getSharedContext()
					.setUserAgentCallback(new UserAgent(renderer.getOutputDevice(), renderer.getSharedContext()));

			renderer.setDocumentFromString(content);
			renderer.layout();
			renderer.createPDF(outputStream);
			outputStream.close();
			renderer.finishPDF();

			return tempFile;
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while processing pdf template.", ex);
		}
	}
}