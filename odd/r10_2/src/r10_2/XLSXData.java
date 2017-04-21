package r10_2;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSXData {
	private Row data;
	private String id;
	private String score;
	private String fileName;

	public XLSXData(Row data, String fileName){
		this.data = data;
		this.fileName = fileName;
	}

	public Row getData(){
		return this.data;
	}

	public void setScore(int scoreIndex){
		if(scoreIndex >= 0){
			this.score = String.valueOf((int)this.data.getCell(scoreIndex).getNumericCellValue());
		}else{
			for(int i = this.data.getFirstCellNum(); i < this.data.getLastCellNum()-1; i++){
				if(this.data.getCell(i).getStringCellValue().matches("..(JK|jk)...")){
					this.score = this.data.getCell(i+1).getStringCellValue();
					break;
				}
			}
		}
	}

	public void setId(){
		for(Cell cell : data){
			if(cell.getStringCellValue().matches("..(JK|jk)...")){
				this.id = cell.getStringCellValue();
				break;
			}
		}
	}

	public String getPrintData(){
		return this.id + " " + this.score + "#" + this.fileName;
	}
}
