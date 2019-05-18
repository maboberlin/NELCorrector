package de.bitsandbooks.nel.nelcorrector.logic;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;

public class PageSelectorHandler implements PageChangedListener {
	
//	------------------------- FIELDS ---------------------------------
	
	private Text text;
	private GeneralProperties properties;
	private JFrame mainFrame;
	private JTextField pageNrField;
	private JRadioButton textNrButton;
	private JButton backward, forward;
	private JLabel nrOfPages;
	private String nrOfPagesText;
	
	
//	----------------- CONSTRUCTOR & INSTANCE -------------------------
	
	private static PageSelectorHandler instance;
	
	private PageSelectorHandler() 
	{
		ComponentMap components = ComponentMap.getInstance();
		text = Text.getInstance();
		properties = GeneralProperties.getInstance();
		pageNrField = (JTextField)components.get(ComponentMap.TextFieldPageNr);
		textNrButton = (JRadioButton)components.get(ComponentMap.RadioButtonTextNr);
		backward = (JButton)components.get(ComponentMap.ButtonPageBackward);
		forward = (JButton)components.get(ComponentMap.ButtonPageForeward);
		nrOfPages = (JLabel)components.get(ComponentMap.LabelNumberOfPages);
		mainFrame = (JFrame)SwingUtilities.getRoot(pageNrField);
	}
	
	
	public static PageSelectorHandler getInstance() 
	{
		if (instance == null)
			instance = new PageSelectorHandler();
		return instance;
	}
	
	
//	------------------------ METHOD ----------------------------------

	@Override
	public void pageChanged(PageChangedEvent e) 
	{
		setPageNumberTextField();
		//en/disable button
		if (text.isFirstPageSelected()) {
			backward.setEnabled(false);
			forward.setEnabled(true);
			forward.requestFocus();
		}
		else if (text.isLastPageSelected()) {
			forward.setEnabled(false);
			backward.setEnabled(true);
			backward.requestFocus();
		}
		else {
			backward.setEnabled(true);
			forward.setEnabled(true);
		}
	}

	
	public void switchSelectionMode() 
	{
		setPageNumberTextField();
	}
	
	
	public void selectPage()
	{
		//get page number
		int nr;
		String tfText = pageNrField.getText();
		if (RomanArabic.isRoman(tfText)) {
			int romNr = RomanArabic.getArabic(tfText);
			nr = Math.negateExact(romNr);
		}
		else
			nr = Integer.parseInt(tfText);
		//select page
		if (textNrButton.isSelected()) 
			text.setSelectedPageByTextNumber(nr);
		else
			text.setSelectedPageByDocumentNumber(nr);
	}
	
	
//	------------------------------ AUX ----------------------------------
	
	private void setPageNumberTextField() 
	{
		if (text.getDocPageMaximum() != 0) {//if text has been loaded already
			//set text field
			int pageNr;
			String textToPrint = "";
			if (textNrButton.isSelected()) {
				pageNr = text.getSelectedPageNumber(true);
				if (pageNr < 0) 
					textToPrint = RomanArabic.getRoman(Math.abs(pageNr));
				else
					textToPrint = String.valueOf(pageNr);
			}
			else {
				pageNr = text.getSelectedPageNumber(false);
				textToPrint = String.valueOf(pageNr);
			}
			pageNrField.setForeground(properties.getDefaultTFForegroundColor());
			pageNrField.setText(textToPrint);
			//set number of pages label
			int maxNumber = textNrButton.isSelected() ? text.getTextPageMaximum() : text.getDocPageMaximum();
			String maxNumberText = String.valueOf(maxNumber);
			if (!maxNumberText.equals(nrOfPagesText)) {
				nrOfPages.setText(String.format("/ %s", maxNumberText));
				nrOfPages.setPreferredSize(null);
			}
			//set focus on forward button
			forward.requestFocus();
		}
	}

}
