package com.yukthitech.webutils.captcha;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.captcha.CaptchaResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.noise.StraightLineNoiseProducer;

@Service
public class CaptchaService
{
	private static Random random = new Random(System.currentTimeMillis());
	
	@Autowired
	private Encryptor encryptor;
	
	public CaptchaResponse generate() throws Exception
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}
		
		Captcha.Builder builder = new Captcha.Builder(200, 50)
				.addText()
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
	
	public boolean validate(String encAns, String userAns)
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}
		
		String decryptedAns = encryptor.decrypt(encAns);
		return decryptedAns.equals(userAns);
	}
}
