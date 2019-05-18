package de.bitsandbooks.nel.nelcorrector.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractListModel;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.resulttype.IndexComparatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.util.MyListUtils;
import de.bitsandbooks.nel.nelcorrector.util.ResultEntryComparator;
import de.bitsandbooks.nel.nelcorrector.util.TRAndIntWrapper;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.ResultTreeEntry;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ResultListModel extends AbstractListModel<Result> {
	
//	--------------------------- FIELDS -----------------------------
	
	private LinkedList<Result> mainResults, perPageResults;
	private EntryTextCreatorIF listTextCreator;
	private ResultTypeHandlerIF resultTypeHandler;
	private IndexComparatorIF indexComparator;
	private String searchType;
	private int longestNameLength;
	private boolean indexModus;
	
	
//	------------------ CONSTRUCTOR & INSTANCE -----------------------
	
	private static ResultListModel instance;
	
	private ResultListModel() {
	}
	
	public static ResultListModel getInstance() {
		if (instance == null)
			instance = new ResultListModel();
		return instance;
	}
	
	
//	---------------------- DATA INITIALIZATION ----------------------
	
	public void setResultData(TreeSet<ResultTreeEntry> resultTree, String searchType, IndexComparatorIF comparatorIF, EntryTextCreatorIF listTextCreatorIF, ResultTypeHandlerIF resultTypeHandlerIF) 
	{
		//set search type
		this.searchType = searchType;
		//set comparators
		indexComparator = comparatorIF;
		listTextCreator = listTextCreatorIF;
		resultTypeHandler = resultTypeHandlerIF;
		//set results
		mainResults = new LinkedList<Result>();
		for (ResultTreeEntry resultTreeEntry : resultTree) {
			for (Result res : resultTreeEntry.getResultList()) {
				mainResults.add(res);
			}
		}
		update();
	}

		
//	---------------------- GENERAL METHODS -----------------------------

	public void changeIndexListMode(boolean mainIndexMode) 
	{
		//set modus
		indexModus = mainIndexMode;
		//fire event
		fireContentsChanged(this, 0, getSize());
	}
	
	
	@Override
	public int getSize()
	{
		if (mainResults !=  null && perPageResults != null) {
			if (indexModus)
				return mainResults.size();
			else
				return perPageResults.size();
		}
		else
			return 0;
	}

	
	@Override
	public Result getElementAt(int index) 
	{
		if (mainResults !=  null && perPageResults != null && mainResults.size() > 0 && perPageResults.size() > 0) {
			if (indexModus) {
				if (index >= 0 && index < mainResults.size())
					return mainResults.get(index);
			}
			else {
				if (index >= 0 && index < perPageResults.size())
					return perPageResults.get(index);
			}
		}
		return null;
	}
	
	
	public Result getElementAtMainResultList(int index) {
		if (index >= 0 && index < mainResults.size())
			return mainResults.get(index);
		else 
			return null;
	}
	
	
	public int getIndexByResult(Result newEmptyResult) 
	{
		List<Result> list = indexModus ? mainResults : perPageResults;
		Result el;
		for (int i = 0; i < list.size(); i++) {
			el = list.get(i);
			if (el == newEmptyResult)
				return i;
		}
		return -1;
	}
	
	
	public int getMainResultIndexByName(Result oldResult) 
	{
		Result otherResult;
		for (int i = 0; i < mainResults.size(); i++) {
			otherResult = mainResults.get(i);
			if (resultTypeHandler.resultContentEquals(otherResult, oldResult))
				return i;
		}
		return 0;
	}

	
	public int getIndexByTextRange(TextRange oldResult) 
	{
		Result otherResult;
		for (int i = 0; i < perPageResults.size(); i++) {
			otherResult = perPageResults.get(i);
			if (otherResult.getAllTextRanges().get(0) == oldResult)
				return i;
		}
		return -1;
	}
	
	
	public TRAndIntWrapper getResultForTextLocation(TextLocation loc) 
	{
		if (!indexModus) 
			return null;
		Result res;
		for (int i = 0; i < mainResults.size(); i++) {
			res = mainResults.get(i);
			for (TextRange tr : res.getAllTextRanges()) {
				if (tr.getStartOffset().compareTo(loc) <= 0 && tr.getEndOffset().compareTo(loc) >= 0) {
					return new TRAndIntWrapper(tr, i);
				}
			}
		}
		return null;
	}
	
	
	public Result getFirstEmptyResult() 
	{
		for (Result entry : mainResults) {
			if (entry.getResultMap().size() == 0)
				return entry;
		}
		return null;
	}
	
	
	public List<TextRange> getThisPageTextRanges(Page pg) 
	{
		List<TextRange> result = new Vector<TextRange>();
		TextLocation start;
		for (Result entry : mainResults) {
			for (TextRange textRange : entry.getAllTextRanges()) {
				start = textRange.getStartOffset();
				if (start.articleNumber == pg.getArticleNumber() && start.sectionNumber == pg.getPageSectionNumber() && start.pageNumber == pg.getPageNumber()) 
					result.add(textRange);
			}
		}
		return result;
	}
	
	
	private void updatePerPageStructure() 
	{
		perPageResults = new LinkedList<Result>();
		//split main list to single result entry list
		for (Result result : mainResults) {
			Collection<? extends Result> singleEntryResults = resultTypeHandler.splitResultToSingleEntryResults(result);
			perPageResults.addAll(singleEntryResults);
		}
		//sort per page result
		Comparator<Result> singleEntryResultComparator = new ResultEntryComparator();
		Collections.sort(perPageResults, singleEntryResultComparator);
		//insert separators
		Result result, separator;
		List<TextRange> trList;
		TextLocation startOffset, seperatorMark;
		int pageNr, lastPageNr = Integer.MIN_VALUE;
		for (ListIterator<Result> li = perPageResults.listIterator(); li.hasNext(); ) {
			result = li.next();
			trList = result.getAllTextRanges();
			if (trList.size() == 1) {
				startOffset = trList.get(0).getStartOffset();
				pageNr = startOffset.pageNumber;
				if (pageNr != lastPageNr) {//add seperator -> result with pageNr set
					separator = new Result();
					seperatorMark = new TextLocation(startOffset.articleNumber, pageNr, startOffset.sectionNumber, 0);
					separator.addResult(new TextRange(seperatorMark, seperatorMark), null);
					li.previous();
					li.add(separator);
				}
				lastPageNr = pageNr;
			}
		}
	}
	
	
	public List<TextRange> getAllResultsForLexicoName(String lexicoResultString) 
	{
		List<TextRange> result = new Vector<>();
		String thisLexicoResultString;
		for (Result res : mainResults) {
			thisLexicoResultString = listTextCreator.getLexicographicText(res);
			if (thisLexicoResultString.contains(lexicoResultString) || lexicoResultString.contains(thisLexicoResultString))
				result.addAll(res.getAllTextRanges());
		}
		return result;
	}

	
	
	
//	----------------------------- MODIFY METHODS -----------------------------------
	
	public Result delete(Result resultToDelete) 
	{
		Result deletedResult = null;
		Result el;
		for (Iterator<Result> it = mainResults.iterator(); it.hasNext(); ) {
			el = it.next();
			if (el == resultToDelete) {
				deletedResult = el;
				it.remove();
				break;
			}
		}
		update();
		return deletedResult;
	}
	
	
	public List<Map.Entry<TextRange, ResultInformation>> delete(Result result, List<TextRange> toDelete) 
	{
		List<Map.Entry<TextRange, ResultInformation>> deletedValues = new Vector<Map.Entry<TextRange, ResultInformation>>();
		int mainListIndex = getMainResultIndexByName(result);
		Result mainListResult = mainResults.get(mainListIndex);
		Map.Entry<TextRange, ResultInformation> el;
		for (Iterator<Map.Entry<TextRange, ResultInformation>> it = mainListResult.getResultMap().entrySet().iterator(); it.hasNext(); ) {
			el = it.next();
			if (MyListUtils.<TextRange>contains2(toDelete, el.getKey())) {
				deletedValues.add(el);
				it.remove();
			}
		}
		update();
		return deletedValues;
	}
	
	
	public void insert(Result newResult) {
		mainResults.add(newResult);
		update();
	}
	
	
	public void insert(List<Result> newResultList) {
		mainResults.addAll(newResultList);
		update();
	}
	
	
	public void insert(Result destiny, List<Entry<TextRange, ResultInformation>> originList, boolean doUpdate) 
	{
		int mainResultIndex = getMainResultIndexByName(destiny);
		Result mainResultEntry = mainResults.get(mainResultIndex);
		Map<TextRange, ResultInformation> resultMap = mainResultEntry.getResultMap();
		for (Map.Entry<TextRange, ResultInformation> entry : originList) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		if (doUpdate)
			update();
	}
	
	
	
//	------------------------------ SORT METHODS ------------------------------------
	
	public void update() {
		sortMainStructure();
		updatePerPageStructure();
		fireContentsChanged(this, 0, getSize());
	}


	private void sortMainStructure() {
		Collections.sort(mainResults, indexComparator);
		listTextCreator.setLongestResultLength();
	}
	
	
//	------------------------------ ITERATORS ---------------------------------------
	
	/**
	 * @param startingPointExcluded if null iterator starts from start / end of list
	 */
	public Iterator<Result> getMainResultIterator(boolean ascending, Result startingPointExcluded) 
	{
		Iterator<Result> resultIterator = ascending ? mainResults.iterator() : mainResults.descendingIterator();
		if (startingPointExcluded == null)
			return resultIterator;
		else {
			while (resultIterator.hasNext()) {
				if (resultIterator.next() == startingPointExcluded)
					break;
			}
		}
		return resultIterator;
	}
	
	
	/**
	 * @param startingIndexExcluded if -1 iterator starts from start of list
	 */
	public ListIterator<Result> getPerPageResultIterator(int startingIndex) 
	{
		if (startingIndex == -1)
			return perPageResults.listIterator();
		else if (startingIndex >= 0 && startingIndex < perPageResults.size())
			return perPageResults.listIterator(startingIndex);
		else
			return null;
	}
	
	
	public String getSearchType() {
		return searchType;
	}


//	----------------------------------- TRASH ---------------------------------------
	
	/*
	private void updatePerPageStructure2() 
	{
		perPageResults = new LinkedList<Result>();
		//build intermediate tree sorted by text ranges
		TreeSet<ResultType> intermediateResult = new TreeSet<>();
		ResultType newEntry;
		for (Result result : mainResults) {
			for (TextRange tr : result.getAllTextRanges()) {
				newEntry = new ResultType(tr, result);
				intermediateResult.add(newEntry);
			}
		}
		//build result structure with seperators added
		Result seperatorResult;
		TextLocation startOffset, seperatorMark;
		int lastPageNr = Integer.MIN_VALUE, pageNr;
		for (ResultType resultType : intermediateResult) {
			startOffset = resultType.textRange.getStartOffset();
			pageNr = startOffset.pageNumber;
			if (pageNr != lastPageNr) {//add seperator -> result with pageNr set
				seperatorResult = new Result();
				seperatorMark = new TextLocation(startOffset.articleNumber, pageNr, startOffset.sectionNumber, 0);
				seperatorResult.addResult(new TextRange(seperatorMark, seperatorMark), null);
				perPageResults.add(seperatorResult);
			}
			perPageResults.add(resultType.result);
			lastPageNr = pageNr;
		}
	}
	
	
	private static class ResultType implements Comparable<ResultType> {
		
		private TextRange textRange;
		private Result result;
		
		private ResultType(TextRange tr, Result res) {
			textRange = tr;
			result = res;
		}

		@Override
		public int compareTo(ResultType o) {
			return this.textRange.compareTo(o.textRange);
		}
		
	}
	*/

}
