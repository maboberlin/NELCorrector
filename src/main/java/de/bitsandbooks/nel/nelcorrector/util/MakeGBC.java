package de.bitsandbooks.nel.nelcorrector.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MakeGBC {
	
	/**
	 * @param x x-pos
	 * @param y y-pos
	 * @param width width
	 * @param height height
	 */
	public static GridBagConstraints makegbc(int x, int y, int width, int height) 
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}

}
