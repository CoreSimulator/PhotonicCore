package edu.salisbury.photonic.cyclical_core_simulator;

import edu.salisbury.photonic.core_simulator.Coordinate;

/**
 * Cyclical UnMapped architecture is a basic cyclical arcitecture. Nodes with coordinates (0, Y) for
 * any Y are reserved for repeaters. Coordinates for its underlying nodes start with (1,0) and are 
 * bounded by (2, (numberofNodes/2)). 
 * 
 * <p>Below is an diagram of the architecture with 8 coreNodes, where coordinates represent nodes,
 * dashes represent links and decimals can be ignored.</p>
 * 
 * <p>(1, 0) - (1, 1) - (1, 2) - (1, 3) - (1, 4)</p>
 * <p>| . . . . . . . . . . . . . . . . . . . |</p>
 * <p>(2, 0) - (2, 1) - (2, 2) - (2, 3) - (2, 4)</p>
 * @author timfoil
 *
 */
public class CyclicalUnMappedArchitecture extends CyclicalArchitecture
{
	/**
	 * Constructor for a CyclicalUnMappedArchitecture
	 * @param 	numberOfCoreNodes Number of nodes in the overlying architecture (non including the 
	 * 			headNode)
	 * @param 	bitsPerFlit The number of bits that exist per flit in this simulation.
	 * @param 	teardownTime Number of cycles it takes to teardown a connection between links in 
	 * 			this simulation
	 */
	public CyclicalUnMappedArchitecture(int numberOfCoreNodes, int bitsPerFlit, int teardownTime)
	{
		super(numberOfCoreNodes, bitsPerFlit, teardownTime, false);
		initArchitecture();
	}
	
	/**
	 * Constructor for a CyclicalUnMappedArchitecture
	 * @param 	numberOfCoreNodes Number of nodes in the overlying architecture (non including the 
	 * 			headNode)
	 * @param 	bitsPerFlit The number of bits that exist per flit in this simulation.
	 * @param 	teardownTime Number of cycles it takes to teardown a connection between links in 
	 * 			this simulation
	 * @param 	printTaskInfo prints task info if true does not otherwise
	 */
	public CyclicalUnMappedArchitecture(int numberOfCoreNodes, int bitsPerFlit, int teardownTime, 
			boolean printTaskInfo)
	{
		super(numberOfCoreNodes, bitsPerFlit, teardownTime, printTaskInfo);
		initArchitecture();
	}
	
	/* Create the node architecture in a clockwise fashion.
	 * Setup the edges as BasicNodeIOPorts */
	private void initArchitecture()
	{
		cyclicalNodeList[0] = new CyclicalNode(headNode, 0, printTaskInfo);
		
		for(int i = 1; i < cyclicalNodeList.length; i++)
		{
			cyclicalNodeList[i] = new CyclicalNode(headNode, i, printTaskInfo);
			
			cyclicalNodeList[i].setCounterClockwiseEdge(cyclicalNodeList[i-1]);
			cyclicalNodeList[i-1].setClockwiseEdge(cyclicalNodeList[i]);
		}
		cyclicalNodeList[cyclicalNodeList.length - 1].setClockwiseEdge(cyclicalNodeList[0]);
		cyclicalNodeList[0].setCounterClockwiseEdge(cyclicalNodeList[cyclicalNodeList.length - 1]);
		headNode.setEdges(cyclicalNodeList);
	}

	public Coordinate numberToCoordinate(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return new Coordinate((nodeNumber/(getNumberOfCoreNodes()/2))+1, 
				(nodeNumber < getNumberOfCoreNodes()/2) ? nodeNumber : 
					((getNumberOfCoreNodes()/2) - 1) - (nodeNumber % (getNumberOfCoreNodes()/2))); 
	}
	
	public CyclicalNode numberToNode(int nodeNumber)
	{
		this.checkForValidNodeNumber(nodeNumber);
		return cyclicalNodeList[nodeNumber];
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
	
	public CyclicalNode coordinatesToNode(Coordinate coord)
	{
		return cyclicalNodeList[coordinatesToNumber(coord)];
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