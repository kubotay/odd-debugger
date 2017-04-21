package r10_2;

import java.io.File;
import java.util.ArrayList;

public class FileScanner {
	public static FileScanner fileScanner = new FileScanner();

	public static FileScanner getInstance(){
		return fileScanner;
	}

	public ArrayList<ScoreFile> getScoreFiles(String arg){
		ArrayList<ScoreFile> list = new ArrayList<ScoreFile>();
		File inputDirectory = new File(arg);
		list.addAll(scanDirectory(inputDirectory));
		return list;
	}

	private ArrayList<ScoreFile> scanDirectory(File inputDirectory){
		ArrayList<ScoreFile> list = new ArrayList<ScoreFile>();
		ScoreFileFactory factory = ScoreFileFactory.getInstance();
		for(File child : inputDirectory.listFiles()){
			if(child.isDirectory()){
				list.addAll(scanDirectory(child));
			}else{
				list.add(factory.create(child));
			}
		}
		return list;
	}
}
