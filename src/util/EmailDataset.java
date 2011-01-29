package util;
import java.util.HashMap;
import java.util.List;


/**
 * Class that represents a set of email messages
 * usefull to be returned by the TFReader class
 * 
 * encapsulates a list of Email Messages to ease the 
 * processing of muliple messages to generate usefull structures
 * like HashMaps of the occurrency of all the tokens in a message set
 * 
 * 
 * @author davide
 * @author ainara
 *
 */
public class EmailDataset {
	private List<EmailMessage> messages; 
	
	/**
	 * Simple constructor recieves a set of email messages
	 * @param messages List<EmailMessage>
	 */
	public EmailDataset(List<EmailMessage> messages){
		this.messages = messages;
	}
	
	public List<EmailMessage> getMessages(){
		return messages;
	}
	
	/**
	 * This method returns a pair of HashMaps that map the tokens 
	 * of the tagged messages to the total number of occurrencies
	 * in all the messages of the dataset
	 * 
	 * TOKEN -> TOTAL OCURR
	 * 
	 * First - spam token ocurrencies
	 * Second - ham token ocurrencies
	 * 
	 * @return Pair<First,Second> 
	 */
	public Pair<HashMap<Integer, Integer>> getTotalTokenOcurr(){
		Pair<HashMap<Integer,Integer>> pair = new Pair<HashMap<Integer,Integer>>();
		
		HashMap<Integer, Integer> spamTable = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> hamTable = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> currentTable = null; //to select between both tables
		
		for(EmailMessage m : messages){	//foreach message in the dataset
			if(m.isTagged()){			//we can only separate tagged messages
				if(m.isSpam())
					currentTable = spamTable;
				else 
					currentTable = hamTable;
				
				//store the tokens in the current table
				HashMap<Integer,Integer> tokens = m.getTokens();
				for(Integer token : tokens.keySet()){
					int currentOcurrencies = 0;
					if(currentTable.containsKey(token))
						currentOcurrencies = currentTable.get(token);
					
					//update the current map token ocurr count
					//sums 0 if the tokens does not exist
					currentTable.put(token, (tokens.get(token) + currentOcurrencies));
				}//end for each token in the current message
				
			}//end is tagged
		}//end for each message
		
		pair.setFirst(spamTable);
		pair.setSecond(hamTable);
		
		return pair;
	}
	
	/**
	 * Method that returns the number of spam messages 
	 * present in this EmailDataset
	 * 
	 * @return Integer number of spam message
	 */
	public int getNumSpam(){
		int numSpam = 0;
		for(EmailMessage m: messages){
			if(m.isTagged()){
				if(m.isSpam()) numSpam++;
			}
		}
		return numSpam;
	}

	/**
	 * Method that returns the number of ham messages 
	 * present in this EmailDataset
	 * 
	 * @return Integer number of spam message
	 */
	public int getNumHam(){
		int numHam = 0;
		for(EmailMessage m: messages){
			if(m.isTagged()){
				if(!m.isSpam()) numHam++;
			}
		}
		return numHam;
	}
	
	/**
	 * Method that returns the number of the messages
	 * in this email dataset
	 * 
	 * @return Integer number of messages in this email dataset
	 */
	public int getNumMessages(){
		return this.messages.size();
	}
	
	
	
}
