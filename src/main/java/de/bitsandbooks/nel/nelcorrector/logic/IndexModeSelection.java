package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import de.bitsandbooks.nel.nelcorrector.ComponentListenerBuilder;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class IndexModeSelection implements ActionListener {
	
	private ResultListModel results;
	private JList<Result> indexList;
	private JList<Map.Entry<TextRange, ResultInformation>> findspotList;
	
	
	public IndexModeSelection() {
		ComponentMap components = ComponentMap.getInstance();
		indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		results = ResultListModel.getInstance();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JRadioButton rb = (JRadioButton)e.getSource();
		//get old selection values
		Result oldResultIndexList = indexList.getSelectedValue();
		Entry<TextRange, ResultInformation> oldResultFindspotList = 
				!findspotList.isSelectionEmpty() ? findspotList.getSelectedValue() : (findspotList.getModel().getSize() > 0 ? findspotList.getModel().getElementAt(0) : null);
		//clear selection
		indexList.clearSelection();
		findspotList.clearSelection();
		//get modus
		boolean indexModus;
		if (rb.getActionCommand().equals(ComponentListenerBuilder.IndexCommand))
			indexModus = true;
		else if (rb.getActionCommand().equals(ComponentListenerBuilder.PerPageCommand))
			indexModus = false;
		else
			return;
		//do modus change on model
		results.changeIndexListMode(indexModus);
		//do modus change on list and select previous value
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if  (indexModus) {
					//set text entry list
					findspotList.setEnabled(true);
					findspotList.setFocusable(true);
					//set index list by old result
					if (oldResultIndexList != null) {
						int index = results.getMainResultIndexByName(oldResultIndexList);
						indexList.setSelectedIndex(index);
						//set text entry list
						ListModel<Entry<TextRange, ResultInformation>> newResultEntryList = findspotList.getModel();
						Entry<TextRange, ResultInformation> el;
						for (int i = 0; i < newResultEntryList.getSize(); i++) {
							el = newResultEntryList.getElementAt(i);
							if (el.getKey().equals(oldResultFindspotList.getKey()))
								findspotList.setSelectedIndex(i);
						}
					}
					else if (findspotList.getModel() != null && findspotList.getModel().getSize() > 0) 
						findspotList.setSelectedIndex(0);
				}
				else {
					//set text entry list
					findspotList.setEnabled(false);
					findspotList.setFocusable(false);
					findspotList.clearSelection();
					//set index list by old result
					if (oldResultFindspotList != null) {
						int index = results.getIndexByTextRange(oldResultFindspotList.getKey());
						if (index >= 0)
							indexList.setSelectedIndex(index);
					}
				}
				RXListUtilities.ensureListIndexVisibility(indexList);
				RXListUtilities.ensureListIndexVisibility(findspotList);
			}
		});
	}

}
