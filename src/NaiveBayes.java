import io.TFReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import util.EmailDataset;
import util.EmailMessage;
import util.Pair;

/**
 * Class that represents a Naive Bayes Classifier
 * this class constructs classifiers from given TF 
 * files using the TFReader
 * 
 * 
 * @author davide
 * @author ainara
 *
 */
public class NaiveBayes {
	//token occurrency tables
	private EmailDataset trainData;
	private HashMap<Integer, Integer> spamOcurrTable; 
	private HashMap<Integer, Integer> hamOcurrTable;

	//token probability tables
	private HashMap<Integer, Double> spamProbTable; 
	private HashMap<Integer, Double> hamProbTable;

	private int threshold; //classification threshold

	private int dim;
	private double spamProb;
	private double hamProb;



	/**
	 * Constructor 
	 * 
	 * @param filename - String the file used to train the classifier
	 * @param sigTokens - Integer number of sinigicative tokens to be considered
	 * 
	 * @throws FileNotFoundException
	 */
	public NaiveBayes(String filename, Integer sigTokens) throws FileNotFoundException{
		//read and store the data
		TFReader reader = new TFReader(filename);
		EmailDataset data = reader.read();
		
		//calculate the threshold
		threshold= 8;//threashold(filename);


		initModel(data);
		//trainData=mostSigTokens(sigTokens,trainData);//filter most significative tokens 
	}
	
	public NaiveBayes(EmailDataset trainData, int threshold, int sigTokens) throws FileNotFoundException{
		this.threshold = threshold;
		initModel(trainData);
		//trainData=mostSigTokens(sigTokens,trainData);//filter most significative tokens 
	}

	/**
	 * Initializes this NaiveBayes instance with the train data supplied
	 * @param data EmailDataset - the data used to initialize and train the model
	 */
	private void initModel(EmailDataset data){
		trainData = data;
		dim=trainData.getDictionaryDim();

		//get and init token dictionary tables
		Pair<HashMap<Integer, Integer>> pair = trainData.getTotalTokenOcurr();
		spamOcurrTable = pair.getFirst();
		hamOcurrTable = pair.getSecont();

		//init probability tables
		spamProbTable = new HashMap<Integer, Double>();
		hamProbTable = new HashMap<Integer, Double>();
		tableTokenProb(); //fills the two previous tables

		//init class prob
		spamProb = getClassProb("spam");
		hamProb = getClassProb("ham");

	}



	public void algoritmoEM(String filename1, String filename2)throws FileNotFoundException{
		TFReader rf1 = new TFReader(filename1);
		TFReader rf2 = new TFReader(filename2);

		//read 2 files
		EmailDataset dataset1 = rf1.read();
		EmailDataset dataset2 = rf2.read();



		//merge data
		EmailDataset datasetMerged = new EmailDataset();
		datasetMerged.merge(dataset1);
		datasetMerged.merge(dataset2);

		//save the current 2 datasets for later iteration
		EmailDataset tempDataset = datasetMerged.clone();
		EmailDataset tempTrain = trainData.clone();


		//classify 2 sets
		classifyAll(datasetMerged, threshold); 
		
		//merge to training data
		datasetMerged.merge(trainData);
		

		double previousLikehood= 0;
		double currentLikehood = 0;
		
		
		initModel(datasetMerged);
		do{
			previousLikehood = currentLikehood;


			//some feedback
			System.out.println("Current Model:");
			System.out.println("num msg: "+trainData.getNumMessages());
			System.out.println("num ham:"+trainData.getNumHam());
			System.out.println("num spam:"+trainData.getNumSpam());


			datasetMerged = tempDataset.clone();
			//classify 
			classifyAll(datasetMerged, threshold);
			datasetMerged.merge(tempTrain);

			initModel(datasetMerged);//reset the model train data changes

			currentLikehood=getLikehood();

			System.out.println("diferença corrente: "+ (currentLikehood - previousLikehood));
			
		}while((currentLikehood - previousLikehood) == 0.0);


	}


	/**
	 * Method used to calculate the likelihood between classified 
	 * datasets with the expression:
	 * 
	 * <log( (pr(ti|c=1)*(pr(c=1)) + (pr(ti|c=-1)*(pr(c=-1))>
	 * 
	 * @return
	 */
	public double getLikehood(){
		double result=0;

		for(Integer token : spamProbTable.keySet()){
			result+= (spamProbTable.get(token) * spamProb) + 
			(hamProbTable.get(token) * hamProb);	
		}

		return Math.log(result);
	}


	//TODO rever isto
	/**
	 * Method that returns the EmailDataset filtered 
	 * considering only the most significative tokens
	 * 
	 * @param t threashold for # tokens
	 * @param trainData current taindata
	 * @return
	 */
	private void filterSignificativeTokens(int threshold){

		LinkedHashMap<Integer, Double> sortedTokens = new LinkedHashMap<Integer, Double>();
		List<Integer> significativeTokens = new LinkedList<Integer>();
		
		
		double result=0;
		for(Integer token: spamProbTable.keySet()){
			result = spamProbTable.get(token) / (hamProbTable.get(token)*1.0);
			sortedTokens.put(token, result);	
		}
		//order the current values
		sortedTokens= orderValues(sortedTokens);


		Iterator<Integer> it = sortedTokens.keySet().iterator();//key iterator
		int numTokens = 0;
		int currentToken = 0;

		if (sortedTokens.size() > threshold){
			while(numTokens < threshold){
				currentToken = it.next();
				significativeTokens.add(currentToken);
			}		
		}
		//create dataset with significative tokens
		
		EmailDataset finalData = new EmailDataset();
		EmailMessage currentMessage = null;
		
		for(EmailMessage m : trainData){
			currentMessage = m.clone();
			currentMessage.filter(significativeTokens);
			finalData.add(currentMessage);
			
		}
		trainData = finalData;

	}


/**
 *	Method used to create a hashmap of the expression 
 *	(P(ti | classe = +) / P(ti | classe = -))
 *	@param map 
 */
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



/**
 * Method used to classify a dataset of messages received
 * 
 * @param data EmailDataset - the data to be classified
 * @param threshold Integer - the threshold of classification
 * 
 * The dataset passed is modified adding the classifications to the data
 */
public void classifyAll(EmailDataset data, int threshold){
	int predict = 0;		
	for(EmailMessage m:data){
		predict= classify(m, threshold);
		//System.out.println(classePredic);
		m.classify(predict);	
	}
}


/**
 * Get the overall probability of a class to ocurr in all the dataset
 * 
 * @param c String class to be considered ("spam" / "ham")
 * 
 * @param dados 
 * @return
 */
private double getClassProb(String c){
	double result = 0;
	if(c.equals("spam")){
		result= trainData.getNumSpam() / (trainData.size()*1.0);
		
	}
	else if(c.equals("ham")){
		result= trainData.getNumHam() / (trainData.size() *1.0);
	}

	return result;
}


/**
 * Gets the probability of a token ocurr for a given class
 * 
 * @param token - Integer - the token to be checked
 * @param c - the class considered ("spam" / "ham")
 * @return probability Double - the conditional probability of the 
 * token occurency
 */
private double getTokenProb(int token, String c){

	HashMap<Integer, Integer> current = null;
	if(c.equals("spam")){
		current = spamOcurrTable;
	}if(c.equals("ham")){
		current = hamOcurrTable;
	}
	
	int ocurrToken = 0;
	if(current.containsKey(token))
		ocurrToken = current.get(token);
	
	
	int sumOcurrToken = 0;
	sumOcurrToken = allTokenOcurr(current);
	
	
	double denom = (sumOcurrToken + dim)*1.0;

	double result = (ocurrToken + 1) / denom;

	
	
	return result;

}

/**
 * Method that calculates the sum of all the ocurrencies 
 * of all the tokens in the given table
 * 
 * @param classTable spamOcurrTable or hamOcurrTable
 * 
 * @return sum Integer sum of the ocurrencies of all the tokens...
 */
private int allTokenOcurr(HashMap<Integer, Integer> classTable){
	int sumOcurrToken = 0;
	for(Integer key : classTable.keySet()){
		sumOcurrToken += classTable.get(key);	
	}
	return sumOcurrToken;
}


/**
 * Method that fills the probability tables of the model
 * 
 */
private void tableTokenProb(){
	for(Integer key : spamOcurrTable.keySet()){
		spamProbTable.put(key, getTokenProb(key, "spam"));
		
	}
	for(Integer key : hamOcurrTable.keySet()){
		
		hamProbTable.put(key, getTokenProb(key, "ham"));
	}
}

/**
 * Method that sets the classification value of the message
 * according to the current model
 * 
 * @param m - EmailMessage message to be classified
 * @param threshold - classification threshold
 * @return the classification given (1, -1)
 */
public int classify(EmailMessage m, double threshold){

	Double result =  (Double) Math.log10(spamProb / (hamProb*1.0)); 
	

	for(Integer token: m){
		if(spamProbTable.containsKey(token)){
			result += Math.log10(spamProbTable.get(token) / (hamProbTable.get(token)*1.0));
		}else{
			result+=  Math.log10((getTokenProb(token, "spam")/(getTokenProb(token, "ham")*1.0)));
			
		}
	}

	
	int classification = 0;
	
	if(result >  Math.log10(threshold))
		classification = 1;
	else
		classification = -1;

	//System.out.println("Classification: "+ classification);

	return classification;
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
public static LinkedHashMap<Double, Pair<Integer>> threashold(String filename)throws FileNotFoundException{
	LinkedHashMap<Double, Pair<Integer>> thresholds = new LinkedHashMap<Double, Pair<Integer>>();
	
	//read file
	TFReader reader = new TFReader(filename);
	EmailDataset readData = reader.read();
	Pair<EmailDataset> pair = readData.split();
	
	EmailDataset train = pair.getFirst();
	EmailDataset validation = pair.getSecont();

	NaiveBayes model = new NaiveBayes(train, 4, 0);
	
	//clasificaçao de emails com diferentes thresholdes
	int predict;
	EmailMessage currentMessage = null;
	for (int t=0; t<20;t++){
		int fp=0;
		int fn=0;
		for(EmailMessage m: validation){
			currentMessage = m.clone();
			predict = model.classify(currentMessage, t);	//classify the message
			if ((predict == 1) && (m.getClassification() == -1)) //FP
				fp++;
			else if((predict == -1) && (m.getClassification() == 1)) //FN
				fn++;

		}
		thresholds.put(new Double(t), new Pair<Integer>(fp, fn));
	}

	
	return thresholds;
}




}
