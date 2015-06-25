package edu.salisbury.core_simulator;

public abstract class CoreTask
{
	/**Total time it took to execute this task */
	protected int taskTime;
	
	public void incrementTotalTaskTime()
	{
		taskTime++;
	}
	
	public void incrementTotalTaskTimeBy(int increment)
	{
		taskTime += increment;
	}
	
	public int getTotalTaskTime()
	{
		return taskTime;
	}
	
	public void simulateCycle()
	{
		taskTime++;
	}
}
