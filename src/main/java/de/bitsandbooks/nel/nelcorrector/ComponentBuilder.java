package de.bitsandbooks.nel.nelcorrector;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedListener;
import de.bitsandbooks.nel.nelcorrector.logic.BibliographyFormatDialog;
import de.bitsandbooks.nel.nelcorrector.logic.FindspotListCellRenderer;
import de.bitsandbooks.nel.nelcorrector.logic.IndexListCellRenderer;
import de.bitsandbooks.nel.nelcorrector.logic.IndexListHandler;
import de.bitsandbooks.nel.nelcorrector.logic.InputHandlerBuilder;
import de.bitsandbooks.nel.nelcorrector.logic.KeyListSearch;
import de.bitsandbooks.nel.nelcorrector.logic.MarkerBoxCellRenderer;
import de.bitsandbooks.nel.nelcorrector.logic.ModifyManager;
import de.bitsandbooks.nel.nelcorrector.logic.Statistics;
import de.bitsandbooks.nel.nelcorrector.logic.TextSelector;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultEntryDialogIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.RiskCalculatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.SingletonFactory;
import de.bitsandbooks.nel.nelcorrector.util.BoundsPopupMenuListener;
import de.bitsandbooks.nel.nelcorrector.util.PersistentCaret;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelcorrector.util.RomanArabic;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;


public class ComponentBuilder implements ResultLoadedListener {
	
//	---------------------- FIELDS ---------------------------------
	
	private MainWindow mainFrame;
	private ComponentMap components;
	private ResultListModel resultModel;
	
	
//	--------------------- CONSTRUCTOR -----------------------------
	
	public ComponentBuilder(MainWindow mainWindow) {
		initializeFields(mainWindow);
		buildMenuBar();
		builButtonGroups();
		setInputHandler();
		buildListenerSystem();
		setPersistentCaret();
		setResultDataModel();
		setListSelectionModes();
		setInitialState();
	}

	
//	---------------------- GETTER ---------------------------------

	public MainWindow getMainFrame() {
		return mainFrame;
	}
	
	
//	-------------------- LISTENER -------------------------------
	
	@Override
	public void resultLoaded(ResultLoadedEvent e) 
	{
		//set cell renderer
		JList<Result> jList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		JComboBox<Result> jComboBox = (JComboBox<Result>)components.get(ComponentMap.ComboBoxIndexMarker);
		jComboBox.addPopupMenuListener(new BoundsPopupMenuListener());
		SingletonFactory sf = SingletonFactory.getInstance();
		EntryTextCreatorIF textCreator = sf.getListTextCreator();
		ResultTypeHandlerIF resultHandler = sf.getResultTypeHandler();
		RiskCalculatorIF riskCalculator = sf.getRiskCalculator();
		ResultEntryDialogIF resultDialog = sf.getResultEntryDialogIF();
		riskCalculator.setFactorValues(resultModel.getMainResultIterator(true, null));
		IndexListCellRenderer listCellRenderer = new IndexListCellRenderer(textCreator, riskCalculator);
		FindspotListCellRenderer findspotCellRenderer = new FindspotListCellRenderer(riskCalculator);
		MarkerBoxCellRenderer markerBoxCellRenderer = new MarkerBoxCellRenderer(textCreator);
		jList.setCellRenderer(listCellRenderer);
		findspotList.setCellRenderer(findspotCellRenderer);
		jComboBox.setRenderer(markerBoxCellRenderer);
		//set text creator in list handler
		IndexListHandler listHandler = IndexListHandler.getInstance();
		listHandler.setResultTypeSingletons(textCreator, resultHandler);
		//set text creator in key finding listener
		KeyListSearch listSearch = KeyListSearch.getInstance(jList);
		listSearch.setTextCreatorAndModelAndResultTypeHandler(textCreator, resultHandler);
		//set text creator in modify manager
		ModifyManager mm = ModifyManager.getInstance();
		mm.setEntryTextCreatorAndDialogResultTypeHandler(textCreator, resultDialog, resultHandler);
		//add bibliography format set listeners
		BibliographyFormatDialog dialog = BibliographyFormatDialog.getInstance(mainFrame);
		Statistics stats = Statistics.getInstance();
		dialog.addBibFormatSetListener(stats);
		dialog.addBibFormatSetListener(findspotCellRenderer);
		dialog.addBibFormatSetListener(textCreator);
		//text area mouse listener
		JTextArea ta = (JTextArea)components.get(ComponentMap.TextAreaMain);
		ta.addMouseListener(TextSelector.getInstance());
		//set text number range
		JRadioButton textNumberRB = (JRadioButton)components.get(ComponentMap.RadioButtonTextNr);
		Text text = Text.getInstance();
		int pageMin = text.getTextPageMinimum();
		String textPageMinimum = pageMin < 0 ? RomanArabic.getRoman(Math.abs(pageMin)) : String.valueOf(pageMin);
		String textPageMaximum = String.valueOf(text.getTextPageMaximum());
		textNumberRB.setText(String.format("Text Number (%s-%s)", textPageMinimum, textPageMaximum));
		//select first index + mark text
		listHandler.reset();
		if (resultModel.getSize() > 0) {
			jList.setSelectedIndex(0);
			RXListUtilities.ensureListIndexVisibility(jList);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ListModel<Entry<TextRange, ResultInformation>> model = findspotList.getModel();
					if (model.getSize() > 0) {
						TextRange tr = model.getElementAt(0).getKey();
						TextSelector.getInstance().markTextFromIndexSelection(tr);
						findspotList.setSelectedIndex(0);
						RXListUtilities.ensureListIndexVisibility(findspotList);
					}
				}
			});
		}
		//update view
		mainFrame.repaint();	
	}
	
	
//	--------------------- METHODS -------------------------------

	private void initializeFields(MainWindow mainWindow) 
	{
		components = ComponentMap.getInstance();
		mainFrame = mainWindow;
	}
	
	
	private void builButtonGroups() 
	{
		//index group
		ButtonGroup groupIndex = new ButtonGroup();
		JRadioButton indexButton = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		JRadioButton perPageButton = (JRadioButton)components.get(ComponentMap.RadioButtonPerPage);
		groupIndex.add(indexButton);
		groupIndex.add(perPageButton);
	}
	
	
	private void buildMenuBar() 
	{
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu options = new JMenu("Options");
		JMenu search = new JMenu("Search");
		JMenu exportoptions = (JMenu)components.get(ComponentMap.MenuItemExportOptions);
		JMenuItem save = (JMenuItem)components.get(ComponentMap.MenuItemSave);
		JMenuItem saveAs = (JMenuItem)components.get(ComponentMap.MenuItemSaveAs);
		JMenuItem load = (JMenuItem)components.get(ComponentMap.MenuItemLoad);
		JMenuItem print = (JMenuItem)components.get(ComponentMap.MenuItemExport);
		JMenuItem setmergefile = (JMenuItem)components.get(ComponentMap.MenuItemMergeFile);
		JCheckBoxMenuItem ixmlexport = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemIXMLExport);
		JMenuItem exportpages = (JMenuItem)components.get(ComponentMap.MenuItemExportPageNumbers);
		JMenuItem exit = (JMenuItem)components.get(ComponentMap.MenuItemExit);
		JMenuItem cut = (JMenuItem)components.get(ComponentMap.MenuItemCut);
		JMenuItem paste = (JMenuItem)components.get(ComponentMap.MenuItemPaste);
		JMenuItem delete = (JMenuItem)components.get(ComponentMap.MenuItemDelete);
		JMenuItem risk = (JMenuItem)components.get(ComponentMap.MenuItemRisk);
		JMenuItem bibFormat = (JMenuItem)components.get(ComponentMap.MenuItemBibliographyFormat);
		JCheckBoxMenuItem merge = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemMerge);
		JCheckBoxMenuItem setPerPagePrint = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemPrintOptionsPerPage);
		JMenuItem showstats = (JMenuItem)components.get(ComponentMap.MenuItemShowStats);
		JMenuItem showshortcuts = (JMenuItem)components.get(ComponentMap.MenuItemShowShortCuts);
		JMenuItem searchAll = (JMenuItem)components.get(ComponentMap.MenuItemSearchAll);
		JMenuItem searchSingle = (JMenuItem)components.get(ComponentMap.MenuItemSearchSingle);
		file.add(save);
		file.add(saveAs);
		file.add(load);
		file.addSeparator();
		file.add(print);
		file.add(exportoptions);
		exportoptions.add(setPerPagePrint);
		exportoptions.add(merge);
		exportoptions.add(ixmlexport);
		exportoptions.add(setmergefile);
		exportoptions.add(exportpages);
		file.addSeparator();
		file.add(exit);
		edit.add(cut);
		edit.add(paste);
		edit.addSeparator();
		edit.add(delete);
		edit.addSeparator();
		edit.add(risk);	
		options.add(bibFormat);
		options.addSeparator();
		options.add(showstats);
		options.add(showshortcuts);
		search.add(searchAll);
		search.add(searchSingle);
		bar.add(file);
		bar.add(edit);
		bar.add(options);
		bar.add(search);
		mainFrame.setJMenuBar(bar);
	}
	
	
	private void setInputHandler() {
		InputHandlerBuilder.initializeInputHandler(components);	
	}
	
	
	private void buildListenerSystem() {
		ComponentListenerBuilder.buildListenerSystem(this, components, mainFrame);	
	}
	
	
	private void setPersistentCaret() {
		JTextArea textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		textArea.setCaret(new PersistentCaret());
	}
	
	
	private void setResultDataModel() {
		JList<Result> jList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		resultModel = ResultListModel.getInstance();
		jList.setModel(resultModel);
	}
	
	
	private void setListSelectionModes() {
		JList<Result> jListIndex = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> jListEntrys = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		JList<String> jListRisk1 = (JList<String>)components.get(ComponentMap.ListIndexRiskList);
		JList<String> jListRisk2 = (JList<String>)components.get(ComponentMap.ListFindspotRiskList);
		//set selection mode
		jListIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListEntrys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListEntrys.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jListEntrys.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//set orientation of entry list
		jListEntrys.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jListEntrys.setVisibleRowCount(-1);
	}
	
	
	private void setInitialState() {
		JRadioButton rb1 = (JRadioButton)components.get(ComponentMap.RadioButtonTextNr);
		rb1.setSelected(true);
		JRadioButton rb2 = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		rb2.doClick();
		JCheckBox cb = (JCheckBox)components.get(ComponentMap.CheckBoxSelectText);
		cb.setSelected(true);
		JCheckBox cb2 = (JCheckBox)components.get(ComponentMap.CheckBoxSelectIndex);
		cb2.setSelected(true);
		JCheckBox cb3 = (JCheckBox)components.get(ComponentMap.CheckBoxDeleteIndexMarker);
		cb3.setSelected(true);
	}
	
}
