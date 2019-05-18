package de.bitsandbooks.nel.nelcorrector.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.nelcorrector.util.FileSelector;

public class RiskValues extends Properties {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("PropertiesLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFile = props.getProperty(GeneralProperties.Log4JFilePath);
		log4JFile = FileSelector.getFileNameByRelativeOrAbsolutePath(log4JFile);
		PropertyConfigurator.configure(log4JFile);
		//logger.setLevel(Level.OFF);
	}
	
	
//	-------------------- VALUE NAMES --------------------------------------
	
	//name result names
	public static final String NameResultFactor = "nameresultfactor";
	public static final String FindspotResultFactor = "findspotresultfactor";
	public static final String UnusualNameOrderRisk = "unusualNameOrderRisk";
	public static final String SuffixRisk = "suffixRisk";
	public static final String HighChanceWordRisk = "highChanceWordRisk";
	public static final String LowChanceWordRisk = "lowChanceWordRisk";
	public static final String MatchedToNoPatternRisk = "matchedToNoPatternRisk";
	public static final String NameIsForenameRisk = "nameIsForenameRisk";
	public static final String SynonymRisk = "synonymRisk";
	public static final String DifferentArticlesRisk = "differentArticlesRisk";
	//name search text result risks
	public static final String SuffixReducedRisk = "suffixReducedRisk";
	public static final String NoForenameInformationRisk = "noForenameInformationRisk";
	public static final String TextEntrySynonymRisk = "textentrysynonymRisk";
	
	
	
//	------------------ CONSTRUCTOR & INSTANCE -----------------------------
	
	private static RiskValues instance;
	
	private RiskValues() 
	{
		super();
		//load risk values
		GeneralProperties props = GeneralProperties.getInstance();
		String riskValueFile = props.getProperty(GeneralProperties.RiskValueFile);
		riskValueFile = FileSelector.getFileNameByRelativeOrAbsolutePath(riskValueFile);
		try {
			load(new BufferedReader(new InputStreamReader(new FileInputStream(new File(riskValueFile)))));
		} catch (IOException | NullPointerException e) {
			String message = String.format("%nFile not found at: %s%n", riskValueFile);
			logger.warn(message);
		} catch (IllegalArgumentException e2) {
			String message = String.format("%nFile malformed at: %s%n", riskValueFile);
			logger.warn(message);
		}
	}
	
	public static RiskValues getInstance() {
		if (instance == null)
			instance = new RiskValues();
		return instance;
	}
	
	
//	------------------------ GET FLOAT VALUE ---------------------------------
	
	public float getRiskValue(String propertyName) 
	{
		String value = getProperty(propertyName).trim();
		value = !value.endsWith("f") ? value.concat("f") : value;
		try {
			float result = Float.parseFloat(value);
			if (result < 0.0f || result > 1.0f)
				throw new NumberFormatException();
			return result;
		}
		catch (NumberFormatException e) {
			logger.warn(e.getMessage());
			return 0.0f;
		}
	}
	
	
	public float getFloatValue(String propertyName) 
	{
		String value = getProperty(propertyName).trim();
		value = !value.endsWith("f") ? value.concat("f") : value;
		try {
			float result = Float.parseFloat(value);
			return result;
		}
		catch (NumberFormatException e) {
			logger.warn(e.getMessage());
			return 0.0f;
		}
	}

}
