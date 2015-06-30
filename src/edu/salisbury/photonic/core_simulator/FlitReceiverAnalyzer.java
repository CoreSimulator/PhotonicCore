package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A subclass of {@link Analyzer} which can be used to evaluate the number
 * of flits each core has received based on a log-file represented by a {@link CoreLog} object.
 * @author timfoil
 *
 */
public class FlitReceiverAnalyzer extends Analyzer 
{
	/**
	 * Constructs a FlitReceiverAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public FlitReceiverAnalyzer()
	{
		resultDescription = "Cores that received the most flits";
	}
	
	/**
	 *  Constructor for FlitReceiverAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public FlitReceiverAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Cores that received the most flits";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<Coordinate,Integer> receivedMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate destCoord = new Coordinate(entry.destX(), entry.destY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(receivedMessages.containsKey(destCoord))
			{
				int timesOccured = receivedMessages.remove(destCoord);
				receivedMessages.put(destCoord, timesOccured + entry.packetSize());
			} 
			else 
			{
				receivedMessages.put(destCoord, entry.packetSize());
			}
		}
		
		ArrayList<Map.Entry<Coordinate, Integer>> sortedList = SortingHelper.SortHashMapByValue(receivedMessages);//analyze receivedMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
