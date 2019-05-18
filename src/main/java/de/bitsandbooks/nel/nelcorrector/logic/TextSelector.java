package de.bitsandbooks.nel.nelcorrector.logic;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelcorrector.util.RXTextUtilities;
import de.bitsandbooks.nel.nelcorrector.util.TRAndIntWrapper;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class TextSelector extends MouseAdapter implements PageChangedListener {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("PropertiesLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFile = FileSelector.getFileNameByRelativeOrAbsolutePath(props.getProperty(GeneralProperties.Log4JFilePath));
		PropertyConfigurator.configure(log4JFile);
		//logger.setLevel(Level.OFF);
	}
	
	
//	---------------------- CONSTANT -------------------------------
	
	private static final String ResultSpanWarning = "result spans over pagelimit !";
	private static final Color HighlightColor = Color.lightGray;
	
	
//	------------------------- FIELDS ------------------------------
	
	private Text text;
	private ResultListModel results;
	private JTextArea textArea;
	private JList<Result> indexList;
	private JLabel spanMessageLabel;
	private HighlightPainter painter1;
	private JCheckBox selectText, selectIndex;
	
	
//	----------------------- CONSTRUCTOR ---------------------------
	
	private static TextSelector instance;
	
	private TextSelector() {
		ComponentMap components = ComponentMap.getInstance();
		textArea = (JTextArea)components.get(ComponentMap.TextAreaMain);
		indexList = (JList<Result>)components.get(ComponentMap.ListIndexList); 
		spanMessageLabel = (JLabel)components.get(ComponentMap.LabelEntrySpansWarning);
		selectText = (JCheckBox)ComponentMap.getInstance().get(ComponentMap.CheckBoxSelectText);
		selectIndex = (JCheckBox)ComponentMap.getInstance().get(ComponentMap.CheckBoxSelectIndex);
		text = Text.getInstance();
		results = ResultListModel.getInstance();
		painter1 = new DefaultHighlighter.DefaultHighlightPainter(HighlightColor);
	}
	
	public static TextSelector getInstance() {
		if (instance == null)
			instance = new TextSelector();
		return instance;
	}
	
	
//	----------------------- METHODS --------------------------------
	
	@Override
	public void pageChanged(PageChangedEvent e) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				markAllFindspots();
			}
		});

	}
	
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 2) {
				changeTextSelection();
			}
			else if (e.getClickCount() == 1 && selectIndex.isSelected()) {
				int offset = textArea.viewToModel(e.getPoint());
				Page page = text.getSelectedPage();
				if (page != null) {
					int articleNumber = page.getArticleNumber();
					int pageNumber = page.getPageNumber();
					int sectionNumber = page.getPageSectionNumber();
					int signNr = page.getFirstSignNumber() + offset;
					TextLocation loc = new TextLocation(articleNumber, pageNumber, sectionNumber, signNr);
					TRAndIntWrapper trix = results.getResultForTextLocation(loc);
					if (trix != null) {
						TextRange tr = trix.textRange;
						int ix = trix.index;
						if (tr != null) {
							markAllFindspots();
							markText(tr, page, false);
							IndexListHandler listHandler = IndexListHandler.getInstance();
							listHandler.setSelectEntryFlag(false);
							indexList.setSelectedIndex(ix);
							listHandler.setSelectEntryFlag(true);
						}
					}
				}
			}
		}
	}
	
	
	public void changeTextSelection() 
	{
		textArea.setCaretPosition(textArea.getSelectionEnd());
		Highlighter highlighter = textArea.getHighlighter();
		Highlight[] highlights = highlighter.getHighlights();
		if (highlights != null && highlights.length == 0) {
			markAllFindspots();
		}
		else {
			highlighter.removeAllHighlights();
		}	
	}
	
	
	public void markTextFromIndexSelection(TextRange tr) 
	{
		//get page
		if (!selectText.isSelected())
			return;
		Page page = text.getAndSetSelectedPageByTextRange(tr);
		if (page != null) {
			markText(tr, page, true);
		}	
	}
	

	private void markText(TextRange tr, Page page, boolean moveScrollPane) {
		//mark text
		Highlighter highlighter = textArea.getHighlighter();
		int firstSignNr = page.getFirstSignNumber();
		int startPosition = tr.getStartOffset().signNumber - firstSignNr;
		int endPosition = tr.getEndOffset().signNumber - firstSignNr;
		int textLength = textArea.getText().length();
		if (endPosition >= textLength) {
			endPosition = textLength - 1;
			spanMessageLabel.setText(ResultSpanWarning);
		}
		else
			spanMessageLabel.setText("");
		textArea.requestFocusInWindow();
		textArea.setCaretPosition(endPosition);
		//remove existing overlapping highlights
		for (Highlight highlight : highlighter.getHighlights()) {
			if ((highlight.getStartOffset() >= startPosition && highlight.getStartOffset() <= endPosition)
				|| (highlight.getEndOffset() >= startPosition && highlight.getEndOffset() <= endPosition))
				highlighter.removeHighlight(highlight);
		}
		//add selection highlight
		textArea.select(startPosition, endPosition);
		if (moveScrollPane) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					RXTextUtilities.centerLineInScrollPane(textArea);
				}
			});
		}
	}
	
	
	private void markAllFindspots() 
	{
		if (results.getSize() == 0)
			return;
		Page page = text.getSelectedPage();
		Highlighter highlighter = textArea.getHighlighter();
		List<TextRange> thisPageResults = results.getThisPageTextRanges(page);
		int firstSignNr = page.getFirstSignNumber(), start, end;
		for (TextRange textRange : thisPageResults) {
			start = textRange.getStartOffset().signNumber - firstSignNr;
			end = textRange.getEndOffset().signNumber - firstSignNr;
			end = end > page.getText().length() ? page.getText().length() : end;
			try {
				highlighter.addHighlight(start, end, painter1);
			} catch (BadLocationException e1) {
				logger.info("Text Highlight could not be set cause of invalid highlight bounds." + System.lineSeparator() + e1.getMessage());
			}
		}
	}

}
