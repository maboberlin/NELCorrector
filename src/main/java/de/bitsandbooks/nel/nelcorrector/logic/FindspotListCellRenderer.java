package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatEvent;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.RiskCalculatorIF;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class FindspotListCellRenderer extends DefaultListCellRenderer implements BibliographyFormatListener {
		
//	--------------------------- FIELDS ----------------------------------
	
	private RiskCalculatorIF riskCalculator;
	private Text text;
	private Font font;
	private boolean cancelBibliographies = false;
	
	private Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private Border lineBorder;
	
	
//	-------------------------- CONSTRUCTOR -----------------------------
		
	public FindspotListCellRenderer(RiskCalculatorIF riskCalculator) {
		GeneralProperties props = GeneralProperties.getInstance();
		text = Text.getInstance();
		font = props.getMainFont();
		this.riskCalculator = riskCalculator;
		lineBorder = (Border)UIManager.get("List.List.focusSelectedCellHighlightBorder");
		lineBorder = lineBorder == null ? (Border)UIManager.get("List.focusCellHighlightBorder") : lineBorder;
	}
	
	
//	-------------------------- METHOD ---------------------------------

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		//set text	
		setFont(font);
		Map.Entry<TextRange, ResultInformation> result = (Map.Entry<TextRange, ResultInformation>)value;
		TextRange resultTR = result.getKey();
		int pageNr = resultTR.getStartOffset().pageNumber;
		String text = pageNr >= 0 ? String.valueOf(pageNr) : RomanArabic.getRoman(Math.abs(pageNr));
		text = cancelBibliographies && this.text.isBibliographyResult(resultTR) ? String.format("<html><strike>%s<strike><html>", text) : text;
		setText(text);
		//set Background Color (Text) & Border
		if (result.getValue() != null) {
			Risk risk = result.getValue().getRisk();
			float riskValue = riskCalculator.getFindspotRiskValue(risk);
			Color riskColor = riskCalculator.convertRiskValueToColor(riskValue, false);
			setBackground(riskColor);
		}
		//set Border
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		borderPanel.add(this);
		borderPanel.setBackground(list.getBackground());
		//paint selected
		if (isSelected && cellHasFocus) {
        	borderPanel.setBackground(list.getSelectionBackground());
        	borderPanel.setForeground(list.getSelectionForeground());
		}
		else if (isSelected) {
			borderPanel.setBackground(Color.lightGray);
			borderPanel.setForeground(list.getForeground());
        } else {
        	borderPanel.setBackground(list.getBackground());
        	borderPanel.setForeground(list.getForeground());
        } 
        borderPanel.setEnabled(true);
        borderPanel.setOpaque(true);
        borderPanel.setBorder(cellHasFocus ? lineBorder : emptyBorder);
        //return panel
		return borderPanel;
	}


	@Override
	public void formatIsSet(BibliographyFormatEvent e) {
		cancelBibliographies = e.cancelBibliographys;
	}

}
