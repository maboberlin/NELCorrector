package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;

import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatEvent;
import de.bitsandbooks.nel.nelcorrector.listener.BibliographyFormatListener;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;



public class BibliographyFormatDialog extends JDialog implements ResultLoadedListener {
	
//	------------------------- FIELDS --------------------------
	
	private static final String[] EntryModeTextParts =
		{
			"1, 1, 3, 4, 6, 7, 8, 10, 11, 12, 13 &rArr; 1, 3, 4, 6, 7, 8, 10, 11, 12, 13",
			"1, 1, 3, 4, 6, 7, 8, 10, 11, 12, 13 &rArr; 1, 3f., 6ff., 10ff.",
			"1, 1, 3, 4, 6, 7, 8, 10, 11, 12, 13 &rArr; 1, 3f., 6ff., 10-13",
			"1, 1, 3, 4, 6, 7, 8, 10, 11, 12, 13 &rArr; 1, 3f., 6ff., 10-3"
		};
	
	
	private List<BibliographyFormatListener> listeners = new Vector<BibliographyFormatListener>();
	
	private Frame mainFrame;
	private JPanel panel;
	
	private JRadioButton[] entryModeButtons;
	private JCheckBox cancelBibliographys;
	private JCheckBox printComma;
	private JButton OKButton;
	
	
//	--------------- CONSTRUCTOR & INSTANCE ------------------------
	
	private static BibliographyFormatDialog instance;
	
	private BibliographyFormatDialog(Frame owner, boolean modal) 
	{
		super(owner, modal);
		mainFrame = owner;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setTitle("Bibliography Format Settings");
		initMainPanel();
		setLogic();
	}


	public static BibliographyFormatDialog getInstance(Frame frame) {
		if (instance == null)
			instance = new BibliographyFormatDialog(frame, true);
		return instance;
	}
	
	
//	------------------- INIT METHODS ---------------------------

	public void addBibFormatSetListener(BibliographyFormatListener listener) {
		listeners.add(listener);
	}
	
	
	@Override
	public void resultLoaded(ResultLoadedEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireEvent();
				mainFrame.revalidate();
				mainFrame.repaint();
			}
		});
		
	}
	
	
	private void initMainPanel() 
	{
		//init general values
		GeneralProperties props = GeneralProperties.getInstance();
		Image img = props.getBookIcon();
		setIconImage(img);
		Font font = props.getMainFont();
		//init components
		panel = new JPanel(new BorderLayout(0, 20));
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));
		entryModeButtons = new JRadioButton[EntryModeTextParts.length];
		String text;
		for (int i = 0; i < EntryModeTextParts.length; i++) {
			text = String.format("<html>%s<html>", EntryModeTextParts[i]);
			entryModeButtons[i] = new JRadioButton(text);
			entryModeButtons[i].setFont(font);	
		}
		cancelBibliographys = new JCheckBox("Cancel bibliographys (results of bibliography findspots get sorted out)");
		cancelBibliographys.setFont(font);
		printComma = new JCheckBox("Set comma (behind result entry; i.e.: 'Adam,')");
		printComma.setFont(font);
		OKButton = new JButton("Ok");
		OKButton.setFont(font);
		OKButton.setPreferredSize(new Dimension(OKButton.getPreferredSize().width * 2, OKButton.getPreferredSize().height));
		//entry mode panel
		JPanel entryModePanel = new JPanel();
		entryModePanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Entry mode options", 0, 0, font));
		BoxLayout entryModeLayout = new BoxLayout(entryModePanel, BoxLayout.Y_AXIS);
		entryModePanel.setLayout(entryModeLayout);
		for (JRadioButton rb : entryModeButtons) {
			entryModePanel.add(rb);
			entryModePanel.add(Box.createRigidArea(new Dimension(0,10)));
		}
		//other settings panel
		JPanel otherSettingsPanel = new JPanel();
		BoxLayout settingsLayout = new BoxLayout(otherSettingsPanel, BoxLayout.Y_AXIS);
		otherSettingsPanel.setLayout(settingsLayout);
		otherSettingsPanel.add(cancelBibliographys);
		otherSettingsPanel.add(Box.createRigidArea(new Dimension(0,12)));
		otherSettingsPanel.add(printComma);
		//set main panel
		JPanel southPanel = new JPanel(new BorderLayout(0, 20));
		southPanel.add(otherSettingsPanel, BorderLayout.CENTER);
		JPanel okButtonP = new JPanel();
		okButtonP.add(OKButton);
		southPanel.add(okButtonP, BorderLayout.SOUTH);
		panel.add(entryModePanel, BorderLayout.CENTER);
		panel.add(southPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);
		//set short cuts
		Action enter = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (OKButton.hasFocus())
					OKButton.doClick();
			}
		};
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		InputMap inputMapOK = OKButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMapOK.put(enterKey, "enter");
		ActionMap actionMapOK = OKButton.getActionMap();
		actionMapOK.put("enter", enter);
	}
	
	
	private void setLogic() 
	{
		//build button group
		ButtonGroup bg = new ButtonGroup();
		for (JRadioButton rb : entryModeButtons) {
			bg.add(rb);
		}
		entryModeButtons[0].setSelected(true);
		//set buttons logic
		OKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//fire format set event
				fireEvent();
				//exit
				setVisible(false);
				dispose();
			}
		});
	}
	
	
//	------------------------- METHODS --------------------------
	
	public void showDialog() 
	{
	    //pack and set visible (+cancel button)
	    pack();
		//set location
		setLocationRelativeTo(null);
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2.7f);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2.7f);
	    setLocation(x, y);
	    OKButton.requestFocusInWindow();
		setVisible(true);
	}
	
	
	private BibliographyFormatEvent getFormatSetEvent() {
		boolean cancelBibliographies = this.cancelBibliographys.isSelected();
		boolean printComma = this.printComma.isSelected();
		int entryMode = 0;
		for (int i = 0; i < entryModeButtons.length; i++) {
			if (entryModeButtons[i].isSelected()) {
				entryMode = i;
				break;
			}
		}
		BibliographyFormatEvent result = new BibliographyFormatEvent(this, entryMode, cancelBibliographies, printComma);
		return result;
	}
	
	
	private void fireEvent() {
		for (BibliographyFormatListener listener : listeners) {
			BibliographyFormatEvent event = getFormatSetEvent();
			listener.formatIsSet(event);
		}
	}

}
