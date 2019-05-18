package de.bitsandbooks.nel.nelcorrector.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFileChooser;

import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;

public class MyFileChooser extends JFileChooser {
	
	Font font;
	
	public MyFileChooser() {
		super();
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getDialogFont();
		setFileChooserFont(getComponents());
		Dimension newDim = getPreferredSize();
		newDim = new Dimension(newDim.width, newDim.height + 30);
		setPreferredSize(newDim);	
	}
	
	private void setFileChooserFont(Component[] comp)  { 
		for(int x = 0; x < comp.length; x++)  {  
			if(comp[x] instanceof Container) setFileChooserFont(((Container)comp[x]).getComponents());  
			try{comp[x].setFont(font);}  
			catch(Exception e){}
	    }  
	}

}
