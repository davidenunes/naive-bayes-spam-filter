

import io.TFReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
