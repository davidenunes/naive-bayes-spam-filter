package util;
/**
 *
 */
public class TabelaLimiar {
	
	private int limiar;
	private int fp;
	private int fn;
	
	
	public TabelaLimiar(){}
	
	public TabelaLimiar(int l, int fp, int fn){
		this.limiar = l;
		this.fp = fp;
		this.fn = fn;
	}
	
	public int getLimiar() {
		return limiar;
	}

	public void setLimiar(int limiar) {
		this.limiar = limiar;
	}

	public int getFp() {
		return fp;
	}

	public void setFp(int fp) {
		this.fp = fp;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	


	


}
