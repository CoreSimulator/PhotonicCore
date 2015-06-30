package edu.salisbury.core_simulator;

/**
 * Basic building block class for Nodes in the simulator.
 * @author timfoil
 *
 */
public abstract class CoreNode
{
	/** An array of the ports containing the edges to other nodes.*/
	public CoreNodeIOPort[] edges;
	
	/**
	 * Simulates the tasks and jobs this node needs to complete for a cycle.
	 */
	public abstract void simulateCycle();
	
	/**
	 * Initiates a connection to the edge specified by the number of the edge in the edges array.
	 * @param edgeNumber The edge specified in the edge array
	 */
	public void initiateConnectionAtEdge(int edgeNumber)
	{
		if(edges[edgeNumber].isAvailable())
		{
			edges[edgeNumber].initiateConnection();
		} 
		else 
			throw new RuntimeException("Cannot initiate connection port at edge is not available");
	}
}
