package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.BasicArchitecture;
import edu.salisbury.core_simulator.Coordinate;

public class BasicUnMappedArchitecture extends BasicArchitecture
{
	public BasicUnMappedArchitecture(int numberOfCoreNodes, int bitsPerFlit, int teardownTime)
	{
		super(numberOfCoreNodes, bitsPerFlit, teardownTime);
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

	public Coordinate numberToCoordinate(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return new Coordinate((nodeNumber/(getNumberOfCoreNodes()/2))+1, 
				(nodeNumber < getNumberOfCoreNodes()/2) ? nodeNumber : 
					((getNumberOfCoreNodes()/2) - 1) - (nodeNumber % (getNumberOfCoreNodes()/2))); 
	}
	
	public BasicNode numberToNode(int nodeNumber)
	{
		this.checkForValidNodeNumber(nodeNumber);
		return basicNodeList[nodeNumber];
	}
	
	public int coordinatesToNumber(Coordinate coord)
	{
		checkForValidCoordinates(coord);
		switch(coord.getX())
		{
			case 1:
				return coord.getY();
			case 2:
				return (getNumberOfCoreNodes() - 1) - coord.getY();
			default:
				throw new RuntimeException("Invalid X coordinate value");
		}
	}
	
	public BasicNode coordinatesToNode(Coordinate coord)
	{
		return basicNodeList[coordinatesToNumber(coord)];
	}
	
	protected void checkForValidCoordinates(Coordinate coord)
	{
		if(coord.getX() > 2 || coord.getX() < 1)
		{
			throw new IllegalArgumentException("X coordinate must be either 1 or 2");
		}
		if(coord.getY() >= (getNumberOfCoreNodes()/2) || coord.getY() < 0)
		{
			throw new IllegalArgumentException("Y coordinate must be between 0 and " +
					"(getNumberOfCoreNodes()/2) - 1 inclusive.");
		}
	}
	
	protected void checkForValidNodeNumber(int nodeNumber)
	{
		if(nodeNumber < 0 || nodeNumber > getNumberOfCoreNodes()-1) 
		{
			throw new IllegalArgumentException("The nodenumber for the basicArchitecture " +
					"must be between 0 and getNumberOfCoreNodes()-1 inclusive.");
		}
	}
}