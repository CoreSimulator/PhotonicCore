package edu.salisbury.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A subclass of {@link Analyzer} which can be used to evaluate the number
 * of times each core has sent data based on a log-file  represented by a {@link CoreLog} object.
 * @author tptravitz
 *
 */
public class ReceiverAnalyzer extends Analyzer 
{
	/**
	 * Constructs a ReceiverAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public ReceiverAnalyzer()
	{
		resultDescription = "Cores that received the most messages";
	}
	
	/**
	 *  Constructor for ReceiverAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public ReceiverAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Cores that received the most messages";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<Coordinate,Integer> sentMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate destCoord = new Coordinate(entry.destX(), entry.destY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(destCoord))
			{
				int timesOccured = sentMessages.remove(destCoord);
				sentMessages.put(destCoord, ++timesOccured);
			} 
			else 
			{
				sentMessages.put(destCoord, 1);
			}
		}
		
		ArrayList<Map.Entry<Coordinate, Integer>> sortedList = SortingHelper.SortHashMapByValue(sentMessages);//analyze sentMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
