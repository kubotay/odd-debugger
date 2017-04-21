package r7_3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class CSVFile {
	ArrayList<CSVData> dataList = new ArrayList<CSVData>();

	public CSVFile(String path){
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
			String line;
			while((line = bufferedReader.readLine()) != null){
				dataList.add(new CSVData(line.split(",")));
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cleanUp(){
		Iterator<CSVData> itr = dataList.iterator();
		int scoreIndex = -1;
		while(itr.hasNext()){
			CSVData csvData = itr.next();
			if(csvData.getData().length < 2){
				itr.remove();
			}else if(Arrays.asList(csvData.getData()).contains("�w��") || Arrays.asList(csvData.getData()).contains("�w�Дԍ�")){
				for(int i = 0; i < csvData.getData().length; i++){
					if(csvData.getData()[i].equals("�_��") || csvData.getData()[i].equals("�����_��")){
						scoreIndex = i;
						break;
					}
				}
				itr.remove();
			}else{
				csvData.setId();
				csvData.setScore(scoreIndex);
			}
		}
	}

	public ArrayList<String> extract(){
		ArrayList<String> extractDataList = new ArrayList<String>();
		for(CSVData csvData : dataList){
			extractDataList.add(csvData.getPrintData());
		}
		return extractDataList;
	}
}
