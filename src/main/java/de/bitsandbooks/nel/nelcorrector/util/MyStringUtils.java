package de.bitsandbooks.nel.nelcorrector.util;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;

public class MyStringUtils {
	
	public static String normalizeString(String s) throws UnsupportedEncodingException
	{
	     String s1 = Normalizer.normalize(s, Normalizer.Form.NFKD);
	     String regex = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
	     String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
	     return s2;
	}
	
}
