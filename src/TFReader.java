import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;




public class TFReader {
	private String filename;
	
	public TFReader(String filename){
		this.filename = filename;
		
	}
	
	
	public List<HashMap<Integer, Integer>> read() throws FileNotFoundException{
		LinkedList<HashMap<Integer,Integer>> maps = new LinkedList<HashMap<Integer,Integer>>();
		maps.add(new HashMap<Integer, Integer>());
		maps.add(new HashMap<Integer, Integer>());
		//fist item spam second ham
		
		//read the file
		Scanner sc = new Scanner(new FileReader(filename));
		
		HashMap<Integer, Integer> currentHashMap = null;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			String[] pairs = line.split(" ");
			
			if(Integer.parseInt(pairs[0]) == 1)
				currentHashMap = maps.getFirst();
			else if(Integer.parseInt(pairs[0]) == -1)
				currentHashMap = maps.getLast();
			
			String[] integers = null;
			for(int i=1; i<pairs.length; i++){
				 integers = pairs[i].split(":");
				 int n1 = Integer.parseInt(integers[0]);
				 int n2 = Integer.parseInt(integers[1]);
				 currentHashMap.put(n1, n2);
				 
			}
				
			
			
			
			
		}
		
		sc.close();
		return maps;
		
	}

}
