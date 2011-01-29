import io.TFReader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NaiveBayes {
	private HashMap<Integer, Integer> spam; 
	private HashMap<Integer, Integer> ham;
	
	private HashMap<Integer, Double> spamProb; 
	private HashMap<Integer, Double> hamProb;
	
	private TFReader tfr;
	
	private int dim;
	
	public NaiveBayes(String filename) throws FileNotFoundException{
		this.tfr = new TFReader(filename);
		List<HashMap<Integer, Integer>> maps = tfr.trainRead();
		this.spam = maps.get(0);
		this.ham = maps.get(1);
		this.dim = getDim();
		
		tableTokenProb();
	}
	
	
	private double probClass(String c){
		double result = 0;
		if(c.equals("spam")){
			result = tfr.getNumSpam() / tfr.getNumSpam() + tfr.getNumHam();
		}
		else if(c.equals("ham")){
			result = tfr.getNumHam() / tfr.getNumSpam() + tfr.getNumHam();
		}
			
		return result;
	}
	
	private int getDim(){
		Set<Integer> keys1 = spam.keySet();
		Set<Integer> keys2 = ham.keySet();
		
		HashSet<Integer> finalDic = new HashSet<Integer>();
		
		for(Integer n: keys1){
			finalDic.add(n);	
		}
		
		for(Integer n: keys2){
			finalDic.add(n);	
		}
		
		return finalDic.size();		
	}
	
	private double getTokenProb(int token, String c){
		HashMap<Integer, Integer> current = null;
		if(c.equals("spam")){
			current = spam;
		}if(c.equals("ham")){
			current = ham;
		}
		
		int ocurrToken = current.get(token);
		int sumOcurrToken = 0;
		for(Integer key : current.keySet()){
			sumOcurrToken += current.get(key);	
		}
		
		double result = (ocurrToken + 1) / sumOcurrToken + dim;
		
		
		
		return result;
	}
	
	private void tableTokenProb(){
		for(Integer key : spam.keySet()){
			spamProb.put(key, getTokenProb(key, "spam"));
		}
		
		for(Integer key : ham.keySet()){
			hamProb.put(key, getTokenProb(key, "ham"));
		}
	}
	
	public int classif(HashMap<Integer, Integer> line, double threshold){
		threshold = 4; //TODO CHANGE THIS LATER
		double probClassSpam = probClass("spam");
		double probClassHam = probClass("ham");
		
		
		
		double result =  Math.log(probClassSpam / probClassHam); 
		
		for(Integer token: line.keySet()){
			result += Math.log(spamProb.get(token) / hamProb.get(token));
		}
		
		int classification = 0;
		
		
	
		if(result > Math.log(threshold))
			classification = 1;
		else
			classification = -1;
		
		
		return classification;
	}
	
	
	public static HashMap<Double,Integer[]> crossThreshold(List<Double> thresholds){
		
		
		
		
		return null;
	}
	
	
	

}
