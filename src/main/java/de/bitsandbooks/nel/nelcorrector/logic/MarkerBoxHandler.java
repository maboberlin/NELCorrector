package de.bitsandbooks.nel.nelcorrector.logic;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelinterface2.Result;

public class MarkerBoxHandler {
	
//	-------------------------- VARIABLES ----------------------------------
	
	private DefaultComboBoxModel<Result> comboModel;
	private ResultListModel resultModel;
	private JComboBox<Result> comboBox;
	private JList<Result> indexList;
	private JCheckBox deleteMarker;
	private JRadioButton indexMode;
	boolean delLock, selLock;
	
	
//	------------------------- CONSTRUCTOR --------------------------------

	public MarkerBoxHandler(JComboBox<Result> jComboBox, JCheckBox jCheckBox, JList<Result> indexList, JRadioButton indexMode) {
		comboBox = jComboBox;
		comboModel = (DefaultComboBoxModel<Result>)comboBox.getModel();
		resultModel = ResultListModel.getInstance();
		this.indexList = indexList;
		deleteMarker = jCheckBox;
		this.indexMode = indexMode;
	}
	
	
//	------------------------ METHODS -------------------------------------
	
	public void createMarker() 
	{
		//check if usage is possible
		if (checkUsage()) {
			//create marker
			Result res = indexList.getSelectedValue();
			if (res != null && !elementAlreadyExists(res)) {
				delLock = true;
				selLock = true;
				comboModel.insertElementAt(res, 0);
				comboModel.setSelectedItem(res);
				selLock = false;
			}
		}	
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				delLock = false;
			}
		});
	}
	
	
	public void selectEntry() 
	{
		//check if usage is possible
		if (checkUsage()) {
			//select index
			Result res = (Result)comboBox.getSelectedItem();
			int ix = resultModel.getIndexByResult(res);
			if (ix >= 0) {
				indexList.setSelectedIndex(ix);
				RXListUtilities.ensureListIndexVisibility(indexList);
				if (!delLock && deleteMarker.isSelected()) {
					selLock = true;
					comboModel.removeElement(res);
					selLock = false;
				}
			}
			else {
				comboModel.removeElement(res);
			}
		}	
	}


	private boolean checkUsage() 
	{
		if (resultModel.getSize() == 0)
			return false;
		if (!indexMode.isSelected()) {
			JOptionPane.showMessageDialog(null, "Index marker usage only possible when index mode is selected", "Index Marker", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		if (selLock)
			return false;
		return true;
	}
	
	
	private boolean elementAlreadyExists(Result res) 
	{
		Result resOther;
		for (int i = 0; i < comboModel.getSize(); i++) {
			resOther = comboModel.getElementAt(i);
			if (resOther == res)
				return true;
		}
		return false;
	}
	
	

}
