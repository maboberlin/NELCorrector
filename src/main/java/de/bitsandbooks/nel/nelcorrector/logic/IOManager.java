package de.bitsandbooks.nel.nelcorrector.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.bitsandbooks.nel.interface1.TextIF;
import de.bitsandbooks.nel.nelcorrector.MainWindow;
import de.bitsandbooks.nel.nelcorrector.data.ResultListModel;
import de.bitsandbooks.nel.nelcorrector.data.Text;
import de.bitsandbooks.nel.nelcorrector.exceptions.MergeFileException;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedEvent;
import de.bitsandbooks.nel.nelcorrector.listener.ResultLoadedListener;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.resulttype.EntryTextCreatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.IndexComparatorIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.ResultTypeHandlerIF;
import de.bitsandbooks.nel.nelcorrector.resulttype.SingletonFactory;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;
import de.bitsandbooks.nel.nelcorrector.util.IgnoreArticleTRComparator;
import de.bitsandbooks.nel.nelcorrector.util.MyFileChooser;
import de.bitsandbooks.nel.nelcorrector.util.ResultDataHelper;
import de.bitsandbooks.nel.nelinterface2.Result;
import de.bitsandbooks.nel.nelinterface2.ResultTreeEntry;
import de.bitsandbooks.nel.nelinterface2.Risk;
import de.bitsandbooks.nel.nelinterface2.TextRange;
import de.bitsandbooks.nel.nelinterface2.io.ResultData;
import de.bitsandbooks.nel.nelinterface2.io.ResultIO;

public class IOManager implements PropertyChangeListener {
	
//	----------------------- WORKER CLASSES -----------------------------
	
	private class LoadWorker extends SwingWorker<ResultData, Void> {
		@Override
		protected ResultData doInBackground() throws Exception {
			ResultData result = ResultIO.deserializeResult(ioFileNames[LoadFilePath]);
			return result;
		}
	}
	
	
	private class SaveWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception 
		{
			File saveFile = new File(ioFileNames[SaveFilePath]);
			String searchType = resultModel.getSearchType();
			SingletonFactory sf = SingletonFactory.getInstance();
			ResultTypeHandlerIF resultHandler = sf.getResultTypeHandler();
			TreeSet<ResultTreeEntry> data = resultHandler.createFinalResultTree(resultModel.getMainResultIterator(true, null));
			Text textModel = Text.getInstance();
			TextIF text = textModel.getText();
			ResultIO.serializeResult(saveFile, searchType, data, text);
			return null;
		}
	}
	
	
	private class ExportWorker extends SwingWorker<Void, Void> {
		//Fields
		private ResultTypeHandlerIF resultHandler;
		private EntryTextCreatorIF entryTextCreator;
		private IndexComparatorIF indexComparator;
		private IgnoreArticleTRComparator trComparator;
		private Class<? extends Risk> riskClass;
		private boolean printIndexList;
		private boolean merge;
		private boolean ixmlExport;
		
		//Constructor
		public ExportWorker(boolean printIndexList, boolean merge, boolean ixmlExport) {
			SingletonFactory sf = SingletonFactory.getInstance();
			this.resultHandler = sf.getResultTypeHandler();
			this.entryTextCreator = sf.getListTextCreator();
			this.indexComparator = sf.getIndexComparator();
			this.trComparator = new IgnoreArticleTRComparator();
			this.printIndexList = printIndexList;
			this.merge = merge;
			this.ixmlExport = ixmlExport;
			riskClass = resultHandler.getFindspotRiskClass();
		}
		
		//Method
		@Override
		protected Void doInBackground() throws Exception 
		{
			//try to merge if file exists
			Iterator<Result> listIterator;
			if (merge) {
				if (!printIndexList)
					throw new MergeFileException();
				//get result tree from file
				ResultData toMergeResult = ResultIO.deserializeResult(ioFileNames[MergeFilePath]);
				TreeSet<ResultTreeEntry> resultTree = toMergeResult.data;
				//merge current result set into tree
				List<TextRange> trList;
				ResultTreeEntry newTreeEntry, oldTreeEntry;
				Result existingResult;
				String lexicoName;
				for (Iterator<Result> it = resultModel.getMainResultIterator(true, null); it.hasNext(); ) {
					existingResult = it.next();
					lexicoName = entryTextCreator.getLexicographicText(existingResult);
					newTreeEntry = new ResultTreeEntry(lexicoName);
					if (resultTree.contains(newTreeEntry)) {
						oldTreeEntry = resultTree.ceiling(newTreeEntry);
						boolean found = false;
						for (Result mergeResult : oldTreeEntry.getResultList()) {
							if (indexComparator.compare(existingResult, mergeResult) == 0) {
								trList = existingResult.getAllTextRanges();
								trList.addAll(mergeResult.getAllTextRanges());
								Collections.sort(trList, trComparator);
								mergeResult.getResultMap().clear();
								mergeResult.addTextRanges("", trList, riskClass);
								found = true;
								break;
							}
						}
						if (!found) {
							oldTreeEntry.addResultEntry(existingResult);
						}
					}
					else {
						newTreeEntry.addResultEntry(existingResult);
						resultTree.add(newTreeEntry);
					}
				}
				//create plain result list from tree
				List<Result> mergedResults= new Vector<>();
				for (ResultTreeEntry resultTreeEntry : resultTree) {
					mergedResults.addAll(resultTreeEntry.getResultList());
				}
				listIterator = mergedResults.iterator();
			}
			else {
				listIterator = printIndexList ? resultModel.getMainResultIterator(true, null) : resultModel.getPerPageResultIterator(-1);
			}
			
			//create output string
			SingletonFactory sf = SingletonFactory.getInstance();
			EntryTextCreatorIF textCreator = sf.getListTextCreator();
			StringBuilder resultString = new StringBuilder();
			Result el;
			String elText, entryText, locatorText;
			if (!ixmlExport) {
				while (listIterator.hasNext()) {
					el = listIterator.next();
					elText = textCreator.getResultText(el, printIndexList, true, exportPageNrs, trComparator);
					if (elText.trim().length() > 0)
						resultString.append(String.format("%s%s", elText, System.lineSeparator()));
				}
			}
			else {
				InputStream is = ClassLoader.getSystemResourceAsStream("ixml/indexbase.xml");
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(is);
				Element indexdata = doc.getRootElement();
				SimpleDateFormat sdfmt = new SimpleDateFormat();
				sdfmt.applyPattern( "yyyy-MM-dd'T'HH:mm:ss" );
				String date = sdfmt.format(new Date()); 
				// set source
				Element source = indexdata.getChild("source");
				source.setAttribute("creator", "nel");
				source.setAttribute("version", "1.0");
				source.setAttribute("time", date);
				// set records
				Element records = indexdata.getChild("records");
				Element record, entryField, locatorField;
				while (listIterator.hasNext()) {
					el = listIterator.next();
					entryText = textCreator.getEntryText(el);
					locatorText = textCreator.getLocatorString(el, exportPageNrs, trComparator);
					if (locatorText.length() == 0)
						continue;
					record = new Element("record");
					record.setAttribute("time", date);
					entryField = new Element("field");
					entryField.setText(entryText);
					record.addContent(entryField);
					locatorField = new Element("field");
					locatorField.setText(locatorText);
					record.addContent(locatorField);
					records.addContent(record);
				}
				// validate
				XMLOutputter out = new XMLOutputter( Format.getPrettyFormat() );
				String txt = out.outputString(doc);
				XMLReaders dtdvalidating = XMLReaders.DTDVALIDATING;
				SAXBuilder builder2 = new SAXBuilder(dtdvalidating);
				builder2.build(new ByteArrayInputStream(txt.getBytes()));
				resultString.append(txt);
			}
			
			//print to file
			String fileSeperator = System.getProperty("file.separator");
			String filePath = !ixmlExport ? ioFileNames[ExportFilePath].replace("/", fileSeperator) : ioFileNames[ExportXMLFilePath].replace("/", fileSeperator);
			File printFile = new File(filePath);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(printFile), encodingOut));
			out.write(resultString.toString());
			out.flush();
			out.close();
			return null;
		}
		
	}

	
//	-------------------------- FIELDS ----------------------------------
	
	private final static int LoadFilePath = 0;
	private final static int SaveFilePath = 1;
	private final static int ExportFilePath = 2;
	private final static int MergeFilePath = 3;
	private final static int ExportXMLFilePath = 4;
	
	private SwingWorker<ResultData, Void> loadWorker;
	private SwingWorker<Void, Void> saveWorker;
	private SwingWorker<Void, Void> exportWorker;
	private List<ResultLoadedListener> listeners;
	private MainWindow mainFrame;
	private JFileChooser fileChooser;
	private ResultListModel resultModel;
	private Text textInstance;
	private String[] ioFileNames = {"", "", "", "", ""}; 
	private String encodingIn, encodingOut;
	private int[] exportPageNrs;
	
	
//	--------------------- CONSTRUCTOR & INSTANCE -----------------------
	
	private static IOManager instance;
	
	private IOManager(MainWindow mainFrame) {
		setDefaultFilePath();
		resultModel = ResultListModel.getInstance();
		textInstance = Text.getInstance();
		this.mainFrame = mainFrame;
		fileChooser = new MyFileChooser();
		listeners = new Vector<ResultLoadedListener>();
		GeneralProperties props = GeneralProperties.getInstance();
		encodingIn = props.getEncodingIn();
		encodingOut = props.getEncodingOut();
	}
	

	public static IOManager getInstance(MainWindow mainFrame) {
		if (instance == null)
			instance = new IOManager(mainFrame);
		return instance;
	}
	
	
//	------------------------- SETTER -----------------------------------
	
	public void setExportPages(int[] pages) {
		exportPageNrs = pages;
	}
	
	
//	------------------------ METHODS -----------------------------------
	
	public void loadData() 
	{
		//set file path
		if (!setFilePath(true, ioFileNames[LoadFilePath], LoadFilePath))
			return;
		//set up & start worker
		loadWorker = new LoadWorker();
		loadWorker.addPropertyChangeListener(this);
		loadWorker.execute();
		mainFrame.showWaitDialog();
	}


	public void saveData(boolean direct) 
	{
		ResultListModel resultModel = ResultListModel.getInstance();
		//check if there is data to save
		if (resultModel.getSize() == 0) {
			JOptionPane.showMessageDialog(mainFrame, "No data to save has been set.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		//set file path
		if (!direct || ioFileNames[SaveFilePath] == null || ioFileNames[SaveFilePath].length() == 0) {//set file path
			if (!setFilePath(false, ioFileNames[SaveFilePath], SaveFilePath))
				return;
		}
		//set up & start worker
		saveWorker = new SaveWorker();
		saveWorker.addPropertyChangeListener(this);
		saveWorker.execute();
		mainFrame.showWaitDialog();
	}
	
	
	public void setMergeFile() 
	{
		setFilePath(true, ioFileNames[MergeFilePath], MergeFilePath);
	}
	
	
	public void exportResults(boolean exportIndexList, boolean merge, boolean ixmlExport) 
	{
		//check if there is data to save
		if (resultModel.getSize() == 0) {
			JOptionPane.showMessageDialog(mainFrame, "No data to print has been set.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		//update model
		resultModel.update();
		//set file path
		if ((ixmlExport && !setFilePath(false, ioFileNames[ExportXMLFilePath], ExportXMLFilePath)) || (!ixmlExport && !setFilePath(false, ioFileNames[ExportFilePath], ExportFilePath)))
			return;
		//set up & start worker
		exportWorker = new ExportWorker(exportIndexList, merge, ixmlExport);
		exportWorker.addPropertyChangeListener(this);
		exportWorker.execute();
		mainFrame.showWaitDialog();	
	}
	

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		if ("state".equals(evt.getPropertyName()) && evt.getNewValue() == SwingWorker.StateValue.DONE) {
			//Loading process finished
			if (loadWorker != null && loadWorker.isDone()) {
				try {
					ResultData result = loadWorker.get();
					TreeSet<ResultTreeEntry> data = result.data;
					TextIF text = result.text;
					//instantiate class factory
					String searchName = result.searchType;
					Result firstResult = ResultDataHelper.getFirstResult(data);
					String language = text.getLanguage();
					Class<? extends Result> resultClass = firstResult.getClass();
					SingletonFactory sf = SingletonFactory.getInstance();
					sf.initializeSingletons(searchName, resultClass, language, mainFrame);
					IndexComparatorIF comparator = sf.getIndexComparator();
					EntryTextCreatorIF textCreator = sf.getListTextCreator();
					ResultTypeHandlerIF resultTypeHandler = sf.getResultTypeHandler();
					//set data
					resultModel.setResultData(data, searchName, comparator, textCreator, resultTypeHandler);
					textInstance.setText(text);
					exportPageNrs = null;
					//fire result loaded event
					fireResultLoadedEvent(searchName, resultClass);
				} catch (InterruptedException | ExecutionException e) {
            		String msg = "Couldn't load text.";
            		if (e.getCause().getClass().getName().equals("de.bitsandbooks.nel.interface1.io.ChecksumException"))
        				msg = msg.concat(" Checksum corrupted!");
					JOptionPane.showMessageDialog(mainFrame,
        				    msg,
        				    "Error",
        				    JOptionPane.ERROR_MESSAGE);
				} finally {
					mainFrame.resetWaitDialog();
					loadWorker = null;
				}
			}
			//Saving process finished
			else if (saveWorker != null && saveWorker.isDone()) {
				try {
					saveWorker.get();
				} catch (InterruptedException | ExecutionException e) {
					String msg = "Couldn't save data.";
					mainFrame.resetWaitDialog();
					JOptionPane.showMessageDialog(mainFrame,
							msg,
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				} finally {
					mainFrame.resetWaitDialog();
					saveWorker = null;
				}
			}
			else if (exportWorker != null && exportWorker.isDone()) {
				try {
					exportWorker.get();
				} catch (InterruptedException | ExecutionException e) {
					String msg = "Couldn't export data.";
					mainFrame.resetWaitDialog();
					JOptionPane.showMessageDialog(mainFrame,
							msg,
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} finally {
					mainFrame.resetWaitDialog();
					exportWorker = null;
				}
			}
 		}
	}
	
	
//	---------------------------- LISTENER METHODS -------------------------------
	
	public void addResultLoadedListener(ResultLoadedListener listener) {
		listeners.add(listener);
	}
	
	
	public void fireResultLoadedEvent(String searchName, Class<? extends Result> resultClass) {
		ResultLoadedEvent event = new ResultLoadedEvent(this, searchName, resultClass);
		for (ResultLoadedListener listener : listeners) {
			listener.resultLoaded(event);
		}
	}
	
	
//	-------------------------------- AUX ----------------------------------------
	
	private boolean setFilePath(boolean isLoad, String filePath, int fileType) 
	{
		if (filePath != null && filePath != "") {
			File currentDir = new File(filePath);
			if (currentDir.exists() && !currentDir.isDirectory()) {
				fileChooser.setCurrentDirectory(currentDir);
				fileChooser.setSelectedFile(new File(filePath));
			}
		}
		if (isLoad)
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		else
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int res = isLoad ? fileChooser.showOpenDialog(mainFrame) : fileChooser.showSaveDialog(mainFrame);
		if (res != JFileChooser.APPROVE_OPTION) //break if 'Cancel' is chosen
			return false;
	    String filename = fileChooser.getSelectedFile().toString();
	    ioFileNames[fileType] = filename; 
	    return true;
	}
	
	
	private void setDefaultFilePath() 
	{
		GeneralProperties props = GeneralProperties.getInstance();
		String defaultFilePath = props.getDefaultFilePath();
		String defaultPrintFilePath = props.getDefaultPrintFilePath();
		ioFileNames[LoadFilePath] = FileSelector.getFileNameByRelativeOrAbsolutePath(defaultFilePath);
		ioFileNames[ExportFilePath] = FileSelector.getFileNameByRelativeOrAbsolutePath(defaultPrintFilePath);
	}

}
