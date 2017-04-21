package r10_2;

import java.io.File;
import java.util.ArrayList;

public class ScoreFileFactory {
	private static ScoreFileFactory scoreFileFactory = new ScoreFileFactory();
	private ScoreFileFactory(){

	}

	public static ScoreFileFactory getInstance(){
		return scoreFileFactory;
	}

	public ScoreFile create(File file){
		if(file.getPath().endsWith(".csv")){
			return new CSVFile(file);
		}else if(file.getPath().endsWith(".xlsx")){
			return new XLSXFile(file);
		}/*
		 else if(file.getPath().endsWith(".xml")){
		 	return XMLFile(file);
		 }
		*/

		return new ScoreFile(){
			@Override
			public void cleanUp() {

			}

			@Override
			public ArrayList<String> extract() {
				return new ArrayList<String>();
			}

		};
	}
}
