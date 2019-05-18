package de.bitsandbooks.nel.nelcorrector.resulttype.nameresulttype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultEntryDialogIF;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;
import de.bitsandbooks.nel.nelcorrector.util.NameUtilities;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;
import de.bitsandbooks.nel.nelinterface2.NameEntry;
import de.bitsandbooks.nel.nelinterface2.NameResult;
import de.bitsandbooks.nel.nelinterface2.Result;

public class ResultEntryDialogNameResult extends JDialog implements ResultEntryDialogIF  {
	
//	------------------------- FIELDS --------------------------
	
	private Result result;
	
	private static JPanel mainPanel;
	
	private static JTextField surname;
	private static JTextField forename;
	private static JTextField initials;
	private static JTextField title;
	private static JTextField romanNumber;
	private static JButton OKButton;
	private static JButton CancelButton;
	private static JLabel warning;
	
	
//	---------------------- CONSTRUCTOR ------------------------
	
	private static ResultEntryDialogNameResult instance;
	
	private ResultEntryDialogNameResult(Frame owner, boolean modal) 
	{
		super(owner, modal);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		initMainPanel();
		setLogic();
	}
	
	public static ResultEntryDialogNameResult getInstance(Frame frame) {
		if (instance == null)
			instance = new ResultEntryDialogNameResult(frame, true);
		return instance;
	}
	
	
	
//	--------------------- METHODS ----------------------------

	@Override
	public Result showDialog(String title, Result result) {
		//reset values 
		resetValues();
		//set textfields in case of old result
		if (result != null) {
			NameResult nameRes = (NameResult)result;
			NameEntry name = nameRes.getName();
			surname.setText(name.getSurname());
			forename.setText(name.getPlainForename());
			initials.setText(name.getInitials());
			this.title.setText(name.getTitle());
			romanNumber.setText(name.getRomanNumber());
			this.result = result;
		}
		//set title
		setTitle(title);
		//set location
		setLocationRelativeTo(null);
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2.7f);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2.7f);
	    setLocation(x, y);
	    //reset warning 
	    warning.setForeground(mainPanel.getBackground());
	    //pack and set visible (+cancel button)
	    pack();
	    CancelButton.requestFocusInWindow();
		setVisible(true);
		//return after dialog has closed
		return this.result;
	}

	
	
//	--------------------- BUILD DIALOG ---------------------------
	
	private void initMainPanel() 
	{
		//init general values
		GeneralProperties props = GeneralProperties.getInstance();
		Font font = props.getMainFont();
		Dimension tfSize = new Dimension(300, 35);
		Insets labelInsets = new Insets(7, 5, 7, 15);
		Image img = props.getBookIcon();
		setIconImage(img);
		//init panels
		mainPanel = new JPanel(new BorderLayout(0, 5));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		JPanel textFieldsPanel = new JPanel(new GridBagLayout());
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		//init components
		surname = new JTextField();
		forename = new JTextField();
		initials = new JTextField();
		title = new JTextField();
		romanNumber = new JTextField();
		OKButton = new JButton("Ok");
		CancelButton = new JButton("Cancel");
		warning = new JLabel();
		JLabel surnameL = new JLabel("Surname:");
		JLabel forenameL = new JLabel("Forename:");
		JLabel initialsL = new JLabel("Initials:");
		JLabel titleL = new JLabel("Title:");
		JLabel romanNumberL = new JLabel("RomanNumber:");
		//set values
		surname.setFont(font);
		forename.setFont(font);
		initials.setFont(font);
		title.setFont(font);
		romanNumber.setFont(font);
		OKButton.setFont(font);
		CancelButton.setFont(font);
		warning.setFont(font);
		warning.setForeground(mainPanel.getBackground());
		surnameL.setFont(font);
		forenameL.setFont(font);
		initialsL.setFont(font);
		titleL.setFont(font);
		romanNumberL.setFont(font);
		surname.setPreferredSize(tfSize);
		forename.setPreferredSize(tfSize);
		initials.setPreferredSize(tfSize);
		title.setPreferredSize(tfSize);
		romanNumber.setPreferredSize(tfSize);
		OKButton.setPreferredSize(CancelButton.getPreferredSize());
		//set texfields panel
		GridBagConstraints gbc;
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(surnameL, gbc);
		gbc = MakeGBC.makegbc(0, 1, 1, 1);
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(forenameL, gbc);
		gbc = MakeGBC.makegbc(0, 2, 1, 1);
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(initialsL, gbc);
		gbc = MakeGBC.makegbc(0, 3, 1, 1);
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(titleL, gbc);
		gbc = MakeGBC.makegbc(0, 4, 1, 1);
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(romanNumberL, gbc);
		gbc = MakeGBC.makegbc(1, 0, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(surname, gbc);
		gbc = MakeGBC.makegbc(1, 1, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(forename, gbc);
		gbc = MakeGBC.makegbc(1, 2, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(initials, gbc);
		gbc = MakeGBC.makegbc(1, 3, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(title, gbc);
		gbc = MakeGBC.makegbc(1, 4, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(romanNumber, gbc);
		gbc = MakeGBC.makegbc(1, 5, 2, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		textFieldsPanel.add(warning, gbc);
		//set bottom panel
		buttonsPanel.add(OKButton);
		buttonsPanel.add(CancelButton);
		//set panels
		mainPanel.add(textFieldsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		//init short cuts
		Action okButtonF = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OKButton.requestFocusInWindow();
			}
		};
		Action cancelButtonF = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CancelButton.requestFocusInWindow();
			}
		};
		Action enter = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CancelButton.hasFocus())
					CancelButton.doClick();
//				else if (OKButton.hasFocus())
				OKButton.doClick();
			}
		};
		KeyStroke leftKey = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		KeyStroke rightKey = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		InputMap inputMapOK = OKButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMapOK.put(leftKey, "okSel");
		inputMapOK.put(enterKey, "enter");
		CancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rightKey, "cancelSel");
		ActionMap actionMapOK = OKButton.getActionMap();
		actionMapOK.put("okSel", okButtonF);
		actionMapOK.put("enter", enter);
		CancelButton.getActionMap().put("cancelSel", cancelButtonF);
	}
	
	
	private void setLogic() 
	{
		//set buttons logic
		OKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setResult();
				exit();
			}
		});
		CancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetValues();
				exit();
			}
		});
		//set input verifier
		InputVerifier surnameIV = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				String txt = surname.getText();
				if (txt.length() == 0) {
					warning.setText("Set surname first!");
					warning.setForeground(Color.RED);
					return false;
				}
				else {
					warning.setForeground(mainPanel.getBackground());
					return true;
				}	
			}
		};
		InputVerifier forenameInitial = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				if (((JTextField)input).getText().length() == 0)
					return true;
				String forenameText = forename.getText();
				String initialsText = initials.getText();
				boolean match = NameUtilities.forenamePartsMatching(forenameText, initialsText);
				if (!match) {
					warning.setText("Forename parts are misfitting!");
					warning.setForeground(Color.RED);
					return false;
				}
				else {
					warning.setForeground(mainPanel.getBackground());
					return true;
				}
			}
		};
		InputVerifier romanNumberIV = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				String toCheck = romanNumber.getText();
				boolean isRoman = RomanArabic.isRoman(toCheck);
				if (!isRoman) {
					warning.setText("Not a roman number!");
					warning.setForeground(Color.RED);
					return false;
				}
				else {
					warning.setForeground(mainPanel.getBackground());
					return true;
				}
			}
		};
		surname.setInputVerifier(surnameIV);
		forename.setInputVerifier(forenameInitial);
		initials.setInputVerifier(forenameInitial);
		romanNumber.setInputVerifier(romanNumberIV);
	}
	
	
	
//	------------------------------- AUX ------------------------------------
	
	private void exit() {
		setVisible(false);
		dispose();
	}
	
	
	private void resetValues() 
	{
		this.result = null;
		surname.setText("");
		forename.setText("");
		initials.setText("");
		title.setText("");
		romanNumber.setText("");
	}
	
	
	private void setResult() 
	{
		//check surname field
		if (surname.getText().length() == 0) {
			warning.setForeground(Color.RED);
			return;
		}
		//init result
		NameResult newResult = (result == null) ? new NameResult() : (NameResult)result;
		NameEntry name = (result == null) ? new NameEntry() : newResult.getName();
		//set basic values
		String surnameText = surname.getText();
		String forenameText = forename.getText();
		String initialText = initials.getText();
		String titleText = title.getText();
		String romanNumberText = romanNumber.getText();
		name.setSurname(surnameText);
		name.setPlainForename(forenameText);
		name.setInitials(initialText);
		name.setTitle(titleText);
		name.setRomanNumber(romanNumberText);
		//set calculated values
		String lexicoSurname = NameUtilities.getLexicographicSurname(surnameText);
		String fullForename = NameUtilities.getFullForename(forenameText, initialText);
		name.setLexicographicSurname(lexicoSurname);
		name.setFullForename(fullForename);
		//set full name
		String fullName = name.toString();
		name.setFullName(fullName);
		//set return result
		newResult.setName(name);
		result = newResult;
	}

}
