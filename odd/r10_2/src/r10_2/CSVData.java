package r10_2;

public class CSVData{
	private String[] data;
	private String id;
	private String score;
	private String fileName;

	public CSVData(String[] data, String fileName){
		this.data = data;
		this.fileName = fileName;
	}

	public String[] getData(){
		return this.data;
	}

	public void setScore(int scoreIndex){
		if(scoreIndex >= 0){
			this.score = this.data[scoreIndex];
		}else{
			for(int i = 0; i < this.data.length-1; i++){
				if(this.data[i].matches("..(JK|jk|JKM)...")){
					this.score = this.data[i+1];
					break;
				}
			}
		}
	}

	public void setId(){
		for(String str : data){
			if(str.matches("..(JK|jk|JKM)...")){
				this.id = str;
				break;
			}
		}
	}

	public String getPrintData(){
		return this.id + " " + this.score + "#" + this.fileName;
	}
}
