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

import util.EmailDataset;
import util.EmailMessage;
import util.Pair;
import util.TabelaLimiar;


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
	
	
	public NaiveBayes(String filename, Integer t) throws FileNotFoundException{
		
		
	
		
	
		 //calcular threshold
		 this.limiar= threashold(filename);
		
		 this.tfr=new TFReader(filename);
		 this.dadosTrain=tfr.read();
		 dadosTrain=tokensMaisSignificativos(t,dadosTrain);
		 algoritmoEM(dadosTrain);
		 
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
	
	private void algoritmoEM(EmailDataset dadosTrain)throws FileNotFoundException{
		
		String filename2="u0_eval.tf";
		String filename3="u1_eval.tf";
		
		createModelNB(dadosTrain);
		System.out.println("Modelo1:");
		System.out.println("nummsg:"+dadosTrain.getNumMessages());
		System.out.println("numham:"+dadosTrain.getNumHam());
		System.out.println("numspam:"+dadosTrain.getNumSpam());
		double x=calcularVerosimilhanca();
		System.out.println("Verosimilhan�a modelo1:" +x);
		
		
		obterDadosClasificacao(filename2);
		addDadosValidacao(filename3);
		clasificationNB(this.dadosClasificacao); 
		addDadosClasificacaoADadosTrain();
		
		//1. store current dataset
		previousItDataset = dadosTrain.clone();
		
		createModelNB(this.dadosTrain);
		System.out.println("Modelo2:");
		System.out.println("nummsg: "+dadosTrain.getNumMessages());
		System.out.println("numham:"+dadosTrain.getNumHam());
		System.out.println("numspam:"+dadosTrain.getNumSpam());  
		double y=calcularVerosimilhanca();
		System.out.println("Verosimilhan�a modelo2:" +y);
		
		
	}
	
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
	
	
	private EmailDataset tokensMaisSignificativos(Integer t, EmailDataset dadosTrain){
		
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

		

	private void createModelNB(EmailDataset dadosTrain){

		 dim=dadosTrain.getDictionaryDim();
		 par=dadosTrain.getTotalTokenOcurr();
		 spam=par.getFirst();
		 ham=par.getSecont();
		 spamProb = new HashMap<Integer, Double>();
		 hamProb = new HashMap<Integer, Double>();
		 tableTokenProb();
	}
	
	private void obterDadosClasificacao(String filename)throws FileNotFoundException{
		
		 this.tfr2=new TFReader(filename);
		 this.dadosClasificacao=tfr2.read();
		
	}
	private void addDadosValidacao(String filename)throws FileNotFoundException{
		this.tfr2=new TFReader(filename);
		this.dadosClasificacao.add(tfr2.read().getMessages());
		
	}
	
	private void clasificationNB(EmailDataset dadosClasif){
	
		int classePredic;
		messages = dadosClasif.getMessages();
	
	 for(EmailMessage m:messages){
		HashMap<Integer,Integer> tokens=m.getTokens();
		classePredic= classif(tokens, limiar, dadosTrain);
		//System.out.println(classePredic);
		m.classify(classePredic);	
	 }
	}
	
	private void addDadosClasificacaoADadosTrain(){
		messagesTrain=dadosTrain.getMessages();
		messagesClasificacao=dadosClasificacao.getMessages();
		
		Iterator<EmailMessage> add= messagesClasificacao.iterator();
		
		while (add.hasNext()){
			messagesTrain.add(add.next());
		}

		this.messages=messagesTrain;
		this.dadosTrain=new EmailDataset(messages);
		
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
	
	
	
	public int classif(HashMap<Integer, Integer> line, double threshold, EmailDataset dados){
		
		double probClassSpam = probClass("spam" ,dados);
		double probClassHam = probClass("ham",dados);
		double result =  Math.log(probClassSpam / probClassHam); 
		
		for(Integer token: line.keySet()){
			//result += Math.log(spamProb.get(token) / hamProb.get(token));
			
			if(spamProb.containsKey(token)&&(hamProb.containsKey(token))){
				result += Math.log(spamProb.get(token) / hamProb.get(token));
				//System.out.println("Sartu da biak betetzen dituenean");
			}else if (spamProb.containsKey(token)){
				result+=Math.log(spamProb.get(token)/(1/dim));
				//System.out.println("Sartu da spam betetzen duenean");
			}else if (hamProb.containsKey(token)){
				result+=Math.log((1/dim)/(hamProb.get(token)));
				//System.out.println("Sartu da ham betetzen duenean");
			}
		}
		
		int classification = 0;
		if(result > Math.log(threshold))
			classification = 1;
		else
			classification = -1;
		
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
				classePredic= this.classif(tokens, i,train);
				
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
