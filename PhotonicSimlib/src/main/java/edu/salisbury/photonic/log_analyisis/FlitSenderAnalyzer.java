package edu.salisbury.photonic.log_analyisis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogEntry;
import edu.salisbury.photonic.core_simulator.SortingHelper;


/**
 * A subclass of {@link Analyzer} which can be used to evaluate the number
 * of flits each core has sent based on a log-file represented by a {@link CoreLog} object.
 * @author timfoil
 *
 */
public class FlitSenderAnalyzer extends Analyzer 
{
	/**
	 * Constructs a FlitSenderAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public FlitSenderAnalyzer()
	{
		resultDescription = "Cores that sent the most flits";
	}
	
	/**
	 *  Constructor for FlitSenderAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public FlitSenderAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Cores that sent the most flits";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<Coordinate,Integer> sentMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate sourceCoord = new Coordinate(entry.sourceX(), entry.sourceY());
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(sourceCoord))
			{
				int timesOccured = sentMessages.remove(sourceCoord);
				sentMessages.put(sourceCoord, entry.packetSize() + timesOccured);
			} 
			else 
			{
				sentMessages.put(sourceCoord, entry.packetSize());
			}
		}
		
		List<Map.Entry<Coordinate, Integer>> sortedList = 
				SortingHelper.SortHashMapByValue(sentMessages);//analyze sentMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
