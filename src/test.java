

import io.TFReader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import util.EmailDataset;
import util.EmailMessage;


public class test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		//NaiveBayes nb=new NaiveBayes("labeled_train.tf",5);
		TFReader reader = new TFReader("labeled_train.tf");
		EmailDataset ds = reader.read();
		
		//print the dataset
		//System.out.println(ds.toString());
		
		//test size
		System.out.println(ds.size());
		
		
		//test the clone operation
		EmailDataset ds2 = ds.clone();
		System.out.println(ds2.toString());
		System.out.println(ds2.size());
		
	
	}
}
