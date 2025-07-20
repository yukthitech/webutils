package com.yukthitech.webutils.captcha;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.captcha.CaptchaResponse;
import com.yukthitech.webutils.common.captcha.CaptchaValidator;

import jakarta.annotation.PostConstruct;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.noise.StraightLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.TextProducer;

@Service
public class CaptchaService
{
	private static Logger logger = LogManager.getLogger(CaptchaService.class);
	
	private static Random random = new Random(System.currentTimeMillis());
	
	private static char[] CAPTCH_CHARS = new char[] {
			'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y',
            '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'D', 'E', 'F', 'H', 'L', 'N', 'P', 'R', 
            'T',
	};
	
	private static TextProducer TEXT_PRODUCER = new DefaultTextProducer(IWebUtilsCommonConstants.CAPTCHA_LENGTH, CAPTCH_CHARS);
	
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
		
		Captcha.Builder builder = new Captcha.Builder(200, 50)
				.addText(TEXT_PRODUCER)
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
		return new CaptchaResponse(base64Img, encryptor.encrypt(captcha.getAnswer()));
	}
	
	/**
	 * Validates user answer for corresponding token.
	 * @param token
	 * @param userAns
	 */
	private boolean validate(String token, String userAns)
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
