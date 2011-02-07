

import io.TFReader;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import util.EmailDataset;
import util.Pair;

/**
 * Main class for testing purposes
 * 
 * @author davide
 * @author ainara
 */

public class RunSpam {

	/**
	 * prints a menu....
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int op = -1;
		Scanner scanner = new Scanner(System.in);
		do{
			System.out.println("******* Welcome to the great spam classifier :D *******");
			System.out.println("1.Train with labeled_train, EM with u0 and u2, classify u2");
			System.out.println("2.Train with u0, EM with labeled_train  and u2, classify u2");
			System.out.println("3.Table thresholds");
			System.out.println("4.Table thresholds with EM");
			System.out.println("0.Exit");
			op = scanner.nextInt();


			switch(op){
			case 0:
				System.out.println("Bye!!");
				break;
			case 1:
				trainRunEMandClassify("labeled_train.tf",
						"u0_eval.tf",
						"u1_eval.tf",
						"u2_eval_lab.tf"
						,30.0,0);
				break;
			case 2:
				trainRunEMandClassify("u0_eval_lab.tf",
						"labeled_train.tf",
						"u1_eval.tf",
						"u2_eval_lab.tf",
						30.0,0);
				break;
			case 3:
				tableThreshold("labeled_train.tf");
				break;

			case 4:
				tableThresholdsEM("u0_eval_lab.tf",
						"labeled_train.tf",
						"u1_eval.tf",
						"u2_eval_lab.tf"
						);
				break;
			}

		}while(op != 0);






	}

	/**
	 * Method used to test the model construction, EM algorithm and classification
	 * @param file1Train
	 * @param file2EM
	 * @param file3EM
	 * @param file4Class
	 * @param threshold
	 * @param significTokens
	 * @throws FileNotFoundException
	 */
	private static void trainRunEMandClassify(String file1Train, 
			String file2EM, 
			String file3EM, 
			String file4Class, 
			double threshold,
			int significTokens) 
	throws FileNotFoundException{
		//create the basic naive bayes model
		NaiveBayes nb = new NaiveBayes(file1Train, threshold, significTokens);
		//refine the model
		nb.algoritmoEM(file2EM, file3EM, true);

		//classify the last file
		TFReader reader = new TFReader(file4Class);

		//check the correctness
		EmailDataset actual = reader.read();
		EmailDataset predict = actual.clone();
		nb.classifyAll(predict);
		printFPFN(actual.getClassifications(), predict.getClassifications());



	}

	
	/**
	 * Method to test the threshold list considering the EM method
	 * @param file1Train
	 * @param file2EM
	 * @param file3EM
	 * @param file4Class
	 * @throws FileNotFoundException
	 */
	private static void tableThresholdsEM(String file1Train, 
			String file2EM, 
			String file3EM, 
			String file4Class) 
	throws FileNotFoundException{
		NaiveBayes nb = null;
		for(int t=0; t<=30; t++){
			//create the basic naive bayes model
			nb = new NaiveBayes(file1Train, t, 0);
			//refine the model
			nb.algoritmoEM(file2EM, file3EM, false);

			//classify the last file
			TFReader reader = new TFReader(file4Class);

			//check the correctness
			EmailDataset actual = reader.read();
			EmailDataset predict = actual.clone();
			nb.classifyAll(predict);
			printFPFN(actual.getClassifications(), predict.getClassifications());
			System.out.println("T:"+t);
		}

	}




	/**
	 * Tables the threshols with the method presented (no enunciado)
	 * @param filename
	 * @throws FileNotFoundException
	 */
	private static void tableThreshold(String filename) throws FileNotFoundException{
		LinkedHashMap<Double, Pair<Integer>> table = threashold(filename);
		for(Double t: table.keySet()){//print the table
			System.out.println("T: "+t+" FP: "+table.get(t).getFirst()+
					" FN: "+table.get(t).getFirst());

		}
	}


	/**
	 * Returns a list of Tables of false positive and false negative
	 * classifications to aid in the threshold choice
	 * 
	 * @param filename - labelled data file to be used
	 * @return List<ThresholdTable> FP and FN table list
	 * 
	 * @throws FileNotFoundException
	 */
	private static LinkedHashMap<Double, Pair<Integer>> threashold(String filename)throws FileNotFoundException{
		LinkedHashMap<Double, Pair<Integer>> thresholds = new LinkedHashMap<Double, Pair<Integer>>();

		//read file
		TFReader reader = new TFReader(filename);
		EmailDataset readData = reader.read();
		Pair<EmailDataset> pair = readData.split();

		EmailDataset train = pair.getFirst();
		EmailDataset validation = pair.getSecont();

		NaiveBayes model = null;

		//clasificaçao de emails com diferentes thresholdes
		for (int t=0; t<30;t++){
			model = new NaiveBayes(train.clone(), t, 0);//create a new model

			//check the correctness
			EmailDataset predict = validation.clone();

			model.classifyAll(predict);
			List<Integer> classifsActual = validation.getClassifications();
			List<Integer> classifsPredicted = predict.getClassifications(); //post classif

			//System.out.println("actual:"+classifsActual);
			//System.out.println("predicted:"+classifsPredicted);

			Iterator<Integer> it = classifsPredicted.iterator();

			int fp = 0;
			int fn = 0;
			for(Integer act : classifsActual){//transverse the actual classifications
				int pred = it.next();//get predicted
				//System.out.println("act:"+act+" pred:"+pred);
				if(act == 1 && pred == -1){
					fn++;
				}else if(act == -1 && pred == 1){
					fp++;
				}


			}
			thresholds.put(new Double(t), new Pair<Integer>(fp, fn));



		}


		return thresholds;
	}


	/**
	 * Prints the FP and FN comparing 2 classification lists
	 * 
	 * @param ca - actual classification List<Integer>
	 * @param cp - predicted classification List<Integer>
	 */
	private static void printFPFN(List<Integer> ca, List<Integer> cp){
		List<Integer> classifsActual = ca;
		List<Integer> classifsPredicted = cp;

		Iterator<Integer> it = classifsPredicted.iterator();

		int correct = 0;
		int fp = 0;
		int fn = 0;
		for(Integer act : classifsActual){//transverse the actual classifications
			int pred = it.next();//get predicted
			if(act == pred)		//correct classification
				correct++;
			else{
				if(act == 1 && pred == -1)
					fn++;
				else if(act == -1 && pred == 1)
					fp++;

			}
		}

		System.out.print("correct: "+correct/(classifsActual.size()*1.0));
		System.out.print(" FP:"+fp);
		System.out.println(" FN: "+fn);
	}

}
