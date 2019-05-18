package de.bitsandbooks.nel.nelcorrector;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;
import de.bitsandbooks.nel.nelcorrector.util.FileSelector;


public class MainWindow extends JFrame {
	
// ----------------------------- LOGGER INITIALIZATION ------------------------

	private static Logger logger = Logger.getLogger("PropertiesLogger");
	static {
		GeneralProperties props = GeneralProperties.getInstance();
		String log4JFile = FileSelector.getFileNameByRelativeOrAbsolutePath(props.getProperty(GeneralProperties.Log4JFilePath));
		PropertyConfigurator.configure(log4JFile);
		//logger.setLevel(Level.OFF);
	}
	
	
//	-------------------------- ATTRIBUTES ----------------------------------
	
	private GeneralProperties windowProperties;
	private MainWindowPanel mainPanel;
	private ComponentBuilder componentBuilder;
	private JDialog loadSaveDialog;
	private JComponent glassPane;
	
	
//------------------------------ CLOSER ------------------------------------

	private class WindowListenerExit extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			int confirm = JOptionPane.showOptionDialog((JFrame)e.getSource(), "Really Exit?", "Quit Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (confirm == 0) {
				((JFrame)e.getSource()).setVisible(false);
				((JFrame)e.getSource()).dispose();
				System.exit(0);			
			}
		}
	}
	
	
//	---------------------------- CONSTRUCTOR -------------------------------

	public MainWindow() {
		super("Correction Window");
		windowProperties = GeneralProperties.getInstance();
		//set l&f
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			logger.info("Could not set system look and feel.");
			logger.info(e.getMessage());
		}
		//set glass pane mouse adapter
		glassPane = (JComponent)this.getGlassPane();
		glassPane.addMouseListener(new MouseAdapter() {
	    });
		//set Tooltip & OptionPane font
		UIManager.put("ToolTip.font", windowProperties.getMainFont());
		UIManager.put("OptionPane.messageFont", windowProperties.getDialogFont());
		UIManager.put("OptionPane.buttonFont", windowProperties.getDialogFont());
		UIManager.put("Menu.font", windowProperties.getDialogFont());
		UIManager.put("MenuItem.font", windowProperties.getDialogFont());
		UIManager.put("CheckBoxMenuItem.font", windowProperties.getDialogFont());
		//set tool tip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setDismissDelay(Integer.MAX_VALUE);
		//set language
		Locale.setDefault(Locale.ENGLISH);
		JComponent.setDefaultLocale(Locale.ENGLISH);
		//set image
		Image img = windowProperties.getBookIcon();
		setIconImage(img);
		//load components
		componentBuilder = new ComponentBuilder(this);
		//load and set main panel
		mainPanel = new MainWindowPanel(ComponentMap.getInstance());
		setContentPane(mainPanel);
		//set close dialog
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JOptionPane.setDefaultLocale(Locale.ENGLISH);
		addWindowListener(new WindowListenerExit());
		//set size
		setLocationByPlatform(false);
		setResizable(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//pack
		pack();	
	}


//	---------------------------- WAIT DIALOG -----------------------------------
	
	public void showWaitDialog() 
	{
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		glassPane.setVisible(true);
		loadSaveDialog = new JDialog(this, "Wait", ModalityType.MODELESS);
		loadSaveDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
	    JLabel label = new JLabel("Please wait .......");
	    label.setFont(windowProperties.getDialogFont());
		panel.add(label, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(new Insets(20, 40, 20, 40)));
	    loadSaveDialog.add(panel);
	    loadSaveDialog.pack();
	    loadSaveDialog.setLocationRelativeTo(this);
	    loadSaveDialog.setSize(new Dimension(200,100));
	    loadSaveDialog.setVisible(true);  	
	}

	
	public void resetWaitDialog() 
	{
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		glassPane.setVisible(false);
		loadSaveDialog.dispose();
	}
}
