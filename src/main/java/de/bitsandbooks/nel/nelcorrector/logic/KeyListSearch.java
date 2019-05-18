package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelcorrector.util.MyStringUtils;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelinterface2.Result;

public class KeyListSearch extends KeyAdapter {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("ListSearchLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFileName = props.getProperty(GeneralProperties.Log4JFilePath);
		log4JFileName = FileSelector.getFileNameByRelativeOrAbsolutePath(log4JFileName);
		PropertyConfigurator.configure(log4JFileName);
		//logger.setLevel(Level.OFF);
	}
	
	
//	--------------------------- FIELDS ------------------------------
	
	private EntryTextCreatorIF entryTextCreator;
	private ResultTypeHandlerIF resultTypeHandler;
	private JList<Result> list;
	private ListModel<Result> model;
	private JRadioButton indexMode;
	private String key = "";
	private long time = 0;
	private final int delta = 1000;


//	----------------- CONSTRUCTOR & INSTANCE  ------------------------
	
	private static KeyListSearch instance;
	
	
	private KeyListSearch(JList<Result> indexList) {
		super();
		list = indexList;
		indexMode = (JRadioButton)ComponentMap.getInstance().get(ComponentMap.RadioButtonIndex);
	}
	
	
	public static KeyListSearch getInstance(JList<Result> indexList) {
		if (instance == null)
			instance = new KeyListSearch(indexList);
		return instance;
	}
	
	
//	------------------------ SETTER ----------------------------
	
	public void setTextCreatorAndModelAndResultTypeHandler(EntryTextCreatorIF textCreator, ResultTypeHandlerIF resultTypeHandler) {
		entryTextCreator = textCreator;
		model = list.getModel();
		this.resultTypeHandler = resultTypeHandler;
	}
	
	
//	------------------------- METHOD ---------------------------

	@Override
	public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		int mod = e.getModifiers();
		if (!Character.isLetterOrDigit(c))
			return;
		if (time+delta < System.currentTimeMillis())
			key = "";
		time = System.currentTimeMillis();
		key += Character.toLowerCase(c);
		int selectedIndex = list.getSelectedIndex();
		for (	int i = selectedIndex < model.getSize() - 1 ? selectedIndex + 1 : 0;
				i != selectedIndex; i = (i + 1) % model.getSize()) {
			Result result = model.getElementAt(i);
			if (indexMode.isSelected()) {//A: Index Mode
				String lexResultString = entryTextCreator.getLexicographicText(result);
				try {
					//normalize string
					lexResultString = MyStringUtils.normalizeString(lexResultString);
					//set selection
					if (lexResultString.toLowerCase().startsWith(key)) {
						if (mod != ActionEvent.ALT_MASK) {
							list.setSelectedIndex(i);
							RXListUtilities.ensureListIndexVisibility(list);
						}
						else {
							Point searchLocation = list.indexToLocation(i);
							list.scrollRectToVisible(new Rectangle(searchLocation));
						}
						break;
					}
				} catch (UnsupportedEncodingException e1) {
					logger.info(e1.getMessage());
				}
			} 
			else {//B: Per Page Mode
				try {
					int pageNr = Integer.parseInt(key.trim());
					if (	resultTypeHandler.isSeperator(result) 
							&& result.getAllTextRanges().get(0).getStartOffset().pageNumber == pageNr) {
						if (i + 1 < model.getSize()) {
							list.setSelectedIndex(i + 1);
							RXListUtilities.ensureListIndexVisibility(list);
							break;
						}
					}
				} catch (NumberFormatException e1) {					
				}
			}
		}
	}

}
