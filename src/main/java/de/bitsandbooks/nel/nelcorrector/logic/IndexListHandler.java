package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelcorrector.util.TextEntryComparator;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class IndexListHandler extends MouseAdapter implements ListSelectionListener, ListDataListener {

//	--------------------------- FIELDS --------------------------------
	
	private ResultListModel resultList;
	private EntryTextCreatorIF entryTextCreator;
	private ResultTypeHandlerIF resultTypeHandler;
	private TextEntryComparator textEntryComparator;
	private TextSelector textSelector;
	private JList<Result> indexList;
	private JList<Map.Entry<TextRange, ResultInformation>> findspotList;
	private JTextField entryTextField;
	private JList<String> riskList1;
	private JList<String> riskList2;
	private JRadioButton indexModeRB;
	
	private Result lastSelectedResult = null;
	private int lastSelectedIndexItem = Integer.MIN_VALUE;
	private boolean selectEntryFlag;
	
	
//	------------------- CONSTRUCTOR & INSTANCE -------------------------
	
	private static IndexListHandler instance;
	
	private IndexListHandler() {
		ComponentMap components = ComponentMap.getInstance();
		indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		entryTextField = (JTextField)components.get(ComponentMap.TextFieldResultField);
		riskList1 = (JList<String>)components.get(ComponentMap.ListIndexRiskList);
		riskList2 = (JList<String>)components.get(ComponentMap.ListFindspotRiskList);
		indexModeRB = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		resultList = ResultListModel.getInstance();
		textEntryComparator = new TextEntryComparator();
		textSelector = TextSelector.getInstance();
		selectEntryFlag = true;
	}
	
	public static IndexListHandler getInstance() {
		if (instance == null)
			instance = new IndexListHandler();
		return instance;
	}
	
	
//	------------------------ SETTER -----------------------------------
	
	public void setResultTypeSingletons(EntryTextCreatorIF textCreator, ResultTypeHandlerIF resultHandler) {
		entryTextCreator = textCreator;
		resultTypeHandler = resultHandler;
	}
	
	public void setSelectEntryFlag(boolean val) {
		selectEntryFlag = val;
	}
	
	
//	------------------------ METHODS ----------------------------------
	
	@Override
	public void mouseClicked(MouseEvent evt) {
		int selIndex = indexList.getSelectedIndex();
		if (evt.getClickCount() == 1 && selIndex == lastSelectedIndexItem) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Entry<TextRange, ResultInformation> entry = findspotList.getSelectedValue();
					if (entry != null) {
						TextRange tr = entry.getKey();
						textSelector.markTextFromIndexSelection(tr);
						indexList.requestFocusInWindow();
					}
				}
			});
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateList();
	}
	
	@Override
	public void intervalAdded(ListDataEvent e) {
		updateList();	
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		updateList();	
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		updateList();
	}
	
	
	public void reset() {
		lastSelectedResult = null;
		lastSelectedIndexItem = Integer.MIN_VALUE;
	}
	

	private void updateList() {
		//check result list
		if (resultList.getSize() == 0) {
			entryTextField.setText("");
			findspotList.setListData(new Vector<>());
			riskList1.setListData(new Vector<>());
			riskList2.setListData(new Vector<>());
			TextSelector.getInstance().changeTextSelection();
			return;
		}
		//cause readjusting of index in case of misplaced index
		int index = indexList.getSelectedIndex();
		Result result = resultList.getElementAt(index);
		ListModel<Result> model = indexList.getModel();
		if (index == -1 || index >= model.getSize()) {
			if (index < 0 && model.getSize() >= 0)
				indexList.setSelectedIndex(index);
			else if (index >= model.getSize() && model.getSize() >= 0)
				indexList.setSelectedIndex(model.getSize() - 1);
			return;
		}
		//-> case: seperator entry
		if (!indexModeRB.isSelected()) {
			if (resultTypeHandler.isSeperator(result)) {
				if (lastSelectedIndexItem <= index && index < resultList.getSize() - 1)
					indexList.setSelectedIndex(index + 1);
				else if (lastSelectedIndexItem >= index && index > 0)
					indexList.setSelectedIndex(index - 1);
				else if (lastSelectedIndexItem < index && index == resultList.getSize() - 1)
					indexList.setSelectedIndex(lastSelectedIndexItem);
				else if (lastSelectedIndexItem > index && index == 0)
					indexList.setSelectedIndex(lastSelectedIndexItem);
				return;
			}
		}
		if (result == null)
			return;
		//set result entry text field
		String resultEntryText = entryTextCreator.getFullResultText(result);
		entryTextField.setText(resultEntryText);
		//set risk list 1 entries
		Risk resultentryRisk = result.getRisk();
		if (resultentryRisk != null) {
			List<String> riskList = resultentryRisk.toStringList();
			riskList1.setListData(riskList.toArray(new String[riskList.size()]));
		}
		//set text entry list entries
		Map<TextRange, ResultInformation> textEntryMap = result.getResultMap();
		Map.Entry<TextRange, ResultInformation>[] entryData = textEntryMap.entrySet().toArray((Map.Entry<TextRange, ResultInformation>[])new Map.Entry[textEntryMap.size()]);
		Arrays.sort(entryData, textEntryComparator);
		findspotList.setListData(entryData);
		if (lastSelectedIndexItem != index && lastSelectedResult != result && selectEntryFlag)
			findspotList.setSelectedIndex(0);
		//mark text if per page mode is selected & set risk list 2
		if (!indexModeRB.isSelected() && result.getResultMap().size() > 0) {
			Map.Entry<TextRange, ResultInformation> entryInfo = getTextEntryFromPerPageModeItem();
			if (entryInfo != null) {
				//mark text
				textSelector.markTextFromIndexSelection(entryInfo.getKey());
				//set risk list 2
				List<String> riskList = entryInfo.getValue().getRisk().toStringList();
				riskList2.setListData(riskList.toArray(new String[riskList.size()]));
			}
		}
		else if (result.getResultMap().size() == 0) // case: result with no findspots -> clear text selection
			textSelector.changeTextSelection();
		//set last selected index
		lastSelectedIndexItem = index;
		lastSelectedResult = result;
		//set view
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RXListUtilities.ensureListIndexVisibility(indexList);
				RXListUtilities.ensureListIndexVisibility(findspotList);
				indexList.requestFocusInWindow();
			}
		});
	}
	
	
//	------------------------- AUX --------------------------------------------
	
	private Entry<TextRange, ResultInformation> getTextEntryFromPerPageModeItem() 
	{
		int index = indexList.getSelectedIndex();
		Result result = resultList.getElementAt(index);
		return result.getResultMap().entrySet().iterator().next();
	}
	


//	--------------------------------- TRASH ------------------------------------------
	
	/*
	private Map.Entry<TextRange, ResultInformation> getTextEntryFromPerPageModeItem2() 
	{
		//get result
		int index = indexList.getSelectedIndex();
		if (index < 0)
			return null;
		Result thisResult = resultList.getElementAt(index);
		//get page number and entry count
		int entryCount = 1;
		ListIterator<Result> it = resultList.getPerPageResultIterator(index);
		Result otherResult = null;
		while (it.hasPrevious()) {//loops until page seperator has been reached
			otherResult = it.previous();
			if (otherResult == thisResult)
				entryCount++;
			if (resultTypeHandler.isSeperator(otherResult))
				break;
		}
		TextLocation thisResultPageNrInfo = otherResult.getAllTextRanges().get(0).getStartOffset();
		//get matching textrange of result
		Map.Entry<TextRange, ResultInformation>[] entrys = thisResult.getResultMap().entrySet().toArray((Map.Entry<TextRange, ResultInformation>[])new Map.Entry[thisResult.getResultMap().size()]);
		Arrays.sort(entrys, textEntryComparator);
		int resultsOnPageCount = 0;
		TextLocation otherResultPageNrInfo;
		for (Map.Entry<TextRange, ResultInformation> entryToCompare : entrys) {
			otherResultPageNrInfo = entryToCompare.getKey().getStartOffset();
			if (	otherResultPageNrInfo.articleNumber == thisResultPageNrInfo.articleNumber
					&& otherResultPageNrInfo.pageNumber == thisResultPageNrInfo.pageNumber
					&& otherResultPageNrInfo.sectionNumber == thisResultPageNrInfo.sectionNumber)
			{//case: same page as selected result
				resultsOnPageCount++;
				if (resultsOnPageCount == entryCount) 
				{//case: same result -> mark text
					return entryToCompare;
				}
			}
		}
		return null;
	}
	 */
	
}
