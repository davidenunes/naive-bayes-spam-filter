

import io.TFReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import util.EmailDataset;
import util.EmailMessage;


public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		NaiveBayes nb = new NaiveBayes("u0_eval_lab.tf", 20, 30);
//		
//		TFReader reader = new TFReader("labeled_train.tf");
//		
//		EmailDataset dataset = reader.read();
//		EmailMessage msg = dataset.getMessages().get(5);
//		
//		int predicted = nb.classify(msg, 4);
//		System.out.println(predicted);
		
		nb.algoritmoEM("labeled_train.tf", "u1_eval.tf");
		
		TFReader reader = new TFReader("u2_eval_lab.tf");
		
		EmailDataset actual = reader.read();
		EmailDataset predict = actual.clone();
		nb.classifyAll(predict);
		
		List<Integer> classifsActual = actual.getClassifications();
		List<Integer> classifsPredicted = predict.getClassifications(); //post classif
		
		Iterator<Integer> it = classifsPredicted.iterator();
		
		int count = 0;
		for(Integer c : classifsActual){
			//System.out.print("c: "+c+" ");
			//System.out.println("c: "+it.next()+" ");
			if(c == it.next())
				count++;
		}
		
		System.out.println("correct: "+count/(classifsActual.size()*1.0));
		
		
		
	}
	
	
	public static LinkedHashMap<Integer, Double> orderValues(LinkedHashMap<Integer, Double> map){ //de valor mais pequeno a mais grande
		LinkedHashMap<Integer, Double> newMap = new LinkedHashMap<Integer, Double>();
			
		ArrayList<Double> values = new ArrayList<Double>(map.values());
		Collections.sort(values);
		
		for(Double value : values){
			for(Integer token : map.keySet()){
				if(value == map.get(token)){
					newMap.put(token, value);
				}
				
			}
		}
		
		return newMap;
	}
}
