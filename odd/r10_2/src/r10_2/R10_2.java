package r10_2;

//�R�}���h���C�������ɂ͓��̓f�B���N�g����������
public class R10_2 {
	public static void main(String[] args) {
		DataPrinter dataPrinter = new DataPrinter(args[0]);
		dataPrinter.organize();
		dataPrinter.createOutputData();
		dataPrinter.print();
	}
}
