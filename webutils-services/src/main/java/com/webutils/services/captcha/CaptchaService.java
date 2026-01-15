package com.webutils.services.captcha;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.common.captcha.CaptchaResponse;
import com.webutils.services.captcha.CaptchaValueFactory.CaptchaValue;
import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.noise.StraightLineNoiseProducer;

@Service
public class CaptchaService
{
	private static Logger logger = LogManager.getLogger(CaptchaService.class);
	
	private static Random random = new Random(System.currentTimeMillis());
	
	@Autowired(required = false)
	private Encryptor encryptor;
	
	public CaptchaResponse generate() throws Exception
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}

		CaptchaValue captchaValue = CaptchaValueFactory.generate();
		
		Captcha.Builder builder = new Captcha.Builder(200, 50)
				.addText(captchaValue)
				.addBackground(new GradiatedBackgroundProducer())
				.addBorder();
		
		if(random.nextBoolean())
		{
			builder.addNoise(new StraightLineNoiseProducer());
		}
		else
		{
			builder.gimp(new FishEyeGimpyRenderer());
		}

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
