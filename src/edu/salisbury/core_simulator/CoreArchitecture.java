package edu.salisbury.core_simulator;

/**
 * A basic abstract building block for an architecture class. Lays down some of the standards that
 * an subclassed architecture are expected to follow
 * @author timfoil
 *
 */
public abstract class CoreArchitecture
{
	private int numberOfCoreNodes;
	private int bitsPerFlit;
	private int teardownTime;
	
	/**
	 * Constructor for a CoreArchitecture.
	 * 
	 * @param 	numberOfCoreNodes number of non-head Nodes that an architecture contains
	 * @param 	bitsPerFlit number of bits that exist per flit
	 * @param 	teardownTime amount of time it takes to destroy a connection between communicating
	 * 			nodes
	 */
	public CoreArchitecture(int numberOfCoreNodes, int bitsPerFlit, int teardownTime)
	{
		this.bitsPerFlit = bitsPerFlit;
		this.numberOfCoreNodes = numberOfCoreNodes;
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
