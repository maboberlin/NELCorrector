package de.bitsandbooks.nel.nelcorrector.util;

import java.util.List;
import java.util.Vector;

public class NameUtilities {
	
	public static boolean forenamePartsMatching(String forenameText, String initialsText) 
	{
		if (forenameText.length() == 0 || initialsText.length() == 0)
			return true;
		String[] forenameParts = forenameText.split("\\s");
		String[] initialParts = initialsText.split("[\\s.]");
		String initialString;
		int firstChar, firstCharInitials, initialPartsIndex = 0;
		boolean matched;
		for (String forename : forenameParts) {
			if (forename.length() == 0)
				continue;
			firstChar = forename.codePointAt(0);
			if (Character.isUpperCase(firstChar)) {
				matched = false;
				for (int i = initialPartsIndex; i < initialParts.length; i++) {
					initialString = initialParts[i];
					if (initialString.length() != 0) {
						firstCharInitials = initialString.codePointAt(0);
						if (firstCharInitials == firstChar) {
							initialPartsIndex = i + 1;
							matched = true;
							break;
						}
					}
				}
				if (!matched && initialPartsIndex < initialParts.length)
					return false;
			}
		}
		return true;
	}

	
	public static String getLexicographicSurname(String surnameText) 
	{
		int c;
		for (int i = 0; i < surnameText.length(); i++) {
			c = surnameText.codePointAt(i);
			if (Character.isUpperCase(c))
				return surnameText.substring(i);
		}
		return surnameText;
	}

	
	public static String getFullForename(String forenameText, String initialText) 
	{
		List<String> resultList = new Vector<String>();
		String[] forenameParts = forenameText.split("\\s");
		String[] initialParts = initialText.split("[\\s.]");
		int namefirstChar, initialfirstChar, forenameIndex = 0;
		boolean matched;
		for (int i = 0; i < initialParts.length; i++) {
			if (initialParts[i].length() == 0) 
				continue;
			initialfirstChar = initialParts[i].codePointAt(0);
			matched = false;
			for (int j = forenameIndex; j < forenameParts.length; j++) {
				if (forenameParts[j].length() == 0) 
					continue;
				namefirstChar = forenameParts[j].codePointAt(0);
				if (namefirstChar == initialfirstChar) {
					forenameIndex = j + 1;
					resultList.add(forenameParts[j]);
					matched = true;
					break;
				}
			}
			if(!matched)
				resultList.add(String.format("%s.", initialParts[i]));
		}
		for (int i = forenameIndex; i < forenameParts.length; i++) 
			resultList.add(forenameParts[i]);
		StringBuilder result = new StringBuilder();
		for (String string : resultList) 
			result.append(String.format("%s ", string));
		return result.toString().trim();
	}


	public static String getSurnameFromFirstCapital(String surname1) 
	{
		int c;
		for (int i = 0; i < surname1.length(); i++) {
			c = surname1.codePointAt(i);
			if (Character.isUpperCase(c))
				return surname1.substring(i);
		}
		return surname1;
	}


	/**
	 * @return returns empty string if no capital letter could be found - else returns first capital letter as string
	 */
	public static String getFirstInitial(String name) {
		int c;
		for (int i = 0; i < name.length(); i++) {
			c = name.codePointAt(i);
			if (Character.isUpperCase(c))
				return String.valueOf((char)c);
		}
		return "";
	}

}
