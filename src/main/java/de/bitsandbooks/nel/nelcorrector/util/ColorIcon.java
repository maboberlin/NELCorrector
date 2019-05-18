package de.bitsandbooks.nel.nelcorrector.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorIcon implements Icon {
	
	private Color color;
	private int preferredSize = -1;
	
	private ColorIcon() {
	}
	
	public ColorIcon(Color color, Dimension dimension) {
		this.color = color;
		preferredSize = dimension.height;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(0, 0, preferredSize, preferredSize);
	}

	@Override
	public int getIconWidth() {
		return preferredSize;
	}

	@Override
	public int getIconHeight() {
		return preferredSize;
	}

}
