package de.bitsandbooks.nel.nelcorrector.util;

import javax.swing.JList;
import javax.swing.SwingUtilities;

public class RXListUtilities {
	
	public static void ensureListIndexVisibility(JList<?> list) {
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				list.ensureIndexIsVisible(list.getSelectedIndex());
			}
		});
	}

}
