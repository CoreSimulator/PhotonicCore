package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.Coordinate;
import edu.salisbury.core_simulator.LogEntry;

public class BasicArchitecture
{
	private BasicHeadNode headNode; 
	private BasicNode[] basicNodeList;
	private int bitsPerFlit;
	private int teardownTime;
	
	public BasicArchitecture(int numberOfCoreNodes, int bitsPerFlit, int teardownTime)
	{
		basicNodeList = new BasicNode[numberOfCoreNodes];
		this.bitsPerFlit = bitsPerFlit;
		this.teardownTime = teardownTime;
		headNode = new BasicHeadNode(this);
		initArchitecture();
	}
	
	/* Create the node architecture in a clockwise fashion.
	 * Setup the edges as BasicNodeIOPorts */
	private void initArchitecture()
	{
		basicNodeList[0] = new BasicNode(headNode, 0);
		
		for(int i = 1; i < basicNodeList.length; i++)
		{
			basicNodeList[i] = new BasicNode(headNode, i);
			
			basicNodeList[i].setCounterClockwiseEdge(basicNodeList[i-1]);
			basicNodeList[i-1].setClockwiseEdge(basicNodeList[i]);
		}
		basicNodeList[basicNodeList.length - 1].setClockwiseEdge(basicNodeList[0]);
		basicNodeList[0].setCounterClockwiseEdge(basicNodeList[basicNodeList.length - 1]);
		headNode.setEdges(basicNodeList);
	}

	public void simulateTask(LogEntry entry)
	{
		BasicTask taskToAssign = new BasicTask(this.coordinatesToNode(entry.sourceNode()), 
				coordinatesToNumber(entry.sourceNode()), coordinatesToNumber(entry.destNode()), 
				entry.packetSize(), bitsPerFlit, teardownTime, this);
		
		numberToNode(taskToAssign.getSourceNodeNum()).addTask(taskToAssign);
	}
	
	public void simCycle()
	{
		for(int i = 0; i < basicNodeList.length; i++)
		{
			basicNodeList[i].simulateCycle();
		}
		headNode.simulateCycle();
	}
	
	//TODO replace with mapping function
	public Coordinate numberToCoordinate(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return new Coordinate((nodeNumber/8)+1 , nodeNumber % 8); 
	}
	
	public BasicNode numberToNode(int nodeNumber)
	{
		this.checkForValidNodeNumber(nodeNumber);
		return basicNodeList[nodeNumber];
	}
	
	//TODO replace with mapping function
	public int coordinatesToNumber(Coordinate coord)
	{
		checkForValidCoordinates(coord);
		return (coord.getY() + (coord.getX()-1) * 8);
	}
	
	//TODO replaceThisAsWell
	public BasicNode coordinatesToNode(Coordinate coord)
	{
		return basicNodeList[coordinatesToNumber(coord)];
	}
	
	//TODO replace with mapping function
	private void checkForValidCoordinates(Coordinate coord)
	{
		if(coord.getX() > 2 || coord.getX() < 1)
		{
			throw new IllegalArgumentException("X coordinate must be either 1 or 2");
		}
		if(coord.getY() > 7 || coord.getY() < 0)
		{
			throw new IllegalArgumentException("Y coordinate must be between 0 and 7 " +
					"inclusive.");
		}
	}
	
	//TODO replace with mapping function
	private void checkForValidNodeNumber(int nodeNumber)
	{
		if(nodeNumber < 0 || nodeNumber > 15) 
		{
			throw new IllegalArgumentException("The nodenumber for the basicArchitecture " +
					"must be between 0 and 15 inclusive.");
		}
	}
	
	public boolean allTasksFinished()
	{
		return headNode.allTasksFinished();
	}
	
	public int numberOfCoreNodes()
	{
		return basicNodeList.length;
	}

	public int bitsPerFlit()
	{
		return bitsPerFlit;
	}

	/**
	 * @return the teardownTime
	 */
	public int getTeardownTime()
	{
		return teardownTime;
	}
}