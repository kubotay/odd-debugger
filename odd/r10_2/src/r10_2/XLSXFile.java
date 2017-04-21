package r10_2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXFile implements ScoreFile{
	ArrayList<XLSXData> dataList = new ArrayList<XLSXData>();

	public XLSXFile(File file){
		Workbook wb = new XSSFWorkbook();
		try{
			wb = WorkbookFactory.create(file);
		}catch(InvalidFormatException | IOException e1){
			e1.printStackTrace();
		}
		Sheet sheet = wb.getSheetAt(0);
		int rowMax = sheet.getLastRowNum();
		for(int rowNumber = sheet.getFirstRowNum(); rowNumber < rowMax + 1; rowNumber++){
			dataList.add(new XLSXData(sheet.getRow(rowNumber), file.getName()));
		}

		try{
			wb.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void cleanUp(){
		new Cleaner().cleanXLSX(this.dataList);
	}

	public ArrayList<String> extract(){
		ArrayList<String> extractDataList = new ArrayList<String>();
		for(XLSXData xlsxData : dataList){
			extractDataList.add(xlsxData.getPrintData());
		}
		return extractDataList;
	}
}
