package de.bitsandbooks.nel.nelcorrector.resulttype;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.Risk;

public interface RiskCalculatorIF {
	
	public void setFactorValues(Iterator<Result> iterator);
	
	public float getFindspotRiskValue(Risk risk);
	
	public float getResultRiskValue(Risk risk);
	
	public Color convertRiskValueToColor(float value, boolean resultTypeMainResult);

}
