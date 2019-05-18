package de.bitsandbooks.nel.nelcorrector.util;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class MyListUtils<T> {
	
	/**
	 * @return returns true if one of the elements in the list has the equal reference value as the object - else false.
	 */
	public static <E> boolean contains2(List<E> list, E el1) 
	{
		for (E el2 : list) {
			if (el2 == el1)
				return true;
		}
		return false;
	}

	
	public static List<TextRange> extractTextRanges(List<Entry<TextRange, ResultInformation>> toDelete) 
	{
		List<TextRange> result = new Vector<TextRange>();
		for (Entry<TextRange,ResultInformation> entry : toDelete) {
			result.add(entry.getKey());
		}
		return result;
	}

}
