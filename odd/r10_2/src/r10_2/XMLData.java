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
		// "..(JK|jk)..."�Ƀ}�b�`����v�f�̎��ɂ���v�f��_���Ƃ݂Ȃ���score�ɑ��
	}

	public void setId(){
		// "..(JK|jk)..."�Ƀ}�b�`����v�f���w�Дԍ��Ƃ݂Ȃ�
	}

	public String getPrintData(){
		return this.id + " " + this.score + "#" + this.fileName;
	}
}
