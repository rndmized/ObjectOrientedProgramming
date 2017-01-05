package ie.gmit.sw.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderReader {
	private String path;
	
	public FolderReader(String path){
		this.path = path;	
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getList() {
		List<String> list = new ArrayList<>();
		File[] listOfFiles = getFileArray();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				list.add("File " + listOfFiles[i].getName());
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				list.add("Directory " + listOfFiles[i].getName());
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		return list;

	}

	private File[] getFileArray() {
		File folder = new File(path);
		return folder.listFiles();
	}

}
