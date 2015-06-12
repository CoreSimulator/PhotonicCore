package edu.salisbury.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.salisbury.core_basic_layout.Coordinate;
import edu.salisbury.core_basic_layout.SortingHelper;

/**
 * A subclass of {@link Analyzer} which can be used to evaluate the number
 * of times each core has sent data based on logfile data represented by a {@link CoreLog} object.
 * @author tptravitz
 *
 */
public class SenderAnalyzer extends Analyzer 
{
	/**
	 * Constructs a SenderAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public SenderAnalyzer()
	{
		resultDescription = "Cores that sent the most messages";
	}
	
	/**
	 *  Constructor for SenderAnalyzer
	 * @param resultEntriesPerRow for the result string of the experiment
	 */
	public SenderAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Cores that sent the most messages";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<Coordinate,Integer> sentMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate coreCoord = new Coordinate(entry.sourceX(), entry.sourceY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(coreCoord))
			{
				int timesOccured = sentMessages.remove(coreCoord);
				sentMessages.put(coreCoord, ++timesOccured);
			} 
			else 
			{
				sentMessages.put(coreCoord, 1);
			}
		}
		
		ArrayList<Map.Entry<Coordinate, Integer>> sortedList = SortingHelper.SortHashMapByValue(sentMessages);//TODO analyze sentMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
