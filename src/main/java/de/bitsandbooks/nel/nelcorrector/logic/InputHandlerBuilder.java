package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIDefaults;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.listener.GhostScriptEvent;
import de.bitsandbooks.nel.nelcorrector.listener.GhostScriptListener;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;

public class InputHandlerBuilder {
	
//	---------------------- FIELDS -----------------------------------
	
	private static Color defaultTextFieldForeground;
	
	
//	-------------------- MAIN METHOD --------------------------------

	public static void initializeInputHandler(ComponentMap components) 
	{
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		defaultTextFieldForeground = defaults.getColor("TextField.foreground");
		buildGhostScript(components);
		buildPagesInputVerifier(components);
	}
	
	
	
//	------------------- MINOR METHODS -------------------------------

	private static void buildGhostScript(ComponentMap components) {
		JTextField pageNrTextField = (JTextField)components.get(ComponentMap.TextFieldPageNr);
		GhostScriptHandler page = new GhostScriptHandler("Page N°");
		pageNrTextField.addFocusListener(page);
		JTextField searchTextField = (JTextField)components.get(ComponentMap.TextFieldSearch);
		GhostScriptHandler search = new GhostScriptHandler("Search");
		search.setGhostScriptListener(SearchExecutor.getInstance());
		searchTextField.addFocusListener(search);	
	}
	

	private static void buildPagesInputVerifier(ComponentMap components) {
		JTextField pageNrTextField = (JTextField)components.get(ComponentMap.TextFieldPageNr);
		pageNrTextField.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField)input;
				String text = tf.getText();
				if 	((text.matches("\\d+") && (Integer.parseInt(text) > 0)) //if text is positive digit 
					|| (RomanArabic.isRoman(text))) //if text is roman
					return true;
				else {
					Toolkit.getDefaultToolkit().beep();
					return false;
				}		
			}
		});	
	}
	
	
	
//	----------------------- GHOST SCRIPT CLASS -----------------------------------------
	
	private static class GhostScriptHandler implements FocusListener {
		
		private boolean showHintPages;
		private String label;
		private GhostScriptListener listener;

		
		public GhostScriptHandler(String label) {
			showHintPages = true;
			this.label = label;
		}

		
		@Override
		public void focusGained(FocusEvent e) {
			if (showHintPages) {
				showHintPages = false;
				JTextField tf = ((JTextField)e.getSource());
				tf.setText("");
				tf.setForeground(defaultTextFieldForeground);
				fireScriptChangedEvent();
			}
		}

	
		@Override
		public void focusLost(FocusEvent e) {
			JTextField tf = (JTextField)e.getSource();
			if (tf.getText().length() == 0) {
				tf.setForeground(Color.LIGHT_GRAY);
				tf.setText(label);
				showHintPages = true;
				fireScriptChangedEvent();
			}	
		}
		
		
		public void setGhostScriptListener(GhostScriptListener listener) {
			this.listener = listener;
		}
		
		
		private void fireScriptChangedEvent() {
			if (listener != null)
				listener.ghostScriptSet(new GhostScriptEvent(this, showHintPages));
		}
		
	}

}
