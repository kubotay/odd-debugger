package r10_2;

import java.io.File;
import java.util.ArrayList;

public class XMLFile implements ScoreFile{
	ArrayList<XMLData> dataList = new ArrayList<>();

	public XMLFile(File file){
		//�t�@�C����ǂݍ���Ŋw����l����XMLData�I�u�W�F�N�g�𐶐����AdataList�Ɋi�[
	}

	public void cleanUp() {
		//new Cleaner().cleanXML(this.dataList);
	}

	public ArrayList<String> extract() {
		// �g��for����dataList����XMLData�I�u�W�F�N�g������u�w�Дԍ� �_��#�t�@�C�����v�̕�������󂯎��
		// ���X�g�ɂ������̂�ԋp����
		return null;
	}

}
