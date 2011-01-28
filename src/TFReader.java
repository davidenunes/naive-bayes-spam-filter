import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;




public class TFReader {
	private String filename;
	private int numSpam;
	private int numHam;
	
	public TFReader(String filename){
		this.filename = filename;
		
	}
	
	
	public List<HashMap<Integer, Integer>> trainRead() throws FileNotFoundException{
		LinkedList<HashMap<Integer,Integer>> maps = new LinkedList<HashMap<Integer,Integer>>();
		maps.add(new HashMap<Integer, Integer>());
		maps.add(new HashMap<Integer, Integer>());
		//fist item spam second ham
		
		//read the file
		Scanner sc = new Scanner(new FileReader(filename));
		
		HashMap<Integer, Integer> currentHashMap = null;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			//System.out.println("current line:" + line);
			String[] pairs = line.split(" ");
			
			//System.out.println("line separated: "+Integer.parseInt(pairs[0]));
			
			if(Integer.parseInt(pairs[0]) == 1){
				currentHashMap = maps.getFirst();
				numSpam++;
			}
			else if(Integer.parseInt(pairs[0]) == -1){
				currentHashMap = maps.getLast();
				numHam++;
			}
			
			String[] integers = null;
			for(int i=1; i<pairs.length; i++){
				 integers = pairs[i].split(":");
				 int n1 = Integer.parseInt(integers[0]);
				 int n2 = Integer.parseInt(integers[1]);
				 //System.out.println("pairs: "+n1+":"+n2);
				 
				 
				 int ocurr = 0;
				 if(currentHashMap.containsKey(n1)){
					 ocurr = currentHashMap.get(n1);
					 ocurr += n2;
				 }
				 
				 currentHashMap.put(n1, ocurr);
				 
			}
				
			
			
			
			
		}
		
		sc.close();
		return maps;
		
	}
	
	
	
	
	
	public List<HashMap<Integer, Integer>> crossRead() throws FileNotFoundException{
		LinkedList<HashMap<Integer,Integer>> maps = new LinkedList<HashMap<Integer,Integer>>();
		maps.add(new HashMap<Integer, Integer>());
		maps.add(new HashMap<Integer, Integer>());
		//fist item spam second ham
		
		//read the file
		Scanner sc = new Scanner(new FileReader(filename));
		
		HashMap<Integer, Integer> currentHashMap = null;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			//System.out.println("current line:" + line);
			String[] pairs = line.split(" ");
			
			
			if(Math.random() > 0.5){//subtrain set
				
				
				if(Integer.parseInt(pairs[0]) == 1){
					currentHashMap = maps.get(0);
					numSpam++;
				}
				else if(Integer.parseInt(pairs[0]) == -1){
					currentHashMap = maps.get(1);
					numHam++;
				}
				
				String[] integers = null;
				for(int i=1; i<pairs.length; i++){
					 integers = pairs[i].split(":");
					 int n1 = Integer.parseInt(integers[0]);
					 int n2 = Integer.parseInt(integers[1]);
					 //System.out.println("pairs: "+n1+":"+n2);
					 
					 
					 int ocurr = 0;
					 if(currentHashMap.containsKey(n1)){
						 ocurr = currentHashMap.get(n1);
						 ocurr += n2;
					 }
					 
					 currentHashMap.put(n1, ocurr);
					 
				}
					
			}//end subtrain set
			else{
				currentHashMap = new HashMap<Integer, Integer>();
				maps.add(currentHashMap);
				
				String[] integers = null;
				for(int i=1; i<pairs.length; i++){
					 integers = pairs[i].split(":");
					 int n1 = Integer.parseInt(integers[0]);
					 int n2 = Integer.parseInt(integers[1]);
					 //System.out.println("pairs: "+n1+":"+n2);
					 
			
					 currentHashMap.put(n1, n2);
					 
				}
				
				
			}
				
				
			
		}
		
		sc.close();
		return maps;
		
	}
	
	
	
	
	
	
	
	
	
	
	public List<HashMap<Integer, Integer>> untaggedRead() throws FileNotFoundException{
		LinkedList<HashMap<Integer,Integer>> maps = new LinkedList<HashMap<Integer,Integer>>();
		
		//fist item spam second ham
		
		//read the file
		Scanner sc = new Scanner(new FileReader(filename));
		
		HashMap<Integer, Integer> currentHashMap = null;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			currentHashMap = new HashMap<Integer, Integer>();
			maps.add(currentHashMap);
			//System.out.println("current line:" + line);
			String[] pairs = line.split(" ");
			
			//System.out.println("line separated: "+Integer.parseInt(pairs[0]));
			
			
			
			String[] integers = null;
			for(int i=0; i<pairs.length; i++){
				 integers = pairs[i].split(":");
				 int n1 = Integer.parseInt(integers[0]);
				 int n2 = Integer.parseInt(integers[1]);
				 //System.out.println("pairs: "+n1+":"+n2);
				 
				 
				 currentHashMap.put(n1, n2);
				 
			}
				
			
			
			
			
		}
		
		sc.close();
		return maps;
		
		
		
	}
	
	
	
	
	public int getNumSpam(){
		return numSpam;
		
	}
	
	
	public int getNumHam(){
		return numHam;
		
	}

}
