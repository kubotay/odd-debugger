package r7_3;

public class CSVData {
	private String[] data;
	private String id;
	private String score;

	public CSVData(String[] data){
		this.data = data;
	}

	public int check(int scoreIndex){

		return scoreIndex;
	}

	public String[] getData(){
		return this.data;
	}

	public void setScore(int scoreIndex){
		if(scoreIndex >= 0){
			this.score = this.data[scoreIndex];
		}else{
			for(int i = 0; i < this.data.length-1; i++){
				if(this.data[i].matches("..(JK|jk)...")){
					this.score = this.data[i+1];
					break;
				}
			}
		}
	}

	public void setId(){
		for(String str : this.data){
			if(str.matches("..(JK|jk)...")){
				this.id = str;
				break;
			}
		}
	}

	public String getPrintData(){
		return this.id + " " + this.score;
	}
}
