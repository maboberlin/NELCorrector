package de.bitsandbooks.nel.nelcorrector;

import de.bitsandbooks.nel.nelcorrector.util.NameUtilities;

public class NameMethodsTest {

	public static void main(String[] args) {
		String f = "Peter Alexander";
		String i = "P.A.A.W";
		String s = NameUtilities.getFullForename(f, i);
		System.out.println(s);
		boolean b = NameUtilities.forenamePartsMatching(f, i);
		System.out.println(b);
	}

}
