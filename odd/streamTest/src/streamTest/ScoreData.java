package streamTest;

public class ScoreData {
	String id;
	String score;

	public ScoreData(String data){
		String[] array = data.split(",");
		this.id = array[1];
		this.score = array[2];
	}

	public String getId(){return this.id;}

	public String print(){
		return this.id + " " + this.score;
	}
}
