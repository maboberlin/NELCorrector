package de.bitsandbooks.nel.nelcorrector.util;

import java.util.Comparator;

import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class IgnoreArticleTRComparator implements Comparator<TextRange> {

	@Override
	public int compare(TextRange o1, TextRange o2) 
	{
		TextLocation o1start = o1.getStartOffset();
		TextLocation o2start = o2.getStartOffset();
		TextLocation o1end = o1.getEndOffset();
		TextLocation o2end = o2.getEndOffset();
		if (o1start.pageNumber == o2start.pageNumber) {
			if (o1start.sectionNumber == o2start.sectionNumber) {
				if (o1start.signNumber == o2start.signNumber) {
					if (o1end.pageNumber == o2end.pageNumber) {
						if (o1end.sectionNumber == o2end.sectionNumber) {
							if (o1end.signNumber == o2end.signNumber) {
								return 0;
							}
							else
								return o1end.signNumber - o2end.signNumber;
						}
						else
							return o1end.sectionNumber - o2end.sectionNumber;
					}
					else
						return o1end.pageNumber - o2end.pageNumber;
				}
				else
					return o1start.signNumber - o2start.signNumber;
			}
			else
				return o1start.sectionNumber - o2start.sectionNumber;
		}
		else
			return o1start.pageNumber - o2start.pageNumber;
	}

}
