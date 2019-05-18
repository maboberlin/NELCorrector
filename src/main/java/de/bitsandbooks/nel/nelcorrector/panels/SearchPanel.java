package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class SearchPanel extends JPanel {

	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 10, 10));
	private Dimension textFieldSize = new Dimension(125, 35);
	private Dimension textFieldSizeMin = new Dimension(25, 35);
	
	
	public SearchPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new GridBagLayout());
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Search", 0, 0, font));
		GridBagConstraints gbc;
		//Button Left
		JButton bt1 = (JButton)components.get(ComponentMap.ButtonSearchBackward);
		bt1.setFont(font);
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		add(bt1, gbc);
		//Text Field
		JTextField tf = (JTextField)components.get(ComponentMap.TextFieldSearch);
		tf.setFont(font);
		tf.setPreferredSize(textFieldSize);
		tf.setMinimumSize(textFieldSizeMin);
		tf.setForeground(Color.LIGHT_GRAY);
		tf.setText("Search");
		gbc = MakeGBC.makegbc(1, 0, 2, 1);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		add(tf, gbc);
		//Button Right
		JButton bt2 = (JButton)components.get(ComponentMap.ButtonSearchForeward);
		bt2.setFont(font);
		gbc = MakeGBC.makegbc(3, 0, 1, 1);
		add(bt2, gbc);
		//Check Box 1
		JCheckBox cb1 = (JCheckBox)components.get(ComponentMap.CheckBoxRegex);
		cb1.setFont(font);
		gbc = MakeGBC.makegbc(0, 1, 4, 1);
		gbc.weightx = 1.0f;
		add(cb1, gbc);
		//Check Box 2
		JCheckBox cb2 = (JCheckBox)components.get(ComponentMap.CheckBoxCaseInsensitiv);
		cb2.setFont(font);
		gbc = MakeGBC.makegbc(0, 2, 4, 1);
		gbc.weightx = 1.0f;
		add(cb2, gbc);
	}
	
}
