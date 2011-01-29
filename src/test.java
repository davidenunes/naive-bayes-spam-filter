

import io.TFReader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;


public class test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		TFReader tfr = new TFReader("labeled_train.tf");
		//List<HashMap<Integer, Integer>> maps = tfr.read();
		//System.out.println(maps.get(0).get(1));

	}

}
