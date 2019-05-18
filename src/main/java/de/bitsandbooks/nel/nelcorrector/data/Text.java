package de.bitsandbooks.nel.nelcorrector.data;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import de.bitsandbooks.nel.interface1.Page;
import de.bitsandbooks.nel.interface1.TextIF;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.PageChangedListener;
import de.bitsandbooks.nel.nelinterface2.TextLocation;
import de.bitsandbooks.nel.nelinterface2.TextRange;

public class Text {
	
//	---------------------------- FIELDS -------------------------------------------------
	
	private int selectedPage;
	private TextIF text;
	private List<Page> pages;
	private List<PageChangedListener> listener;
	private int textPageMinNumber, textPageMaxNumber, docPageMaxNumber;
	
	
//	-------------------------- CONSTRUCTOR & INSTANCE -----------------------------------
	
	private static Text instance;
	
	private Text() {
		listener = new Vector<PageChangedListener>();
	}
	
	public static Text getInstance() {
		if (instance == null)
			instance = new Text();
		return instance;
	}
	
		
//	-------------------------- INITIALIZATION METHODS ----------------------------------

	public TextIF getText() {
		return text;
	}
	
	
	public void addPageChangeListener(PageChangedListener listener) {
		this.listener.add(listener);
	}
	
	
	public void setText(TextIF text) 
	{
		this.text = text;
		pages = text.getPageList();
		sort();
		//set maximum pages
		Page firstPage = pages.get(0);
		Page lastPage = pages.get(pages.size() - 1);
		textPageMinNumber = firstPage.getPageNumber();
		textPageMaxNumber = lastPage.getPageNumber();
		docPageMaxNumber = lastPage.getPageFileListNumber();
		//set first page as selected page
		setSelectedPageByIndex(0);
	}
	

	public void sort() {
		Collections.sort(pages);
	}
	
	
//	--------------------- OTHER METHODS ----------------------------------------------
	
	private void firePageChangedEvent() 
	{
		if (text == null)
			return;
		Page page = pages.get(selectedPage);
		PageChangedEvent event = new PageChangedEvent(this, page);
		for (PageChangedListener lis : listener) {
			lis.pageChanged(event);
		}	
	}
	
	
	public int getNrOfPages() {
		if (pages != null)
			return pages.size();
		else
			return -1;
	}
	
	
	public Page getPage(int ix) {
		Page page = pages.get(ix);
		return page;
	}
	
	
	public int getSelectedPageNumber(boolean textNumber) 
	{
		if (text == null)
			return 0;
		Page page = pages.get(selectedPage);
		int result = textNumber ? page.getPageNumber() : page.getPageFileListNumber();
		return result;
	}
	
	
	public int getSelectedPageIndex() {
		return selectedPage;
	}
	

	public void setSelectedPageByIndex(int i) {
		if (text == null)
			return;
		selectedPage = i;
		firePageChangedEvent();
	}
	
	
	public void setSelectedPageByTextNumber(int nr) 
	{
		Page page;
		if (text == null)
			return;
		for (int i = selectedPage < pages.size() - 1 ? selectedPage + 1 : 0; i != selectedPage; i = (i + 1) % pages.size()) {//starts at selected page ... forward ... starts at the beginning of the document when document has end
			page = pages.get(i);
			if (page.getPageNumber() == nr) {
				selectedPage = i;
				firePageChangedEvent();
				break;
			}		
		}
	}
	
	
	public Page getSelectedPage() {
		if (text != null && pages != null)
			return pages.get(selectedPage);
		else
			return null;
	}
	
	
	/**
	 * @return returns null if page couldn't be found
	 */
	public Page getPageByTextRange(TextRange tr) 
	{
		TextLocation startLoc = tr.getStartOffset();
		for (Page page : pages) {
			if (	page.getArticleNumber() == startLoc.articleNumber
					&& page.getPageNumber() == startLoc.pageNumber
					&& page.getPageSectionNumber() == startLoc.sectionNumber)
				return page;
		}
		return null;
	}
	
	
	public Page getAndSetSelectedPageByTextRange(TextRange tr) 
	{
		Page page;
		TextLocation startLoc = tr.getStartOffset();
		for (int i = 0; i < pages.size(); i++) {
			page = pages.get(i);
			if (	page.getArticleNumber() == startLoc.articleNumber
					&& page.getPageNumber() == startLoc.pageNumber
					&& page.getPageSectionNumber() == startLoc.sectionNumber) 
			{
				selectedPage = i;
				firePageChangedEvent();
				return pages.get(selectedPage);
			}	
		}
		return null;
	}
	
	
	public void setSelectedPageByDocumentNumber(int docNr)
	{
		Page page;
		if (text == null)
			return;
		for (int i = 0; i < pages.size(); i++) {
			page = pages.get(i);
			if (page.getPageFileListNumber() == docNr) {
				selectedPage = i;
				firePageChangedEvent();
				break;
			}
		}
	}
	
	
	public void decreaseSelectedPage() 
	{
		if (text != null && selectedPage > 0) {
			selectedPage--;
			firePageChangedEvent();
		}
	}

	
	public void increaseSelectedPage() 
	{
		if (text != null && selectedPage + 1 < pages.size()) {
			selectedPage++;
			firePageChangedEvent();
		}
	}
	
	
	public String getTextOfSelectedPage() 
	{
		if (text == null)
			return "";
		Page page = pages.get(selectedPage);
		String text = page.getText();
		return text;
	}
	
	
	public String getTextOfIndex(int index) 
	{
		Page page = pages.get(index);
		String text = page.getText();
		return text;
	}

	
	public boolean isFirstPageSelected() 
	{
		if (selectedPage == 0)
			return true;
		else
			return false;
	}
	
	
	public boolean isLastPageSelected() 
	{
		if (selectedPage == pages.size() - 1)
			return true;
		else
			return false;
	}
	
	
	public int getTextPageMinimum() {
		return textPageMinNumber;
	}
	
	public int getTextPageMaximum() {
		return textPageMaxNumber;
	}
	
	public int getDocPageMaximum() {
		return docPageMaxNumber;
	}

	
	public int getIndexPageMaximum() {
		if (pages == null)
			return 0;
		return pages.size();
	}

	
	public boolean isBibliographyResult(TextRange resultTR) 
	{
		Page page = getPageByTextRange(resultTR);
		if (page == null)
			return false;
		boolean result = page.isBibliography();
		return result;
	}

	
	public String getNextPageTextStart(int ix, int nrOfCharacters) {
		StringBuilder sb = new StringBuilder();
		Page startPage, nextPage;
		String nextPageText;
		int startArticle, length = nrOfCharacters;
		if (ix >= 0 && ix < pages.size()) {
			startPage = pages.get(ix);
			startArticle = startPage.getArticleNumber();
			for (int i = ix + 1; i < pages.size(); i++) {
				nextPage = pages.get(i);
				if (nextPage.getArticleNumber() != startArticle)
					break;
				nextPageText = nextPage.getText();
				if (nextPageText.length() >= length) {
					sb.append(nextPageText.substring(0, length));
					break;
				}
				else {
					sb.append(nextPageText);
					length -= nextPageText.length();
				}
			}
		}
		return sb.toString();
	}

}
