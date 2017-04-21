package r7_3;

import java.util.ArrayList;
import java.util.Collections;

public class DataPrinter {
	ArrayList<CSVFile> csvFiles = new ArrayList<CSVFile>();
	ArrayList<String> outputData = new ArrayList<String>();

	public DataPrinter(String[] args){
		for(String arg : args){
			csvFiles.add(new CSVFile(arg));
		}
	}

	public void organize(){
		for(CSVFile file : csvFiles){
			file.cleanUp();
		}
	}

	public void createOutputData(){
		for(CSVFile file : csvFiles){
			outputData.addAll(file.extract());
		}
		Collections.sort(outputData);
		outputData.add(0, "�w�Дԍ� �_��");
	}

	public void print(){
		for(String line : outputData){
			System.out.println(line);
		}
	}
}
