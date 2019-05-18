package de.bitsandbooks.nel.nelcorrector.properties;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class GeneralProperties extends Properties {
	
// ------------------------- FILENAMES ----------------------------------

	private static final String currentPath;
	private static final String defaultFile;
	private static final String log4JFile;
	
	static {
		currentPath = System.getProperty("user.dir").replace('\\', '/');
		defaultFile = currentPath + "/config/defaultValues.properties";
		log4JFile = currentPath + "/config/log4j.properties";
	}
		
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("PropertiesLogger");
	static {
		PropertyConfigurator.configure(log4JFile);
		//logger.setLevel(Level.OFF);
	}
	
	
//	------------------------  PROPERTY-ATTRIBUTE NAMES ------------------------
	
	//direct fetch
	public static final String Log4JFilePath = "logsettings";
	public static final String RiskValueFile = "riskvalues";
	public static final String AutoSortMode = "autosort";
	public static final String SortLocale = "sortlocale";
	//method fetch
	private static final String defaultResultFilePath = "defaultresultfile";
	private static final String defaultPrintFilePath = "defaultprintfile";	
	private static final String encodingInKey = "indexin";
	private static final String encodingOutKey = "indexout";
	private static final String mainFontName = "mainfontname";
	private static final String mainFontSize = "mainfontsize";
	private static final String dialogFontName = "dialogfontname";
	private static final String dialogFontSize = "dialogfontsize";
	private static final String textAreaFontName = "textareafontname";
	private static final String textAreaFontSize = "textareafontsize";
	
	
//	----------------------- RESOURCES -------------------------------------
	
	private static final String bookIconPath = "images/Book-icon.png";
	private static final String arrowUpIconPath = "images/arrow_up.png";
	private static final String arrowDownIconPath = "images/arrow_down.png";
	
	
//	----------------------- ATTRIBUTES ------------------------------------
	
	private String resultFilePath;
	private String printFilePath;
	private String encodingIn;
	private String encodingOut;
	private Font mainFont;
	private Font dialogFont;
	private Font textAreaFont;
	private ImageIcon bookicon;
	private ImageIcon arrowUpIcon;
	private ImageIcon arrowDownIcon;
	private Color defaultTextFieldForeground;
	
	
//	------------------ CONSTRUCTOR & INSTANCE -----------------------------
	
	private static GeneralProperties instance;
	
	private GeneralProperties() 
	{
		super();
		//class loader
		ClassLoader classLoader = this.getClass().getClassLoader();
		//load default values via FILENAME
		try {
			load(new BufferedReader(new InputStreamReader(new FileInputStream(new File(defaultFile)))));
		} catch (IOException | NullPointerException e) {
			String message = String.format("%nFile not found at: %s%n", defaultFile);
			logger.warn(message);
		} catch (IllegalArgumentException e2) {
			String message = String.format("%nFile malformed at: %s%n", defaultFile);
			logger.warn(message);
		}
		//load file paths
		resultFilePath = getProperty(defaultResultFilePath);
		printFilePath = getProperty(defaultPrintFilePath);
		//load encodings
		encodingIn = getProperty(encodingInKey);
		encodingOut = getProperty(encodingOutKey);		
		//load Fonts
		String fontName = getProperty(mainFontName);
		int fontSize = getIntValue(mainFontSize);
		mainFont = new Font(fontName, Font.PLAIN, fontSize);
		String fontName2 = getProperty(dialogFontName);
		int fontSize2 = getIntValue(dialogFontSize);
		dialogFont = new Font(fontName2, Font.PLAIN, fontSize2);
		String fontName3 = getProperty(textAreaFontName);
		int fontSize3 = getIntValue(textAreaFontSize);
		textAreaFont = new Font(fontName3, Font.PLAIN, fontSize3);
		//load colors
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		defaultTextFieldForeground = defaults.getColor("TextField.foreground");
		//load images
		bookicon = new ImageIcon(classLoader.getResource(bookIconPath));
		arrowUpIcon = new ImageIcon(classLoader.getResource(arrowUpIconPath));
		arrowDownIcon = new ImageIcon(classLoader.getResource(arrowDownIconPath));
	}
	
	public static GeneralProperties getInstance() {
		if (instance == null)
			instance = new GeneralProperties();
		return instance;
	}
	
	
//	------------------------- GETTER --------------------------------------
	
	public Font getMainFont() {
		return mainFont;
	}
	
	public Font getDialogFont() {
		return dialogFont;
	}
	
	public Font getTextAreaFont() {
		return textAreaFont;
	}
	
	public Image getBookIcon() {
		return bookicon.getImage();
	}
	
	public Icon getArrowUpIcon() {
		return arrowUpIcon;
	}
	
	public Icon getArrowDownIcon() {
		return arrowDownIcon;
	}
	
	public Color getDefaultTFForegroundColor() {
		return defaultTextFieldForeground;
	}
	
	public String getDefaultFilePath() {
		return resultFilePath;
	}
	
	public String getDefaultPrintFilePath() {
		return printFilePath;
	}
	
	public String getEncodingIn() {
		return encodingIn;
	}
	
	public String getEncodingOut() {
		return encodingOut;
	}

	
//	------------------- BASIC VALUES -------------------------------
	
	public boolean getBooleanValue(String propertyName) 
	{
		String value = getProperty(propertyName).trim();
		try {
			boolean result = Boolean.parseBoolean(value);
			return result;
		}
		catch (NumberFormatException e) {
			logger.warn(e.getMessage());
			return false;
		}
	}
	
	
	public int getIntValue(String propertyName) 
	{
		String value = getProperty(propertyName).trim();
		try {
			int result = Integer.parseInt(value);
			return result;
		}
		catch (NumberFormatException e) {
			logger.warn(e.getMessage());
			return -1;
		}
	}

}
