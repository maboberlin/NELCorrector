package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.MainWindow;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.GhostScriptEvent;
import de.bitsandbooks.nel.nelcorrector.listener.GhostScriptListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.SingletonFactory;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelcorrector.util.RXTextUtilities;
import de.bitsandbooks.nel.nelcorrector.util.TextUtil;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultInformation;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class SearchExecutor implements GhostScriptListener, PropertyChangeListener {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("SearchExecutorLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFileName = props.getProperty(GeneralProperties.Log4JFilePath);
		log4JFileName = FileSelector.getFileNameByRelativeOrAbsolutePath(log4JFileName);
		PropertyConfigurator.configure(log4JFileName);
		//logger.setLevel(Level.OFF);
	}
	
	
//	------------------------- CONSTANT -----------------------------------------
	
	private static final String SearchEntrySuffix = "(|##|2|##|)";
	
	
//	--------------------- ENTRY SEARCH WORKER ----------------------------------
	
	private class EntrySearcher extends SwingWorker<List<Result>, Void> 
	{
		private Text textObject4Worker;
		private JList<Result> indexList;
		private ResultListModel resultList4Worker;
		private EntryTextCreatorIF entryTextCreator;
		private ResultTypeHandlerIF resultTypeHandler;
		private boolean searchAllEntries;
		
		EntrySearcher(boolean all) {
			searchAllEntries = all;
			ComponentMap components = ComponentMap.getInstance();
			SingletonFactory singletonFactory = SingletonFactory.getInstance();
			indexList = (JList<Result>)components.get(ComponentMap.ListIndexList);
			textObject4Worker = Text.getInstance();
			resultList4Worker = ResultListModel.getInstance();
			entryTextCreator = singletonFactory.getListTextCreator();
			resultTypeHandler = singletonFactory.getResultTypeHandler();
		}

		@Override
		protected List<Result> doInBackground() throws Exception {
			List<Result> newResultsList = new Vector<Result>();
			if (searchAllEntries) {
				for(Iterator<Result> it = resultList4Worker.getMainResultIterator(true, null); it.hasNext(); ) {
					Result res = it.next();
					doEntrySearch(newResultsList,  res);
				}
			}
			else {
				int index = indexList.getSelectedIndex();
				Result res = resultList4Worker.getElementAt(index);
				doEntrySearch(newResultsList, res);
			}
			return newResultsList;
		}
		
		
		private void doEntrySearch(List<Result> newResultsList, Result result) throws InstantiationException, IllegalAccessException 
		{
			if (result == null)
				return;
			String lexicoResultString = entryTextCreator.getLexicographicText(result);
			//check if last results name equals
			if (newResultsList.size() > 0) {
				Result lastRes = newResultsList.get(newResultsList.size() - 1);
				String prefixAddedName = String.format("%s%s", lexicoResultString.trim(), SearchEntrySuffix);
				if (entryTextCreator.getLexicographicText(lastRes).trim().equals(prefixAddedName))
					return;
			}
			//init pattern, new result and old result list (all names which are similar to lexico search name)
			Result newResult = resultTypeHandler.createResult(String.format("%s%s", lexicoResultString, SearchEntrySuffix));
			List<TextRange> oldResultList = resultList4Worker.getAllResultsForLexicoName(lexicoResultString);
			Pattern pattern = Pattern.compile(lexicoResultString);
			ResultInformation info;
			Matcher matcher;
			Page page;
			TextRange findspot;
			TextLocation startLoc, endLoc;
			String pageText, nextPageTextStart, concatPageText;
			int startIndex, endIndex, articleNr, pageNr, sectionNr, firstSignNumber;
			//search all pages
			for (int ix = 0; ix < textObject4Worker.getNrOfPages(); ix++) {
				page = textObject4Worker.getPage(ix);
				articleNr = page.getArticleNumber();
				pageNr = page.getPageNumber();
				sectionNr = page.getPageSectionNumber();
				firstSignNumber = page.getFirstSignNumber();
				pageText = textObject4Worker.getTextOfIndex(ix);
				nextPageTextStart = textObject4Worker.getNextPageTextStart(ix, 50);
				concatPageText = getText(pageText, nextPageTextStart);
				matcher = pattern.matcher(concatPageText);
				while (matcher.find()) {
					startIndex = matcher.start();
					endIndex = matcher.end();
					if (startIndex >= pageText.length())
						continue;
					startLoc = new TextLocation(articleNr, pageNr, sectionNr, startIndex + firstSignNumber);
					endLoc = new TextLocation(articleNr, pageNr, sectionNr, Math.min(endIndex + firstSignNumber, page.getText().length() + firstSignNumber));
					findspot = new TextRange(startLoc, endLoc);
					if (!alreadyIsResult(findspot, oldResultList)) {
						info = resultTypeHandler.getEmptyResultInformation();
						newResult.addResult(findspot, info);
					}
				}	
			}
			if (newResult.getAllTextRanges().size() > 0) 
				newResultsList.add(newResult);
		}
		
		
		// ------------------------ WORKER AUX METHODS --------------------------------------
		
		private String getText(String pageText, String nextPageTextStart) {
			String res;
			//check for hyphen
			boolean pageEndsWithHyphen = TextUtil.pageEndsWithHyphen(pageText);
			boolean nextPageStartsWithlowerCase = TextUtil.pageStartsWithLowerCase(nextPageTextStart);
			if (pageEndsWithHyphen && nextPageStartsWithlowerCase) {
				res = pageText.trim().substring(0, pageText.trim().length() - 1).concat(nextPageTextStart.trim());
			}
			else
				res = pageText.concat(nextPageTextStart);
			return res;
		}
		
		private boolean alreadyIsResult(TextRange findspot, List<TextRange> oldResultList) {
			for (TextRange textRange : oldResultList) {
				if (textRange.isOverlapping(findspot))
					return true;
			}
			return false;
		}
		
	}
	
//	----------------------- FIELDS ---------------------------------------------

	private Text textObject;
	private ResultListModel resultList;
	private MainWindow mainFrame;
	private EntrySearcher searcher;
	private JTextField textField;
	private JTextArea textArea;
	private JCheckBox caseInsensitivBox;
	private String lastSearchWord = "";
	private int lastSearchPage = -1;
	private Point lastSearchPosition = new Point(0, 0);
	private int currentSearchPage;
	private String currentSearchWord;
	private boolean isNewSearch;
	private int nrOfPages;
	private boolean showHintSearch;
	private boolean caseInsensitiv;
	
	
//	------------------ CONSTRUCTOR & INSTANCE ----------------------------------
	
	private static SearchExecutor instance;
	
	private SearchExecutor() {
		ComponentMap components = ComponentMap.getInstance();
		textField = (JTextField)components.get(ComponentMap.TextFieldSearch);
		textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		caseInsensitivBox = (JCheckBox)components.get(ComponentMap.CheckBoxCaseInsensitiv);
		textObject = Text.getInstance();
		resultList = ResultListModel.getInstance();
		showHintSearch = true;
	}
	
	public static SearchExecutor getInstance() {
		if (instance == null)
			instance = new SearchExecutor();
		return instance;
	}
	
	
//	------------------------ PLAIN SEARCH METHODS ---------------------------------
	
	public void searchForward()
	{
		//init search fields
		initializeSearch();
		if (currentSearchWord == null)
			return;
		//search	
		String text = "";
		int foundPosition = 0; 
		int pageIndex = currentSearchPage;
		int index = !isNewSearch ? lastSearchPosition.y : 0;
		boolean found = false;
		/*search*/
		while (!found) {
			text = textObject.getTextOfIndex(pageIndex);
			//case select
			if (caseInsensitiv)
				text = text.toLowerCase();
			//search
			foundPosition = text.indexOf(currentSearchWord, index);
			/*case: nextPage*/
			if (foundPosition != -1)
				found = true;
			else {
				pageIndex = (pageIndex + 1) % nrOfPages;
				/*case: whole document searched*/
				if (pageIndex == currentSearchPage)
					break;
				/*case: end of document reached*/
				if (pageIndex == 0)
					JOptionPane.showMessageDialog(mainFrame, "End of document reached", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
			index = 0;
		}
		/*set last search values*/
		lastSearchWord = currentSearchWord;
		lastSearchPage = pageIndex;
		lastSearchPosition = new Point(0, 0);
		/*check whether found or not*/
		if (found) {
			int preSpace = text.lastIndexOf((char)32, foundPosition);
			int startPosition = (preSpace != -1) ? preSpace + 1 : foundPosition;
			int postSpace = text.indexOf((char)32, foundPosition + currentSearchWord.length());
			int endPosition = (postSpace != -1) ? postSpace : foundPosition + currentSearchWord.length();
			setNewFoundPosition(startPosition, endPosition);
		}
		else
			JOptionPane.showMessageDialog(mainFrame, "Document searched. No matches found.", "Message", JOptionPane.INFORMATION_MESSAGE);
	}
	

	public void searchBackward()
	{
		//init search fields
		initializeSearch();
		if (currentSearchWord == null)
			return;
		//search
		String text = "";
		int foundPosition = 0; 
		int pageIndex = currentSearchPage;
		int index = !isNewSearch ? lastSearchPosition.x - 1 : 0;
		/*Backward searching has to search current page two times*/
		boolean wholeDocFlag = false;
		boolean found = false;
		boolean isFirstPage = true;
		/*search*/
		while (!found) {
			text = textObject.getTextOfIndex(pageIndex);
			//case select
			if (caseInsensitiv)
				text = text.toLowerCase();
			//search
			if (isNewSearch || !isFirstPage)
				index = text.length();
			foundPosition = text.lastIndexOf(currentSearchWord, index);
			/*case: nextPage*/
			if (foundPosition != -1)
				found = true;
			else {
				pageIndex = (pageIndex == 0) ? pageIndex = nrOfPages - 1 : pageIndex - 1;
				/*case: whole document searched*/
				if ((pageIndex == currentSearchPage - 1) && (wholeDocFlag))
					break;
				/*case: end of document reached*/
				if (pageIndex == nrOfPages - 1) {
					JOptionPane.showMessageDialog(mainFrame, "Start of document reached", "Message", JOptionPane.INFORMATION_MESSAGE);
					wholeDocFlag = true;
				}
			}
			isFirstPage = false;
		}
		/*set last search values*/
		lastSearchWord = currentSearchWord;
		lastSearchPage = pageIndex;
		lastSearchPosition = new Point(0, 0);
		/*check whether found or not*/
		if (found) {
			int preSpace = text.lastIndexOf((char)32, foundPosition);
			int startPosition = (preSpace != -1) ? preSpace + 1 : foundPosition;
			int postSpace = text.indexOf((char)32, foundPosition + currentSearchWord.length());
			int endPosition = (postSpace != -1) ? postSpace : foundPosition + currentSearchWord.length();
			setNewFoundPosition(startPosition, endPosition);
		}
		else
			JOptionPane.showMessageDialog(mainFrame, "Document searched. No matches found.", "Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	
//	------------------------ INDEX SEARCH METHODS --------------------------------
	
	public void doEntrySearch(boolean all, MainWindow mainWindow) {
		if (resultList.getSize() == 0)
			return;
		mainFrame = mainWindow;
		resultList.update();
		searcher = new EntrySearcher(all);
		searcher.addPropertyChangeListener(this);
		searcher.execute();
		mainFrame.showWaitDialog();
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("state".equals(evt.getPropertyName()) && evt.getNewValue() == SwingWorker.StateValue.DONE) {	
			try {
				List<Result> newResults = searcher.get();
				resultList.insert(newResults);
				String msg = String.format("%d new index entries created. Entry suffix: %s", newResults.size(), SearchEntrySuffix);
				JOptionPane.showMessageDialog(mainFrame, msg, "Entry Search", JOptionPane.INFORMATION_MESSAGE);
			} catch (InterruptedException | ExecutionException e) {
				logger.warn(e.getMessage());
				String msg = "Index-Entry-Search aborted abnormally!";
				JOptionPane.showMessageDialog(mainFrame, msg, "Entry Search", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (mainFrame != null)
					mainFrame.resetWaitDialog();
				searcher = null;
			}	
		}
	}

	
//	------------------------ AUX -------------------------------------------------

	@Override
	public void ghostScriptSet(GhostScriptEvent e) {
		showHintSearch = e.ghostScriptSet;
	}
	
	
	private void initializeSearch() 
	{
		//search word
		currentSearchWord = textField.getText();
		if (currentSearchWord == null || currentSearchWord.equals("") || showHintSearch || textObject.getIndexPageMaximum() == 0) {
			currentSearchWord = null;
			return;
		}
		//other settings
		currentSearchPage = textObject.getSelectedPageIndex();
		isNewSearch = ((currentSearchPage == lastSearchPage) && (currentSearchWord.equals(lastSearchWord))) ? false : true;
		nrOfPages = textObject.getIndexPageMaximum();
		//case selection
		caseInsensitiv = caseInsensitivBox.isSelected();
		if (caseInsensitiv)
			currentSearchWord = currentSearchWord.toLowerCase();
	}
	

	private void setNewFoundPosition(int startPosition, int endPosition) 
	{
		textObject.setSelectedPageByIndex(lastSearchPage);
		lastSearchPosition = new Point(startPosition, endPosition);
		textArea.requestFocusInWindow();
		textArea.setCaretPosition(endPosition);
		textArea.select(startPosition, endPosition);
		RXTextUtilities.centerLineInScrollPane(textArea);
		textField.requestFocusInWindow();	
	}
	
}
