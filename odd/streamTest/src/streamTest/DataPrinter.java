package streamTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataPrinter {
	List<ScoreData> list = new ArrayList<ScoreData>();

	public void print(String arg){
		File file = new File(arg);
		ScoreDataFactory scoreDataFactory = new ScoreDataFactory();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			list = br.lines()
				.map(scoreDataFactory::create)
				.collect(Collectors.toList());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		list.parallelStream()/*.sorted(new Comparator<ScoreData>() {
			@Override
			public int compare(ScoreData d1, ScoreData d2){
				return d1.getId().compareTo(d2.getId());
			}
		})*/
		.map(data -> data.print())
			.forEach(System.out::println);
	}
}
