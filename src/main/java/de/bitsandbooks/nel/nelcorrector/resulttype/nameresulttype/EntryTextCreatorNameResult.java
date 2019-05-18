package de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatEvent;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.util.NameUtilities;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;
import de.bitsandbooks.nel.nelinterface2.NameEntry;
import de.bitsandbooks.nel.nelinterface2.NameResult;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class EntryTextCreatorNameResult implements EntryTextCreatorIF {
	
//	------------------ FIELDS ---------------------------------------

	private Text text;
	private ResultListModel listModel;
	private ResultTypeHandlerIF resultTypeHandler;
	private String seperatorString;
	private int longestResultLength;
	private int entryMode = 0;
	private boolean cancelBibliographys = false;
	private boolean printComma = true;
	
	
//	----------------- CONSTRUCTOR & INSTANCE ------------------
	
	private static EntryTextCreatorNameResult instance;
	
	private EntryTextCreatorNameResult() {
		text = Text.getInstance();
		listModel = ResultListModel.getInstance();
		seperatorString = StringUtils.repeat('-', 15);
	}
	
	public static EntryTextCreatorNameResult getInstance() {
		if (instance == null)
			instance = new EntryTextCreatorNameResult();
		return instance;
	}
	
	
//	-------------------- METHODS ------------------------------
	
	@Override
	public void formatIsSet(BibliographyFormatEvent e) {
		entryMode = e.entryMode;
		cancelBibliographys = e.cancelBibliographys;
		printComma = e.printComma;
	}
	
	
	@Override
	public void setResultTypeHandler(ResultTypeHandlerIF resultTypeHandler) {
		this.resultTypeHandler = resultTypeHandler;
	}
	
	
	@Override
	public void setLongestResultLength() 
	{
		longestResultLength = Integer.MIN_VALUE;
		Iterator<Result> it = listModel.getMainResultIterator(true, null);
		NameResult nameRes;
		NameEntry name;
		int length;
		while (it.hasNext()) {
			nameRes = (NameResult)it.next();
			name = nameRes.getName();
			length = name.getFullName().length();
			if (length  > longestResultLength) 
				longestResultLength = length;
		}
	}
	

	@Override
	public String getFullResultText(Result result) 
	{
		if (result instanceof NameResult) {
			NameResult nameRes = (NameResult)result;
			NameEntry name = nameRes.getName();
			return name.toString();
		}
		else
			return "";
	}
	
	
	@Override
	public String getLexicographicText(Result result) 
	{
		if (result instanceof NameResult) {
			NameResult nameRes = (NameResult)result;
			NameEntry name = nameRes.getName();
			String lexicographicName = name.getLexicographicSurname();
			return lexicographicName;
		}
		else
			return "";
	}
	

	@Override
	public String getResultText(Result result, boolean indexListSelected, boolean printMode, int[] pageNumbers, Comparator<TextRange> comp) 
	{
		String text;
		if (indexListSelected) 
			text = getIndexText(result, printMode, pageNumbers, comp);
		else
			text = getPerPageText(result, pageNumbers);
		return text;
	}
	
	
	@Override
	public String getEntryText(Result result) 
	{
		NameResult nameRes = (NameResult)result;
		NameEntry name = nameRes.getName();
		Iterator<Result> backwardIt = listModel.getMainResultIterator(false, result);
		Iterator<Result> forwardIt = listModel.getMainResultIterator(true, result);
		NameLevels nameLevels1 = getNameLevels(backwardIt, name);
		NameLevels nameLevels2 = getNameLevels(forwardIt, name);
		//set nameLevels1 as combined result
		nameLevels1.initials = nameLevels1.initials || nameLevels2.initials;
		nameLevels1.forename = nameLevels1.forename || nameLevels2.forename;
		nameLevels1.fullname = nameLevels1.fullname || nameLevels2.fullname;
		//build name string
		String nameString;
		if (nameLevels1.fullname)
			nameString = name.getFullName();
		else if (nameLevels1.forename) 
			nameString = name.getSurname().concat(", ").concat(name.getFullForename());
		else if (nameLevels1.initials)
			nameString = name.getSurname().concat(", ").concat(name.getInitials());
		else
			nameString = name.getSurname();
		return nameString;
	}
	
	
	@Override
	public String getLocatorString(Result res, int[] pageNumbers, Comparator<TextRange> comp)
	{
		NameResult nameRes = (NameResult)res;
		StringBuilder resultsStringBuilder = new StringBuilder();
		List<TextRange> trList = nameRes.getAllTextRanges();
		if (comp == null) 
			Collections.sort(trList);
		else
			Collections.sort(trList, comp);
		TextRange tr;
		String pageNrText;
		int pageNr, articleNr, lastPageNr = Integer.MIN_VALUE, lastArticleNr = Integer.MIN_VALUE, lastPrintedNr = 0, lastContigousNr = 0;
		boolean isNewArticle, isContigousNr = false, isLastEntry, isBibliographyResult, isPreviousPageNr, contigousFlag = false;
		for (int i = 0; i < trList.size(); i++) {
			tr = trList.get(i);
			pageNr = tr.getStartOffset().pageNumber;
			isPreviousPageNr = pageNr == lastPageNr;
			isLastEntry = i == trList.size() - 1;
			isBibliographyResult = text.isBibliographyResult(tr);
			isContigousNr = pageNr == lastPageNr + 1 || pageNr == lastPageNr - 1 || (isPreviousPageNr && isContigousNr);
			articleNr = tr.getStartOffset().articleNumber;
			isNewArticle = articleNr != lastArticleNr;
			pageNrText = pageNr >= 0 ? String.valueOf(pageNr) : RomanArabic.getRoman(Math.abs(pageNr));
			//create text depending on entry mode
			if (pageNumbers == null || (pageNumbers != null && ArrayUtils.contains(pageNumbers, pageNr))) {
				if (entryMode == 0) {//MODE 1: print all page numbers
					if ((!isPreviousPageNr || isNewArticle) && !(isBibliographyResult && cancelBibliographys))
						resultsStringBuilder = printActual(resultsStringBuilder, pageNrText);
				}
				else if (entryMode >= 1 && entryMode <= 3) {//MODE Description -> see 'printContigous' method
					if (!isContigousNr && !isNewArticle && !isPreviousPageNr) {
						if (contigousFlag) {
							resultsStringBuilder = printContigous(entryMode, resultsStringBuilder, pageNrText, lastPrintedNr,lastContigousNr);
							contigousFlag = false;
						}
						if (!(isBibliographyResult && cancelBibliographys)) {
							printActual(resultsStringBuilder, pageNrText);
							lastPrintedNr = pageNr;
						}
					}
					else if (isContigousNr && !isNewArticle) {
						if (isBibliographyResult && cancelBibliographys) {
							if (contigousFlag) 
								printContigous(entryMode, resultsStringBuilder, pageNrText, lastPrintedNr,lastContigousNr);
							contigousFlag = false;
						}
						else if (isLastEntry) {
							lastContigousNr = pageNr;
							printContigous(entryMode, resultsStringBuilder, pageNrText, lastPrintedNr,lastContigousNr);
						}
						else {
							contigousFlag = true;
							lastContigousNr = pageNr;
						}
					}
					else if (isNewArticle) {
						if (contigousFlag) {
							resultsStringBuilder = printContigous(entryMode, resultsStringBuilder, pageNrText, lastPrintedNr, lastContigousNr);
							contigousFlag = false;
						}
						if (!(isBibliographyResult && cancelBibliographys)) {
							printActual(resultsStringBuilder, pageNrText);
							lastPrintedNr = pageNr;
						}
					}
				}
			}
			else if (contigousFlag) {
				printContigous(entryMode, resultsStringBuilder, pageNrText, lastPrintedNr,lastContigousNr);
				contigousFlag = false;
			}
			lastPageNr = pageNr;
			lastArticleNr = articleNr;
		}
		String resultsString = resultsStringBuilder.toString().trim();
		return resultsString;
	}

	
	private String getIndexText(Result result, boolean printMode, int[] pageNumbers, Comparator<TextRange> comp) 
	{
		String resultText;
		String nameString = getEntryText(result);
		String resultsString = getLocatorString(result, pageNumbers, comp);
		if (printMode && resultsString.length() == 0)
			return "";
		if (resultsString.endsWith(","))
			resultsString = resultsString.substring(0, resultsString.length() - 1);
		//build full string
		resultText = String.format("%s%s %s", nameString, printComma ? "," : "", resultsString);
		return resultText;
	}



	//PER PAGE TEXT
	private String getPerPageText(Result result, int[] pageNumbers) 
	{
		String resultText;
		if (resultTypeHandler.isSeperator(result)) {//CASE: separator
			int pageNr = result.getAllTextRanges().get(0).getStartOffset().pageNumber;
			String pageNrText;
			if (pageNr >= 0)
				pageNrText = String.valueOf(pageNr);
			else 
				pageNrText = RomanArabic.getRoman(Math.abs(pageNr));
			resultText = String.format("%s %s %s", seperatorString, pageNrText, seperatorString);
			return resultText;
		}
		else {//CASE: Name Result
			NameResult nameRes = (NameResult)result;
			NameEntry name = nameRes.getName();
			resultText = name.toString();
			TextRange resultRange = nameRes.getAllTextRanges().get(0);
			int pageNr = resultRange.getStartOffset().pageNumber;
			if (pageNumbers == null || (pageNumbers != null && ArrayUtils.contains(pageNumbers, pageNr))) {
				resultText = cancelBibliographys && text.isBibliographyResult(resultRange) ? String.format("<html><strike>%s</strike></html>", resultText) : resultText;
			}
			else
				resultText = "";
			return resultText;
		}
	}
	
	
//	------------------------------ AUXILIARY ---------------------------------------
	
	private NameLevels getNameLevels(Iterator<Result> it, NameEntry name) 
	{
		NameLevels result = new NameLevels();
		NameResult nameRes;
		NameEntry nameToCompare;
		while (it.hasNext()) {
			nameRes = (NameResult)it.next();
			nameToCompare = nameRes.getName();
			if (!nameToCompare.getSurname().equals(name.getSurname()))
				return result;
			else {//equal surname exists
				result.initials = true;
				if (!NameUtilities.getFirstInitial(nameToCompare.getInitials()).equals(NameUtilities.getFirstInitial(name.getInitials())))
					return result;
				else {//equal initials exist (initials are count as equal if first initial is equal)
					result.forename = true;
					if (!nameToCompare.getFullForename().equals(name.getFullForename()))
						return result;
					else
						result.fullname = true;
				}
			}
		}
		return result;
	}
	
	
	private StringBuilder printActual(StringBuilder resultsStringBuilder, String pageNrText) 
	{
		resultsStringBuilder = resultsStringBuilder.length() == 0 ? resultsStringBuilder.append(String.format("%s", pageNrText)) : resultsStringBuilder.append(String.format(", %s", pageNrText));
		return resultsStringBuilder;
	}
	
	
	private StringBuilder printContigous(int entryMode, StringBuilder resultsStringBuilder, String pageNrText, int lastPrintedNr, int lastContigousNr) 
	{
		switch (entryMode) {
		case 1: //MODE 2: print f./ff. for all following page numbers
			resultsStringBuilder = (lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 1) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 1) ? resultsStringBuilder.append("ff.") : resultsStringBuilder.append("f.");
			return resultsStringBuilder;
		case 2: //MODE 3: print f./ff. for +1/+2 pages and x-y for following pages
			if ((lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 2)) {
				String lastContigousText = lastContigousNr >= 0 ? String.valueOf(lastContigousNr) : RomanArabic.getRoman(Math.abs(lastContigousNr));
				resultsStringBuilder.append(String.format("-%s", lastContigousText));
			} 
			else if ((lastPrintedNr >= 0 && lastPrintedNr == lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr == lastContigousNr + 2)) 
				resultsStringBuilder.append("ff.");
			else
				resultsStringBuilder.append("f.");
			return resultsStringBuilder;
		case 3: //MODE 4: print f./ff. for +1/+2 pages and x-y for following pages, thereby y is shortened if possible
			if ((lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 2)) {
				if (lastContigousNr >= 0) {
					int exponent = pageNrText.length() - 1;
					int pow = (int)Math.pow((double)10, (double)exponent);
					int reducedContigousNr = lastContigousNr % pow;
					if (reducedContigousNr < pow)
						resultsStringBuilder.append(String.format("-%s", String.valueOf(reducedContigousNr)));
					else
						resultsStringBuilder.append(String.format("-%s", String.valueOf(lastContigousNr)));
				}
				else {
					String lastContigousText = lastContigousNr >= 0 ? String.valueOf(lastContigousNr) : RomanArabic.getRoman(Math.abs(lastContigousNr));
					resultsStringBuilder.append(String.format("-%s", lastContigousText));	
				}
			} 
			else if ((lastPrintedNr >= 0 && lastPrintedNr == lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr == lastContigousNr + 2)) 
				resultsStringBuilder.append("ff.");
			else
				resultsStringBuilder.append("f.");
			return resultsStringBuilder;
		default:
			resultsStringBuilder = (lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 1) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 1) ? resultsStringBuilder.append("ff.") : resultsStringBuilder.append("f.");
			return resultsStringBuilder;
		}

	}
	
	
//	----------------------------- NAME LEVEL CLASS ---------------------------------
	
	private class NameLevels {
		private boolean initials;
		private boolean forename;
		private boolean fullname;
	}

}


// -------------------------------- TRASH ------------------------------------------

/*
case 2: //MODE 3: print f./ff. for +1/+2 pages and x-y for following pages
	if (!isContigousNr && !isNewArticle && !isPreviousPageNr) {
		if (contigousFlag) 
			resultsStringBuilder = printContigousFFAndPagesFull(resultsStringBuilder, lastPrintedNr,lastContigousNr);
		if (!(isBibliographyResult && cancelBibliographys)) {
			printActual(resultsStringBuilder, pageNrText);
			lastPrintedNr = pageNr;
		}
		contigousFlag = false;
	}
	else if (isContigousNr && !isNewArticle) {
		if (isBibliographyResult && cancelBibliographys) {
			if (contigousFlag) 
				printContigousFFAndPagesFull(resultsStringBuilder, lastPrintedNr,lastContigousNr);
			contigousFlag = false;
		}
		else if (isLastEntry) {
			lastContigousNr = pageNr;
			printContigousFFAndPagesFull(resultsStringBuilder, lastPrintedNr,lastContigousNr);
		}
		else {
			contigousFlag = true;
			lastContigousNr = pageNr;
		}
	}
	else if (isNewArticle) {
		if (contigousFlag) {
			resultsStringBuilder = printContigousFFAndPagesFull(resultsStringBuilder, lastPrintedNr, lastContigousNr);
			contigousFlag = false;
		}
		printActual(resultsStringBuilder, pageNrText);
		lastPrintedNr = pageNr;
	}
	break;
	*/
	
/*	
case 2: //MODE 3: print f./ff. for +1/+2 pages and x-y for following pages
	if (isContigousNr && !isNewArticle) {
		lastContigousNr = pageNr;
		contigousFlag = true;
	}
	else {
		if (contigousFlag) {
			if ((lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 2)) {
				String lastContigousText = lastContigousNr >= 0 ? String.valueOf(lastContigousNr) : RomanArabic.getRoman(Math.abs(lastContigousNr));
				resultsStringBuilder.append(String.format("-%s", lastContigousText));
			} 
			else if ((lastPrintedNr >= 0 && lastPrintedNr == lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr == lastContigousNr + 2)) 
				resultsStringBuilder.append("ff.");
			else
				resultsStringBuilder.append("f.");
			contigousFlag = false;
		}		
		resultsStringBuilder = printActual(resultsStringBuilder,
				pageNrText);
		lastPrintedNr = pageNr;
	}
	break;
	*/
/*
case 3: //MODE 4: print f./ff. for +1/+2 pages and x-y for following pages, thereby y is shortened if possible
	if (isContigousNr && !isNewArticle) {
		lastContigousNr = pageNr;
		contigousFlag = true;
	}
	else {
		if (contigousFlag) {
			if ((lastPrintedNr >= 0 && lastPrintedNr < lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr > lastContigousNr + 2)) {
				if (lastContigousNr >= 0) {
					int exponent = pageNrText.length() - 1;
					int pow = (int)Math.pow((double)10, (double)exponent);
					int reducedContigousNr = lastContigousNr / pow;
					if (reducedContigousNr < pow)
						resultsStringBuilder.append(String.format("-%s", String.valueOf(reducedContigousNr)));
					else
						resultsStringBuilder.append(String.format("-%s", String.valueOf(lastContigousNr)));
				}
				else {
					String lastContigousText = lastContigousNr >= 0 ? String.valueOf(lastContigousNr) : RomanArabic.getRoman(Math.abs(lastContigousNr));
					resultsStringBuilder.append(String.format("-%s", lastContigousText));	
				}
			} 
			else if ((lastPrintedNr >= 0 && lastPrintedNr == lastContigousNr - 2) || (lastPrintedNr < 0 && lastPrintedNr == lastContigousNr + 2)) 
				resultsStringBuilder.append("ff.");
			else
				resultsStringBuilder.append("f.");
			contigousFlag = false;
		}		
		resultsStringBuilder = printActual(resultsStringBuilder,
				pageNrText);
		lastPrintedNr = pageNr;
	}
	break;
default:
	if (!isPreviousPageNr || isNewArticle)
		resultsStringBuilder = resultsStringBuilder.length() == 0 ? resultsStringBuilder.append(String.format("%s", pageNrText)) : resultsStringBuilder.append(String.format(", %s", pageNrText));
	break;
	
}
*/


