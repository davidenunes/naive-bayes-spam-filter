

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
		
		NaiveBayes nb = new NaiveBayes("labeled_train.tf", 20);
		nb.algoritmoEM("u0_eval.tf", "u1_eval.tf");
		
	
	}
}
