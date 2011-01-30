package io;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import util.EmailDataset;
import util.EmailMessage;




public class TFReader {
	private String filename;
	
	
	public TFReader(String filename){
		this.filename = filename;
		
	}
	
	
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
			for(int i=k; i<pairs.length; i++){
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
