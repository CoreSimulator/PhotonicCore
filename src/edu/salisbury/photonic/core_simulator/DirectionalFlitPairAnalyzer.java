package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A subclass of {@link Analyzer} which can be used to discover pairs of cores that are sending/receiving
 * the most flits with the aim to discover the "dominant flows" among the cores.
 * 
 * <p>Similar to {@link FlitReceiverAnalyzer} and {@link FlitSenderAnalyzer} however the class enumerates 
 * communication core pairs rather than just single cores. The direction of the communication and order 
 * of the coordinates in the pair does matter. For instance, pair {(0, 0), (0, 1)} is not equivalent to 
 * {(0, 1), (0, 0)}.
 * </p>
 * 
 * <p>
 * The following pair {(0,0), (0, 1)} indicates that core (0,0) is the sender/initiator of a message 
 * while core (0, 1) is the receiver/recipient of a message.
 * </p>
 * @author timfoil
 *
 */
public class DirectionalFlitPairAnalyzer extends Analyzer 
{
	/**
	 * Constructs a DirectionalFlitPairAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public DirectionalFlitPairAnalyzer()
	{
		resultDescription = "Directional Core pair flit enumeration";
	}
	
	/**
	 * Constructor for DirectionalFlitPairAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public DirectionalFlitPairAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Directional core pair flit enumeration";
	}
	
	@Override
	public String analyze(CoreLog log) 
	{
		HashMap<CoordinatePair, Integer> sentMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate sourceCoord = new Coordinate(entry.sourceX(), entry.sourceY());
			Coordinate destCoord = new Coordinate(entry.destX(), entry.destY());
			
			CoordinatePair pair = new CoordinatePair(sourceCoord, destCoord);
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(pair))
			{
				int timesOccured = sentMessages.remove(pair);
				sentMessages.put(pair, entry.packetSize() + timesOccured);
			} 
			else 
			{
				sentMessages.put(pair, entry.packetSize());
			}
		}
		ArrayList<Map.Entry<CoordinatePair, Integer>> sortedList = SortingHelper.SortHashMapByValue(sentMessages);//analyze sentMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}

}
