package de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelinterface2.NameEntry;
import de.bitsandbooks.nel.nelinterface2.NameFindspotRisk;
import de.bitsandbooks.nel.nelinterface2.NameResult;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.ResultTreeEntry;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ResultTypeHandlerNameResult implements ResultTypeHandlerIF {
	
//	------------------------- FIELDS --------------------------
	
	
//	----------------- CONSTRUCTOR & INSTANCE ------------------
	
	private static ResultTypeHandlerNameResult instance;
	
	private ResultTypeHandlerNameResult() {
	}
	
	public static ResultTypeHandlerNameResult getInstance() {
		if (instance == null)
			instance = new ResultTypeHandlerNameResult();
		return instance;
	}
	
	
//	------------------- METHODS -------------------------------

	@Override
	public boolean isSeperator(Result result) 
	{
		return 	!(result instanceof NameResult) 
				&& result.getResultMap().size() == 1 
				&& result.getAllTextRanges().get(0).getStartOffset().equals(result.getAllTextRanges().get(0).getEndOffset());
	}
	
	
	@Override
	public Collection<? extends Result> splitResultToSingleEntryResults(Result oldResult) 
	{
		List<Result> resultList = new Vector<Result>();
		NameResult nameRes = (NameResult)oldResult;
		NameEntry name = nameRes.getName();
		Risk risk = nameRes.getRisk();
		NameResult newResult;
		for (Map.Entry<TextRange, ResultInformation> textEntryResult : oldResult.getResultMap().entrySet()) {
			newResult = new NameResult();
			newResult.setName(name);
			newResult.setRisk(risk);
			newResult.addResult(textEntryResult.getKey(), textEntryResult.getValue());
			resultList.add(newResult);
		}
		return resultList;
	}

	
	@Override
	public boolean resultContentEquals(Result otherResult, Result oldResult) 
	{
		NameResult nameRes1 = (NameResult)otherResult;
		NameResult nameRes2 = (NameResult)oldResult;
		return nameRes1.getName() == nameRes2.getName();
	}

	
	@Override
	public TreeSet<ResultTreeEntry> createFinalResultTree(Iterator<Result> mainResultIterator) 
	{
		TreeSet<ResultTreeEntry> result = new TreeSet<ResultTreeEntry>();
		ResultTreeEntry newEntry, exisitingEntry;
		Result el;
		NameResult nameRes;
		String lexicographicName;
		while (mainResultIterator.hasNext()) {
			el = mainResultIterator.next();
			nameRes = (NameResult)el;
			lexicographicName = nameRes.getName().getLexicographicSurname();
			newEntry = new ResultTreeEntry(lexicographicName);
			if (result.contains(newEntry)) {//add to exisiting entry
				exisitingEntry = result.floor(newEntry);
				exisitingEntry.addResultEntry(nameRes);
			}
			else {//add new entry
				newEntry.addResultEntry(nameRes);
				result.add(newEntry);
			}
		}
		return result;
	}

	
	@Override
	public ResultInformation getEmptyResultInformation() throws InstantiationException, IllegalAccessException 
	{
		ResultInformation info = new ResultInformation(new Vector<String>(), null, NameFindspotRisk.class);
		return info;
	}
	
	
	@Override
	public Result createResult(String lexicoResultString) 
	{
		NameResult res = new NameResult();
		NameEntry name = new NameEntry(lexicoResultString, lexicoResultString, "", "", "", lexicoResultString, "", "");
		res.setName(name);
		return res;
	}

	
	@Override
	public Class<? extends Risk> getFindspotRiskClass() {
		Class<? extends Risk> result = NameFindspotRisk.class;
		return result;
	}

}
