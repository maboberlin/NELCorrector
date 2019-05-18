package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

public class ModifyCommandForwarder implements ActionListener, PropertyChangeListener {
	
//	--------------------------- FIELDS ------------------------------
	
	private JComponent focusOwner = null;
	
	
//	----------------------- CONSTRUCTOR -----------------------------
	
	private static ModifyCommandForwarder instance;
	
	private ModifyCommandForwarder() 
	{
		KeyboardFocusManager km = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		km.addPropertyChangeListener("permanentFocusOwner", this);
	}
	
	public static ModifyCommandForwarder getInstance() {
		if (instance == null)
			instance = new ModifyCommandForwarder();
		return instance;
	}
	
	
//	----------------------- METHODS --------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		Object o = (evt.getNewValue());
		if (o instanceof JComponent)
			focusOwner = (JComponent)o;
		else
			focusOwner = null;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (focusOwner == null)
			return;
		String actionName = e.getActionCommand();
		Action action = focusOwner.getActionMap().get(actionName);
		if (action != null) {
			action.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
		}
	}

}
