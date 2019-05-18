package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;
import de.bitsandbooks.nel.nelinterface2.Result;

public class IndexPanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 10, 10));
	private Dimension resultListSize = new Dimension(500, 500);
	private Dimension riskListMinSize = new Dimension(200, 100);
	
	public IndexPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new GridBagLayout());
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Index / Results per page", 0, 0, font));
		GridBagConstraints gbc;
		//Radio Button 1
		JRadioButton rb1 = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		rb1.setFont(font);
		gbc = MakeGBC.makegbc(0, 0, 2, 1);
		add(rb1, gbc);
		//Radio Button 2
		JRadioButton rb2 = (JRadioButton)components.get(ComponentMap.RadioButtonPerPage);
		rb2.setFont(font);
		gbc = MakeGBC.makegbc(0, 1, 2, 1);
		add(rb2, gbc);
		//Checkbox 1
		JCheckBox cb = (JCheckBox)components.get(ComponentMap.CheckBoxSelectText);
		cb.setFont(font);
		gbc = MakeGBC.makegbc(2, 0, 1, 1);
		gbc.insets = new Insets(0, 30, 0, 30);
		add(cb, gbc);
		//Checkbox 2
		JCheckBox cb2 = (JCheckBox)components.get(ComponentMap.CheckBoxSelectIndex);
		cb2.setFont(font);
		gbc = MakeGBC.makegbc(2, 1, 1, 1);
		gbc.insets = new Insets(0, 30, 0, 30);
		add(cb2, gbc);
		//Buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		JButton bt1 = (JButton)components.get(ComponentMap.ButtonCreateIndexMarker);
		bt1.setFont(font);
		JButton bt2 = (JButton)components.get(ComponentMap.ButtonSelectIndexMarker);
		bt2.setFont(font);
		JPanel bt2Panel = new JPanel(new FlowLayout());
		bt2Panel.setBorder(new EmptyBorder(0, 10, 0, 0));
		bt2Panel.add(bt2);
		buttonPanel.add(bt1);
		buttonPanel.add(bt2Panel);
		gbc = MakeGBC.makegbc(3, 1, 1, 1);
		gbc.insets = new Insets(10, 20, 0, 0);
		add(buttonPanel, gbc);
		//ComboBox + Checkbox
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		JComboBox<Result> cbb = (JComboBox<Result>)components.get(ComponentMap.ComboBoxIndexMarker);
		cbb.setPreferredSize(bt1.getPreferredSize());
		cbb.setFont(font);
		JCheckBox cb3 = (JCheckBox)components.get(ComponentMap.CheckBoxDeleteIndexMarker);
		cb3.setFont(font);
		panel.add(cbb);
		panel.add(cb3);
		gbc = MakeGBC.makegbc(3, 0, 1, 1);
		gbc.insets = new Insets(0, 10, 0, 0);
		add(panel, gbc);
		//JList Index
		JList<ResultListModel> jList = (JList<ResultListModel>)components.get(ComponentMap.ListIndexList);
		jList.setFont(font);
		JScrollPane sp1 = (JScrollPane)components.get(ComponentMap.ScrollPaneIndex);
		sp1.setViewportView(jList);
		sp1.setWheelScrollingEnabled(true);
		sp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp1.setPreferredSize(sp1.getPreferredSize());
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.setBorder(borderPlain);
		listPanel.add(sp1, BorderLayout.CENTER);
		gbc = MakeGBC.makegbc(0, 2, 4, 5);
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		add(listPanel, gbc);
		//JList RiskList
		JList<String> jList2 = (JList<String>)components.get(ComponentMap.ListIndexRiskList);
		jList2.setFont(font);
		JScrollPane sp2 = new JScrollPane();
		sp2.setViewportView(jList2);
		sp2.setWheelScrollingEnabled(true);
		sp2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp2.setPreferredSize(sp2.getPreferredSize());
		sp2.setMinimumSize(riskListMinSize);
		JPanel listPanel2 = new JPanel(new BorderLayout());
		listPanel2.setBorder(borderPlain);
		listPanel2.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Risk List", 0, 0, font));
		listPanel2.add(sp2, BorderLayout.CENTER);
		gbc = MakeGBC.makegbc(4, 2, 2, 3);
		gbc.ipady = 50;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.BOTH;
		add(listPanel2, gbc);
	}

}
