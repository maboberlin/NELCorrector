package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class PagesSelectPanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 10, 10));
	private Dimension textFieldSize = new Dimension(75, 35);
	private Dimension textFieldSizeMin = new Dimension(25, 35);
	private Dimension labelMin = new Dimension(5, 35);
	
	
	public PagesSelectPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new GridBagLayout());
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Select Page", 0, 0, font));
		GridBagConstraints gbc;
		//------- Panel 1 ---------------
		JPanel panelTop = new JPanel(new GridBagLayout());
		//Radio Button 1
		JRadioButton rb1 = (JRadioButton)components.get(ComponentMap.RadioButtonTextNr);
		rb1.setFont(font);
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		gbc.weightx = 1.0f;
		panelTop.add(rb1, gbc);
		//Radio Button 2 
		JRadioButton rb2 = (JRadioButton)components.get(ComponentMap.RadioButtonDocNr);
		rb2.setFont(font);
		gbc = MakeGBC.makegbc(0, 1, 1, 1);
		gbc.weightx = 1.0f;
		panelTop.add(rb2, gbc);
		//------- Panel 2 ---------------
		JPanel panelBottom = new JPanel(new GridBagLayout());
		//Button Left
		JButton bt1 = (JButton)components.get(ComponentMap.ButtonPageBackward);
		bt1.setFont(font);
		gbc = MakeGBC.makegbc(0, 0, 2, 1);
//		gbc.weightx = 1.0f;
		gbc.anchor = GridBagConstraints.LINE_START;
		panelBottom.add(bt1, gbc);
		//Page Text Field
		JTextField tf = (JTextField)components.get(ComponentMap.TextFieldPageNr);
		tf.setFont(font);
		tf.setPreferredSize(textFieldSize);
		tf.setMinimumSize(textFieldSizeMin);
		tf.setForeground(Color.LIGHT_GRAY);
		tf.setText("Page N°");
		gbc = MakeGBC.makegbc(2, 0, 2, 1);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		panelBottom.add(tf, gbc);
		//Panel (number of pages)
		JLabel label = (JLabel)components.get(ComponentMap.LabelNumberOfPages);
		label.setFont(font);
		label.setPreferredSize(new Dimension(label.getText().length() * 10, 35));
		label.setMinimumSize(labelMin);
		gbc = MakeGBC.makegbc(4, 0, 1, 1);
		panelBottom.add(label, gbc);
		//Button Right
		JButton bt2 = (JButton)components.get(ComponentMap.ButtonPageForeward);
		bt2.setFont(font);
		gbc = MakeGBC.makegbc(5, 0, 2, 1);
		panelBottom.add(bt2, gbc);	
		//connect panels
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		gbc.weightx = 1.0f;
		add(panelTop, gbc);
		gbc = MakeGBC.makegbc(0, 1, 1, 1);
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		add(panelBottom, gbc);
	}

}
