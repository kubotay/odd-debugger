package r7_3;

public class R7_3 {
	public static void main(String[] args){
		DataPrinter dataPrinter = new DataPrinter(args);
		dataPrinter.organize();
		dataPrinter.createOutputData();
		dataPrinter.print();
	}
}
