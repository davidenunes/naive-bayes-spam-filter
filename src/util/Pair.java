package util;
/**
 * Class that represents a pair of object
 * for utility purposes
 * 
 * @author davide
 *
 * @param <T>
 */
public class Pair<T> {
	private T first;
	private T second;
	
	/**
	 * If you use this constructor
	 * use the setFirst() and setSecond() method
	 */
	public Pair(){}
	
	public Pair(T first, T second){
		this.first = first;
		this.second = second;
	}
	
	public T getFirst(){
		return first;
	}
	
	public T getSecont(){
		return second;
	}
	
	public void setFirst(T first){
		this.first = first;
	}
	
	public void setSecond(T second){
		this.second = second;
	}

}
