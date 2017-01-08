package ie.gmit.sw.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * This class contains methods to return a List of files in a folder.
 * @author RnDMizeD
 * @version 1.0
 */
public class FolderReader {
	private String path;
	/**
	 * Construct a FolderReader for given path.
	 * @param path the path where files will be read from.
	 */
	public FolderReader(String path){
		this.path = path;	
	}
/**
 * 
 * @return current path.
 */
	public String getPath() {
		return path;
	}
/**
 * 
 * @param path Set the path.
 */
	public void setPath(String path) {
		this.path = path;
	}
/**
 * This method returns a List of files
 * @return List of Strings containing file names and directories.
 */
	public List<String> getList() {
		/*
		 * Create array list and get file array from folder contents.
		 */
		List<String> list = new ArrayList<>();
		File[] listOfFiles = getFileArray();
		/*
		 * for each element in the folder, determine whether it is a file or a 
		 * directory and add it to the list consequently.
		 */
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				list.add("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				list.add("Directory " + listOfFiles[i].getName());
			}
		}
		return list;

	}
/**
 * 
 * @return a File array of files of a folder.
 */
	private File[] getFileArray() {
		File folder = new File(path);
		return folder.listFiles();
	}

}
