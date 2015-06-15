package edu.salisbury.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A subclass of {@link Analyzer} which can be used to evaluate the number
 * of flits each core has sent or received based on a log-file 
 * represented by a {@link CoreLog} object.
 * @author timfoil
 *
 */
public class CumulativeIOFlitAnalyzer extends Analyzer 
{
	/**
	 * Constructs a CumulativeIOFlitAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public CumulativeIOFlitAnalyzer()
	{
		resultDescription = "Cores that are sending/receiving the most flits";
	}
	
	/**
	 * Constructor for CumulativeIOFlitAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public CumulativeIOFlitAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Cores that are sending/receiving the most flits";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<Coordinate,Integer> messages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate destCoord = new Coordinate(entry.destX(), entry.destY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(messages.containsKey(destCoord))
			{
				int timesOccured = messages.remove(destCoord);
				messages.put(destCoord, timesOccured + entry.packetSize());
			} 
			else 
			{
				messages.put(destCoord, entry.packetSize());
			}
			
			Coordinate sourceCoord = new Coordinate(entry.sourceX(), entry.sourceY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(messages.containsKey(sourceCoord))
			{
				int timesOccured = messages.remove(sourceCoord);
				messages.put(sourceCoord, timesOccured + entry.packetSize());
			} 
			else 
			{
				messages.put(sourceCoord, entry.packetSize());
			}
			
		}
		
		ArrayList<Map.Entry<Coordinate, Integer>> sortedList = SortingHelper.SortHashMapByValue(messages);//analyze messages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
