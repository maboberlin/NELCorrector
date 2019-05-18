package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import sun.swing.DefaultLookup;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.RiskCalculatorIF;
import de.bitsandbooks.nel.nelcorrector.util.ColorIcon;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.Risk;

public class IndexListCellRenderer extends DefaultListCellRenderer {
	
//	-------------------------- FIELDS ---------------------------------
	
	private EntryTextCreatorIF textCreator;
	private RiskCalculatorIF riskCalculator;
	private JRadioButton rbIndexMode;
	private Font font;
	
	private Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private Border lineBorder;
	
	
//	------------------------ CONSTRUCTOR ------------------------------
	
	public IndexListCellRenderer(EntryTextCreatorIF textCreator, RiskCalculatorIF riskCalculator) {
		super();
		this.textCreator = textCreator;
		this.riskCalculator = riskCalculator;
		ComponentMap components = ComponentMap.getInstance();
		rbIndexMode = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		lineBorder = (Border)UIManager.get("List.List.focusSelectedCellHighlightBorder");
		lineBorder = lineBorder == null ? (Border)UIManager.get("List.focusCellHighlightBorder") : lineBorder;
	}


//	-------------------------- METHOD ---------------------------------
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel label = new JLabel();
		Result result = (Result)value;
		boolean indexMode = rbIndexMode.isSelected();
		//set text	
		String text = textCreator.getResultText(result, indexMode, false, null, null);
		label.setText(text);
		label.setFont(font);
		//set icon
		Risk risk = result.getRisk();
		float riskValue = riskCalculator.getResultRiskValue(risk);
		Color riskColor = riskCalculator.convertRiskValueToColor(riskValue, true);
		ColorIcon icon = new ColorIcon(riskColor, label.getPreferredSize());
		label.setIcon(icon);
		//paint drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (	dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) 
        {
            isSelected = true;
        }
		//paint selected	
        if (isSelected && cellHasFocus) {
        	label.setBackground(list.getSelectionBackground());
        	label.setForeground(list.getSelectionForeground());
        }
        else if (isSelected) {
        	label.setBackground(Color.lightGray);
        	label.setForeground(list.getForeground());
        } else {
        	label.setBackground(list.getBackground());
        	label.setForeground(list.getForeground());
        }
        label.setBorder(cellHasFocus ? lineBorder : emptyBorder);
        label.setEnabled(list.isEnabled());
        label.setOpaque(true);
        //return label
		return label;
	}

}
