package edu.salisbury.core_simulator;


import edu.salisbury.core_simulator.CoreNode;

/**
 * Represents an IOPort for a node. Can either be available or unavailable depending on if it is
 * currently communicationg or not. Contains a link to the node opposed by this port. Also handles
 * basic communication events through its {@link #teardownConnection()} or 
 * {@link #initiateConnection()} methods.
 * 
 * @author timfoil
 *
 */
public class CoreNodeIOPort
{
	/**
	 * An enum that represents the status of a CoreNodeIOPort either UNAVAILABLE or 
	 * AVAILABLE.
	 * @author timfoil
	 *
	 */
	public enum PortIOStatus {AVAILABLE, UNAVAILABLE}
	private PortIOStatus status;
	private CoreNode link;
	private boolean toTeardown = false;
	
	public CoreNodeIOPort(CoreNode edgeLink)
	{
		link = edgeLink;
		status = PortIOStatus.AVAILABLE;
	}
	/**
	 * Check the status of this CoreNodeIOPort.
	 * @return
	 */
	public PortIOStatus checkStatus()
	{
		return status;
	}
	
	/**
	 * Returns a boolean that indicates if this CoreNodeIOPort is currently AVAILABLE OR UNAVAILABLE
	 * @return 	<code>true</code> if this node is currently available;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isAvailable()
	{
		switch(status)
		{
			case AVAILABLE:
				return true;
			case UNAVAILABLE:
				return false;
			default:
				throw new RuntimeException("CoreNodeIOPort must either be available or " +
						"unavailable");
		}
	}
	
	/**
	 * Simulates initiating a connection, switches the CoreNode status from available to 
	 * unavailable. 
	 */
	public void initiateConnection()
	{
		if(status == PortIOStatus.AVAILABLE)
		{
			status = PortIOStatus.UNAVAILABLE;
		} else throw new RuntimeException("Connection currently exists, cannot initiate " +
				"connection");
		
	}
	
	/**
	 * Teardown the connection for the next cycle
	 */
	public void teardownConnection()
	{
		if(status == PortIOStatus.AVAILABLE) 
			throw new RuntimeException("No connection to teardown.");
		
		toTeardown = true;
	}

	/**
	 * Simulate a single cycle for this CoreNodeIOPort.
	 */
	public void simulateCycle()
	{
		if (toTeardown)
		{
			status = PortIOStatus.AVAILABLE;
			toTeardown = false;
		}
	}
	
	/**
	 * @return the node on the other end of this link
	 */
	public CoreNode getLink()
	{
		return link;
	}
}
