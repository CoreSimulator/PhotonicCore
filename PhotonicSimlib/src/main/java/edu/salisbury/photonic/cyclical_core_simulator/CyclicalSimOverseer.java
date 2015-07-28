package edu.salisbury.photonic.cyclical_core_simulator;

import java.util.HashMap;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreSimOverseer;
import edu.salisbury.photonic.core_simulator.LogEntry;

/**
 * An overseer used to run architectures that are cyclical in nature and have an overlying headNode.
 * 
 * @author timfoil
 *
 */
public class CyclicalSimOverseer extends CoreSimOverseer
{	
	public static int totalTasks = 0;
	public static int totalRequestingTime = 0;
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalUnMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture}
	 * @param numberOfNonHeadNodes The number of nonheadNodes that will be used to create the 
	 * underlying network
	 */
	public CyclicalSimOverseer(int numberOfNonHeadNodes)
	{
		this(numberOfNonHeadNodes, 64, 1);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalUnMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture}
	 *
	 */
	public CyclicalSimOverseer(int numberOfNonHeadNodes, int bitsPerFlit, int teardownTime)
	{
		if(bitsPerFlit < 0 || teardownTime < 0 || numberOfNonHeadNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalUnMappedArchitecture(
				numberOfNonHeadNodes, bitsPerFlit, teardownTime);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalUnMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture}
	 *
	 */
	public CyclicalSimOverseer(int numberOfNonHeadNodes, int bitsPerFlit, int teardownTime, 
		int[] mrrSwitchesTopLeftNodeNumbers, boolean printDebugInfo)
	{
		if(bitsPerFlit < 0 || teardownTime < 0 || numberOfNonHeadNodes < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalUnMappedArchitecture(numberOfNonHeadNodes, bitsPerFlit, 
				teardownTime, mrrSwitchesTopLeftNodeNumbers, printDebugInfo);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture} 
	 * @param 	numberOfNonHeadNodes The number of nonheadNodes that will be used to create the 
	 * 			underlying network
	 * @param	bitsPerFlit The number of bits in each flit of information
	 * @param	teardownTime The amount of time it takes to teardown connections
	 * @param	coordinateSwitchingMap A map which maps coordinates to a specific number for each
	 * 			node. Numbers should begin at 0 and end at map.length()-1
	 */
	public CyclicalSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalMappedArchitecture(bitsPerFlit, teardownTime, 
				coordinateSwitchingMap);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture} 
	 * @param 	numberOfNonHeadNodes The number of nonheadNodes that will be used to create the 
	 * 			underlying network
	 * @param	bitsPerFlit The number of bits in each flit of information
	 * @param	teardownTime The amount of time it takes to teardown connections
	 * @param	coordinateSwitchingMap A map which maps coordinates to a specific number for each
	 * 			node. Numbers should begin at 0 and end at map.length()-1
	 * @param 	mrrSwitchesTopLeftNodeNumbers Designates a top left node to a MRR switch
	 * @param	taskInfo prints debug info about tasks if set to true
	 */
	public CyclicalSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap, 
			int[] mrrSwitchesTopLeftNodeNumbers, boolean taskInfo)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalMappedArchitecture(bitsPerFlit, teardownTime, 
				coordinateSwitchingMap, null, mrrSwitchesTopLeftNodeNumbers, taskInfo);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture} 
	 * @param 	numberOfNonHeadNodes The number of nonheadNodes that will be used to create the 
	 * 			underlying network
	 * @param	bitsPerFlit The number of bits in each flit of information
	 * @param	teardownTime The amount of time it takes to teardown connections
	 * @param	coordinateSwitchingMap A map which maps coordinates to a specific number for each
	 * 			node. Numbers should begin at 0 and end at map.length()-1
	 * @param	switchingMap used to switch nodes at coordinate positions to different positions. 
	 * 			Each coordinate should exist as a key in the coordinateSwitchingMap and each key and
	 *  		value specified should appear once and only once in both the key and value sections 
	 * 			of the map. In addition this map should be bijective.
	 */
	public CyclicalSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap, 
			HashMap<Coordinate, Coordinate> switchingMap)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalMappedArchitecture(bitsPerFlit, teardownTime, 
				coordinateSwitchingMap, switchingMap, null, false);
	}
	
	/**
	 * A constructor for CyclicalSimOverseer which utilizes a {@link CyclicalMappedArchitecture} 
	 * as its underlying {@link CyclicalArchitecture} 
	 * @param 	numberOfNonHeadNodes The number of nonheadNodes that will be used to create the 
	 * 			underlying network
	 * @param	bitsPerFlit The number of bits in each flit of information
	 * @param	teardownTime The amount of time it takes to teardown connections
	 * @param	coordinateSwitchingMap A map which maps coordinates to a specific number for each
	 * 			node. Numbers should begin at 0 and end at map.length()-1
	 * @param	switchingMap used to switch nodes at coordinate positions to different positions. 
	 * 			Each coordinate should exist as a key in the coordinateSwitchingMap and each key and
	 *  		value specified should appear once and only once in both the key and value sections 
	 * 			of the map. In addition this map should be bijective.
	 * @param	taskInfo prints debug info about tasks if set to true
	 */
	public CyclicalSimOverseer(int bitsPerFlit, int teardownTime, 
			HashMap<Coordinate, Integer> coordinateSwitchingMap, 
			HashMap<Coordinate, Coordinate> switchingMap, int[] mrrSwitchesTopLeftNodeNumbers, 
			boolean taskInfo)
	{
		if(bitsPerFlit < 0 || teardownTime < 0)
		{
			throw new IllegalArgumentException("Arguments must be non-negative.");
		}
		simulation = new CyclicalMappedArchitecture(bitsPerFlit, teardownTime, 
				coordinateSwitchingMap, switchingMap, mrrSwitchesTopLeftNodeNumbers, taskInfo);
	}
	
	public CyclicalSimOverseer(CyclicalMappedArchitecture existing)
	{
		if(existing == null) 
		{
			throw new NullPointerException();
		}
		simulation = existing;
	}

	@Override
	public void delegateTaskToNode(LogEntry entry, int taskIndex)
	{
		((CyclicalArchitecture) simulation).simulateTask(entry, taskIndex);
	}

	@Override
	protected void simulateCycle()
	{
		((CyclicalArchitecture) simulation).simCycle();
	}

	@Override
	public boolean allTasksFinished()
	{
		return ((CyclicalArchitecture) simulation).allTasksFinished();
	}
}
