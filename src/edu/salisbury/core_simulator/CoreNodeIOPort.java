package edu.salisbury.core_simulator;


import edu.salisbury.core_simulator.CoreNode;

public class CoreNodeIOPort
{
	public enum PortIOStatus {AVAILABLE, UNAVAILABLE}
	private PortIOStatus status;
	private CoreNode link;
	private boolean toTeardown = false;
	
	public CoreNodeIOPort(CoreNode edgeLink)
	{
		link = edgeLink;
		status = PortIOStatus.AVAILABLE;
	}
	
	public PortIOStatus checkStatus()
	{
		return status;
	}
	
	public boolean isAvailable()
	{
		switch(status)
		{
			case AVAILABLE:
				return true;
			case UNAVAILABLE:
				return false;
			default:
				throw new RuntimeException("CoreNodeIOPort must either be available or unavailable");
		}
	}
	
	public void initiateConnection()
	{
		if(status == PortIOStatus.AVAILABLE)
		{
			status = PortIOStatus.UNAVAILABLE;
		} else throw new RuntimeException("Connection currently exists, cannot initiate " +
				"connection");
		
	}
	
	public void teardownConnection()
	{
		if(status == PortIOStatus.AVAILABLE) 
			throw new RuntimeException("No connection to teardown.");
		
		toTeardown = true;
	}

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
