package de.bitsandbooks.nel.nelcorrector.logic;

import javax.swing.JTextArea;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedListener;

public class PageHandler implements PageChangedListener {

//	-------------------------- FIELDS -------------------------------
	
	private JTextArea textArea;
	
	
//	---------------------- CONSTRUCTOR & INSTANCE ---------------------------
	
	private static PageHandler instance;
	
	private PageHandler() 
	{
		ComponentMap components = ComponentMap.getInstance();
		textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
	}
	
	public static PageHandler getInstance() {
		if (instance == null)
			instance = new PageHandler();
		return instance;
	}
	
	
//	-------------------------- METHODS ------------------------------
	
	@Override
	public void pageChanged(PageChangedEvent e) {
		Page page = e.page;
		if (page != null) {
			String text = page.getText();
			textArea.setText(text);
			textArea.setCaretPosition(0);
		}
		else
			textArea.setText("");

	}

}
