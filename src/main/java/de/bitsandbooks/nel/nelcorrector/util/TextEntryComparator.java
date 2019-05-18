package de.bitsandbooks.nel.nelcorrector.util;

import java.util.Comparator;
import java.util.Map.Entry;

import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class TextEntryComparator implements Comparator<Entry<TextRange, ResultInformation>> {
	
	@Override
	public int compare(	Entry<TextRange, ResultInformation> o1, 
						Entry<TextRange, ResultInformation> o2) 
	{
		return o1.getKey().compareTo(o2.getKey());
	}

}
