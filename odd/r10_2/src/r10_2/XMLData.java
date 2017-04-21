package r10_2;

public class XMLData{
	private String data;
	private String id;
	private String score;
	private String fileName;

	public XMLData(String data, String fileName){
		this.data = data;
		this.fileName = fileName;
	}

	public void setScore(){
		// "..(JK|jk)..."にマッチする要素の次にある要素を点数とみなしてscoreに代入
	}

	public void setId(){
		// "..(JK|jk)..."にマッチする要素を学籍番号とみなす
	}

	public String getPrintData(){
		return this.id + " " + this.score + "#" + this.fileName;
	}
}
