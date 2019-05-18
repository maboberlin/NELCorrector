package de.bitsandbooks.nel.nelcorrector.resulttype;

import java.awt.Frame;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype.EntryTextCreatorNameResult;
import de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype.IndexComparatorNameResult;
import de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype.ResultEntryDialogNameResult;
import de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype.ResultTypeHandlerNameResult;
import de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype.RiskCalculatorNameResult;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelinterface2.NameResult;
import de.bitsandbooks.nel.nelinterface2.Result;

public class SingletonFactory {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("PropertiesLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFileName = props.getProperty(GeneralProperties.Log4JFilePath);
		log4JFileName = FileSelector.getFileNameByRelativeOrAbsolutePath(log4JFileName);
		PropertyConfigurator.configure(log4JFileName);
		//logger.setLevel(Level.OFF);
	}
	
	
//	--------------- CLASS NAME FIELDS ----------------------------
	
	//search type
	private static final String ResultTypeName = "name";
	
	//result class
	private static final Class<? extends Result> ResultClassName = NameResult.class;
	
	
//	----------------CLASSES SINGLETONS ---------------------------
	
	private EntryTextCreatorIF listTextCreator;
	private RiskCalculatorIF riskCalculator;
	private IndexComparatorIF indexComparator;
	private ResultTypeHandlerIF resultTypeHandler;
	private ResultEntryDialogIF resultEntryDialog;
	
	
//	-------------- CONSTRUCTOR & INSTANCE ------------------------
	
	private static SingletonFactory instance;
	
	private SingletonFactory() {
	}
	
	public static SingletonFactory getInstance() {
		if (instance == null)
			instance = new SingletonFactory();
		return instance;
	}
	
	
//	----------------------- GETTER -------------------------------
	
	public EntryTextCreatorIF getListTextCreator() {
		return listTextCreator;
	}
	
	public RiskCalculatorIF getRiskCalculator() {
		return riskCalculator;
	}
	
	public IndexComparatorIF getIndexComparator() {
		return indexComparator;
	}
	
	public ResultTypeHandlerIF getResultTypeHandler() {
		return resultTypeHandler;
	}
	
	public ResultEntryDialogIF getResultEntryDialogIF() {
		return resultEntryDialog;
	}
	
	
	
//	------------------- INITIALIZATION -------------------------
	
	//MAIN INIT
	public void initializeSingletons(String searchName, Class<? extends Result> resultClass, String language, Frame owner) {
		boolean done = initializeByClass(resultClass, language, owner);
		if (!done)
			done = initializeByName(searchName, language, owner);
		if (!done)
			logger.warn(String.format("Could not load result classes by name '%s' and class '%s'", searchName, resultClass.toString()));
			
	}

	//BY CLASS
	private boolean initializeByClass(Class<? extends Result> resultClass, String language, Frame owner) {
		if (resultClass.equals(ResultClassName)) { 
			initializeNameClasses(language, owner);
			return true;
		}
		else {
			return false;
		}
	}
	
	//BY NAME
	private boolean initializeByName(String searchName, String language, Frame owner) {
		if (searchName.equals(ResultTypeName)) { 
			initializeNameClasses(language, owner);
			return true;
		}
		else {
			return false;
		}	
	}

	//NAME RESULT INITIALIZATION
	private void initializeNameClasses(String language, Frame owner) {
		listTextCreator = EntryTextCreatorNameResult.getInstance();
		riskCalculator = RiskCalculatorNameResult.getInstance();
		indexComparator = IndexComparatorNameResult.getInstance(language);
		resultEntryDialog = ResultEntryDialogNameResult.getInstance(owner);
		resultTypeHandler = ResultTypeHandlerNameResult.getInstance();
		//connect classes
		listTextCreator.setResultTypeHandler(resultTypeHandler);
	}

}
