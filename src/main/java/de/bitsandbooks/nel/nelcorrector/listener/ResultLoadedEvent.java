package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventObject;

public class ResultLoadedEvent extends EventObject {

	public String searchName;
	public Class<?> resultClass;
	
	public ResultLoadedEvent(Object source, String searchName, Class<?> resultClass) {
		super(source);
		this.searchName = searchName;
		this.resultClass = resultClass;
	}

}
