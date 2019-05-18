package de.bitsandbooks.nel.nelcorrector.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ResultEntryComparator implements Comparator<Result> {

	@Override
	public int compare(Result o1, Result o2) {
		List<TextRange> trList1 = o1.getAllTextRanges();
		List<TextRange> trList2 = o2.getAllTextRanges();
		//check for zero size lists
		if (trList1.size() == 0 && trList2.size() == 0)
			return 0;
		else if (trList1.size() == 0)
			return -1;
		else if (trList2.size() == 0)
			return 1;
		//sort lists
		Collections.sort(trList1);
		Collections.sort(trList2);
		//compare first entry
		return trList1.get(0).compareTo(trList2.get(0));
	}

}
