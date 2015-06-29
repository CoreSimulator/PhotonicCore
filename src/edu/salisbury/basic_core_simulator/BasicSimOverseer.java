package edu.salisbury.basic_core_simulator;

import java.util.HashMap;

import edu.salisbury.core_simulator.BasicArchitecture;
import edu.salisbury.core_simulator.Coordinate;
import edu.salisbury.core_simulator.CoreSimOverseer;
import edu.salisbury.core_simulator.LogEntry;

public class BasicSimOverseer extends CoreSimOverseer
{	
	private BasicArchitecture simulation;
	
	public BasicSimOverseer(int numberOfNonHeadNodes)
	{
		if(numberOfNonHeadNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicUnMappedArchitecture(numberOfNonHeadNodes, 64, 1);
	}
	
	public BasicSimOverseer(int numberOfNonHeadNodes, int bitsPerFlit, int teardownTime)
	{
		if(bitsPerFlit < 0 || teardownTime < 0 || numberOfNonHeadNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicUnMappedArchitecture(numberOfNonHeadNodes, bitsPerFlit, teardownTime);
	}
	
	public BasicSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicMapArchitecture(bitsPerFlit, teardownTime, coordinateSwitchingMap);
	}
	
	public BasicSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap, 
			HashMap<Coordinate, Coordinate> switchingMap)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new BasicMapArchitecture(bitsPerFlit, teardownTime, coordinateSwitchingMap,
				switchingMap);
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
