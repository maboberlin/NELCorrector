package de.bitsandbooks.nel.nelcorrector.resulttype;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.ResultTreeEntry;
import de.bitsandbooks.nel.nelinterface2.Risk;

public interface ResultTypeHandlerIF {
	
	public boolean isSeperator(Result result);

	public Collection<? extends Result> splitResultToSingleEntryResults(Result oldResult);

	public boolean resultContentEquals(Result otherResult, Result oldResult);

	public TreeSet<ResultTreeEntry> createFinalResultTree(Iterator<Result> mainResultIterator);

	public ResultInformation getEmptyResultInformation() throws InstantiationException, IllegalAccessException;

	public Result createResult(String lexicoResultString);
	
	public Class<? extends Risk> getFindspotRiskClass();
	
}
