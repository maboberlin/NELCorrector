package de.bitsandbooks.nel.nelcorrector;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.text.Highlighter;

import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.logic.BibliographyFormatDialog;
import de.bitsandbooks.nel.nelcorrector.logic.FindspotListHandler;
import de.bitsandbooks.nel.nelcorrector.logic.IOManager;
import de.bitsandbooks.nel.nelcorrector.logic.IndexListHandler;
import de.bitsandbooks.nel.nelcorrector.logic.IndexModeSelection;
import de.bitsandbooks.nel.nelcorrector.logic.IndexTransferHandler;
import de.bitsandbooks.nel.nelcorrector.logic.KeyListSearch;
import de.bitsandbooks.nel.nelcorrector.logic.MarkerBoxHandler;
import de.bitsandbooks.nel.nelcorrector.logic.ModifyCommandForwarder;
import de.bitsandbooks.nel.nelcorrector.logic.ModifyManager;
import de.bitsandbooks.nel.nelcorrector.logic.PageHandler;
import de.bitsandbooks.nel.nelcorrector.logic.PageSelectorHandler;
import de.bitsandbooks.nel.nelcorrector.logic.SearchExecutor;
import de.bitsandbooks.nel.nelcorrector.logic.Statistics;
import de.bitsandbooks.nel.nelcorrector.logic.TextSelector;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.SingletonFactory;
import de.bitsandbooks.nel.nelcorrector.util.RXListUtilities;
import de.bitsandbooks.nel.nelcorrector.util.TextUtil;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class ComponentListenerBuilder {
	
//	----------------------------- ACTION COMMANDS ---------------------------------
	
	public static final String IndexCommand = "index";
	public static final String PerPageCommand = "perpage";
	
	
//	------------------------- SHORT CUT MESSAGE -----------------------------------
	
	public static final String shortCuts =
				"       \t up    \t: move index list selection 1 down" + System.lineSeparator()
			+ 	"       \t down  \t: move index list selection 1 up" + System.lineSeparator()
			+ 	"       \t left  \t: move findspot list selection 1 down" + System.lineSeparator()
			+ 	"       \t right \t: move findspot list selection 1 up" + System.lineSeparator()
			+ 	"<Alt>  \t up    \t: move findspot list selection 1 row up" + System.lineSeparator()
			+ 	"<Alt>  \t down  \t: move findspot list selection 1 row down" + System.lineSeparator()
			+ 	"<Alt>  \t left  \t: move findspot list selection 1 down" + System.lineSeparator()
			+ 	"<Alt>  \t right \t: move findspot list selection 1 up" + System.lineSeparator()
			+ 	"       \t del   \t: delete result entry (findspot or index)" + System.lineSeparator()
			+ 	"<Ctrl> \t del   \t: delete index entry" + System.lineSeparator()
			+ 	"<Ctrl> \t up    \t: change page selection 1 down" + System.lineSeparator()
			+ 	"<Ctrl> \t down  \t: change page selection 1 up" + System.lineSeparator()
			+ 	"<Ctrl> \t L     \t: load results" + System.lineSeparator()
			+ 	"<Ctrl> \t S     \t: save results (to selected save file)" + System.lineSeparator()
			+ 	"<Ctrl> \t X     \t: cut result entry (index or findspot)" + System.lineSeparator()
			+ 	"<Ctrl> \t V     \t: paste result entry (index or findspot to index list)" + System.lineSeparator()
			+ 	"<Ctrl> \t R     \t: create result entry (index if no text is selected - findspot if text is selected)" + System.lineSeparator()
			+ 	"<Ctrl> \t E     \t: edit result entry (index if no text is selected - findspot if text is selected)" + System.lineSeparator()
			+ 	"<Ctrl> \t I     \t: set index list mode to index mode" + System.lineSeparator()
			+ 	"<Ctrl> \t P     \t: set index list mode to per page mode" + System.lineSeparator()
			+ 	"<Ctrl> \t D     \t: clear text selection" + System.lineSeparator()
			+ 	"<Ctrl> \t Q     \t: focus index list" + System.lineSeparator()
			+ 	"       \t a-z   \t: only if index list is focused: selects alphabetically closest entry in index" + System.lineSeparator()
			+ 	"<Alt>  \t a-z   \t: only if index list is focused: scrolls to alphabetically closest entry in index" + System.lineSeparator();
	
	
//	--------------------------------- FIELDS --------------------------------------
	
	private static ComponentBuilder componentBuilder;
	private static ComponentMap components;
	private static MainWindow mainWindow;
	private static ResultListModel results;
	private static DefaultComboBoxModel<Result> markerModel;
	private static Text text;
	private static PageSelectorHandler pageSelectorHandler;
	private static SearchExecutor searchExecutor;
	private static IOManager ioManager;
	private static GeneralProperties properties;
	
	
//	-------------------------------- MAIN METHOD ----------------------------------
	
	public static void buildListenerSystem(ComponentBuilder componentBuilder, ComponentMap components, MainWindow mainFrame) 
	{
		ComponentListenerBuilder.componentBuilder = componentBuilder;
		ComponentListenerBuilder.components = components;
		ComponentListenerBuilder.mainWindow =  mainFrame;
		results = ResultListModel.getInstance();
		text = Text.getInstance();
		pageSelectorHandler = PageSelectorHandler.getInstance();
		searchExecutor = SearchExecutor.getInstance();
		ioManager = IOManager.getInstance(componentBuilder.getMainFrame());
		properties = GeneralProperties.getInstance();
		//build listener system
		rebindKeyBindings();
		disableKeyBindings();
		buildIOLogic();
		buildExportResultsLogic();
		buildTextListenerSystem();
		setMaxPageLabelResizing();
		buildChangePageLogic();
		buildPageSelectionModeLogic();
		buildSearchLogic();
		buildRegexCaseLogic();
		buildIndexListModeLogic();
		setListHandler();
		buildIndexMarkerLogic();
		setFocusIndexListShortCut();
		buildResetTextSelectionLogic();
		addTextLoadedListener();
		setBibliographyFormatDialog();
		buildSelectExportPagesLogic();
		buildShowStatsItem();
		setShowShortCuts();
		buildModifyCommandSystem();
		builDnDAndCPSystem();
		setMouseDraggedListeners();
		setExitListener();	
		
//		test();	
	}

	

//	---------------------------- MINOR METHODS ------------------------------------

	private static void buildIOLogic() 
	{
		JMenuItem load = (JMenuItem)components.get(ComponentMap.MenuItemLoad);
		JMenuItem save = (JMenuItem)components.get(ComponentMap.MenuItemSave);
		JMenuItem saveAs = (JMenuItem)components.get(ComponentMap.MenuItemSaveAs);
		//load action
		Action loadAction = new AbstractAction("Load      ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ioManager.loadData();
			}
		};
		load.setAction(loadAction);
		//save action
		Action saveAction = new AbstractAction("Save      ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ioManager.saveData(true);
			}
		};
		save.setAction(saveAction);
		//save as action
		Action saveAsAction = new AbstractAction("Save As   ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ioManager.saveData(false);
			}
		};
		saveAs.setAction(saveAsAction);
		//short cuts
		KeyStroke keyLoad = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK);
		loadAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		loadAction.putValue(Action.ACCELERATOR_KEY, keyLoad);
		load.getActionMap().put("performLoad", loadAction);
		load.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyLoad, "performLoad");
		KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
		saveAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveAction.putValue(Action.ACCELERATOR_KEY, keySave);
		load.getActionMap().put("performSave", saveAction);
		load.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "performSave");		
	}
	
	
	private static void buildExportResultsLogic() 
	{
		JMenuItem export = (JMenuItem)components.get(ComponentMap.MenuItemExport);
		JMenuItem setMergeFile = (JMenuItem)components.get(ComponentMap.MenuItemMergeFile);
		JCheckBoxMenuItem setPerPagePrint = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemPrintOptionsPerPage);
		JCheckBoxMenuItem merge = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemMerge);
		JCheckBoxMenuItem ixml = (JCheckBoxMenuItem)components.get(ComponentMap.MenuItemIXMLExport);
		//export index action
		Action printAction = new AbstractAction("Export    ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ioManager.exportResults(!setPerPagePrint.isSelected(), merge.isSelected(), ixml.isSelected());
			}
		};
		export.setAction(printAction);
		//set merge file
		Action setMergeFileAction = new AbstractAction("Choose File for Merging ") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ioManager.setMergeFile();
				merge.setSelected(true);
			}
		};
		setMergeFile.setAction(setMergeFileAction);
	}
	
	
	private static void buildTextListenerSystem() 
	{
		PageHandler pageHandler = PageHandler.getInstance();
		PageSelectorHandler pageSelectorHandler = PageSelectorHandler.getInstance();
		TextSelector textSelector = TextSelector.getInstance();
		text.addPageChangeListener(pageHandler);
		text.addPageChangeListener(pageSelectorHandler);
		text.addPageChangeListener(textSelector);
	}
	
	
	private static void setMaxPageLabelResizing() 
	{
		JLabel label = (JLabel)components.get(ComponentMap.LabelNumberOfPages);
		label.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("text")) {
					JLabel label = (JLabel)evt.getSource();
					Graphics g = label.getGraphics();
					FontMetrics fm = g.getFontMetrics();
					label.setMinimumSize(new Dimension(fm.stringWidth(label.getText()), fm.getHeight()));
				}
			}
		});		
	}
	
	
	private static void buildChangePageLogic() 
	{
		JButton backwardButton = (JButton)components.get(ComponentMap.ButtonPageBackward);
		JButton forwardButton = (JButton)components.get(ComponentMap.ButtonPageForeward);
		JTextField textField = (JTextField)components.get(ComponentMap.TextFieldPageNr);
		Icon arrowUp = properties.getArrowUpIcon();
		Icon arrowDown = properties.getArrowDownIcon();
		//backward action
		AbstractAction backwardAction = new AbstractAction(null, arrowUp) {
			@Override
			public void actionPerformed(ActionEvent e) {
				text.decreaseSelectedPage();
			}
		};
		backwardAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_UP);
		backwardAction.putValue(Action.SHORT_DESCRIPTION, "Short-Cut: Ctrl-UP");
		backwardButton.setAction(backwardAction);
		//forward action
		AbstractAction forwardAction = new AbstractAction(null, arrowDown) {
			@Override
			public void actionPerformed(ActionEvent e) {
				text.increaseSelectedPage();
			}
		};
		forwardAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_DOWN);
		forwardAction.putValue(Action.SHORT_DESCRIPTION, "Short-Cut: Ctrl-DOWN");
		forwardButton.setAction(forwardAction);
		//select page
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pageSelectorHandler.selectPage();
			}
		});
		//short cuts
		KeyStroke keyPageBackward = KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK);
		backwardButton.getActionMap().put("performBack", backwardAction);
		backwardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyPageBackward, "performBack");
		KeyStroke keyPageForward = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK);
		forwardButton.getActionMap().put("performForward", forwardAction);
		forwardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyPageForward, "performForward");
	}
	
	
	private static void buildPageSelectionModeLogic() 
	{
		//build button group
		ButtonGroup groupPages = new ButtonGroup();
		JRadioButton textNrButton = (JRadioButton)components.get(ComponentMap.RadioButtonTextNr);
		JRadioButton docNrButton = (JRadioButton)components.get(ComponentMap.RadioButtonDocNr);
		groupPages.add(textNrButton);
		groupPages.add(docNrButton);
		//set logic
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pageSelectorHandler.switchSelectionMode();
			}
		};
		textNrButton.addActionListener(action);
		docNrButton.addActionListener(action);
	}
	
	
	private static void buildSearchLogic() 
	{
		// normal text search
		JButton backwardButton = (JButton)components.get(ComponentMap.ButtonSearchBackward);
		JButton forwardButton = (JButton)components.get(ComponentMap.ButtonSearchForeward);
		JTextField textField = (JTextField)components.get(ComponentMap.TextFieldSearch);
		JTextArea textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		Icon arrowUp = properties.getArrowUpIcon();
		Icon arrowDown = properties.getArrowDownIcon();
		//backward action
		AbstractAction backward = new AbstractAction(null, arrowUp) {	
			@Override
			public void actionPerformed(ActionEvent e) {
				searchExecutor.searchBackward();	
			}
		};
		backwardButton.setAction(backward);
		//forward action
		AbstractAction forward = new AbstractAction(null, arrowDown) {	
			@Override
			public void actionPerformed(ActionEvent e) {
				searchExecutor.searchForward();	
			}
		};
		forwardButton.setAction(forward);
		textField.setAction(forward);
		
		// index entry search
		JMenuItem allSearch = (JMenuItem)components.get(ComponentMap.MenuItemSearchAll);
		JMenuItem singleSearch = (JMenuItem)components.get(ComponentMap.MenuItemSearchSingle);
		allSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchExecutor.doEntrySearch(true, mainWindow);	
			}
		});
		singleSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchExecutor.doEntrySearch(false, mainWindow);	
			}
		});
	}


	private static void buildRegexCaseLogic() 
	{
		JCheckBox regex = (JCheckBox)components.get(ComponentMap.CheckBoxRegex);
		JCheckBox sensitiv = (JCheckBox)components.get(ComponentMap.CheckBoxCaseInsensitiv);
		regex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox regex = (JCheckBox)e.getSource();
				if (regex.isSelected())
					sensitiv.setEnabled(false);
				else
					sensitiv.setEnabled(true);				
			}
		});
	}
	
	
	private static void buildIndexListModeLogic() 
	{
		IndexModeSelection indexModeHandler = new IndexModeSelection();
		JRadioButton indexRB = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		JRadioButton perPageRB = (JRadioButton)components.get(ComponentMap.RadioButtonPerPage);
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> textEntryList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		//set up listener
		indexRB.setActionCommand(IndexCommand);
		perPageRB.setActionCommand(PerPageCommand);
		indexRB.addActionListener(indexModeHandler);
		perPageRB.addActionListener(indexModeHandler);	
		//short cuts
		AbstractAction selectIndex = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				indexRB.doClick();
			}
		};
		AbstractAction selectPerPage = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				perPageRB.doClick();
			}
		};
		KeyStroke keyCtrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
		indexRB.getActionMap().put("selectIndex", selectIndex);
		indexRB.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyCtrlI, "selectIndex");
		indexRB.setToolTipText("Short-Cut: Ctrl-I");
		KeyStroke keyCtrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
		perPageRB.getActionMap().put("selectPerPage", selectPerPage);
		perPageRB.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyCtrlP, "selectPerPage");
		perPageRB.setToolTipText("Short-Cut: Ctrl-P");
	}
	
	
	private static void setListHandler() 
	{
		//set list handler and listener
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		IndexListHandler indexListHandler = IndexListHandler.getInstance();
		indexList.addListSelectionListener(indexListHandler);
		indexList.addMouseListener(indexListHandler);
		results.addListDataListener(indexListHandler);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		FindspotListHandler findspotListHandler = FindspotListHandler.getInstance();
		findspotList.addListSelectionListener(findspotListHandler);

		//set list-by-key finding
		JTextArea textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		JScrollPane sp = (JScrollPane)components.get(ComponentMap.ScrollPaneIndex);
		KeyListSearch indexListKeySearch = KeyListSearch.getInstance(indexList);
		indexList.addKeyListener(indexListKeySearch);
		findspotList.addKeyListener(indexListKeySearch);
		textArea.addKeyListener(indexListKeySearch);
		sp.addKeyListener(indexListKeySearch);

		//key short cuts for selection (reset arrow key bindings to global)
		//->findspot list (actions are initialized so that cursor moves over list end border (left and right end))
		AbstractAction findSpotListUp = new AbstractAction() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (findspotList.getSelectedIndex() >= 1 && findspotList.getSelectedIndex() < findspotList.getModel().getSize())
					findspotList.setSelectedIndex(findspotList.getSelectedIndex() - 1);
				RXListUtilities.ensureListIndexVisibility(findspotList);
				findspotList.requestFocusInWindow();
			}
		};
		AbstractAction findSpotListDown = new AbstractAction() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (findspotList.getSelectedIndex() >= 0 && findspotList.getSelectedIndex() < findspotList.getModel().getSize() - 1)
					findspotList.setSelectedIndex(findspotList.getSelectedIndex() + 1);
				RXListUtilities.ensureListIndexVisibility(findspotList);
				findspotList.requestFocusInWindow();
			}
		};
		//->findspot list up and down actions (triggered by (alt-)left and (alt-)right keys)
		KeyStroke keyAltLeft = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK);
		KeyStroke keyAltRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK);	
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "findSpotListUp");
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyAltLeft, "findSpotListUp");
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "findSpotListDown");
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyAltRight, "findSpotListDown");
		findspotList.getActionMap().put("findSpotListUp", findSpotListUp);
		findspotList.getActionMap().put("findSpotListDown", findSpotListDown);
	}
	
	
	private static void buildIndexMarkerLogic() {
		JComboBox<Result> markerBox = (JComboBox<Result>)components.get(ComponentMap.ComboBoxIndexMarker);
		markerModel = new DefaultComboBoxModel<Result>();
		markerBox.setModel(markerModel);
		JCheckBox deleteMarker = (JCheckBox)components.get(ComponentMap.CheckBoxDeleteIndexMarker);
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JRadioButton indexMode = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		JButton createMarker = (JButton)components.get(ComponentMap.ButtonCreateIndexMarker);
		JButton selectMarker = (JButton)components.get(ComponentMap.ButtonSelectIndexMarker);
		MarkerBoxHandler handler = new MarkerBoxHandler(markerBox, deleteMarker, indexList, indexMode);
		createMarker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.createMarker();
			}
		});
		selectMarker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.selectEntry();
			}
		});
		markerBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.selectEntry();
			}
		});
	}
	
	
	private static void setFocusIndexListShortCut() 
	{
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		Action focusIndexList = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						indexList.requestFocusInWindow();
					} 
				});
			}
		};
		KeyStroke keySpace = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK);
		indexList.getActionMap().put("focusIndexListABC", focusIndexList);
		indexList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySpace, "focusIndexListABC");
	}
	
	
	private static void buildResetTextSelectionLogic() 
	{
		//by key
		JTextArea ta = (JTextArea)components.get(ComponentMap.TextAreaMain);
		KeyStroke qKeyCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK);
		ta.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(qKeyCtrl, "clearSelection");
		ta.getActionMap().put("clearSelection", new AbstractAction() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				TextSelector selector = TextSelector.getInstance();
				selector.changeTextSelection();
			}
		});
	}
	
	
	private static void addTextLoadedListener() 
	{
		ioManager.addResultLoadedListener(componentBuilder);
		BibliographyFormatDialog formatDialog = BibliographyFormatDialog.getInstance(mainWindow);
		ioManager.addResultLoadedListener(formatDialog);
	}
	
	
	private static void setBibliographyFormatDialog() 
	{
		JMenuItem menuItem = (JMenuItem)components.get(ComponentMap.MenuItemBibliographyFormat);
		BibliographyFormatDialog dialog = BibliographyFormatDialog.getInstance(mainWindow);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.showDialog();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						mainWindow.revalidate();
						mainWindow.repaint();
					}
				});
			}
		});
	}
	
	

	private static void buildSelectExportPagesLogic() 
	{
		JMenuItem exportPages = (JMenuItem)components.get(ComponentMap.MenuItemExportPageNumbers);
		exportPages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberString = (String)JOptionPane.showInputDialog(mainWindow, "Choose range of page numbers to export:\n(for example: I-X,1,2-5)", "Select export pages", JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (numberString != null && numberString.length() > 0) {
					try {
						int[] numbers = TextUtil.extractPageRanges(numberString);
						if (numbers != null && numbers.length > 0) {
							IOManager io = IOManager.getInstance(mainWindow);
							io.setExportPages(numbers);
						}
					}
					catch (NumberFormatException exc) {}
				}
			}
		});
		
	}
	
	
	private static void buildShowStatsItem() 
	{
		JMenuItem showstats = (JMenuItem)components.get(ComponentMap.MenuItemShowStats);
		Statistics statistics = Statistics.getInstance();
		showstats.setAction(new AbstractAction(showstats.getText()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				String stats = statistics.getStatistics();
				stats = stats.replaceAll("\r|\n|\r\n|\n\r|"+System.lineSeparator(), "<br>");
				String msg = String.format("<html><b>%s</b></html>", stats);
				JOptionPane.showMessageDialog(mainWindow, msg, "Result statistics", JOptionPane.INFORMATION_MESSAGE);	
			}
		});
	}
	
	
	private static void setShowShortCuts() 
	{
		JMenuItem showshortcuts = (JMenuItem)components.get(ComponentMap.MenuItemShowShortCuts);
		showshortcuts.setAction(new AbstractAction(showshortcuts.getText()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				Scanner sc = new Scanner(shortCuts);
				sc.useDelimiter(System.lineSeparator());
				JTextArea ta = new JTextArea();
				int dialogFontSize = GeneralProperties.getInstance().getDialogFont().getSize();
				ta.setFont(new Font(Font.MONOSPACED, Font.BOLD, dialogFontSize));
				ta.setEditable(false);
				String[] lineParts;
				String newLine;
				while (sc.hasNext()) {
					lineParts = sc.next().split("\t");
					newLine = String.format("%1$5s %2$-5s %3$-5s", lineParts[0], lineParts[1], lineParts[2]);
					ta.append(newLine);
					if (sc.hasNext())
						ta.append("\n");
				}
				sc.close();
				JScrollPane sp = new JScrollPane();
				sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				sp.setViewportView(ta);
				JOptionPane.showMessageDialog(mainWindow, sp, "Short-Cut list", JOptionPane.INFORMATION_MESSAGE);
			}	
		});
	}
	
	
	private static void buildModifyCommandSystem() 
	{
		ModifyManager mm = ModifyManager.getInstance();
		ModifyCommandForwarder transferListener = ModifyCommandForwarder.getInstance();
		//components
		JButton editButton = (JButton)components.get(ComponentMap.ButtonEditEntry);
		JButton createButton = (JButton)components.get(ComponentMap.ButtonCreateEntry);
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		JMenuItem deleteMenu = (JMenuItem)components.get(ComponentMap.MenuItemDelete);
		ActionMap indexListActionMap = indexList.getActionMap();
		ActionMap findspotListActionMap = findspotList.getActionMap();
		//instantiate actions
		Action deleteIndexListAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mm.delete(true);
			}
		};
		Action deleteFindspotListAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mm.delete(false);
			}
		};
		String editString = editButton.getText();
		Action editAction = new AbstractAction(editString) {
			@Override
			public void actionPerformed(ActionEvent e) {
				mm.edit(null);
			}
		};
		editAction.putValue(Action.SHORT_DESCRIPTION, "Short-Cut: Ctrl-E");
		String createString = createButton.getText();
		Action createAction = new AbstractAction(createString) {
			@Override
			public void actionPerformed(ActionEvent e) {
				mm.create();
			}
		};
		createAction.putValue(Action.SHORT_DESCRIPTION, "Short-Cut: Ctrl-R");
		//set actions (forward actions via 'ModifyCommandForwarder')
		//->edit (mouse listener)
		indexList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					mm.edit(true);
				}
			}
		});
		//->edit and create
		editButton.setAction(editAction);
		createButton.setAction(createAction);
		KeyStroke eKeyCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
		KeyStroke rKeyCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK);
		editButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(eKeyCtrl, "edit");
		createButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(rKeyCtrl, "create");
		editButton.getActionMap().put("edit", editAction);
		createButton.getActionMap().put("create", createAction);
		//->delete
		KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		KeyStroke delKeyCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_MASK);
		indexListActionMap.put("deleteAction", deleteIndexListAction);
		findspotListActionMap.put("deleteAction", deleteFindspotListAction);
		indexList.getInputMap().put(delKey, "deleteAction");
		indexList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delKeyCtrl, "deleteAction");
		findspotList.getInputMap().put(delKey, "deleteAction");
		deleteMenu.setActionCommand("deleteAction");
		deleteMenu.addActionListener(transferListener);
		deleteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		
	}
	
	
	private static void builDnDAndCPSystem() 
	{
		IndexTransferHandler transferHandler = IndexTransferHandler.getInstance();
		ModifyCommandForwarder transferListener = ModifyCommandForwarder.getInstance();
		ModifyManager mm = ModifyManager.getInstance();
		//components
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		JTextArea textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		ActionMap indexListActionMap = indexList.getActionMap();
		ActionMap findspotListActionMap = findspotList.getActionMap();
		//set transfer handler
		indexList.setDragEnabled(true);
		findspotList.setDragEnabled(true);
		textArea.setDragEnabled(true);
		indexList.setDropMode(DropMode.ON);
		indexList.setTransferHandler(transferHandler);
		findspotList.setTransferHandler(transferHandler);
		textArea.setTransferHandler(transferHandler);
		//set cut and paste actions
		indexListActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
		indexListActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());	
		findspotListActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
		JMenuItem cutMenu = (JMenuItem)components.get(ComponentMap.MenuItemCut);
		JMenuItem pasteMenu = (JMenuItem)components.get(ComponentMap.MenuItemPaste);
		cutMenu.setActionCommand((String)TransferHandler.getCutAction().getValue(Action.NAME));
		pasteMenu.setActionCommand((String)TransferHandler.getPasteAction().getValue(Action.NAME));
		cutMenu.addActionListener(transferListener);
		pasteMenu.addActionListener(transferListener);
		cutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		pasteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

	}
	
	
	private static void setMouseDraggedListeners() 
	{
        MouseMotionListener doScrollRectToVisible = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JList<?>)e.getSource()).scrollRectToVisible(r);
            }
        };
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		indexList.addMouseMotionListener(doScrollRectToVisible);
		findspotList.addMouseMotionListener(doScrollRectToVisible);
	}

	
	
	private static void setExitListener() 
	{
		JMenuItem exit = (JMenuItem)components.get(ComponentMap.MenuItemExit);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.setVisible(false);
				mainWindow.dispose();
				System.exit(0);	
			}
		});
	}
	
	
	
	private static void rebindKeyBindings() 
	{
		//->index list
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		Object upActionKeyIL = indexList.getInputMap().get(KeyStroke.getKeyStroke("UP"));
		Object downActionKeyIL = indexList.getInputMap().get(KeyStroke.getKeyStroke("DOWN"));
		indexList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), upActionKeyIL);
		indexList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), downActionKeyIL);
		//->findspot list move selection up and down (triggered by alt-up and alt-down keys)
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		KeyStroke keyAltUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK);
		KeyStroke keyAltDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK);
		Object upActionKeyFL = findspotList.getInputMap().get(KeyStroke.getKeyStroke("UP"));
		Object downActionKeyFL = findspotList.getInputMap().get(KeyStroke.getKeyStroke("DOWN"));
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyAltUp, upActionKeyFL);
		findspotList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyAltDown, downActionKeyFL);
	}
	
	
	private static void disableKeyBindings()
	{
		InputMap textAreaInputMap = (InputMap)UIManager.get("TextArea.focusInputMap");
		InputMap scrollPaneInputMap = (InputMap)UIManager.get("ScrollPane.ancestorInputMap");
		InputMap scrollBarInputMap = (InputMap)UIManager.get("ScrollBar.ancestorInputMap");
		InputMap splitPaneInputMap = (InputMap)UIManager.get("SplitPane.ancestorInputMap");
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> findspotList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListFindspotList);
		JList<String> riskList1 = (JList<String>)components.get(ComponentMap.ListIndexRiskList);
		JList<String> riskList2 = (JList<String>)components.get(ComponentMap.ListFindspotRiskList);
		InputMap indexListMap = indexList.getInputMap();
		InputMap findspotListMap = findspotList.getInputMap();
		InputMap riskList1Map = riskList1.getInputMap();
		InputMap riskList2Map = riskList2.getInputMap();
		disableInputs(textAreaInputMap);
		disableInputs(scrollPaneInputMap);
		disableInputs(scrollBarInputMap);
		disableInputs(splitPaneInputMap);
		disableInputs(indexListMap);
		disableInputs(findspotListMap);
		disableInputs(riskList1Map);
		disableInputs(riskList2Map);
	}
	
	
	
//	------------------------------------ AUX -------------------------------------------------
	
	private static void disableInputs(InputMap map)
	{
		map.put(KeyStroke.getKeyStroke("UP"), "none");
		map.put(KeyStroke.getKeyStroke("DOWN"), "none");
		map.put(KeyStroke.getKeyStroke("LEFT"), "none");
		map.put(KeyStroke.getKeyStroke("RIGHT"), "none");
		map.put(KeyStroke.getKeyStroke("SPACE"), "none");
		map.put(KeyStroke.getKeyStroke("DELETE"), "none");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK), "none");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.CTRL_MASK), "none");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK), "none");
	}
	
	
//	------------------------------------ TRASH ------------------------------------------------
	
	/*
	private static void buildIndexListModeLogic() 
	{
		JRadioButton indexRB = (JRadioButton)components.get(ComponentMap.RadioButtonIndex);
		JRadioButton perPageRB = (JRadioButton)components.get(ComponentMap.RadioButtonPerPage);
		JList<Result> indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
		JList<Map.Entry<TextRange, ResultInformation>> textEntryList = (JList<Map.Entry<TextRange, ResultInformation>>)components.get(ComponentMap.ListEntryList);
		//set result list model as listener
		indexRB.setActionCommand(IndexCommand);
		perPageRB.setActionCommand(PerPageCommand);
		IndexModeSelection indexModeListener = new IndexModeSelection();
		indexRB.addActionListener(indexModeListener);
		perPageRB.addActionListener(indexModeListener);	
		//set text entry list selectability
		
		
		ActionListener setTextEntryListMode = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton rb = (JRadioButton)e.getSource();
				Result oldResultIndexList = indexList.getSelectedValue();
				Entry<TextRange, ResultInformation> oldResultTextEntryList = textEntryList.getSelectedValue();
				if (rb.getActionCommand().equals(IndexCommand)) {
					results.changeIndexListMode(true);
					//set text entry list
					textEntryList.setEnabled(true);
					//set index list by old result
					if (oldResultIndexList != null) {
						int index = results.getIndexByName(oldResultIndexList);
						indexList.setSelectedIndex(index);
						//set text entry list
						ListModel<Entry<TextRange, ResultInformation>> newResultEntryList = textEntryList.getModel();
						Entry<TextRange, ResultInformation> el;
						for (int i = 0; i < newResultEntryList.getSize(); i++) {
							el = newResultEntryList.getElementAt(i);
							if (el.getKey() == oldResultIndexList.getAllTextRanges().get(0)) 
								textEntryList.setSelectedIndex(i);
						}
					}
					else if (textEntryList.getModel() != null && textEntryList.getModel().getSize() > 0) 
						textEntryList.setSelectedIndex(0);
				}
				else if (rb.getActionCommand().equals(PerPageCommand)) {
					results.changeIndexListMode(false);
					//set text entry list
					textEntryList.setEnabled(false);
					textEntryList.clearSelection();
					//set index list by old result
					if (oldResultTextEntryList != null) {
						int index = results.getIndexByTextRange(oldResultTextEntryList.getKey());
						indexList.setSelectedIndex(index);
					}
				}
				//set view
				SwingUtilities.invokeLater(new Runnable() {	
					@Override
					public void run() {
						indexList.ensureIndexIsVisible(indexList.getSelectedIndex());
						textEntryList.ensureIndexIsVisible(textEntryList.getSelectedIndex());
					}
				});
			}
		};
		
	}
	*/
}
