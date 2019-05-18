package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class FindspotListPanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 10, 10));
	private Dimension resultListSize = new Dimension(500, 500);
	private Dimension riskListPreferredSize = new Dimension(200, 60);
	private Dimension riskListMinSize = new Dimension(200, 100);
	
	public FindspotListPanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new GridBagLayout());
		setBorder(borderPlain);
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Findspot List", 0, 0, font));
		GridBagConstraints gbc;
		//Entry List
		JList<Integer> jList = (JList<Integer>)components.get(ComponentMap.ListFindspotList);
		jList.setFont(font);
		JScrollPane sp1 = (JScrollPane)components.get(ComponentMap.ScrollPaneFindspot);
		sp1.setViewportView(jList);
		sp1.setWheelScrollingEnabled(true);
		sp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp1.setPreferredSize(sp1.getPreferredSize());
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.setBorder(borderPlain);
		listPanel.add(sp1, BorderLayout.CENTER);
		gbc = MakeGBC.makegbc(0, 0, 4, 5);
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		add(listPanel, gbc);
		//Entry Risk List
		JList<String> jList2 = (JList<String>)components.get(ComponentMap.ListFindspotRiskList);
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
		gbc = MakeGBC.makegbc(4, 0, 2, 3);
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.BOTH;
		add(listPanel2, gbc);
	}

}
