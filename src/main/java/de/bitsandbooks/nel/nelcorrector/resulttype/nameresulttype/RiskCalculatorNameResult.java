package de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.bitsandbooks.nel.nelcorrector.properties.RiskValues;
import de.bitsandbooks.nel.nelcorrector.resulttype.RiskCalculatorIF;
import de.bitsandbooks.nel.nelinterface2.NameFindspotRisk;
import de.bitsandbooks.nel.nelinterface2.NameRisk;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class RiskCalculatorNameResult implements RiskCalculatorIF {
	
//	------------------------- FIELDS --------------------------
	
	private RiskValues riskValues;
	
	private static final float NumberOfNameResultVariables = 8.0f;
	private static final float NumberOfFindSpotVariables = 5.0f;
	//name result values
	private float nameResultFactor;
	private float unusualNameOrderRisk;
	private float suffixRisk;
	private float highChanceWordRisk;
	private float lowChanceWordRisk;
	private float matchedToNoPatternRisk;
	private float nameIsForenameRisk;
	private float synonymRisk;
	private float differentArticlesRisk;
	//findspot values
	private float findspotResultFactor;
	private float suffixReducedRisk;
	private float noForenameInformationRisk;
	private float textentrysynonymRisk;

	
	
//	----------------- CONSTRUCTOR & INSTANCE ------------------
	
	private static RiskCalculatorNameResult instance;
	
	private RiskCalculatorNameResult() {
		riskValues = RiskValues.getInstance();
		//initialize name result values
		unusualNameOrderRisk = riskValues.getRiskValue(RiskValues.UnusualNameOrderRisk);
		suffixRisk = riskValues.getRiskValue(RiskValues.SuffixRisk);
		highChanceWordRisk = riskValues.getRiskValue(RiskValues.HighChanceWordRisk);
		lowChanceWordRisk = riskValues.getRiskValue(RiskValues.LowChanceWordRisk);
		matchedToNoPatternRisk = riskValues.getRiskValue(RiskValues.MatchedToNoPatternRisk);
		nameIsForenameRisk = riskValues.getRiskValue(RiskValues.NameIsForenameRisk);
		synonymRisk = riskValues.getRiskValue(RiskValues.SynonymRisk);
		differentArticlesRisk = riskValues.getRiskValue(RiskValues.DifferentArticlesRisk);
		//init findspot value
		suffixReducedRisk = riskValues.getRiskValue(RiskValues.SuffixReducedRisk);
		noForenameInformationRisk = riskValues.getRiskValue(RiskValues.NoForenameInformationRisk);
		textentrysynonymRisk = riskValues.getRiskValue(RiskValues.TextEntrySynonymRisk);
	}
	

	public static RiskCalculatorNameResult getInstance() {
		if (instance == null)
			instance = new RiskCalculatorNameResult();
		return instance;
	}
	
	
//	--------------------- METHODS ----------------------------

	@Override
	public void setFactorValues(Iterator<Result> iterator) 
	{
		//get max risk values
		Result el;
		Risk resultRisk, findspotRisk;
		float resultRiskValue, resultRiskValueMax = 0.0f, findspotRiskValue, findspotRiskValueMax = 0.0f;
		while (iterator.hasNext()) {
			//result risk
			el = iterator.next();
			resultRisk = el.getRisk();
			resultRiskValue = getResultRiskValue(resultRisk);
			resultRiskValueMax = resultRiskValue > resultRiskValueMax ? resultRiskValue : resultRiskValueMax;
			//findspot risk
			for (Map.Entry<TextRange, ResultInformation> entry : el.getResultMap().entrySet()) {
				findspotRisk = entry.getValue().getRisk();
				findspotRiskValue = getFindspotRiskValue(findspotRisk);
				findspotRiskValueMax = findspotRiskValue > findspotRiskValueMax ? findspotRiskValue : findspotRiskValueMax;
			}
		}
		//set factors
		nameResultFactor = 1.0f / resultRiskValueMax;
		findspotResultFactor = (1.0f / findspotRiskValueMax) * 0.75f;
	}
	
	
	@Override
	public float getResultRiskValue(Risk risk) 
	{
		if (risk == null || !(risk instanceof NameRisk))
			return 0.0f;
		NameRisk nameRisk = (NameRisk)risk;
		float val1 = toFloat(nameRisk.unusualNameOrderRisk) * unusualNameOrderRisk;
		float val2 = toFloat(nameRisk.suffixRisk) * suffixRisk;
		float val3 = toFloat(nameRisk.highChanceWordRisk) * highChanceWordRisk;
		float val4 = toFloat(nameRisk.lowChanceWordRisk) * lowChanceWordRisk;
		float val5 = toFloat(nameRisk.matchedToNoPatternRisk) * matchedToNoPatternRisk;
		float val6 = toFloat(nameRisk.nameIsForenameRisk) * nameIsForenameRisk;
		float val7 = toFloat(nameRisk.synonymRisk) * synonymRisk;
		float val8 = toFloat(nameRisk.differentArticlesRisk) * differentArticlesRisk;
		float result = (val8 + val7 + val6 + val5 + val4 + val3 + val2 + val1) / NumberOfNameResultVariables;
		return result;
	}
	
	
	@Override
	public float getFindspotRiskValue(Risk risk) 
	{
		NameFindspotRisk findspotRisk = (NameFindspotRisk)risk;
		float val1 = toFloat(findspotRisk.suffixReducedRisk) * suffixReducedRisk;
		float val2 = toFloat(findspotRisk.noForenameInformationRisk) * noForenameInformationRisk;
		float val3 = toFloat(findspotRisk.synonymRisk) * textentrysynonymRisk;
		float result = (val3 + val2 + val1) / NumberOfFindSpotVariables;
		return result;
	}
	
	
	@Override
	public Color convertRiskValueToColor(float value, boolean mainResult) 
	{
		float resultFactor = mainResult ? nameResultFactor : findspotResultFactor;
		value *= resultFactor;
		value = value > 1.0f ? 1.0f : value;
		value = 1.0f - value;
		Color result = new Color(1.0f, value, value);
		return result;
	}
	
	
//	--------------------- AUX -------------------------------
	
	private float toFloat(boolean val) {
		return val ? 1.0f : 0.0f;
	}


}
