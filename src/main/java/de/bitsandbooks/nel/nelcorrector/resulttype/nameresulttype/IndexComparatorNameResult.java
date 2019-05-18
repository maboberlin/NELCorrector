package de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype;

import java.text.Collator;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;

import de.bitsandbooks.nel.nelcorrector.language.GetLocale;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.IndexComparatorIF;
import de.bitsandbooks.nel.nelcorrector.util.NameUtilities;
import de.bitsandbooks.nel.nelinterface2.NameEntry;
import de.bitsandbooks.nel.nelinterface2.NameResult;
import de.bitsandbooks.nel.nelinterface2.Result;

public class IndexComparatorNameResult implements IndexComparatorIF {
	
//	-------------------- FIELDS -------------------------------
	
	private static final String[] GermanIdentifier = {"german", "deutsch", "German", "Deutsch", "GERMAN"};
	private static final String[] EnglishIdentifier = {"english", "englisch", "English", "Englisch", "ENGLISH"};
	
	private Locale locale;
	private Collator collator;		
	
	
//	----------------- CONSTRUCTOR & INSTANCE ------------------

	private static IndexComparatorNameResult instance;
	
	private IndexComparatorNameResult(String language) 
	{
		//init locale
		GeneralProperties props = GeneralProperties.getInstance();
		boolean autoSort = props.getBooleanValue(GeneralProperties.AutoSortMode);
		if (autoSort) {
			locale = GetLocale.getLocale(language);
		}
		else {
			String locale = props.getProperty(GeneralProperties.SortLocale);
			this.locale = new Locale(locale);
		}
		//init collator
		collator = Collator.getInstance(locale);
		collator.setStrength(Collator.TERTIARY);
	}
	
	
	public static IndexComparatorNameResult getInstance(String language) {
		if (instance == null)
			instance = new IndexComparatorNameResult(language);
		return instance;
	}

	
//	------------------- COMPARE METHOD -------------------------
	
	@Override
	public int compare(Result o1, Result o2) 
	{
		NameResult nameRes1 = (NameResult)o1;
		NameResult nameRes2 = (NameResult)o2;
		NameEntry name1 = nameRes1.getName();
		NameEntry name2 = nameRes2.getName();
		//check surname
		String surname1 = name1.getLexicographicSurname();
		String surname2 = name2.getLexicographicSurname();
		surname1 = NameUtilities.getSurnameFromFirstCapital(surname1);
		surname2 = NameUtilities.getSurnameFromFirstCapital(surname2);
		int surnameComparison = collator.compare(surname1, surname2);
		if (surnameComparison != 0)
			return surnameComparison;
		//check forenames (if given) (assumption: surnames are equal)
		String forename1 = name1.getFullForename();
		String forename2 = name2.getFullForename();
		if (forename1 != null && forename2 != null && forename1.length() > 0 && forename2.length() > 0) {
			int forenameComparison = collator.compare(forename1, forename2);
			if (forenameComparison != 0)
				return forenameComparison;
		}
		//check initials (assumption: surnames are equal and forenames are equal or one not given)
		String initial1 = name1.getInitials();
		String initial2 = name2.getInitials();
		if (initial1 != null && initial2 != null) {
			int initialComparison = collator.compare(initial1, initial2);
			if (initialComparison != 0)
				return initialComparison;
		}
		//return zero
		return 0;
	}



}
