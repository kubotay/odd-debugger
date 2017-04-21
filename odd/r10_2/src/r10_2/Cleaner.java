package r10_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;

public class Cleaner {

	public Cleaner(){

	}

	public void cleanCSV(ArrayList<CSVData> dataList){
		Iterator<CSVData> itr = dataList.iterator();
		int scoreIndex = -1;
		while(itr.hasNext()){
			CSVData csvData = itr.next();
			if(csvData.getData().length < 2){
				itr.remove();
			}else if(Arrays.asList(csvData.getData()).contains("学籍") || Arrays.asList(csvData.getData()).contains("学籍番号")){
				for(int i = 0; i < csvData.getData().length; i++){
					if("点数".equals(csvData.getData()[i]) || "総合点数".equals(csvData.getData()[i])){
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

	public void cleanXLSX(ArrayList<XLSXData> dataList){
		Iterator<XLSXData> itr = dataList.iterator();
		int scoreIndex = -1;
		while(itr.hasNext()){
			XLSXData xlsxData = itr.next();
			if(xlsxData.getData() == null){
				continue;
			}
			if(xlsxData.getData().getLastCellNum() < 2){
				itr.remove();
				continue;
			}
			xlsxData.setId();
			xlsxData.setScore(scoreIndex);

			if(scoreIndex != -1){
				continue;
			}

			for(int i = xlsxData.getData().getFirstCellNum(); i < xlsxData.getData().getLastCellNum(); i++){
				Cell cell = xlsxData.getData().getCell(i);
				if("点数".equals(cell.getStringCellValue()) || "総合点数".equals(cell.getStringCellValue())){
					scoreIndex = i;
					itr.remove();
					break;
				}
			}
		}
	}

	public void cleanXML(ArrayList<XMLData> dataList){
		// Iteratorでwhile文を回す
		// 要素数２未満なら成績データでないとしてremove()
		// setId()とsetScore()を呼ぶ
	}
}
