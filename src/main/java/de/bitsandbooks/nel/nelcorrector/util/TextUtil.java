package de.bitsandbooks.nel.nelcorrector.util;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

public class TextUtil {
	
	private static final String[] HYPHEN_UTF = {"002D", "007E", "058A", "1806", "2010", "2011", "2012", "2013", "2014", "2015", "2053", "207B", "208B", "2212", "301C", "3030"}; 
	
	private static final int[] HYPHEN_UTF_CODEPOINTS;
	static {
		HYPHEN_UTF_CODEPOINTS = new int[HYPHEN_UTF.length];
		for (int i = 0; i < HYPHEN_UTF_CODEPOINTS.length; i++) {
			HYPHEN_UTF_CODEPOINTS[i] = Integer.parseInt(HYPHEN_UTF[i], 16);
		}
	}
	
	public static boolean pageEndsWithHyphen(String pageText) 
	{
		if (pageText == null)
			return false;
		String text = pageText.trim();
		int lastSpace = text.lastIndexOf((char)32);
		if (lastSpace != -1) {
			String lastWord = text.substring(lastSpace + 1);
			if (lastWord.length() <= 0)
				return false;
			char lastChar = lastWord.charAt(lastWord.length() - 1);
			if (ArrayUtils.contains(HYPHEN_UTF_CODEPOINTS, lastChar))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	
	public static boolean pageStartsWithLowerCase(String pageText) 
	{
		if (pageText != null) {
			String text = pageText.trim();
			int firstSpace = text.indexOf((char)32);
			if (firstSpace != -1) {
				String firstWord = text.substring(0, firstSpace);
				if (firstWord.length() > 0 && Character.isLowerCase(firstWord.codePointAt(0)))
					return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param pageString for example: I-X,1,5,6-10
	 */
	public static int[] extractPageRanges(String pageString) throws NumberFormatException {
		List<Integer> numberList = new Vector<Integer>();
		String[] pageData = pageString.split("\\s*,\\s*");
		String[] range;
		String pageDataString;
		Integer nr;
		int start, end;
		for (String string : pageData) {
			pageDataString = string.trim();
			if (pageDataString.matches("\\d+")) {
				nr = Integer.parseInt(pageDataString);
				numberList.add(nr);
			}
			else if (RomanArabic.isRoman(pageDataString)) {
				nr = RomanArabic.getArabic(pageDataString);
				numberList.add(nr);
			}
			else if (pageDataString.matches("\\d+-\\d+")) {
				range = pageDataString.split("-");
				start = Integer.parseInt(range[0]);
				end = Integer.parseInt(range[1]);
				numberList.addAll(IntStream.rangeClosed(Math.min(start, end), Math.max(start, end)).boxed().collect(Collectors.toList()));
			}
			else if (RomanArabic.isRomanRange(pageDataString)) {
				range = pageDataString.split("-");
				start = RomanArabic.getArabic(range[0]);
				end = RomanArabic.getArabic(range[1]);
				numberList.addAll(IntStream.rangeClosed(Math.negateExact(Math.max(start, end)), Math.negateExact(Math.min(start, end))).boxed().collect(Collectors.toList()));
			}
		}
		int[] result = new int[numberList.size()];
		for (int i = 0; i < numberList.size(); i++) {
			result[i] = numberList.get(i);
		}
		return result;
	}
	
}
