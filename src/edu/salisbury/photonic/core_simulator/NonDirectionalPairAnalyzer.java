package edu.salisbury.photonic.core_simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A subclass of {@link Analyzer} which can be used to discover pairs of cores that are sending/receiving
 * the most messages with the aim to discover the "dominant flows" among the cores.
 * 
 * <p>Similar to {@link ReceiverAnalyzer} and {@link SenderAnalyzer} but it enumerates communication core 
 * pairs rather than just single cores. The direction of the communication and order of the 
 * {@link Coordinate}s in the pair does not matter. For instance, pair {(0, 0), (0, 1)} is equivalent to 
 * {(0, 1), (0, 0)} however only one of these two pairs of coordinates will be enumerated in the results.
 * </p>
 * 
 * <p>
 * The following pair {(0,0), (0, 1)} indicates that core (0,0) or core (0,1) have messaged the other at 
 * some point.
 * </p>
 * @author timfoil
 *
 */
public class NonDirectionalPairAnalyzer extends Analyzer 
{
	/**
	 * Constructs a NonDirectionalPairAnalyzer. With resultEntriesPerRow equal to 2.
	 */
	public NonDirectionalPairAnalyzer()
	{
		resultDescription = "Non-directional core pair message enumeration";
	}
	
	/**
	 * Constructor for NonDirectionalPairAnalyzer
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public NonDirectionalPairAnalyzer(int resultEntriesPerRow)
	{
		this.resultEntriesPerRow = resultEntriesPerRow;
		resultDescription = "Non-directional core pair message enumeration";
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
			CoordinatePair pair = coordinatePairEquivalence(sourceCoord, destCoord);
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(pair))
			{
				int timesOccured = sentMessages.remove(pair);
				sentMessages.put(pair, ++timesOccured);
			} 
			else 
			{
				sentMessages.put(pair, 1);
			}
		}
		List<Map.Entry<CoordinatePair, Integer>> sortedList = 
				SortingHelper.SortHashMapByValue(sentMessages);//analyze sentMessages
		return sortMapEntriesByDescendingValue(sortedList);
	}
	
	public HashMap<CoordinatePair, Integer> analyzeNoString(CoreLog log) 
	{
		HashMap<CoordinatePair, Integer> sentMessages = new HashMap<>();
		
		for(int i = 0; i < log.logSize(); i++)
		{
			LogEntry entry = log.getEntry(i);
			
			Coordinate sourceCoord = new Coordinate(entry.sourceX(), entry.sourceY());
			Coordinate destCoord = new Coordinate(entry.destX(), entry.destY());
			CoordinatePair pair = coordinatePairEquivalence(sourceCoord, destCoord);
			
			//If the key already exists update the value, timesOccured
			//otherwise put a new entry in for the key
			if(sentMessages.containsKey(pair))
			{
				int timesOccured = sentMessages.remove(pair);
				sentMessages.put(pair, ++timesOccured);
			} 
			else 
			{
				sentMessages.put(pair, 1);
			}
		}
		return sentMessages;
	}
	
	/*
	 * In non-directional/bi-directional pairs, the order in which the coordinates are ordered do not/should not matter. 
	 * However, we are using a HashMap with a coordinate pair key to arrange our results. Since the equivalence method 
	 * equals() determines equivalence with order in mind we need to use the method as a standard way to order 
	 * coordinates in coordinate pairs for our HashMap keys.
	 */
	private CoordinatePair coordinatePairEquivalence(Coordinate coord1, Coordinate coord2)
	{
		int x1 = coord1.getX();
		int y1 = coord1.getY();
		
		int x2 = coord2.getX();
		int y2 = coord2.getY();
		
		if(x1 < x2)
		{
			return new CoordinatePair(coord1, coord2);
		} 
		else if(x1 > x2)
		{
			return new CoordinatePair(coord2, coord1);
		}
		else if(y1 < y2)
		{
			return new CoordinatePair(coord1, coord2);
		}
		else if(y1 > y2)
		{
			return new CoordinatePair(coord2, coord1);
		} 
		else
		{
			//equivalence case, shouldn't happen but we'll humor it here 
			return new CoordinatePair(coord1, coord2);
		}
	}
}
