import io.TFReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.text.StyleContext.SmallAttributeSet;

import util.EmailDataset;
import util.EmailMessage;
import util.Pair;
import util.TabelaLimiar;

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
	private HashMap<Integer, Integer> spam; 
	private HashMap<Integer, Integer> ham;

	private HashMap<Integer, Integer> tokensFinais;


	private Pair<HashMap<Integer,Integer>> par;

	private HashMap<Integer, Double> spamProb; 
	private HashMap<Integer, Double> hamProb;


	private TFReader tfr,tfr2;

	private int dim;

	private EmailDataset dadosTrain;
	private EmailDataset dadosClasificacao;
	private EmailDataset dadosFinais;
	private List<EmailMessage> messages;

	private List<EmailMessage> messagesTrain;
	private List<EmailMessage> messagesClasificacao;
	private List<EmailMessage> messagesFinais;
	private EmailMessage emailFinai;

	private EmailDataset previousItDataset; 

	Vector <TabelaLimiar> v;
	private double limiar;

	/**
	 * Constructor 
	 * 
	 * @param filename - String the file used to train the classifier
	 * @param sigTokens - Integer number of sinigicative tokens to be considered
	 * 
	 * @throws FileNotFoundException
	 */
	public NaiveBayes(String filename, Integer sigTokens) throws FileNotFoundException{

		//calcular threshold
		this.limiar= 8;//threashold(filename);

		this.tfr=new TFReader(filename);
		this.dadosTrain=tfr.read();
		//dadosTrain=mostSigTokens(sigTokens,dadosTrain);
		createModelNB(dadosTrain);
		

		/* //Algoritmo EM
		 //1.- Calcular NB so com labeled_train.tf

		 createModelNB(this.dadosTrain);
		 //calculo verosimilhan�a
		 double x=calcularVerosimilhan�a();

		 //2.- Step E
		 obterDadosClasifica�ao(filename2);
		 addDadosValida�ao(filename3);

		  clasificationNB(this.dadosClasifica�ao); 
		  addDadosClasifica�aoADadosTrain();


		 //1. store current dataset
		  previousItDataset = dadosTrain.clone();

		 //2.
		 createModelNB(this.dadosTrain);

		 //3.comparas verisimilhan�a

		 //iterar voltar a 1
		 */	 
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
		EmailDataset tempTrain = dadosTrain.clone();
		
		
		//1 calculate likehood
		


		
		
		clasificationNB(datasetMerged); 
		datasetMerged.merge(dadosTrain);
		dadosTrain = datasetMerged;
		
		double previousLikehood= 0;
		double currentLikehood = 0;
		
		int i=0; //TODO delete later
		//ITERATE
		
		createModelNB(this.dadosTrain);
		do{
			
			previousLikehood = currentLikehood;
			
			
			//some feedback
			System.out.println("Modelo2:");
			System.out.println("nummsg: "+dadosTrain.getNumMessages());
			System.out.println("numham:"+dadosTrain.getNumHam());
			System.out.println("numspam:"+dadosTrain.getNumSpam());
			
			
			datasetMerged = tempDataset.clone();
			//classify 
			clasificationNB(datasetMerged);
			
			
			datasetMerged.merge(tempTrain);
			
			this.dadosTrain = datasetMerged.clone();
			
			
			createModelNB(this.dadosTrain);
			
			currentLikehood=calcularVerosimilhanca();
			
			System.out.println("diferença corrente: "+(currentLikehood - previousLikehood));
			i++;
		}while(i<3);
		
		
		
		
		
		
		

		
		  
		
		

		
	}


	/**
	 * Method used to calculate the likelihood between classified 
	 * datasets with the expression:
	 * 
	 * <log( (pr(ti|c=1)*(pr(c=1)) + (pr(ti|c=-1)*(pr(c=-1))>
	 * 
	 * @return
	 */
	private double calcularVerosimilhanca(){

		//log( (pr(ti|c=1)*(pr(c=1)) + (pr(ti|c=-1)*(pr(c=-1))

		double probClassSpam = probClass("spam" ,dadosTrain);
		double probClassHam = probClass("ham",dadosTrain);
		double result=0;

		Iterator it= spamProb.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry e=(Map.Entry)it.next();
			result+=(spamProb.get(e.getKey())* probClassSpam) + (hamProb.get(e.getKey())* probClassHam);
		}
		return Math.log(result);
	}

	
	//TODO rever isto
	/**
	 * Method that returns the EmailDataset filtered 
	 * considering only the most significative tokens
	 * 
	 * @param t threashold for # tokens
	 * @param dadosTrain current taindata
	 * @return
	 */
	private EmailDataset mostSigTokens(Integer t, EmailDataset dadosTrain){

		createModelNB(dadosTrain);

		LinkedHashMap sortTokens = new LinkedHashMap();
		LinkedHashMap sortTokensFinais=new LinkedHashMap();


		Iterator it= spamProb.entrySet().iterator();
		double result=0;
		while(it.hasNext()){
			Map.Entry e=(Map.Entry)it.next();
			result=spamProb.get(e.getKey()) / hamProb.get(e.getKey());
			sortTokens.put((Integer)e.getKey(), result);
		}

		sortTokens=orderForValues(sortTokens);

		// criar HashMap so com T elementos --> sortTokensFinais

		if (sortTokens.size()> t){
			int count=0;
			Iterator it2=sortTokens.entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry e=(Map.Entry)it2.next();
				count++;
				if(count<=t){
					sortTokensFinais.put(e.getKey(), e.getValue());
				}
			}

			//criar novo EmailDataset s� com T tokens mais significativos 
			dadosFinais=dadosTrain.clone();
			messagesFinais=new LinkedList<EmailMessage>();

			for(EmailMessage m : dadosFinais.getMessages()){
				emailFinai=m.clone();
				Iterator it3= m.getTokens().entrySet().iterator();
				while(it3.hasNext()){
					Map.Entry e=(Map.Entry)it3.next();
					if(!sortTokensFinais.containsKey(e.getKey())){
						emailFinai.getTokens().remove(e.getKey());
					}
				}
				messagesFinais.add(emailFinai);
			}
			dadosTrain=new EmailDataset(messagesFinais);

		}//end if
		//se nao ficam como todos os dados de dadosTrain
		return dadosTrain;

	}


	/**
	 *	Method used to create a hashmap of the expression 
	 *	(P(ti | classe = +) / P(ti | classe = -))
	 *	@param map 
	 */
	private static LinkedHashMap orderForValues(LinkedHashMap map){ //de valor mais pequeno a mais grande
		LinkedHashMap newMap = new LinkedHashMap();
		ArrayList values = new ArrayList(map.values());

		Collections.sort(values);
		Iterator it = values.iterator();
		double tmp=0;
		while(it.hasNext()){

			tmp = Double.parseDouble(it.next().toString());
			Map.Entry k;
			map.entrySet();

			Iterator it2=map.entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry e=(Map.Entry)it2.next();

				if(tmp==Double.parseDouble(e.getValue().toString())){
					newMap.put(e.getKey(), e.getValue());
				}
			}
		}
		return newMap;
	}


	/**
	 * Creates a model from the datast
	 * TODO simplify?
	 * 
	 * @param dadosTrain
	 */
	private void createModelNB(EmailDataset dadosTrain){

		dim=dadosTrain.getDictionaryDim();
		par=dadosTrain.getTotalTokenOcurr();
		spam=par.getFirst();
		ham=par.getSecont();
		spamProb = new HashMap<Integer, Double>();
		hamProb = new HashMap<Integer, Double>();
		tableTokenProb();
	}


	private void clasificationNB(EmailDataset dadosClasif){

		int classePredic;
		messages = dadosClasif.getMessages();

		for(EmailMessage m:messages){
			HashMap<Integer,Integer> tokens=m.getTokens();
			classePredic= classify(tokens, limiar, dadosTrain);
			//System.out.println(classePredic);
			m.classify(classePredic);	
		}
	}



	private double probClass(String c, EmailDataset dados){
		double result = 0;
		if(c.equals("spam")){
			result=dados.getNumSpam() /dados.getNumSpam()+ dados.getNumHam();
		}
		else if(c.equals("ham")){
			result=dados.getNumHam() / dados.getNumHam() + dados.getNumSpam();
		}

		return result;
	}

	private double getTokenProb(int token, String c){

		HashMap<Integer, Integer> current = null;
		if(c.equals("spam")){
			current = spam;
		}if(c.equals("ham")){
			current = ham;
		}
		int ocurrToken = 0;
		if(current.containsKey(token))
			ocurrToken = current.get(token);
		int sumOcurrToken = 0;
		sumOcurrToken = allTokenOcurr(current);
		double result = (ocurrToken + 1) / sumOcurrToken + dim;

		return result;

	}
	
	
	private int allTokenOcurr(HashMap<Integer, Integer> classTable){
		int sumOcurrToken = 0;
		for(Integer key : classTable.keySet()){
			sumOcurrToken += classTable.get(key);	
		}
		return sumOcurrToken;
	}

	private void tableTokenProb(){
		for(Integer key : spam.keySet()){
			spamProb.put(key, getTokenProb(key, "spam"));
		}
		for(Integer key : ham.keySet()){
			hamProb.put(key, getTokenProb(key, "ham"));
		}
	}



	public int classify(HashMap<Integer, Integer> line, double threshold, EmailDataset dados){

		double probClassSpam = probClass("spam" ,dados);
		double probClassHam = probClass("ham",dados);
		double result =  Math.log(probClassSpam / probClassHam); 

		for(Integer token: line.keySet()){
			if(spamProb.containsKey(token)){
				result += Math.log(spamProb.get(token) / hamProb.get(token));
			}else{
				result+=Math.log((1/getTokenProb(token, "spam")/getTokenProb(token, "ham")));
			}
		}

		int classification = 0;
		if(result > Math.log(threshold))
			classification = 1;
		else
			classification = -1;

		System.out.println("Classification: "+ classification);
		
		return classification;
	}




	public double threashold(String filename)throws FileNotFoundException{
		int classePredic;

		v=new Vector <TabelaLimiar>();
		this.tfr2 = new TFReader(filename);
		EmailDataset train = tfr2.divisaoConjuntoDados(filename).getFirst();
		EmailDataset validacao=tfr2.divisaoConjuntoDados(filename).getSecont();

		dim=train.getDictionaryDim();
		par=train.getTotalTokenOcurr();
		spam=par.getFirst(); //HashMap spam
		ham=par.getSecont();//hasmap ham
		spamProb = new HashMap<Integer, Double>(); 
		hamProb = new HashMap<Integer, Double>();

		tableTokenProb();

		/*for(Integer key : spam.keySet()){
			System.out.printf("El key %d con valor %d.\n", key,spam.get(key) );
		}

		System.out.println("Tama�o spam "+spam.size());
		System.out.println("Tama�o ham "+ham.size());*/



		// fin de train para el algoritmo

		//clasifica�ao de emails com diferentes limiares
		messages = validacao.getMessages();
		for (int i=1; i<9;i++){
			int fp=0;
			int fn=0;
			for(EmailMessage m:messages){
				HashMap<Integer,Integer> tokens=m.getTokens();
				classePredic= this.classify(tokens, i,train);

				if ((classePredic==1)&& (m.getClassification()==-1)) //FP
					fp++;
				else if((classePredic==-1)&& (m.getClassification()==1)) //FN
					fn++;

			}

			v.add(new TabelaLimiar(i,fp,fn)); 

		}

		//escolher o limiar

		int minFP=Integer.MAX_VALUE;
		int limiar=0;
		Enumeration<TabelaLimiar> enumeration = v.elements();
		while(enumeration.hasMoreElements()){
			if ((int)enumeration.nextElement().getFp()< minFP){
				limiar=	(int)enumeration.nextElement().getLimiar();
			}
		}
		return limiar;
	}




}
