package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.exceptions.TextSelectionError;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultEntryDialogIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.SingletonFactory;
import de.bitsandbooks.nel.nelcorrector.util.MyListUtils;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class IndexTransferHandler extends TransferHandler {
	
//	-------------------------- FIELDS -------------------------------	
	
	private ResultListModel resultModel;
	private ResultTypeHandlerIF resultTypeHandler;
	private ResultEntryDialogIF resultEntryDialog;
	private ModifyManager modifyManager;
	private JList<Result> indexList;
	private JList<Map.Entry<TextRange, ResultInformation>> findspotList;
	private JTextArea textArea;
	private JRadioButton indexModeRB;
	
	
//	------------------- CONSTRUCTOR & INSTANCE ----------------------
	
	private static IndexTransferHandler instance;
	
	private IndexTransferHandler() {
		resultModel = ResultListModel.getInstance();
		modifyManager = ModifyManager.getInstance();
		ComponentMap components = ComponentMap.getInstance();
		indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		indexModeRB = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
	}
	
	public static IndexTransferHandler getInstance() {
		if (instance == null)
			instance = new IndexTransferHandler();
		return instance;
	}
	
	
//	----------------------- METHODS --------------------------------

	@Override
	public Transferable createTransferable(JComponent c) 
	{
		IndexTransferable result = null;
		List<Map.Entry<TextRange, ResultInformation>> findspotResult;
	    if (c == indexList) {
	    	Result indexResult = (Result)indexList.getSelectedValue();
	    	findspotResult = new Vector<>();
	    	for (Map.Entry<TextRange, ResultInformation> entry : indexResult.getResultMap().entrySet()) {
	    		findspotResult.add(entry);
			}
	    	result = new IndexTransferable(findspotResult);
	    }
	    else if (c == findspotList) {
	    	findspotResult = (List<Map.Entry<TextRange, ResultInformation>>)findspotList.getSelectedValuesList();
	    	result = new IndexTransferable(findspotResult);
	    }
	    else if (c == textArea) {
	    	try {
	    		String selectedText = modifyManager.getSelectedTextString();
	    		result = new IndexTransferable(selectedText);
			} catch (TextSelectionError e) {
				JOptionPane.showMessageDialog(textArea, "Select single text part for editing entry.", "Invalid action", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
	    }
	    return result;
	}
	
	
	@Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }
		
	
	@Override
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		//check if component is indexList
		Component list = support.getComponent();
		if (list != indexList)
			return false;
		//check data type
        if (!support.isDataFlavorSupported(IndexTransferable.findspot_flavor) && !support.isDataFlavorSupported(IndexTransferable.text_flavor))
            return false;
		//check drop position
        if (support.isDrop()) {
            JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
            if (dl.getIndex() == -1) 
                return false;
            //check if drop and drag location are equal by comparing result values
            try {
    			Object data = support.getTransferable().getTransferData(IndexTransferable.findspot_flavor);
    			List<Map.Entry<TextRange, ResultInformation>> originList = (List<Map.Entry<TextRange, ResultInformation>>)data;
    			Point dropPoint = support.getDropLocation().getDropPoint();
    			int index = indexList.locationToIndex(dropPoint);
    			Result result = indexList.getModel().getElementAt(index);
    			List<Map.Entry<TextRange, ResultInformation>> destinyList = new Vector<>(result.getResultMap().entrySet());
    			if (originList != null && destinyList != null && originList.containsAll(destinyList) && destinyList.containsAll(originList)) {
    				return false;
    			
    			}
            } catch (UnsupportedFlavorException | IOException e) {
    			return false;
    		}
        }
		return true;
	}
	
	
	@Override
	public boolean importData(TransferHandler.TransferSupport support) 
	{
        if (!canImport(support))
            return false;
		//check data type
        if (!support.isDataFlavorSupported(IndexTransferable.findspot_flavor) && !support.isDataFlavorSupported(IndexTransferable.text_flavor))
            return false;
        //get result to which the drop has to be inserted
        Result destiny = null;
        if (support.isDrop()) {//drag
            JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
            int index = dl.getIndex();
            destiny = (Result)indexList.getModel().getElementAt(index);
        }
        else {//paste
        	destiny = indexList.getSelectedValue();
        }
        if (destiny == null)
        	return false;
        //get data
        Transferable t = support.getTransferable();
        try {
        	Object data1 = t.getTransferData(IndexTransferable.text_flavor);
        	Object data2 = t.getTransferData(IndexTransferable.findspot_flavor);
        	if (data1 instanceof String) {
        		SingletonFactory singletonFactory = SingletonFactory.getInstance();
				resultTypeHandler = singletonFactory.getResultTypeHandler();
        		resultEntryDialog = singletonFactory.getResultEntryDialogIF();
        		String text = (String)data1;
        		TextRange tr = modifyManager.getSelectedTextPart(false);
        		Result result = resultTypeHandler.createResult(text);
        		ResultInformation emptyResultInformation = resultTypeHandler.getEmptyResultInformation();
				result.addResult(tr, emptyResultInformation);
				result = resultEntryDialog.showDialog("New Entry", result);
				if (result != null)
					resultModel.insert(result);
				int indexOfNewResult = resultModel.getIndexByResult(result);
				modifyManager.update(indexOfNewResult, null);
        		return true;
        	}
        	else if (data2 instanceof List) {
            	List<Map.Entry<TextRange, ResultInformation>> originList = (List<Map.Entry<TextRange, ResultInformation>>)data2;
        		resultModel.insert(destiny, originList, !support.isDrop());
        		clearClipboard();
        		if (indexModeRB.isSelected()) {
        	   		SwingUtilities.invokeLater(new Runnable() {
        				@Override
        				public void run() {
        					if (originList != null && originList.size() > 0) {
        						Entry<TextRange, ResultInformation> newFindspot = originList.get(0);
        						ListModel<Entry<TextRange, ResultInformation>> model = findspotList.getModel();
        						if (model != null && model.getSize() > 0) {
        							Entry<TextRange, ResultInformation> el;
        							int toSelectIx = 0;
        							for (int i = 0; i < model.getSize(); i++) {
        								el = model.getElementAt(i);
        								if (el.getKey().equals(newFindspot.getKey())) {
        									toSelectIx = i;
        									break;
        								}
        							}
        							findspotList.setSelectedIndex(toSelectIx);
        							findspotList.requestFocusInWindow();
        						}	
        					}
        				}
        			});
        		}
        		return true;
        	}
        } 
        catch (Exception e) { return false; }
        return false; 
	}
	
	
	@Override
	public void exportDone(JComponent source, Transferable t, int action)
	{
		if (action != DnDConstants.ACTION_MOVE)
			return;
		try {
			Object data1 = t.getTransferData(IndexTransferable.text_flavor);
			if (data1 instanceof String)
				return;
			Object data2 = t.getTransferData(IndexTransferable.findspot_flavor);
			List<Map.Entry<TextRange, ResultInformation>> originList = (List<Map.Entry<TextRange, ResultInformation>>)data2;
			List<TextRange> toDelete = MyListUtils.extractTextRanges(originList);
			Result result = indexList.getSelectedValue();
			if (source == indexList && indexModeRB.isSelected()) {
				resultModel.delete(result);
			}
			else if (source == findspotList || (source == indexList && !indexModeRB.isSelected())) {
				resultModel.delete(result, toDelete);
				modifyManager.checkEmptyResults(result);
			}
			//set focus
			if (indexModeRB.isSelected()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (findspotList.getModel() != null && findspotList.getModel().getSize() > 0)
							findspotList.requestFocusInWindow();
					}
				});
			}
		} catch (UnsupportedFlavorException | IOException e) {
		}
		
	}

	
	private void clearClipboard() 
	{
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return false;
			}
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[0];
			}
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				throw new UnsupportedFlavorException(flavor);
			}
		}, null);
	}
	
}
