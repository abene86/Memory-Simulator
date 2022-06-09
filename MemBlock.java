//TO DO: Add JavaDocs, you can add more things too, but don't break anything


/**
 * MemBlock class.
 * @author Professor Russell and Abenezer Gebeyehu.
 */
public class MemBlock {
	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THESE INTS, THIS   ****
	//****    BOOLEAN, OR THE CONSTRUCTOR.              ****
	//******************************************************
	/**
	 * Addr is where the MemBlock starts at
	 * can't be easily changed.
	 */
	public final int addr;
	/**
	 * size is how much memory in bytes it holds.
	 * can't be easily changed too.
	 */
	public final int size;
	/**
	 * isFree is boolean state which tell us whether Memblock is in use or free.
	 * this too can't be easily changed.
	 */
	public final boolean isFree;
	/**
	 * Construcor that intalizes the instance variables.
	 * @param addr tells us where memblock/memory start at.
	 * @param size tells us how much memory it holds.
	 * @param isFree tells us the state whether it is free or not.
	 */
	public MemBlock(int addr, int size, boolean isFree) {
		this.addr = addr;
		this.size = size;
		this.isFree = isFree;
	}
	/**
	 * Constuctor used to copy the values of instance variables of an  object to an other.
	 * @param block is a variable of type MemBlock that is going to be copied.
	 */
	public MemBlock(MemBlock block){
		this.addr=block.addr;
		this.size=block.size;
		this.isFree=block.isFree;
	}
	/**
	 * It prints the instance variables of MemBlock in string format for debugging.
	 * @return string  is addr, size and the state of free in a string format. 
	 */
	public String toString(){
		return "addr:"+ addr+"\nsize:"+size+"\nisFree:"+isFree+"\n";
	}
	
	//some "copy constructors" might be useful here...
}