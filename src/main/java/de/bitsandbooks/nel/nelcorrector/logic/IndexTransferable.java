package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class IndexTransferable implements Transferable {
	
//	-------------------------- FIELDS -----------------------------------
	
	//supported data type
	static final DataFlavor findspot_flavor = new DataFlavor(List.class, "findspotlist");
	static final DataFlavor text_flavor = new DataFlavor(String.class, "text");
	
	
	//result fields
	private List<Map.Entry<TextRange, ResultInformation>> indexData;
	private String textData;
	
		
//	-------------------------- CONSTRUCTORS -----------------------------

	public IndexTransferable(List<Map.Entry<TextRange, ResultInformation>> data) {
		indexData = data;
	}
	
	
	public IndexTransferable(String data) {
		textData = data;
	}
	
	
	
//	---------------------------- METHODS -------------------------------

	@Override
	public DataFlavor[] getTransferDataFlavors() 
	{
		DataFlavor[] result = {findspot_flavor, text_flavor};
		return result;
	}

	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) 
	{
        if (flavor.equals(IndexTransferable.findspot_flavor)) 
            return true;
        if (flavor.equals(IndexTransferable.text_flavor)) 
            return true;
        else
        	return false;
	}

	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
	{
        if (flavor.equals(IndexTransferable.findspot_flavor)) {
            return (Object)indexData;
        } else if (flavor.equals(IndexTransferable.text_flavor)) {
        	return (Object)textData;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
	}

}
