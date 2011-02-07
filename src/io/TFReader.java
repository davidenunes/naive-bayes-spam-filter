package io;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import util.EmailDataset;
import util.EmailMessage;



/**
 * Class used to create reader objects for .tf files
 * the files contain token information about email messages
 * 
 * 
 * @author davide
 * @author ainara
 */
public class TFReader {
	private String filename;
	
	/**
	 * Constructor
	 * 
	 * @param filename String the file to be read
	 */
	public TFReader(String filename){
		this.filename = filename;
		
	}
	
	/**
	 * Method used to read the tf file supplied in the constructor
	 * and return an EmailDataset object containing all the 
	 * EmailMessages of the file
	 * 
	 * @return messages EmailDataset - the messages loaded into a dataset
	 * 
	 * @throws FileNotFoundException
	 */
	public EmailDataset read() throws FileNotFoundException{
		//fist item spam second ham
		List<EmailMessage> messages = new LinkedList<EmailMessage>();
		//read the file
		Scanner sc = new Scanner(new FileReader(filename));
		
		HashMap<Integer, Integer> currentHashMap = null;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			//System.out.println("current line:" + line);
			String[] pairs = line.split(" ");
			
			//System.out.println("line separated: "+Integer.parseInt(pairs[0]));
			
			int k = 0;
			int classification = Integer.parseInt(pairs[0]);
			if(classification == 1 || classification == -1)
				k = 1;//if classification is present start extracting tokens from index 1 of pairs
					  //usefull as a flag to choose the EmailMessage constructor
			
			currentHashMap = new HashMap<Integer, Integer>();
			
			String[] integers = null;
			
			for(int i=1; i<pairs.length; i++){
				 integers = pairs[i].split(":");
				 int n1 = Integer.parseInt(integers[0]);
				 int n2 = Integer.parseInt(integers[1]);				 
				 currentHashMap.put(n1, n2);
			}
			
			if(k == 1)//the line had a class
				messages.add(new EmailMessage(classification, currentHashMap));
			else//line was untagged (no classification information)
				messages.add(new EmailMessage(currentHashMap));	
		}
		
		sc.close();
		return new EmailDataset(messages);
		
	}
}

