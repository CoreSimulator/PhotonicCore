package edu.salisbury.photonic.cyclical_core_simulator;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreArchitecture;
import edu.salisbury.photonic.core_simulator.LogEntry;

/**
 * Foundation for a cyclical type architecture, one where the non coreNodes form a circular ring. In
 * addition each node has a connection to the headnode which handles message routing between the 
 * other nodes. 
 * @author timfoil
 *
 */
public abstract class CyclicalArchitecture extends CoreArchitecture
{
	/**The headNode for this architecture which handles message routing*/
	protected CyclicalHeadNode headNode; 
	
	/**
	 * A list of the non-headNodes that exist in this architecture, ordered by their assigned
	 * nodeNumbers.
	 */
	protected CyclicalNode[] cyclicalNodeList;
	
	/**
	 * Constructor for a cyclical architecture.
	 * 
	 * @param 	numberOfCoreNodes the number of non-headNodes that exist in this architecture.
	 * @param 	bitsPerFlit number of bits that exist per flit
	 * @param 	teardownTime amount of time it takes to destroy a connection between communicating 
	 * 			nodes
	 */
	public CyclicalArchitecture(int numberOfCoreNodes, int bitsPerFlit,
			int teardownTime)
	{
		super(numberOfCoreNodes, bitsPerFlit, teardownTime);
		
		cyclicalNodeList = new CyclicalNode[getNumberOfCoreNodes()];
		headNode = new CyclicalHeadNode(this);
			//BasicArchitecture and uncomment this
	}
	
	//replace with mapping function
	/**
	 * Converts a nodeNumber to its corresponding Coordinate
	 * @param nodeNumber to be converted to a Coordinate
	 * @return a Coordinate that represents the given nodeNumber
	 */
	public abstract Coordinate numberToCoordinate(int nodeNumber);
	
	/**
	 * Gets the node represented by the given coordinate.
	 * 
	 * @param coord that represents the node to get
	 * @return the node represented by the given coordinate
	 */
	public abstract CyclicalNode coordinatesToNode(Coordinate coord);
	
	/**
	 * Converts a number to a node designated by that number.
	 * @param nodeNumber the number of the node to get
	 * @return the node designated by the given nodeNumber
	 */
	public abstract CyclicalNode numberToNode(int nodeNumber);
	
	/**
	 * Converts a coordinate representing a specific node to a the same node's designated 
	 * nodeNumber.
	 * @param coord the coordinate to convert to a nodeNumber.
	 * @return the nodeNumber that is represented by the given coordinate
	 */
	public abstract int coordinatesToNumber(Coordinate coord);
	
	/**
	 * Checks to ensure that the coordinate given is a valid one in the given architecture.
	 * @param coord a coordinate to check
	 */
	protected abstract void checkForValidCoordinates(Coordinate coord);
	
	/**
	 * Checks to ensure that the nodeNumber given is a valid one in the given architecture.
	 * @param nodeNumber to check
	 */
	protected abstract void checkForValidNodeNumber(int nodeNumber);
	
	/**
	 * Simulates the creation of a task described by the given {@link LogEntry}.
	 * 
	 * @param entry an entry that describes a task to be simulated
	 */
	public void simulateTask(LogEntry entry)
	{
		CyclicalTask taskToAssign = new CyclicalTask(coordinatesToNode(entry.sourceNode()), 
				coordinatesToNumber(entry.destNode()), entry.packetSize(), entry.timeStamp(), this);
		
		numberToNode(taskToAssign.getSourceNodeNum()).addTask(taskToAssign);
	}
	
	/**
	 * @return The number of core nodes (non head-nodes) in the architecture
	 */
	public int numberOfCoreNodes()
	{
		return cyclicalNodeList.length;
	}
	
	/**
	 * Returns <code>true</code> if all tasks in the simulator have been simulated and finished.
	 * 
	 * @return 	<code>true</code> if all tasks in the simulator have been simulated and finished;
	 * 			<code>false</code> otherwise.
	 */
	public boolean allTasksFinished()
	{
		return headNode.allTasksFinished();
	}
	
	/**
	 * Simulate a single cycle for this architecture.
	 */
	public void simCycle()
	{
		for(int i = 0; i < cyclicalNodeList.length; i++)
		{
			cyclicalNodeList[i].simulateCycle();
		}
		headNode.simulateCycle();
	}
}
