package de.bitsandbooks.nel.nelcorrector.logic;

import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class FindspotListHandler implements ListSelectionListener {
	
//	--------------------------- FIELDS --------------------------------
	
	private TextSelector textSelector;
	private JList<Map.Entry<TextRange, ResultInformation>> findspotList;
	private JList<String> riskList2;
	
	
//	------------------- CONSTRUCTOR & INSTANCE -------------------------
	
	private static FindspotListHandler instance;
	
	private FindspotListHandler() {
		ComponentMap components = ComponentMap.getInstance();
		findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		riskList2 = (JList<String>)components.get(ComponentMap.ListFindspotRiskList);
		textSelector = TextSelector.getInstance();
	}
	
	
	public static FindspotListHandler getInstance() {
		if (instance == null)
			instance = new FindspotListHandler();
		return instance;
	}
	

	
//	-------------------- METHODS -------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectEntry();
	}


	private void selectEntry() {
		//get selected value
		int index = findspotList.getSelectedIndex();
		if (index == -1)
			return;
		ListModel<Map.Entry<TextRange, ResultInformation>> listModel = findspotList.getModel();
		if (index >= 0 && index < listModel.getSize()) {
			Map.Entry<TextRange, ResultInformation> selectedValue = listModel.getElementAt(index);
			//mark text
			textSelector.markTextFromIndexSelection(selectedValue.getKey());
			//set risk list
			ResultInformation info = selectedValue.getValue();
			Risk risk = info.getRisk();
			List<String> riskList = risk.toStringList();
			riskList2.setListData(riskList.toArray(new String[riskList.size()]));
		}
	}

}
