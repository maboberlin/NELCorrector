package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class ResultPanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 0, 10));
	private Dimension textFieldSize = new Dimension(400, 35);
	
	public ResultPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new GridBagLayout());
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Full Result", 0, 0, font));
		setMinimumSize(new Dimension(120, 45));
		GridBagConstraints gbc;
		//Text Field
		JTextField tf = (JTextField)components.get(ComponentMap.TextFieldResultField);
		tf.setFont(font);
		tf.setPreferredSize(textFieldSize);
		tf.setMinimumSize(textFieldSize);
		tf.setEditable(false);
		gbc = MakeGBC.makegbc(0, 0, 4, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(tf, gbc);
	}
	
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

}
