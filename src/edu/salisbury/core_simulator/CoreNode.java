package edu.salisbury.core_simulator;

/**
 * Basic building block class for a Nodes in the simulator.
 * @author timfoil
 *
 */
public abstract class CoreNode
{
	public CoreNodeIOPort[] edges;
	public abstract void simulateCycle();
	
	public void initiateConnectionAtEdge(int edgeNumber)
	{
		if(edges[edgeNumber].isAvailable())
		{
			edges[edgeNumber].initiateConnection();
		} else throw new RuntimeException("Cannot initiate connection port at edge is not " +
				"available");
	}
}
