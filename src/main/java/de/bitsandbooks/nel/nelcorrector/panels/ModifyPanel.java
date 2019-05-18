package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class ModifyPanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 0, 10));
	
	
	public ModifyPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		FlowLayout mgr = new FlowLayout(FlowLayout.CENTER, 20, 10);
		
		setLayout(mgr);
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Modify index list", 0, 0, font));
		//Button 1
		JButton bt = (JButton)components.get(ComponentMap.ButtonCreateEntry);
		bt.setFont(font);
		add(bt);
//		JPanel bt1P = new JPanel();
//		bt1P.add(bt);
//		add(bt1P);
		//Button 2
		JButton bt2 = (JButton)components.get(ComponentMap.ButtonEditEntry);
		bt2.setFont(font);
		add(bt2);
//		JPanel bt2P = new JPanel();
//		bt2P.add(bt2);
//		add(bt2P);
		//set sizes
//		bt.setPreferredSize(bt1P.getPreferredSize());
		bt2.setPreferredSize(new Dimension(bt.getPreferredSize().width, bt.getPreferredSize().height));
	}
	
	/*
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(getPreferredSize().width - 10, 0);
	}
*/
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
}
