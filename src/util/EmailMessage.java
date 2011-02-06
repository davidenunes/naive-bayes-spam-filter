package util;
import java.util.HashMap;

/**
 * Class that represents a line of data with a set of tokens and their
 * 
 * @author davide
 * @author ainara
 *
 */
public class EmailMessage {
	private int classification; // 1 spam -1 ham
	private HashMap <Integer, Integer> tokens;
	private boolean tagged;
	
	/**
	 * Constructor for tagged message (classification information)
	 * @param classification - 1 spam | -1 ham
	 * @param tokens HashMap<Integer, Integer> that maps a Token to a number of its occurences
	 * token -> ocurrences
	 */
	public EmailMessage(int classification, HashMap <Integer, Integer> tokens){
		tagged = true;
		this.tokens = tokens;
		this.classification = classification;
	}
	
	/**
	 * Constructor for untagged message (with no classification information)
	 * @param tokens list of HashMap<Integer, Integer> that maps a Token to a number of its occurences
	 * token -> ocurrences
	 */
	public EmailMessage(HashMap <Integer, Integer> tokens){
		tagged = false;
		this.tokens = tokens;
		this.classification = 0;
	}
	
	/**
	 * Method that returns a list of maps that map a token to its number of occurrences
	 * 
	 * @return
	 */
	public HashMap<Integer, Integer> getTokens(){
		return this.tokens;
	}
	
	/**
	 * Method that returns true if this is a spam message
	 * @return true - spam message
	 *         false - non spam or untagged message
	 */
	public boolean isSpam(){
		return (classification == 1);
	}
	
	/**
	 * Method that returns true if the message has 
	 * information about its classification
	 * @return true - tagged message
	 */
	public boolean isTagged(){
		return tagged;
	}
	
	/**
	 * Method that returns the classification of the message 
	 * 1 if it is spam, -1 if it is not spam
	 * @return Integer 1 - spam
	 * 				   -1 - ham
	 * 
	 * this.isSpam() -> this.getClassification() == 1 
	 * 
	 */
	public int getClassification(){
		return classification;
	}
	
	
	/**
	 * Attribute a classification to this message 
	 * if the value of the class is correct 1 / -1
	 * 
	 * @param c Integer 1 or -1 otherwise the message will not be classified
	 */
	public void classify(int c){
		if(c == 1 || c == -1){
			this.classification = c;
			this.tagged = true;
		}
	}
	
	public EmailMessage clone(){
		return new EmailMessage(getClassification(),(HashMap<Integer, Integer>) tokens.clone());
	}
	

	

}
