package de.bitsandbooks.nel.nelcorrector.util;

import java.util.TreeSet;

import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultTreeEntry;

public class ResultDataHelper {
	
	public static Result getFirstResult(TreeSet<ResultTreeEntry> data) {
		for (ResultTreeEntry resultTreeEntry : data) {
			for (Result result : resultTreeEntry.getResultList()) {
				return result;
			}
		}
		return null;
	}

}
