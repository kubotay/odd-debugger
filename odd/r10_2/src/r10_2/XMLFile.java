package r10_2;

import java.io.File;
import java.util.ArrayList;

public class XMLFile implements ScoreFile{
	ArrayList<XMLData> dataList = new ArrayList<>();

	public XMLFile(File file){
		//ファイルを読み込んで学生一人ずつXMLDataオブジェクトを生成し、dataListに格納
	}

	public void cleanUp() {
		//new Cleaner().cleanXML(this.dataList);
	}

	public ArrayList<String> extract() {
		// 拡張for文でdataList内のXMLDataオブジェクト一つ一つから「学籍番号 点数#ファイル名」の文字列を受け取り
		// リストにしたものを返却する
		return null;
	}

}
