package de.bitsandbooks.nel.nelcorrector.logic;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.exceptions.TextSelectionError;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultEntryDialogIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelcorrector.util.MyListUtils;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ModifyManager {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("ModifyLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFile = props.getProperty(GeneralProperties.Log4JFilePath);
		log4JFile = FileSelector.getFileNameByRelativeOrAbsolutePath(log4JFile);
		PropertyConfigurator.configure(log4JFile);
		//logger.setLevel(Level.OFF);
	}
	
	
//	----------------------------- FIELDS -----------------------------------
	
	private ResultListModel results;
	private Text text;
	private EntryTextCreatorIF textCreator;
	private ResultTypeHandlerIF resultTypeHandler;
	private ResultEntryDialogIF dialog;
	private JList<Result> indexList;
	private JList<Map.Entry<TextRange, ResultInformation>> findspotList;
	private JTextArea textArea;
	private JRadioButton indexMode;
	private JFrame mainFrame;
	
	
	
//	-------------------  CONSTRUCTOR & INSTANCE -----------------------------
	
	private static ModifyManager instance;
	
	private ModifyManager() {
		results = ResultListModel.getInstance();
		text = Text.getInstance();
		ComponentMap components = ComponentMap.getInstance();
		indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		indexMode = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
	}
	
	public static ModifyManager getInstance() {
		if (instance == null)
			instance = new ModifyManager();
		return instance;
	}
	
	
//	------------------------- SETTER --------------------------------------
	
	public void setEntryTextCreatorAndDialogResultTypeHandler(EntryTextCreatorIF textCreator, ResultEntryDialogIF dialog, ResultTypeHandlerIF resultHandler) {
		this.textCreator = textCreator;
		this.dialog = dialog;
		this.resultTypeHandler = resultHandler;
	}
	
	
//	------------------------- METHODS -------------------------------------
	
	public void delete(boolean indexListResult) 
	{
		TextRange toSelect = null;
		Integer indexToSelect = null;
		if (indexListResult) {
			Result toDelete = indexList.getSelectedValue();
			if (toDelete != null) {
				if (indexMode.isSelected()) {
					results.delete(toDelete);
				}
				else { 
					List<TextRange> allTextRanges = toDelete.getAllTextRanges();
					results.delete(toDelete, allTextRanges);
					checkEmptyResults(toDelete);
				}
				indexToSelect = indexList.getSelectedIndex();
			}		
		}
		else {
			Result toDeleteResult = indexList.getSelectedValue();
			List<Map.Entry<TextRange, ResultInformation>> toDeleteFindspots = findspotList.getSelectedValuesList();
			//get result entry for reselect
			int[] selectedIxs = findspotList.getSelectedIndices();
			if (selectedIxs.length != 0) {
				ListModel<Entry<TextRange, ResultInformation>> model = findspotList.getModel();
				if (selectedIxs.length < model.getSize()) {//case: not all elements deleted
					boolean found = false;
					for (int i = selectedIxs[0] + 1; i < model.getSize(); i++) {//look for next not deleted element forward
						if (!ArrayUtils.contains(selectedIxs, i)) {
							toSelect = model.getElementAt(i).getKey();
							found = true;
							break;
						}
					}
					if (!found && selectedIxs[0] > 0) //take previous element
						toSelect = model.getElementAt(selectedIxs[0] - 1).getKey();
				}
			}
			//do delete
			List<TextRange> toDeleteTRs = MyListUtils.extractTextRanges(toDeleteFindspots);
			if (toDeleteFindspots != null && toDeleteFindspots.size() > 0)
				results.delete(toDeleteResult, toDeleteTRs);
			checkEmptyResults(toDeleteResult);	
		}
		update(indexToSelect, toSelect);
		//set focus
		if (indexMode.isSelected()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (findspotList.getModel() != null && findspotList.getModel().getSize() > 0)
						findspotList.requestFocusInWindow();
				}
			});
		}
	}
	
	
	public void create() 
	{
		if (results.getSize() == 0)
			return;
		//check for selected text parts
		TextRange tr;
		try {
			tr = getSelectedTextPart(null);
		} catch (TextSelectionError e) {
			JOptionPane.showMessageDialog(mainFrame, "Select single text part for editing entry.", "Invalid action", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		//do create
		Integer indexOfNewResult = null;
		TextRange newFindspot = null;
		if (tr == null) {//create empty result
			
			Result newEmptyResult = dialog.showDialog("Create result", null);//DIALOG
			
			if (newEmptyResult != null)
				results.insert(newEmptyResult);
			indexOfNewResult = results.getIndexByResult(newEmptyResult);
		}
		else {//create new findspot
			if (results.getSize() == 0)
				return;
			Result oldResult = indexList.getSelectedValue();
			int index = results.getMainResultIndexByName(oldResult);
			Result oldMainResult = results.getElementAtMainResultList(index);
			if (oldMainResult.getAllTextRanges().contains(tr)) // case: no new findspot has been selected -> return
				return;
			try {
				ResultInformation info = resultTypeHandler.getEmptyResultInformation();
				oldMainResult.addResult(tr, info);
				results.update();
				newFindspot = tr;
			} catch (InstantiationException | IllegalAccessException e) {
				logger.warn(e.getMessage());
			}
		}
		update(indexOfNewResult, newFindspot);
	}
	

	public void edit(Boolean indexListSelected) 
	{
		if (results.getSize() == 0)
			return;
		//check for selected text parts
		TextRange tr;
		try {
			tr = getSelectedTextPart(indexListSelected);
		} catch (TextSelectionError e) {
			JOptionPane.showMessageDialog(mainFrame, "Select single text part for editing entry.", "Invalid action", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		//do edit
		Integer indexOfNewResult = null;
		TextRange newFindspot = null;
		if (tr == null || (indexListSelected != null && indexListSelected)) {//no text selected -> edit result
			Result oldResult = indexList.getSelectedValue();
			if (findspotList.getSelectedValue() != null)
				newFindspot = findspotList.getSelectedValue().getKey();
			int index = results.getMainResultIndexByName(oldResult);
			Result oldMainResult = results.getElementAtMainResultList(index);
			
			Result newResult = dialog.showDialog("Edit result", oldMainResult);//DIALOG
			
			if (newResult != null) {
				results.delete(oldMainResult);
				results.insert(newResult);
			}
//			indexOfNewResult = results.getIndexByName(newResult);
		}
		else {//text selected -> overwrite findspot of existing result
			//select old text range depending on selection mode
			TextRange oldRange;
			if (indexMode.isSelected()) {
				List<Map.Entry<TextRange, ResultInformation>> spotList = findspotList.getSelectedValuesList();
				if (spotList.size() != 1) {
					JOptionPane.showMessageDialog(mainFrame, "Select single findspot for editing entry.", "Invalid action", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				oldRange = spotList.get(0).getKey();
			}
			else {
				Result result = indexList.getSelectedValue();
				Map<TextRange, ResultInformation> resultMap = result.getResultMap();
				Entry<TextRange, ResultInformation> mapEntry = resultMap.entrySet().iterator().next();
				oldRange = mapEntry.getKey();
			}
			//overwrite old text range (if new range has been selected)
			TextLocation oldStart = oldRange.getStartOffset();
			TextLocation oldEnd = oldRange.getEndOffset();
			TextLocation newStart = tr.getStartOffset();
			TextLocation newEnd = tr.getEndOffset();
			if (oldStart.equals(newStart) && oldEnd.equals(newEnd)) //case: no new findspot has been selected -> return
				return;
			oldStart.articleNumber = newStart.articleNumber;
			oldStart.pageNumber = newStart.pageNumber;
			oldStart.sectionNumber = newStart.sectionNumber;
			oldStart.signNumber = newStart.signNumber;
			oldEnd.articleNumber = newEnd.articleNumber;
			oldEnd.pageNumber = newEnd.pageNumber;
			oldEnd.sectionNumber = newEnd.sectionNumber;
			oldEnd.signNumber = newEnd.signNumber;
			//remind findspot
			newFindspot = oldRange;
			//update results
			results.update();
		}
		update(indexOfNewResult, newFindspot);
	}

	
	
//	---------------------------------- AUX ---------------------------------------------
	
	public void checkEmptyResults(Result result) 
	{
		 int index = results.getMainResultIndexByName(result);
		 Result toCheck = results.getElementAtMainResultList(index);
		 if (toCheck == null)
			return;
		if (toCheck.getResultMap().size() != 0)
			return;
		String fullResultText = textCreator.getFullResultText(toCheck);
		String msg = String.format("The result '%s' is empty. Delete this result?", fullResultText);
		int val = JOptionPane.showConfirmDialog(mainFrame, msg, "Delete result", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (val == JOptionPane.OK_OPTION) {
			results.delete(toCheck);
			checkEmptyResults(result);
		}
	}
	
	
	public String getSelectedTextString() throws TextSelectionError 
	{
		Highlighter highlighter = textArea.getHighlighter();
		Highlight[] hls = highlighter.getHighlights();
		if (hls == null || hls.length == 0)
			return null;
		Highlight hl = hls[0];
		if (hls.length > 1 || hl.getStartOffset() == hl.getEndOffset()) {
			throw new TextSelectionError();
		}
		else
			return textArea.getSelectedText();
	}
	
	
	public TextRange getSelectedTextPart(Boolean indexListSelected) throws TextSelectionError 
	{
		Highlighter highlighter = textArea.getHighlighter();
		Highlight[] hls = highlighter.getHighlights();
		if (hls == null || hls.length == 0)
			return null;
		Highlight hl = hls[0];
		if (hls.length > 1 || hl.getStartOffset() == hl.getEndOffset()) {
			if (indexListSelected != null && indexListSelected == true)
				return null;
			else
				throw new TextSelectionError();
		}
			
		Page page = text.getSelectedPage();
		int articleNr = page.getArticleNumber();
		int pageNr = page.getPageNumber();
		int sectionNr = page.getPageSectionNumber();
		int firstSignNr = page.getFirstSignNumber();
		TextLocation start = new TextLocation(articleNr, pageNr, sectionNr, firstSignNr + hl.getStartOffset());
		TextLocation end = new TextLocation(articleNr, pageNr, sectionNr, firstSignNr + hl.getEndOffset());
		TextRange result = new TextRange(start, end);
		return result;
		
	}

	
	/**
	 * @param indexListIndex to set index to visible if new result has been created
	 */
	public void update(Integer indexListIndex, TextRange newFindspot) 
	{
		indexList.revalidate();
		indexList.repaint();
		findspotList.revalidate();
		findspotList.repaint();
		//set indexes to visible if new result has been created
		if (indexListIndex != null && indexList.getModel().getSize() >= 0 && indexListIndex < indexList.getModel().getSize()) {
			indexList.setSelectedIndex(indexListIndex);
			int findspotListIx = findspotList.getSelectedIndex();
			if (findspotListIx == -1 && findspotList.getModel().getSize() > 0)
				findspotList.setSelectedIndex(0);
		}
		if (newFindspot != null) {
			if (indexMode.isSelected()) {
				ListModel<Entry<TextRange, ResultInformation>> model = findspotList.getModel();
				Entry<TextRange, ResultInformation> el;
				for (int i = 0; i < model.getSize(); i++) {
					el = model.getElementAt(i);
					if (el.getKey().equals(newFindspot)) {
						findspotList.setSelectedIndex(i);
						break;
					}	
				}
			}
			else {
				int index = results.getIndexByTextRange(newFindspot);
				if (index >= 0)
					indexList.setSelectedIndex(index);
			}			
		}
		RXListUtilities.ensureListIndexVisibility(indexList);
		RXListUtilities.ensureListIndexVisibility(findspotList);
	}

}
