package edu.salisbury.core_simulator;

/**
 * A basic abstract building block for a task class. Lays down some of the standards that
 * a subclassed task are expected to follow.
 * @author timfoil
 *
 */
public abstract class CoreTask
{
	/**Total time it took to execute this task */
	private int taskTime;
	
	/**
	 * Increment the totalTaskTime by 1
	 */
	public void incrementTotalTaskTime()
	{
		taskTime++;
	}
	
	/**
	 * Increment the totalTaskTimeBy a givenArgument.
	 * @param increment amount to increment the totalTaskTime by
	 */
	public void incrementTotalTaskTimeBy(int increment)
	{
		taskTime += increment;
	}
	
	/**
	 * @return the totalTaskTime for whicht this task has been running for
	 */
	public int getTotalTaskTime()
	{
		return taskTime;
	}
	
	/**
	 * Simulates a singleCycle for this task
	 */
	public void simulateCycle()
	{
		taskTime++;
	}
}
