package de.bitsandbooks.nel.nelcorrector.resulttype;

import java.util.Comparator;

import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatListener;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public interface EntryTextCreatorIF extends BibliographyFormatListener {
	
	public void setResultTypeHandler(ResultTypeHandlerIF resultTypeHandler);
	
	/**
	 * @param printMode if true the results with no findspots are returned as empty string
	 * @param exportPageNrs 
	 */
	public String getResultText(Result result, boolean indexListSelected, boolean printMode, int[] exportPageNrs, Comparator<TextRange> comparator);

	public void setLongestResultLength();

	public String getFullResultText(Result result);

	public String getLexicographicText(Result result);
	
	public String getEntryText(Result result);
	
	public String getLocatorString(Result res, int[] pageNumbers, Comparator<TextRange> comp);

}
