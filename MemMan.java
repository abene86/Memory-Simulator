//TO DO: Complete this class, add JavaDocs

//Do not add any more imports!
import java.util.Iterator;
import java.util.Set;
/**
 * MemMan class.
 * @author Abenezer Gebeyehu
 */
public class MemMan implements Iterable<MemBlock> {

	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THESE INTS HERE... ****
	//******************************************************
	/**
	 * Integer that represent the first policy
	 * used by sim for changing.
	 */
	public static final int FIRST_FIT = 0;
	/**
	 * Integer that represents the second policy.
	 * used by the sim for changing
	 */
	public static final int BEST_FIT = 1;
	/**
	 * Integer that represents the third policy.
	 * used by the sim for changing.
	 */
	public static final int WORST_FIT = 2;
	/**
	 * Integer that represents the fourth policy
	 * used by the sim for changing.
	 */
	public static final int NEXT_FIT = 3;
	
	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THE INSTANCE       ****
	//****    VARIABLES IN THIS NESTED CLASS. ALSO      ****
	//****    DON'T REMOVE THE CLASS ITSELF OR ANYTHING ****
	//****    STRANGE LIKE THAT.                        ****
	//******************************************************
	/**
	 * It is the head of Memblocks/the heap.
	 */
	private BareNode head;
	/**
	 *It a copy of what policy the user/sim used.
	 * 
	 */
	private int type;
	/**
	 * It holds that last allocated free block.
	 * by different policies
	 */
	private static BareNode lastUsedFree;
	/**
	 * BareNode class that houses our MemBlocks.
	 */
	public static class BareNode {
		/**
		 * block holds important information about the size/addr/isfree.
		 * and is oftype MemBlock.
		 */
		public MemBlock block;
		/**
		 * BareNode object that holds reference to the next Memblock(BareNode).
		 */
		public BareNode next;
		/**
		 * BareNode object that holds reference to its prev Memblock.
		 */
		public BareNode prev;
		/**
		 * state whether a node is marked or not.
		 */
		public boolean marked;
		/**
		 * constructor that takes of type block and intalizes.
		 * @param block data of Memblock.
		 */
		public BareNode(MemBlock block) {
			this.block = block;
		}
	}
	/**
	 *  It is a constructor that takes head and type(policy) and intalizes them.
	 *  It is very important because it helps direct the malloc method which policy to perform based on type.
	 * @param type is the policy(Best fit, first fit, worst fit, next fit).
	 * @param head the head of list/ MemBlocks.
	 */
	public MemMan(int type, BareNode head){
		this.head=head;
		this.type=type;
	}
	/**
	 * It produces and returns a MemManager used by sim.
	 * @param type is what policy it is.
	 * @param head the head of list of MemBlocks.
	 * @return MemMan / a manager that performs the given policy.
	 */
	public static MemMan factory(int type, BareNode head){
		MemMan manager= new MemMan(type, head);
		return manager;
	}
	/**
	 * It returns the head of the list.
	 * @return head;
	 */
	public BareNode getHead(){
		return this.head;
	}
	/**
	 * It allocates bytes(size) of memory block based on the policy set by the MemMan
	 * If it does not have enough memory to give back or does not meet the policy requirment returns null.
	 * it calls four helper methods to help it.
	 * @param size is bytes of memory to allocate.
	 * @return BareNode which is node with memort allocated or null
	 */
	public BareNode malloc(int size){
		BareNode returnValue=null;
		switch(this.type){
			case 0: returnValue=mallocFirstFit(size); break;
			case 1: returnValue=mallocBestFit(size); break;
			case 2: returnValue=mallocWorstFit(size); break;
			case 3: returnValue=mallocNextFit(size); break;
		}
		return returnValue;
	}
	/**
	 * Helper method called by malloc.
	 * Its function is to go through the heap and find first free memblock that fits the size
	 * and allocate Memory/ it it does not find it returns null.
	 * @param size is the number of bytes we want to allocate.
	 * @return bareNode which could either be null if we don't find size/ or allocate Memblock with the given size.
	 */
	private BareNode mallocFirstFit(int size){
		BareNode temp=head, returnValue=null;
		if(size>=1){
			while(temp!=null){
				if(temp.block.isFree && size<=temp.block.size){
					splitAllocation(temp, size);
					returnValue=temp;
					break;
				}
				temp=temp.next;
			}
		}
		return returnValue;
	}
	/**
	 * Helper method called by malloc.
	 * It function is use a helper methods to go through the heap and find the best fit block and allocate Memory.
	 * If not to return null.
	 * @param  size the number of bytes we want to allocate.
	 * @return BareNode allocate Memblock/(node)
	 */
	private BareNode mallocBestFit(int size){
		BareNode bestFit=null;
		if(size>=1){
			bestFit=searchBestFit(size);
			if(bestFit!=null){
				splitAllocation(bestFit, size);
			}
		}
		return bestFit;
	}
	/**
	 * Helper method to mallocBestFit.
	 * Its function is to go through the heap and find BestFit/MemBlock big enough to hold size bytes and also close enough to the size.
	 * It costs O(N).
	 * @param size number of bytes.
	 * @return BareNode free Memblock that is big and close to the size.
	 */
	private BareNode searchBestFit(int size){
		int diff=1000*1000;
		BareNode temp=head, bestFit=null;
		while(temp!=null){
			if(temp.block.isFree && temp.block.size-size>=0 && temp.block.size-size<diff){
				bestFit=temp;
				diff=temp.block.size-size;
			}
			temp=temp.next;
		}
		return bestFit;
	}
	/**
	 * Helper  methods that is used by all of the polcies.
	 * it takes current(free block we want to allocate) and splits to allocate Memblock with the given size
	 * It costs O(1)
	 * @param current free block we want to allocate memory to.
	 * @param size is how much memory memory we want to allocate that block.
	 */
	private void splitAllocation(BareNode current, int size){
		int oldaddr=current.block.addr, oldsize=current.block.size;
		current.block=new MemBlock(oldaddr, size, false);
		lastUsedFree=current;
		if(oldsize-size>0){
			BareNode newNode=new BareNode(new MemBlock(oldaddr+size, oldsize-size, true));
			newNode.next=current.next;
			if(newNode.next!=null){
				newNode.next.prev=newNode;
			}
			current.next=newNode;
			newNode.prev=current;
			lastUsedFree=newNode;
		}	
	}
	/**
	 * Helper method 4 called mallocNextFit executes policy 3.
	 * It first search next fit form last used free memblock/ if it did not find any it goes foward
	 * If It does not find anything, It searches again from the beginning.
	 * The key for this method working is the static instace variable called lastUsedFree
	 * @param size the number memory in bytes.
	 * @return BareNode  allocated MemBlock node in next fit policy.
	 */
	private BareNode mallocNextFit(int size){
		BareNode nextFit=null;
		if(size>=1){ 
			nextFit=searchNextFitFrUsedLastFree(size);
			// if still we can't find it
			// we will search again but instead from the beginning to lastfreeused node
			// better than having a circiular linked list.
			if(nextFit==null){
				nextFit=searchNextFitfrBegnToLastUsed(size);
			}
			if(nextFit!=null){
				splitAllocation(nextFit, size);
			}
		}
		return nextFit;	  
	}
	/**
	 * helper method that is called by malloc next fit.
	 * it job is to search from last used Free to the end to find a free memblock that is big enough to hold the given size.
	 * @param size how much memory in bytes we are looking for in the search.
	 * @return BareNode free block that fits the policy and have enough size to hold our given size.
	 */
	private BareNode searchNextFitFrUsedLastFree(int size){
		BareNode temp= lastUsedFree, nextFit=null;
		while(temp!=null){
			if(temp.block.isFree && size<=temp.block.size){
				nextFit=temp;
				break;
			}
			temp=temp.next;
		}
		return nextFit;
	}
	/**
	 * Second helper method that is called by malloc next fit.
	 * Its job is to search from the beginning to but not including that lastused free node to find a free memblock that is big enough to hold
	 * the given size.
	 * @param size how much memory in bytes we are looking for in the search.
	 * @return BareNode free block that fits the policy and  have enough size to hold our given size.
	 */
	private BareNode searchNextFitfrBegnToLastUsed(int size){
		BareNode temp=head, nextFit=null;
		while(temp!=lastUsedFree){
			if(temp.block.isFree && size<=temp.block.size){
				nextFit=temp;
				break;
			}
			temp=temp.next;
		}
		return nextFit;
	}
	/**
	 * Helper methods 3 that is called by malloc.
	 * Its job is to use use helper methods to finds worst fit free block of memory and to do split allocation/ else to return null
	 * @param size how much memory we want to allocate
	 * @return BareNode allocate memblock(node).
	 */
	private BareNode mallocWorstFit(int size){
		BareNode worstFit=null;
		if(size>=1){
			worstFit=searchWorstFit(size);
			if(worstFit!=null){
				splitAllocation(worstFit, size);
			}
		}
		return worstFit;
	}
	/**
	 * helper method called by mallocWorstFit.
	 * its job is to search for the biggest free Memblock and to return if it can else to return.
	 * @param size memory size  we are looking for.
	 * @return BareNode the biggest free Memblock(node).
	 */
	private BareNode searchWorstFit(int size){
		BareNode worstFit=null, temp=head;
		int biggestSize=-1;
		while(temp!=null){
			if(temp.block.isFree && size< temp.block.size && temp.block.size>biggestSize){
				biggestSize=temp.block.size;
				worstFit=temp;
			}
			temp=temp.next;
		}
		return worstFit;	
	}
	/**
	 * It creates an instance of Iterator that iterates through MemBlocks.
	 * @return Iterator
	 */
	public Iterator<MemBlock> iterator(){
		return new Iterator<MemBlock>(){
			private BareNode temp=head;
			public MemBlock next(){
				MemBlock returnValue=temp.block;
				temp=temp.next;
				return returnValue;
			}
			public boolean hasNext(){
				return temp!=null;
			}
		};
	}
	/**
	 * free takes in a node and frees if it is in use and not null.
	 * @param node is a memblock in use/assigned by malloc we want to free.
	 * @return true/false depending on if it was successful or not.
	 */
	public boolean free(BareNode node){
		boolean returnValue=false;
        if(node!=null && !node.block.isFree){
			if(node.next!=null && node.prev!=null && node.prev.block.isFree && node.next.block.isFree){
				//this tackles the case where if we free the middle memblock and either of its sides are free.
				//I chose right over left first because it is easier to follow logically we have the current memblock for both
				// instead the prev.
				colaseingRight(node, node.next);
				colaseingLeft(node.prev, node);
			}
			else if(node.next!=null && node.next.block.isFree){
				colaseingRight(node, node.next);
			}
			else if(node.prev!=null && node.prev.block.isFree){
				colaseingLeft(node.prev, node);
			}
			else{
				node.block=new MemBlock(node.block.addr, node.block.size, true);
			}
			returnValue=true;

		}
		return returnValue;
	}
	/**
	 * Helper Method 1 that is called by free called colaseingRight.
	 * Its job is to first free the current Memblock and then combine it with the next free memeblock next to it.
	 * @param current is current MemBlock we want to free.
	 * @param nextNode is Memblock next to current which is free.
	 * 
	 */
	private void colaseingRight(BareNode current, BareNode nextNode){
	    current.block=new MemBlock(current.block.addr, current.block.size+nextNode.block.size, true);
		if(nextNode == lastUsedFree)
			lastUsedFree=nextNode.next;
		current.next=nextNode.next;
		if(nextNode.next!=null)
			nextNode.next.prev=current;
	}
	/**
	 * Helper Method 2 that is called by free called colaseingLeft.
	 * Its job is similar to colasing right but it first g=free current MemBlock first and then it combine it with the prev memnlock to current
	 * that is free.
	 * @param prev is previous memblock to current that is free.
	 * @param current is the current memblock we are freeing.
	 * 
	 */
	private void colaseingLeft(BareNode prev, BareNode current){
		prev.block=new MemBlock(prev.block.addr, current.block.size+prev.block.size, true);
		if(current==lastUsedFree)
			lastUsedFree=current.next;
		prev.next=current.next;
		if(current.next!=null)
			current.next.prev=prev;
	}
	/**
	 * realloc takes MemBlock and depending where it greater than or less than the current Mem size
	 * we shrink/expanding if we can/ if not we call malloc and free/ if malloc does have enough space to allocate we just leave it alone.
	 * @param node we are trying to resize/shrink
	 * @param size number of bytes we are trying to reallocate.
	 * @return BareNode new reallocate Memblock(bareNode).
	 */
	public BareNode realloc(BareNode node, int size){
		BareNode nodeHolder=node;
		if(size>0 && size<node.block.size){
			node=shrinkMemBlock(node, size);
		}
		else if(size>node.block.size){
			node=expandMemBlock(node, size);
			if(node==null){
				node=malloc(size);
				if(node!=null){
					free(nodeHolder);
				}
			}
		}
		return node;
	}
	/**
	 * It is a helper method called by rellaoc.
	 * Its function is to shrink a given Memblock to a given size and free the remaining
	 * @param node is node we are shrinking/ reallocating by a size;
	 * @param size is number of bytes we want to reallocate
	 * @return BareNode which barenode with Memblock that has been reduced.
	 */
	private BareNode shrinkMemBlock(BareNode node, int size){
		int freedMemSize=node.block.size-size;
		if(node.next!=null && node.next.block.isFree){
            node.next.block=new MemBlock(node.block.addr+size, freedMemSize+node.next.block.size, true);
		}
		else{
			BareNode newNode=new BareNode(new MemBlock(node.block.addr+size, freedMemSize, true));
			newNode.next=node.next;
			if(newNode.next!=null){
				newNode.next.prev=newNode;
			}
			node.next=newNode;
			newNode.prev=node;
		}
		node.block=new MemBlock(node.block.addr, size, false);
		return node;
	}
	/**
	 * Its helper method called by realloc.
	 * Its function is to expand a given Memblock by given size if it is possible to return expanded else return null 
	 * @param node what we are expanding.
	 * @param size is number of bytes we want to reallocate
	 * @return BareNode is a barenode whose Memblock has been expaneded.
	 */
	private BareNode expandMemBlock(BareNode node, int size){
		int memBlockSizeNeeded=size-node.block.size, whatIsLeft=node.next.block.size- memBlockSizeNeeded;
		if(node.next!=null && node.next.block.isFree && whatIsLeft>=0){
			node.block=new MemBlock(node.block.addr, size, false);
			node.next.block=new MemBlock(node.block.addr+size, whatIsLeft, true);
			if(whatIsLeft==0){
				node.next=node.next.next;
				if(node.next.next!=null){
					node.next.next.prev=node;
				}
			}
		}
		else
			node=null;
		return node;
	}
	/**
	 * It goes through heap based addrs that in use it will mark them and free the unmarked ones.
	 * @param addrs set of addrs in use.
	 * @return int the total number of memory block freed.
	 */
	public int garbageCollect(Set<Integer> addrs){
		int totalFreedSize=0;
		markInUseNodes(addrs);
		totalFreedSize=freeUnmarkedNodes();
		unMarkNodes();
		return totalFreedSize;
	}
	/**
	 * It helper method called by garab collect.
	 * Its function is to go through the heap marking mem in use.
	 * @param addrs is the set of mem address in use.
	 */
	private void markInUseNodes(Set<Integer> addrs){
		BareNode temp=head;
		if(addrs.size()>0){
			while(temp!=null){
				if(addrs.contains(temp.block.addr)){
					temp.marked=true;
				}
				temp=temp.next;
			}
		}
	}
	/**
	 * It is a helper method called by garabage collect.
	 * Its function is to go through the heap freeing the unmarked nodes.
	 * @return int the total bytes(size) freed.
	 */
	private int freeUnmarkedNodes(){
		BareNode temp=head;
		int totalFreedSize=0;
		while(temp!=null){
			if(!temp.marked && !temp.block.isFree){
				totalFreedSize+=temp.block.size;
				free(temp);
			}
			temp=temp.next;
		}
		return totalFreedSize;
	}
	/**
	 * It is a helper method called by garabage collect.
	 * Its function is to go through the heap unmarking the marked nodes.
	 */
	private void unMarkNodes(){
		BareNode temp=head;
		while(temp!=null){
			if(temp.marked){
				temp.marked=false;
			}
			temp=temp.next;
		}
	}
	/**
	 * MemManger class used for testing.
	 * @param args is not used.
	 */
	public static void main(String[] args){
		MemMan manager=factory(3, new BareNode(new MemBlock(0, 23, true)));
		// BareNode x= manager.malloc();
		// //System.out.println(x.block);
		// BareNode y= manager.malloc(10);
		// //System.out.println(y.block);
		// System.out.println(manager.free(y));
		// BareNode z= manager.malloc(10);
		// BareNode g= manager.malloc(1);
		// for(MemBlock element : manager){
		// 	System.out.print(element+" \n");
		// Test 1}
		//10+5+15+10
		BareNode x=manager.malloc(4);
		BareNode y=manager.malloc(4);
		BareNode z=manager.malloc(4);
		BareNOde h=manager.malloc(11);
		manager.free(h);
		manager.free(x);
		manager.free(z);
		free(y);
		x=null;
		//manager.garbageCollect({4,8});

		for(MemBlock element: manager){
			System.out.println(element+"\n");
		}
		// manager.free(x);
		// manager.free(h);
		// manager.malloc(6);
		// manager.malloc(2);
		// for(MemBlock element: manager){
		// 	System.out.println(element+"\n");
		// }
		
	}
}