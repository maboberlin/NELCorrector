package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class TextAreaPanel extends JPanel {
	
	private Font font;
	
	public TextAreaPanel(ComponentMap componentMap) {
		font = GeneralProperties.getInstance().getTextAreaFont();
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));
		GridBagConstraints gbc;
		JTextArea ta = (JTextArea)componentMap.get(ComponentMap.TextAreaMain);
		ta.setEditable(false);
		ta.setFont(font);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(ta);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setWheelScrollingEnabled(true);
		sp.setBorder(BorderFactory.createLineBorder(Color.black));
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		add(sp, gbc);
		sp.setPreferredSize(ta.getPreferredSize());
	}

}
