package r10_2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFile implements ScoreFile{
	ArrayList<CSVData> dataList = new ArrayList<CSVData>();

	public CSVFile(File file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null){
				dataList.add(new CSVData(line.split(","), file.getName()));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cleanUp(){
		new Cleaner().cleanCSV(this.dataList);
	}

	public ArrayList<String> extract(){
		ArrayList<String> extractDataList = new ArrayList<String>();
		for(CSVData csvData : dataList){
			extractDataList.add(csvData.getPrintData());
		}
		return extractDataList;
	}
}
