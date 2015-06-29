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
		return new Coordinate((nodeNumber/8)+1 , nodeNumber % 8); 
	}
	
	public BasicNode numberToNode(int nodeNumber)
	{
		this.checkForValidNodeNumber(nodeNumber);
		return basicNodeList[nodeNumber];
	}
	
	public int coordinatesToNumber(Coordinate coord)
	{
		checkForValidCoordinates(coord);
		return (coord.getY() + (coord.getX()-1) * 8);
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
		if(coord.getY() > 7 || coord.getY() < 0)
		{
			throw new IllegalArgumentException("Y coordinate must be between 0 and 7 " +
					"inclusive.");
		}
	}
	
	protected void checkForValidNodeNumber(int nodeNumber)
	{
		if(nodeNumber < 0 || nodeNumber > 15) 
		{
			throw new IllegalArgumentException("The nodenumber for the basicArchitecture " +
					"must be between 0 and 15 inclusive.");
		}
	}
}