package de.bitsandbooks.nel.nelcorrector.util;

public class RomanArabic {
	
	static class RomanArabicEntry {
		String roman;
		int arabic;
		RomanArabicEntry(int arab, String rom) {
			roman = rom;
			arabic = arab;
		}
	}
	
	private static RomanArabicEntry[] table = new RomanArabicEntry[13];
	
	static {
		table[0] = new RomanArabicEntry(1, "I");
		table[1] = new RomanArabicEntry(4, "IV");
		table[2] = new RomanArabicEntry(5, "V");
		table[3] = new RomanArabicEntry(9, "IX");
		table[4] = new RomanArabicEntry(10, "X");
		table[5] = new RomanArabicEntry(40, "XL");
		table[6] = new RomanArabicEntry(50, "L");
		table[7] = new RomanArabicEntry(90, "XC");
		table[8] = new RomanArabicEntry(100, "C");
		table[9] = new RomanArabicEntry(400, "CD");
		table[10] = new RomanArabicEntry(500, "D");
		table[11] = new RomanArabicEntry(900, "CM");
		table[12] = new RomanArabicEntry(1000, "M");
	}


	public static String getRoman(int arabicNumber) {
	    if (arabicNumber > 0 && arabicNumber < 4000) {
	        String romanNumeral = "";
	        while (arabicNumber > 0) {
	            int highestFound = 0;
	            for (int i = 0; i < table.length; i++) {
	                if (table[i].arabic <= arabicNumber) {
	                	highestFound = table[i].arabic; 
	                }
				}
	            for (int j = 0; j < table.length; j++) {
	            	if (table[j].arabic == highestFound) {
	            		romanNumeral += table[j].roman;
	                	arabicNumber -= table[j].arabic;
	                	break;
	            	}
				}
	        }
	        return romanNumeral;
	    } 
	    else 
	    	return "";
	}
	
	public static int getArabic(String romanNumber) {
		if (romanNumber == "" || romanNumber == null)
			return 0;
		else {
			for (int i = table.length-1; i >= 0; i--) {
				if (romanNumber.startsWith(table[i].roman)) {
					return table[i].arabic + getArabic(romanNumber.substring(table[i].roman.length()));
				}
			}
		}
		return 0;
	}
	
	
	public static boolean isRoman(String toCheck) {
		String first;
		for (int i = 0; i < toCheck.length(); i++) {
			first = String.valueOf(toCheck.charAt(i));
			boolean res = false;
			for (int j = 0; j < table.length; j++) {
				if (table[j].roman.equals(first)) {
					res = true;
					break;
				}
			}
			if (!res)
				return false;
		}
		return true;
	}
	
	
	public static boolean isRomanRange(String toCheck) {
		String[] ranges = toCheck.split("-");
		if (ranges.length == 2) {
			if (isRoman(ranges[0]) && isRoman(ranges[1]))
				return true;
		}
		return false;
	}
	      
	
//	------------------------------- STANDARD TEST ----------------------------
	
	
	public static void main(String[] args) {
		System.out.println(getRoman(16));
		System.out.println(getArabic("I"));
		System.out.println(isRoman("IIIIXX"));

	}
	

}
