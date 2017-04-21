package r10_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DataPrinter {
	ArrayList<ScoreFile> files = new ArrayList<ScoreFile>();
	ArrayList<String> outputData = new ArrayList<String>();
	ArrayList<String> duplicationList = new ArrayList<String>();

	public DataPrinter(String arg){
		FileScanner fileScanner = FileScanner.getInstance();// �r����ScoreFile�����܂��Ȃ��Ńt�@�C���ǂݍ��񂾎��_��ScoreData��ID��Score�̃Z�b�g�܂ŏI��点��DataList�����ɂ����outputData���K�v�Ȃ��Ȃ�H
		files.addAll(fileScanner.getScoreFiles(arg));
	}

	public void organize(){
		for(ScoreFile file : files){
			file.cleanUp();
		}
	}

	public void createOutputData(){
		for(ScoreFile file : files){
			outputData.addAll(file.extract());
		}
		Collections.sort(outputData);
	}

	public void print(){
		String tmpId = "*******";
		for(String line : outputData){
			System.out.println(line.split("#")[0]);
			if(line.substring(0,7).equals(tmpId.substring(0,7))){
				duplicationList.add(tmpId);
				duplicationList.add(line);
			}
			tmpId = line;
		}
		System.out.println();
		System.out.println("～重複しているデータ～");
		Set<String> set = new HashSet<String>(duplicationList);
		duplicationList = new ArrayList<String>(set);
		Collections.sort(duplicationList);
		for(String line : duplicationList){
			System.out.println(line);
		}
	}
}
