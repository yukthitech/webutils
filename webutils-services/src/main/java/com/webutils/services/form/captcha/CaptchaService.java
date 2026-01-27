package com.webutils.services.form.captcha;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.common.form.captcha.CaptchaResponse;
import com.webutils.common.form.captcha.CaptchaValidator;
import com.webutils.services.form.captcha.CaptchaValueFactory.CaptchaValue;
import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.text.renderer.DefaultWordRenderer;

@Service
public class CaptchaService
{
	private static Logger logger = LogManager.getLogger(CaptchaService.class);
	
	private static Random random = new Random(System.currentTimeMillis());
	
	private static GradiatedBackgroundProducer[] BG_PRODUCERS = new GradiatedBackgroundProducer[3];
	
	private static FishEyeGimpyRenderer[] GIMPS = {
		new FishEyeGimpyRenderer(new Color(130, 130, 200), new Color(130, 130, 200)),
		new FishEyeGimpyRenderer(new Color(130, 130, 130), new Color(130, 130, 130)),
		new FishEyeGimpyRenderer(new Color(200, 130, 130), new Color(200, 130, 130))
	};
	
	private static DefaultWordRenderer[] WORD_RENDERERS = {
		new DefaultWordRenderer(Color.RED, null),
		new DefaultWordRenderer(Color.BLUE, null),
		new DefaultWordRenderer(Color.BLACK, null),
		new DefaultWordRenderer(new Color(0, 150, 0), null)
	};
	
	static
	{
		Color[] bgColors = {
			new Color(170, 170, 170),
			new Color(170, 220, 170),
			new Color(170, 170, 220),
		};
		
		for(int i = 0 ; i < bgColors.length; i++)
		{
			BG_PRODUCERS[i] = new GradiatedBackgroundProducer();
			BG_PRODUCERS[i].setFromColor(bgColors[i]);
			BG_PRODUCERS[i].setToColor(Color.WHITE);
		}
		
		
	}
	
	@Autowired(required = false)
	private Encryptor encryptor;
	
	@PostConstruct
	private void init()
	{
		CaptchaValidator.setValidatorFunction(valueWithToken -> 
		{
			if(valueWithToken == null)
			{
				return true;
			}

			return validate(valueWithToken.getToken(), valueWithToken.getValue());
		});
	}

	public CaptchaResponse generate() throws Exception
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}

		CaptchaValue captchaValue = CaptchaValueFactory.generate();
		
		Captcha.Builder builder = new Captcha.Builder(200, 50)
				.addBackground(BG_PRODUCERS[random.nextInt(BG_PRODUCERS.length)])
				.addBorder()
				.gimp(GIMPS[random.nextInt(GIMPS.length)])
				.addText(captchaValue, WORD_RENDERERS[random.nextInt(WORD_RENDERERS.length)]);
		
		Captcha captcha = builder.build();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(captcha.getImage(), "png", bos);
		bos.flush();
		
		String base64Img = Base64.getEncoder().encodeToString(bos.toByteArray());
		return new CaptchaResponse(base64Img, encryptor.encrypt(captchaValue.getAnswer()));
	}
	
	/**
	 * Validates user answer for corresponding token.
	 * @param token
	 * @param userAns
	 */
	public boolean validate(String token, String userAns)
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}
		
		String decryptedAns = null;
		
		try
		{
			decryptedAns = encryptor.decrypt(token);
		}catch(Exception ex)
		{
			logger.trace("User specified invalid token (which cannot be decrypted): {}", token);
			return false;
		}
		
		if(!decryptedAns.equals(userAns))
		{
			return false;
		}
		
		return true;
	}
}
