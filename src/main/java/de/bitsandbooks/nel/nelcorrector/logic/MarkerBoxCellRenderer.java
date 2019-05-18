package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelinterface2.Result;

public class MarkerBoxCellRenderer implements ListCellRenderer<Result> {
	
	private DefaultListCellRenderer defaultRenderer;
	private EntryTextCreatorIF textCreator;
	private Font font;
	
	public MarkerBoxCellRenderer(EntryTextCreatorIF textCreator) {
		this.textCreator = textCreator;
		this.defaultRenderer = new DefaultListCellRenderer();
		font = GeneralProperties.getInstance().getMainFont();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Result> list, 
			Result value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel label = (JLabel)defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		String txt = textCreator.getLexicographicText(value);
		//check for equal text
		if (index > 0) {
			ListModel<? extends Result> model = list.getModel();
			Result otherVal;
			int cnt = 1;
			for (int i = 0; i < index; i++) {
				otherVal = model.getElementAt(i);
				if (textCreator.getLexicographicText(otherVal).equals(txt))
					cnt++;
			}
			if (cnt > 1)
				txt = String.format("%s (%d)", txt, cnt);
		}
		//set text
		label.setText(txt);
		label.setFont(font);
		return label;
	}

}
