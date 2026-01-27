package com.webutils.services.form.captcha;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.TextProducer;

public class CaptchaValueFactory 
{
    private static final int CAPTCHA_LENGTH = 5;

    private static char[] CAPTCH_CHARS = new char[] {
        'a', 'b', 'c', 'd',
        'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y',
        '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'D', 'E', 'F', 'H', 'L', 'N', 'P', 'R', 
        'T',
    };

    private static TextProducer TEXT_PRODUCER = new DefaultTextProducer(CAPTCHA_LENGTH, CAPTCH_CHARS);

    private static Random random = new Random(System.currentTimeMillis());

    @Data
    @AllArgsConstructor
    public static class CaptchaValue implements TextProducer
    {
        private String question;
        private String answer;

        @Override
        public String getText()
        {
            return question;
        }
    }

    public static interface ICaptchValueGenerator
    {
        public CaptchaValue generate();
    }

    public static class DefaultCaptchaValueGenerator implements ICaptchValueGenerator
    {
        @Override
        public CaptchaValue generate()
        {
            return new CaptchaValue(TEXT_PRODUCER.getText(), TEXT_PRODUCER.getText());
        }
    }

    public static class SumValueGenerator implements ICaptchValueGenerator
    {
        @Override
        public CaptchaValue generate()
        {
            int num1 = 1 + random.nextInt(10);
            int num2 = 1 + random.nextInt(10);
            CaptchaValue res = new CaptchaValue(num1 + " + " + num2, String.valueOf(num1 + num2));
            return res;
        }
    }

    public static class SubtractValueGenerator implements ICaptchValueGenerator
    {
        @Override
        public CaptchaValue generate()
        {
            int num1 = 8 + random.nextInt(13);
            int num2 = 1 + random.nextInt(7);
            CaptchaValue res = new CaptchaValue(num1 + " - " + num2, String.valueOf(num1 - num2));
            return res;
        }
    }

    private static List<ICaptchValueGenerator> generators = Arrays.asList(
        new DefaultCaptchaValueGenerator(),
        new SumValueGenerator(),
        new SubtractValueGenerator()
    );

    public static CaptchaValue generate()
    {
        return generators.get(random.nextInt(generators.size())).generate();
    }
}
