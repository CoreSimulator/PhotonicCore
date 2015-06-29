package edu.salisbury.core_simulator;

public abstract class CoreArchitecture
{
	private int numberOfCoreNodes;
	private int bitsPerFlit;
	private int teardownTime;
	
	public CoreArchitecture(int numberOfCoresNodes, int bitsPerFlit, int teardownTime)
	{
		this.bitsPerFlit = bitsPerFlit;
		this.numberOfCoreNodes = numberOfCoresNodes;
		this.teardownTime = teardownTime;
	}

	/**
	 * @return the numberOfCoreNodes
	 */
	public int getNumberOfCoreNodes()
	{
		return numberOfCoreNodes;
	}

	/**
	 * @return the bitsPerFlit
	 */
	public int getBitsPerFlit()
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
