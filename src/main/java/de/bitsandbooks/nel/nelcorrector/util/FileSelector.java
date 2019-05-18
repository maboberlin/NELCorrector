package de.bitsandbooks.nel.nelcorrector.util;

import java.io.File;

public class FileSelector {
	
	public static File getFileByRelativeOrAbsolutePath(String filenameTry)
	{
		File result = null;
		String filename = getFileNameByRelativeOrAbsolutePath(filenameTry);
		if (filename != null) 
			result = new File(filename);
		return result;
	}

	
	public static String getFileNameByRelativeOrAbsolutePath(String filename) 
	{
		String filePath = filename.replace('\\', '/');
		File tryFile = new File(filePath);
		if (tryFile.exists() && !tryFile.isDirectory()) 
			return filePath;
		//try relative path
		String workingDir = System.getProperty("user.dir");
		filePath = workingDir.concat(filePath);
		filePath = filePath.replace('\\', '/');
		tryFile = new File(filePath);
		if (tryFile.exists() && !tryFile.isDirectory()) 
			return filePath;
		return null;
	}
	
}
