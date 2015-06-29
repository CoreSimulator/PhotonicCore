package edu.salisbury.core_simulator;

import edu.salisbury.basic_core_simulator.BasicHeadNode;
import edu.salisbury.basic_core_simulator.BasicNode;
import edu.salisbury.basic_core_simulator.BasicTask;

public abstract class BasicArchitecture extends CoreArchitecture
{
	protected BasicHeadNode headNode; 
	protected BasicNode[] basicNodeList;
	
	public BasicArchitecture(int numberOfCoresNodes, int bitsPerFlit,
			int teardownTime)
	{
		super(numberOfCoresNodes, bitsPerFlit, teardownTime);
		
		basicNodeList = new BasicNode[getNumberOfCoreNodes()];
		headNode = new BasicHeadNode(this);
			//BasicArchitecture and uncomment this
	}
	
	//replace with mapping function
	public abstract Coordinate numberToCoordinate(int nodeNumber);
	public abstract BasicNode coordinatesToNode(Coordinate coord);
	public abstract BasicNode numberToNode(int nodeNumber);
	public abstract int coordinatesToNumber(Coordinate coord);
	protected abstract void checkForValidCoordinates(Coordinate coord);
	protected abstract void checkForValidNodeNumber(int nodeNumber);
	
	
	public void simulateTask(LogEntry entry)
	{
		BasicTask taskToAssign = new BasicTask(coordinatesToNode(entry.sourceNode()), 
				coordinatesToNumber(entry.destNode()), entry.packetSize(), entry.timeStamp(), this);
		
		numberToNode(taskToAssign.getSourceNodeNum()).addTask(taskToAssign);
	}
	
	/**
	 * @return The number of core nodes (non head-nodes) in the architecture
	 */
	public int numberOfCoreNodes()
	{
		return basicNodeList.length;
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
	
	public void simCycle()
	{
		for(int i = 0; i < basicNodeList.length; i++)
		{
			basicNodeList[i].simulateCycle();
		}
		headNode.simulateCycle();
	}
	
	//public void simulateTask(LogEntry entry) //Implement this here eventually
	//{;}
	//public void simCycle()
}
