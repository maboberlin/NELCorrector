package de.bitsandbooks.nel.nelcorrector.logic;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatEvent;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatListener;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class Statistics implements BibliographyFormatListener {
	
//	--------------------------- FIELDS --------------------------------------------
	
	private boolean bibResultsCancelled = false;
	
	
//	----------------------- INSTANCE AND CONSTRUCTOR ------------------------------
	
	private static Statistics instance;
	
	private Statistics() {
	}

	public static Statistics getInstance() {
		if (instance == null)
			instance = new Statistics();
		return instance;
	}
	
	
//	------------------------- METHODS --------------------------------------------
	
	@Override
	public void formatIsSet(BibliographyFormatEvent e) {
		bibResultsCancelled = e.cancelBibliographys;
	}
	
	
	public String getStatistics() 
	{
		Text text = Text.getInstance();
		ResultListModel results = ResultListModel.getInstance();
		if (text != null && results != null && text.getText() != null && results.getSize() != 0) {
			StringBuilder result = new StringBuilder();
			//set number of pages
			int nrOfPages = 0; 
			int lastPageNr1 = Integer.MIN_VALUE, lastArticleNr = Integer.MIN_VALUE;
			for (Page pg : text.getText().getPageList()) {
				if ((pg.getPageNumber() != lastPageNr1 || pg.getArticleNumber() != lastArticleNr)
					&& !(bibResultsCancelled && pg.isBibliography()))
					nrOfPages++; //only count different pages (not also different sections)
				lastPageNr1 = pg.getPageNumber();
				lastArticleNr = pg.getArticleNumber();
			}
			//get number of names and findspots and rates
			Result el;
			List<TextRange> TRList;
			TextLocation start;
			int nrOfNames = 0, nrOfEntries = 0, nrOfIndexEntries = 0, bibResults = 0, bibIndexEntries = 0, thisResultBibResults, lastPageNr2, lastArticleNr2;
			for (Iterator<Result> it = results.getMainResultIterator(true, null); it.hasNext(); ) {
				el = it.next();
				nrOfNames++;
				nrOfEntries += el.getResultMap().size();
				TRList = el.getAllTextRanges();
				Collections.sort(TRList);
				lastPageNr2 = Integer.MIN_VALUE;
				lastArticleNr2 = Integer.MIN_VALUE;
				thisResultBibResults = 0;
				for (TextRange textRange : TRList) {
					start = textRange.getStartOffset();
					if (start.pageNumber != lastPageNr2 && start.articleNumber != lastArticleNr2) {
						nrOfIndexEntries++;
						bibIndexEntries = text.isBibliographyResult(textRange) ? bibIndexEntries + 1 : bibIndexEntries;
					}
					thisResultBibResults = text.isBibliographyResult(textRange) ? thisResultBibResults + 1 : thisResultBibResults;
					lastPageNr2 = start.pageNumber;
					lastArticleNr = start.articleNumber;
				}
				bibResults += thisResultBibResults;
				nrOfNames = bibResultsCancelled && thisResultBibResults == TRList.size() ? nrOfNames - 1 : nrOfNames; //all entries are bib entries
			}
			if (bibResultsCancelled) {
				nrOfEntries -= bibResults;
				nrOfIndexEntries -= bibIndexEntries;
			}
			float namesPerPage = (float)nrOfNames / (float)nrOfPages;
			float fsPerPage = (float)nrOfEntries / (float)nrOfPages;
			float ixEPerPage = (float)nrOfIndexEntries / (float)nrOfPages;
			//set and return result
			result.append(String.format("Number of pages: %d%n", nrOfPages));
			result.append(String.format("Number of names: %d%n", nrOfNames));
			result.append(String.format("Number of text results: %d%n", nrOfEntries));
			result.append(String.format("Number of index entries: %d%n", nrOfIndexEntries));
			result.append(String.format("Number of names per page: %.2f%n", namesPerPage));
			result.append(String.format("Number of text results per page: %.2f%n", fsPerPage));
			result.append(String.format("Number of index entries per page: %.2f%n", ixEPerPage));
			return result.toString();
		}
		else
			return "No data has been set yet ...";
	}

}
