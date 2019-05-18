package de.bitsandbooks.nel.nelcorrector.language;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;

public class GetLocale {
	
//	-------------------- FIELDS -------------------------------
	
	private static final String[] GermanIdentifier = {"german", "deutsch", "German", "Deutsch", "GERMAN"};
	private static final String[] EnglishIdentifier = {"english", "englisch", "English", "Englisch", "ENGLISH"};
	
	
//	------------------- METHOD -------------------------------
	
	public static Locale getLocale(String language) {
		Locale locale = null;
		if (ArrayUtils.contains(GermanIdentifier, language))
			locale = new Locale("GERMAN");
		else if (ArrayUtils.contains(EnglishIdentifier, language))
			locale = new Locale("ENGLISH");
		return locale;
	}

}
