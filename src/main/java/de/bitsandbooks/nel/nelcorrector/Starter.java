package de.bitsandbooks.nel.nelcorrector;

public class Starter {
	
//	--------------------------- FIELDS -------------------------------------
	
	private static MainWindow mainWindow;
	
	
//	---------------------- WINDOW STARTER ----------------------------------
	
	private static void openWindow() {
		if (mainWindow == null) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainWindow = new MainWindow();
					mainWindow.setVisible(true);
				}
			});
		}
	}
	

//	---------------------------- MAIN METHOD -------------------------------

	public static void main(String[] args) {
		//go
		openWindow();
	}

}
