package de.bitsandbooks.nel.nelcorrector;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ComponentMap extends HashMap<String, JComponent>{
	
//	----------------------- COMPONENT NAMES ---------------------------------------
	
	//Menu Bar
	public static final String MenuItemSave = "menuitemsave";
	public static final String MenuItemSaveAs = "menuitemsaveas";
	public static final String MenuItemLoad = "menuitemload";
	public static final String MenuItemExport = "menuitemprint";
	public static final String MenuItemExportOptions = "menuitemprintoptions";
	public static final String MenuItemMerge = "menuitemprintoptionssetfile";
	public static final String MenuItemMergeFile = "menuitemmergefile";
	public static final String MenuItemIXMLExport = "menuitemixmlfile";
	public static final String MenuItemPrintOptionsPerPage= "menuitemprintoptionsprintperpage";
	public static final String MenuItemExportPageNumbers = "exportpagenumbers";
	public static final String MenuItemExit = "menuitemexit";
	public static final String MenuItemDelete = "menuitemdelete";
	public static final String MenuItemCut = "menuitemcut";
	public static final String MenuItemPaste = "menuitempaste";
	public static final String MenuItemRisk = "menuitemrisk";
	public static final String MenuItemBibliographyFormat = "menuitembibliographieformat";
	public static final String MenuItemShowStats = "menuitemshowstats";
	public static final String MenuItemShowShortCuts = "menuitemshowshortcuts";
	public static final String MenuItemSearchAll = "searchall";
	public static final String MenuItemSearchSingle = "searchsingle";
	//Labels
	public static final String LabelNumberOfPages = "numberofpages";
	public static final String LabelEntrySpansWarning = "entryspanswarning";
	//Buttons
	public static final String ButtonPageForeward = "pageforward";
	public static final String ButtonPageBackward = "pagebackward";
	public static final String ButtonSearchForeward = "searchforward";
	public static final String ButtonSearchBackward = "searchbackward";
	public static final String ButtonCreateEntry = "createEntry";
	public static final String ButtonEditEntry = "editEntry";
	public static final String ButtonCreateIndexMarker = "createIndexMarker";
	public static final String ButtonSelectIndexMarker = "chooseIndexMarker";
	//RadioButtons
	public static final String RadioButtonTextNr = "textNr";
	public static final String RadioButtonDocNr = "docNr";
	public static final String RadioButtonIndex = "index";
	public static final String RadioButtonPerPage = "perPage";
	//CheckBox 
	public static final String CheckBoxRegex = "regex";
	public static final String CheckBoxCaseInsensitiv = "caseInsensitiv";
	public static final String CheckBoxSelectText = "selectText";
	public static final String CheckBoxSelectIndex = "selectIndex";
	public static final String CheckBoxDeleteIndexMarker = "deleteIndexMarker";
	//TextField
	public static final String TextFieldPageNr = "pageNr";	
	public static final String TextFieldSearch = "search";
	public static final String TextFieldResultField = "resultField";
	//ComboBox
	public static final String ComboBoxIndexMarker = "indexMarker";
	//List
	public static final String ListIndexList = "indexList";
	public static final String ListIndexRiskList = "indexRiskList";
	public static final String ListFindspotList = "entryList";
	public static final String ListFindspotRiskList = "entryRiskList";
	//ScrollPane
	public static final String ScrollPaneIndex = "scrollPaneIndex";
	public static final String ScrollPaneFindspot = "scrollPaneFindspot";
	//TextArea
	public static final String TextAreaMain = "textArea";
	
	
//	----------------------- INSTANCE AND CONSTRUCTOR ------------------------------
	
	private static ComponentMap instance;
	
	private ComponentMap() {
		initializeComponents();
	}

	public static ComponentMap getInstance() {
		if (instance == null)
			instance = new ComponentMap();
		return instance;
	}
	
	
//	--------------------------- INITIALIZE ---------------------------------------
	
	private void initializeComponents() {
		//Menu Bar
		put(MenuItemSave, new JMenuItem("Save"));
		put(MenuItemSaveAs, new JMenuItem("Save as"));
		put(MenuItemLoad, new JMenuItem("Load"));
		put(MenuItemExport, new JMenuItem("Export"));
		put(MenuItemExportOptions, new JMenu("Export Options"));
		put(MenuItemMerge, new JCheckBoxMenuItem("Merge"));
		put(MenuItemIXMLExport, new JCheckBoxMenuItem("IXML Export"));
		put(MenuItemMergeFile, new JMenuItem("Choose File for Merging"));
		put(MenuItemPrintOptionsPerPage, new JCheckBoxMenuItem("Print Per Page Index"));
		put(MenuItemExportPageNumbers, new JMenuItem("Select Page Numbers"));
		put(MenuItemExit, new JMenuItem("Exit"));
		put(MenuItemDelete, new JMenuItem("Delete"));
		put(MenuItemCut, new JMenuItem("Cut"));
		put(MenuItemPaste, new JMenuItem("Paste"));
		put(MenuItemRisk, new JMenuItem("Risk Options"));
		put(MenuItemBibliographyFormat, new JMenuItem("Bibliography Format Settings"));
		put(MenuItemShowStats, new JMenuItem("Show Statistics"));
		put(MenuItemShowShortCuts, new JMenuItem("Show Short-Cut List"));
		put(MenuItemSearchAll, new JMenuItem("All Index Entries"));
		put(MenuItemSearchSingle, new JMenuItem("Selected Index Entry"));
		//JLabel
		put(LabelNumberOfPages, new JLabel());
		put(LabelEntrySpansWarning, new JLabel());
		//JButton
		put(ButtonPageForeward, new JButton());
		put(ButtonPageBackward, new JButton());
		put(ButtonSearchForeward, new JButton());
		put(ButtonSearchBackward, new JButton());
		put(ButtonCreateEntry, new JButton("Create Entry"));
		put(ButtonEditEntry, new JButton("Edit Entry"));
		put(ButtonCreateIndexMarker, new JButton("Set Marker"));
		put(ButtonSelectIndexMarker, new JButton("Select Marker"));
		//JRadioButton
		put(RadioButtonTextNr, new JRadioButton("Text Number"));
		put(RadioButtonDocNr, new JRadioButton("Doc Number"));
		put(RadioButtonIndex, new JRadioButton("Index"));
		put(RadioButtonPerPage, new JRadioButton("Results per page"));
		//JCheckBox
		put(CheckBoxRegex, new JCheckBox("Regex"));
		put(CheckBoxCaseInsensitiv, new JCheckBox("Case Insensitiv"));
		put(CheckBoxSelectText, new JCheckBox("Select Text"));
		put(CheckBoxSelectIndex, new JCheckBox("Select Index"));
		put(CheckBoxDeleteIndexMarker, new JCheckBox("Delete Marker"));
		//JTextField
		put(TextFieldPageNr, new JTextField());
		put(TextFieldSearch, new JTextField());
		put(TextFieldResultField, new JTextField());
		//JComboBox
		put(ComboBoxIndexMarker, new JComboBox<Result>());
		//JList
		put(ListIndexList, new JList<Result>());
		put(ListFindspotList, new JList<Map.Entry<TextRange, ResultInformation>>());
		put(ListIndexRiskList, new JList<String>());
		put(ListFindspotRiskList, new JList<String>());
		//JScrollPane
		put(ScrollPaneIndex, new JScrollPane());
		put(ScrollPaneFindspot, new JScrollPane());
		//JTextArea
		put(TextAreaMain, new JTextArea());
	}

}
