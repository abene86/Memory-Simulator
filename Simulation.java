//TO DO: Nothing required here.

//******************************************************
//*******  DO NOT EDIT ANYTHING BELOW THIS LINE  *******
//******************************************************

import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Scanner;
import java.io.File;

/**
 *  The graphical user interface for the simulation.
 *  
 *  @author K. Raven Russell
 */
public final class Simulation {
	/**
	 * The GUI tied to this simulation.
	 */
	private UserInterface gui;
	
	/**
	 * The mapping of variables tracked by the simulation to their (bare
	 * nodes) pointers.
	 */
	private final Map<String,MemMan.BareNode> symbolTable;
	
	/**
	 * A mapping in the other direction for quick reference.
	 */
	private final Map<Integer,String> revSymbolTable;
	
	/**
	 * The actual memory manager.
	 */
	private MemMan manager;
	
	/**
	 * The number of memory rows in the simulation.
	 */
	private final int numRows;
	
	/**
	 * The number of memory columns in the simulation.
	 */
	private final int numCols;
	
	/**
	 * Creates a simulation of the proper size and optionally runs some number
	 * of commands to run.
	 * 
	 * @param numRows the number of rows in memory expected
	 * @param numCols the number of columns in memory expected
	 * @param commandFile the file with the "program" to run first
	 */
	public Simulation(int numRows, int numCols, File commandFile) {
		this.numRows = numRows;
		this.numCols = numCols;
		
		this.symbolTable = new TreeMap<>();
		this.revSymbolTable = new HashMap<>();
		this.manager = MemMan.factory(MemMan.FIRST_FIT, new MemMan.BareNode(new MemBlock(0, numRows*numCols, true)));
		
		if(commandFile != null) {
			System.out.println(commandsFromFile(commandFile));
		}
	}
	
	/**
	 * Kickoff the GUI.
	 */
	public void go() {
		this.gui = new UserInterface(this, "Memory Simulation", numRows, numCols);
		this.update();
	}
	
	/**
	 * Run various commands from the file.
	 * 
	 * @param file the file with the "program"
	 * @return log information about running the file
	 */
	public String commandsFromFile(File file) {
		StringBuilder sb = new StringBuilder();
		
		try {
			Scanner s = new Scanner(file);
			s.useDelimiter("\\s*[)(,=\r\n]\\s*");
			
			if(!s.hasNextInt()) {
				throw new RuntimeException("No type specified..." + s.next());
			}
			
			int memManType = s.nextInt();
			setMemMan(memManType);
			sb.append("Set MemMan to type ");
			sb.append(memManType);
			sb.append("\n");
					
			while(s.hasNext()) {
				String firstToken = s.next();
				
				//freeing a variable
				if(firstToken.equals("free")) {
					String varName = s.next();
					String goal = "free \"" + varName + "\"";
					if(!free(varName)) {
						throw new RuntimeException("Can't " + goal);
					}
					
					sb.append("\t");
					sb.append(goal);
					sb.append("\n");
				}
				//realloc a variable
				else if(firstToken.equals("realloc")) {
					String varName = s.next();
					int size = Integer.parseInt(s.next());
					String goal = "realloc \"" + varName + "\" to " + size + " bytes";
					
					if(!realloc(varName, size)) {
						throw new RuntimeException("Can't " + goal);
					}
					
					sb.append("\t");
					sb.append(goal);
					sb.append("\n");
				}
				//null or allocate a variable
				else {
					String varName = firstToken;
					String secondToken = s.next();
					
					//null a variable
					if(secondToken.equals("null")) {
						String goal = "null \"" + varName + "\"";
						
						if(!nullOut(varName)) {
							throw new RuntimeException("Can't " + goal);
						}
					
						sb.append("\t");
						sb.append(goal);
						sb.append("\n");
					}
					//allocate
					else if(secondToken.equals("newmemory")) {
						int size = Integer.parseInt(s.next());
						String goal = "malloc \"" + varName + "\" to " + size + " bytes";
					
						if(!malloc(firstToken, size)) {
							throw new RuntimeException("Can't " + goal);
						}
					
						sb.append("\t");
						sb.append(goal);
						sb.append("\n");
					}
					//something is wrong...
					else {
						throw new RuntimeException("Syntax confused..." + secondToken);
					}
				}
			}
			
			if(memManType != MemMan.FIRST_FIT) {
				setMemMan(MemMan.FIRST_FIT);
				sb.append("Set MemMan to type ");
				sb.append(MemMan.FIRST_FIT);
			}
		}
		catch(Exception e) {
			sb.append("Error while processing file.\n" + e.toString());
			System.err.println(sb.toString());
			System.exit(1);
		}
			
		return sb.toString();
	}
	
	/**
	 * Sets up the memory manager to be a certain type. Hands off the
	 * current memory to the right place.
	 * 
	 * @param type the type of memory manager to use (see MemMan)
	 */
	public void setMemMan(int type) {
		this.manager = MemMan.factory(type, this.manager.getHead());
	}
	
	/**
	 * Allocate X (size) bytes for variable Y (name). This method is
	 * called by the GUI.
	 * 
	 * @param name the variable name
	 * @param size the size in memory
	 * @return whether or not it was possible to malloc
	 */
	public boolean malloc(String name, int size) {
		MemMan.BareNode bn = this.manager.malloc(size);
		if(bn != null) {
			MemMan.BareNode oldBn = symbolTable.put(name, bn);
			if(oldBn != null) {
				revSymbolTable.remove(oldBn.block.addr);
			}
			revSymbolTable.put(bn.block.addr, name);
			return true;
		}
		return false;
	}
	
	/**
	 * Free variable Y (name). This method is called by the GUI.
	 * 
	 * @param name the variable name
	 * @return whether or not it was possible to free the variable
	 */
	public boolean free(String name) {
		if(symbolTable.containsKey(name)) {
			MemMan.BareNode  bn = symbolTable.put(name, null);
			
			if(bn != null && this.manager.free(bn)) {
				revSymbolTable.remove(bn.block.addr);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Re-allocate X (size) bytes for variable Y (name). This method is
	 * called by the GUI.
	 * 
	 * @param name the variable name
	 * @param size the size in memory
	 * @return whether or not it was possible to realloc
	 */
	public boolean realloc(String name, int size) {
		//clear everything out
		MemMan.BareNode oldBn = symbolTable.get(name);
		if(oldBn == null) return false;
		
		//try to realloc
		MemMan.BareNode newBn = this.manager.realloc(oldBn, size);
		
		if(newBn != null) {
			//remove the old lookup
			revSymbolTable.remove(oldBn.block.addr);
			//override in symbol table
			symbolTable.put(name, newBn);
			//lookup again
			revSymbolTable.put(newBn.block.addr, name);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Set variable Y (name) to null without freeing the memory.
	 * This method is called by the GUI.
	 * 
	 * @param name the variable name
	 * @return whether or not it was possible to null the variable
	 */
	public boolean nullOut(String name) {
		if(symbolTable.containsKey(name)) {
			MemMan.BareNode bn = symbolTable.put(name, null);
			if(bn != null) {
				revSymbolTable.remove(bn.block.addr);
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Run mark and sweep garbage collector.
	 * 
	 * @return the number of bytes freed
	 */
	public int gc() {
		int amtFreed = this.manager.garbageCollect(this.revSymbolTable.keySet());
		return amtFreed;
	}
	
	/**
	 * Runs all the updates.
	 */
	public void update() {
		updateImage();
		updateVariables();
		gui.repaint();
	}
	
	/**
	 * Update just the image.
	 */
	public void updateImage() {
		gui.paintItBlack();
		
		for(MemBlock m : this.manager) {
			//in the symbol table
			if(revSymbolTable.containsKey(m.addr)) {
				gui.setBlock(revSymbolTable.get(m.addr), m.addr, m.size, new Color(173,216,230));
			}
			//forgotten about
			else if(!m.isFree) {
				gui.setBlock("?", m.addr, m.size, new Color(173,216,230));
			}
			//actually free
			else {
				gui.setBlock("free", m.addr, m.size, Color.WHITE);
			}
		}
	}
	
	/**
	 * Update just the variables.
	 */
	public void updateVariables() {
		gui.setVariables(this.symbolTable);
	}
	
	/**
	 * Main method that kicks off the simulator.
	 * @param args command line args for number of rows, columns, and an optional program file
	 */
	public static void main(String[] args) {
		String usageMsg = "Usage: java Simulation [numRows] [numCols] [optional:file]";
		
		try {
			int numRows = Integer.parseInt(args[0]);
			int numCols = Integer.parseInt(args[1]);
			if(numRows < 1 || numCols < 1) throw new NumberFormatException();
			Simulation sim = new Simulation(numRows,numCols, ((args.length == 2) ? null : new File(args[2])));
			sim.go();
		}
		catch(RuntimeException e) {
			e.printStackTrace();
			System.err.println(usageMsg);
		}
	}
}