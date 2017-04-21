package r10_2;

//コマンドライン引数には入力ディレクトリ名を入れる
public class R10_2 {
	public static void main(String[] args) {
		DataPrinter dataPrinter = new DataPrinter(args[0]);
		dataPrinter.organize();
		dataPrinter.createOutputData();
		dataPrinter.print();
	}
}
