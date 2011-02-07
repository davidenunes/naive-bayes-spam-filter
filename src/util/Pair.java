package util;
/**
 * Class that represents a pair of object
 * for utility purposes
 * 
 * @author davide
 * @author ainara
 *
 * @param <T> type of the two elements in the pair
 */
public class Pair<T> {
	private T first;
	private T second;
	
	/**
	 * If you use this constructor
	 * use the setFirst() and setSecond() method
	 */
	public Pair(){}
	
	/**
	 * Creates a pair given two elements
	 * 
	 * @param first T first element in the pair
	 * @param second T second element in the pair
	 */
	public Pair(T first, T second){
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Method that returns the first element of the pair
	 * 
	 * @return first T - first element 
	 */
	public T getFirst(){
		return first;
	}
	
	/**
	 * Method that returns the second element of the pair
	 * 
	 * @return second T - second element 
	 */
	public T getSecont(){
		return second;
	}
	
	/**
	 * Method that sets the first element of the pair
	 * 
	 * (usefull if you construct empty pairs and instantiate them latter on)
	 * 
	 * 
	 */
	public void setFirst(T first){
		this.first = first;
	}
	
	/**
	 * Method that sets the second element of the pair
	 * 
	 * (usefull if you construct empty pairs and instantiate them latter on)
	 * 
	 * 
	 */
	public void setSecond(T second){
		this.second = second;
	}

}
