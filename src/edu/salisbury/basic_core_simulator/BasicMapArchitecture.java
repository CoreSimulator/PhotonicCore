package edu.salisbury.basic_core_simulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import edu.salisbury.core_simulator.BasicArchitecture;
import edu.salisbury.core_simulator.Coordinate;

public class BasicMapArchitecture extends BasicArchitecture
{
	private HashMap<Coordinate, Integer> coordsToNumberMapping;
	private HashMap<Integer, Coordinate> numberToCoordsMapping;
	
	private HashMap<Integer, Integer> switchingMap;
	
	public BasicMapArchitecture(int bitsPerFlit,
			int teardownTime, HashMap<Coordinate, Integer> coordsToNumberMapping, 
			HashMap<Coordinate, Coordinate> switchingMap)
	{
		super(coordsToNumberMapping.size(), bitsPerFlit, teardownTime);
		this.coordsToNumberMapping = coordsToNumberMapping;
		init(switchingMap);
	}
	
	public BasicMapArchitecture(int bitsPerFlit,
			int teardownTime, HashMap<Coordinate, Integer> coordsToNumberMapping)
	{
		super(coordsToNumberMapping.size(), bitsPerFlit, teardownTime);
		this.coordsToNumberMapping = coordsToNumberMapping;
		init(null);
	}
	
	/*initalize the basicMapArchitecture with the given map*/
	private void init(HashMap<Coordinate, Coordinate> coordSwitchingMap)
	{
		if(coordsToNumberMapping.isEmpty())
		{
			throw new IllegalArgumentException("coordToNumberMapping must have at least one " +
					"mapping");
		}
		// Check to see that the numberMapping to coords is legitimate
		for(int i = 0; i < coordsToNumberMapping.size(); i++)
		{
			
			if(!coordsToNumberMapping.containsValue(i)){
				throw new IllegalArgumentException("Must map coordinate values from 0 to " +
						"coordsToNumberMap.size()-1");
			}
		}
		
		Iterator<Entry<Coordinate, Integer>> mapIterator = 
				coordsToNumberMapping.entrySet().iterator();
		
		//create hashMap for translating from integer to Coordinate value
		numberToCoordsMapping = new HashMap<Integer, Coordinate>();
		
		//Fill in reversing hashmap
		while(mapIterator.hasNext())
		{
			Entry<Coordinate, Integer> mapEntry = mapIterator.next();
			numberToCoordsMapping.put(mapEntry.getValue(),mapEntry.getKey());
		}	
		
		//also generate the number to coordsMapping
		if(coordSwitchingMap != null)
		{
			switchCoordsSwapMapToIntegerSwap(coordSwitchingMap);
		}
		
		basicNodeList[0] = new BasicNode(headNode, 0);
		
		for(int i = 1; i < basicNodeList.length; i++)
		{
			basicNodeList[i] = new BasicNode(headNode, i);
			
			basicNodeList[i].setCounterClockwiseEdge(basicNodeList[i-1]);
			basicNodeList[i-1].setClockwiseEdge(basicNodeList[i]);
		}
		basicNodeList[basicNodeList.length - 1].setClockwiseEdge(basicNodeList[0]);
		basicNodeList[0].setCounterClockwiseEdge(basicNodeList[basicNodeList.length - 1]);
		headNode.setEdges(basicNodeList);
	}
	
	/* Changes a coordinate to coordinate swappingMap to a Integer to Integer map */
	private void switchCoordsSwapMapToIntegerSwap(
			HashMap<Coordinate, Coordinate> coordSwitchingMap)
	{
		switchingMap = new HashMap<Integer, Integer>();
		
		Iterator<Entry<Coordinate, Coordinate>> mapIterator = 
				coordSwitchingMap.entrySet().iterator();
		
		while(mapIterator.hasNext())
		{
			Entry<Coordinate, Coordinate> mapEntry = mapIterator.next();
			
			//see if the map being built contains more than one of each key or value
			if(switchingMap.containsKey(mapEntry.getKey()) ||
					switchingMap.containsValue(mapEntry.getValue()))
			{
				throw new IllegalArgumentException("Invalid switching map, can only contain one " +
						"of each key and one of each value");
			}
			//check to see if mapIterator is valid by seeing if every Coordinate key exists as 
			//a value and vice versa

			if(!coordSwitchingMap.containsKey(mapEntry.getValue()) ||
			!coordSwitchingMap.containsValue(mapEntry.getKey()))
			{
				throw new IllegalArgumentException("Every coordinate key must exist as a value " +
						"and vice versa.");
			}
			
			//make sure these are valid coordinates
			if(!coordsToNumberMapping.containsKey(mapEntry.getKey()) ||
					!coordsToNumberMapping.containsKey(mapEntry.getValue()))
			{
				switchingMap.put(
						unSwitchedCoordsToUnswitchedNumber(mapEntry.getKey()), 
						unSwitchedCoordsToUnswitchedNumber(mapEntry.getValue()));
			} else throw new IllegalArgumentException("Both coordinates to switch must be one of" +
					" the ones provided in the coordsToNumberMapping");
		}
	}

	/*For use of converting unswitched Coords to unswitched numbers*/
	private int unSwitchedCoordsToUnswitchedNumber(Coordinate coords)
	{
		if(!coordsToNumberMapping.containsKey(coords))
		{
			throw new RuntimeException("The given coordinate could not be converted to an integer" +
					"using the mapping provided");
		}
		return coordsToNumberMapping.get(coords);
	}
	
	/*Takes an non-switched number and converts it to a switched one*/
	private int unswitchedNumberToSwitchedNumber(int toConvert)
	{
		if(switchingMap == null || switchingMap.isEmpty() || !switchingMap.containsKey(toConvert))
		{
			//No switching occurs if the switchingMap doesn't exist
			//or if no entry for the number to convertExists
			return toConvert;
		}
		
		return switchingMap.get(toConvert);
	}
	
	/**
	 * Turns an unswitchedCoordinate into a switchedNumber
	 * @param unswitched coordinate to convert
	 * @return a switched number
	 */
	@Override
	public int coordinatesToNumber(Coordinate coord)
	{	
		return unswitchedNumberToSwitchedNumber(unSwitchedCoordsToUnswitchedNumber(coord));
	}
	
	/*Takes an unswitched coordinate to a switched node*/
	@Override
	public BasicNode coordinatesToNode(Coordinate coord)
	{
		return basicNodeList[coordinatesToNumber(coord)]; 
	}
	
	/**
	 * Converts a switched number back to switched coordinates, 
	 * (useful for printing out final results).
	 * 
	 * @param number to convert to coordinates
	 * @return Coordinates of the node represented by the number
	 */
	@Override
	public Coordinate numberToCoordinate(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return numberToCoordsMapping.get(nodeNumber);
	}
	
	/**
	 * Converts a switched number to a switchedNode
	 * 
	 * @param nodeNumber a switched number for a corresponding Node
	 * @return The node corresponding to the switched nodeNumber
	 */
	@Override
	public BasicNode numberToNode(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return basicNodeList[nodeNumber];
	}

	/* Ensure the coordinates given are valid */
	@Override
	protected void checkForValidCoordinates(Coordinate coord)
	{
		if(!coordsToNumberMapping.containsKey(coord))
		{
			throw new IllegalArgumentException("Invalid coordinates.");
		}
		
	}
	
	/* Ensure the coordinates given are valid */
	@Override
	protected void checkForValidNodeNumber(int nodeNumber)
	{
		if(nodeNumber >= coordsToNumberMapping.size()|| nodeNumber < 0)
		{
			throw new IllegalArgumentException("Invalid nodeNumber.");
		}
	}
}
