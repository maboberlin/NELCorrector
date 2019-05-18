package de.bitsandbooks.nel.nelcorrector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.bitsandbooks.nel.nelcorrector.panels.ModifyPanel;
import de.bitsandbooks.nel.nelcorrector.panels.FindspotListPanel;
import de.bitsandbooks.nel.nelcorrector.panels.IndexPanel;
import de.bitsandbooks.nel.nelcorrector.panels.PagesSelectPanel;
import de.bitsandbooks.nel.nelcorrector.panels.ResultPanel;
import de.bitsandbooks.nel.nelcorrector.panels.SearchPanel;
import de.bitsandbooks.nel.nelcorrector.panels.SpanMessagePanel;
import de.bitsandbooks.nel.nelcorrector.panels.TextAreaPanel;
import de.bitsandbooks.nel.nelcorrector.util.MakeGBC;

public class MainWindowPanel extends JPanel {
	
//	---------------------- ATTRIBUTES -----------------------------
	
	private static Border border = new EmptyBorder(new Insets(5, 5, 15, 5));
	
	
//	---------------------- CONSTRUCTOR ----------------------------
	
	public MainWindowPanel(ComponentMap componentMap) 
	{
		setLayout(new GridBagLayout());
		JPanel eastPanel = new JPanel(new GridBagLayout());
		JPanel westPanel = new JPanel(new GridBagLayout());
		//instantiate panels
		JPanel pagesSelectPanel = new PagesSelectPanel(componentMap);
		JPanel searchPanel = new SearchPanel(componentMap);
		JPanel createEntryPanel = new ModifyPanel(componentMap);
		JPanel textAreaPanel = new TextAreaPanel(componentMap);
		JPanel entrySpanWarningPanel = new SpanMessagePanel(componentMap);
		JPanel resultPanel = new ResultPanel(componentMap);
		JPanel indexPanel = new IndexPanel(componentMap);
		JPanel findspotListPanel = new FindspotListPanel(componentMap);
		//order panels
		JPanel eastPanelComponents = new JPanel(new GridBagLayout());
		JPanel westPanelComponents = new JPanel(new GridBagLayout());
		
		//set top west panel
		JPanel topWestPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc;
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //pages select
		gbc.weightx = 0.25f;
		gbc.fill = GridBagConstraints.BOTH;
		topWestPanel.add(pagesSelectPanel, gbc);
		gbc = MakeGBC.makegbc(1, 0, 1, 1); //search panel
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		topWestPanel.add(searchPanel, gbc);
		//set west panel
		gbc = MakeGBC.makegbc(0, 0, 1, 1);
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		westPanelComponents.add(topWestPanel, gbc);
		gbc = MakeGBC.makegbc(0, 1, 1, 1);
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		westPanelComponents.add(textAreaPanel, gbc);
		gbc = MakeGBC.makegbc(0, 2, 1, 1);
		gbc.weightx = 1.0f;
		westPanelComponents.add(entrySpanWarningPanel, gbc);
		
		//set east panel
		JPanel eastTopPanel = new JPanel(new GridBagLayout());
		JPanel eastBottomPanel = new JPanel(new GridBagLayout());
		//set east top panel
		gbc = MakeGBC.makegbc(0, 0, 2, 1); //result panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.05f;
		eastTopPanel.add(resultPanel, gbc);
		gbc = MakeGBC.makegbc(2, 0, 1, 1); //create entry panel
		gbc.weightx = 0.5f;
		gbc.fill = GridBagConstraints.BOTH;
		eastTopPanel.add(createEntryPanel, gbc);
		gbc = MakeGBC.makegbc(0, 1, 3, 4); //index panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.5f;
		eastTopPanel.add(indexPanel, gbc);
		gbc = MakeGBC.makegbc(0, 1, 3, 4); //whole east top panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		//set east bottom panel
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //findspot list panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		eastBottomPanel.add(findspotListPanel, gbc);
		//set east splitter
		JSplitPane eastSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, eastTopPanel, eastBottomPanel);
		eastSplitPane.setOneTouchExpandable(true);
		eastSplitPane.setContinuousLayout(true);
		eastSplitPane.setDividerLocation(0.95f);
		eastSplitPane.setResizeWeight(0.95f);
		eastSplitPane.setContinuousLayout(true);
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //east panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		eastPanelComponents.add(eastSplitPane, gbc);
		
		//set panels
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //west panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.25f;
		gbc.weighty = 1.0f;
		gbc.anchor = GridBagConstraints.LINE_START;
		westPanel.add(westPanelComponents, gbc);
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //east panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.anchor = GridBagConstraints.LINE_END;
		eastPanel.add(eastPanelComponents, gbc);
		//set east and west scroll panes
		JScrollPane scrollPaneWest = new JScrollPane();
		scrollPaneWest.setViewportView(westPanel);
		scrollPaneWest.getVerticalScrollBar().setUnitIncrement(16);
		scrollPaneWest.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneWest.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollPaneEast = new JScrollPane();
		scrollPaneEast.setViewportView(eastPanel);
		scrollPaneEast.getVerticalScrollBar().setUnitIncrement(16);
		scrollPaneEast.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneEast.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//set main splitter
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneWest, scrollPaneEast);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setDividerLocation(0.3f);
		mainSplitPane.setResizeWeight(0.3f);
		mainSplitPane.setContinuousLayout(true);
		gbc = MakeGBC.makegbc(0, 0, 1, 1); //east panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		add(mainSplitPane, gbc);
	}
		
	

}
