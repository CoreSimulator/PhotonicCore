package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.CoreSimOverseer;
import edu.salisbury.core_simulator.LogEntry;

public class BasicSimOverseer extends CoreSimOverseer
{	
	private BasicArchitecture simulation;
	
	public BasicSimOverseer(int numberOfCoreNodes)
	{
		if(numberOfCoreNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicArchitecture(numberOfCoreNodes, 64, 1);
	}
	
	public BasicSimOverseer(int numberOfCoreNodes, int bitsPerFlit, int teardownTime)
	{
		if(bitsPerFlit < 0 || teardownTime < 0 || numberOfCoreNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicArchitecture(numberOfCoreNodes, bitsPerFlit, teardownTime);
	}
	
	@Override
	protected void delegateTaskToNode(LogEntry entry)
	{
		simulation.simulateTask(entry);
	}

	@Override
	protected void simulateCycle()
	{
		simulation.simCycle();
	}

	@Override
	public boolean allTasksFinished()
	{
		return simulation.allTasksFinished();
	}
}
