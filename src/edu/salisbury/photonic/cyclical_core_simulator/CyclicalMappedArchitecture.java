package edu.salisbury.photonic.cyclical_core_simulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import edu.salisbury.photonic.core_simulator.Coordinate;

/**
 * Cyclical Map architecture is a cyclical architecture which allows you to individually specify 
 * what nodes and nodeNumbers correspond to which coordinates. 
 * 
 * <p>
 * The Cyclical map is implemented using a Coordinate to Integer map. Nodes form links with nodes
 * with NodeNumber values 1 larger (for the clockwise direction) and 1 less (for the 
 * counterclockwise direction). Nodes with NodeNumbers at 0 and map.size() - 1 will connect with 
 * each other forming a cyclical structure. The nodeNumbers specified must contain 0 to the 
 * numberToCoordMapping.size() such that no nodeNumbers which the cyclical architecture is being 
 * mapped to can be skipped or repeated.
 * </p>
 * 
 * @author timfoil
 *
 */
public class CyclicalMappedArchitecture extends CyclicalArchitecture
{
	private HashMap<Coordinate, Integer> coordsToNumberMapping;
	private HashMap<Integer, Coordinate> numberToCoordsMapping;
	
	private HashMap<Integer, Integer> switchingMap;
	
	/**
	 * Constructor for CyclicalMap architecture. 
	 * 
	 * <p> In addition to a coordsToNumberMapping, the 
	 * constructor also takes a switching map. This map will keep track of which nodes are 
	 * effectively swapped in the architecture, this map must be bijective. Each coordinate specified 
	 * must exist both as a key and a value once and only once, coordinates not specified are 
	 * automatically assumed to map to themselves.
	 * </p>
	 * 
	 * @param 	bitsPerFlit number of bits that exist in a single flit
	 * @param 	teardownTime amount of time it takes to destroy a connection between communicating 
	 * 			nodes
	 * @param 	coordsToNumberMapping The map that matches coordinates to designated node-Numbers
	 * @param 	switchingMap The map which keeps track of which nodes are 
	 * 			effectively swapped in the architecture
	 * @param 	cyclicalMRRSwitchList The list of MRR switches
	 */
	public CyclicalMappedArchitecture(int bitsPerFlit,
			int teardownTime, HashMap<Coordinate, Integer> coordsToNumberMapping, 
			HashMap<Coordinate, Coordinate> switchingMap, int[] mrrSwitchesTopLeftNodeNumbers)
	{
		super(coordsToNumberMapping.size(), bitsPerFlit, teardownTime, mrrSwitchesTopLeftNodeNumbers);
		this.coordsToNumberMapping = coordsToNumberMapping;
		init(switchingMap);
	}
	/**
	 * Constructor for CyclicalMap architecture. 
	 * 
	 * @param 	bitsPerFlit number of bits that exist in a single flit
	 * @param 	teardownTime amount of time it takes to destroy a connection between communicating 
	 * 			nodes
	 * @param 	coordsToNumberMapping The map that matches coordinates to designated node-Numbers
	 */
	public CyclicalMappedArchitecture(int bitsPerFlit,
			int teardownTime, HashMap<Coordinate, Integer> coordsToNumberMapping, int[] mrrSwitchesTopLeftNodeNumbers)
	{
		super(coordsToNumberMapping.size(), bitsPerFlit, teardownTime, mrrSwitchesTopLeftNodeNumbers);
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
		
		if (mrrSwitchesTopLeftNodeNumbers[0] != -1) {
			setUpMRRSwitchLinks();
		}
		cyclicalNodeList[0] = new CyclicalNode(headNode, 0);
		
		for(int i = 1; i < cyclicalNodeList.length; i++)
		{
			cyclicalNodeList[i] = new CyclicalNode(headNode, i);
			
			cyclicalNodeList[i].setCounterClockwiseEdge(cyclicalNodeList[i-1]);
			cyclicalNodeList[i-1].setClockwiseEdge(cyclicalNodeList[i]);
		}
		cyclicalNodeList[cyclicalNodeList.length - 1].setClockwiseEdge(cyclicalNodeList[0]);
		cyclicalNodeList[0].setCounterClockwiseEdge(cyclicalNodeList[cyclicalNodeList.length - 1]);
		headNode.setEdges(cyclicalNodeList);
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
			if(coordsToNumberMapping.containsKey(mapEntry.getKey()) &&
					coordsToNumberMapping.containsKey(mapEntry.getValue()))
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
	public CyclicalNode coordinatesToNode(Coordinate coord)
	{
		return cyclicalNodeList[coordinatesToNumber(coord)]; 
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
	public CyclicalNode numberToNode(int nodeNumber)
	{
		checkForValidNodeNumber(nodeNumber);
		return cyclicalNodeList[nodeNumber];
	}
	
	/**
	 * Converts an unswiched number to the node corresponding to the switched number.
	 * 
	 * @param nodeNumber an unSwitched number for a Node
	 * @return The node corresponding to the switched nodeNumber
	 */
	public CyclicalNode unswitchedNumberToNode(int nodeNumber)
	{
		return numberToNode(unswitchedNumberToSwitchedNumber(nodeNumber));
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
	
	/**
	 * Applies the top left node number to a designated switch
	 * @param topLeftNodeNumber node number for the top left node
	 * @return the designated MRR Switch
	 */
	@Override
	public void setUpMRRSwitchLinks() {
		for (int i = 0; i < mrrSwitchesTopLeftNodeNumbers.length; i ++){
			int topLeftNodeNumber = unswitchedNumberToSwitchedNumber(mrrSwitchesTopLeftNodeNumbers[i]);
			
			checkForValidTopLeftNodeNumber(topLeftNodeNumber);
			
			CyclicalNode topLeftNode = numberToNode(topLeftNodeNumber);
			CyclicalNode topRightNode = numberToNode(topLeftNodeNumber+1);
			CyclicalNode bottomRightNode = numberToNode((coordsToNumberMapping.size()/2 - topLeftNodeNumber)*2);
			CyclicalNode bottomLeftNode = numberToNode((coordsToNumberMapping.size()/2 - topLeftNodeNumber)*2 + 1);
			
			cyclicalMRRSwitchList[i] = new CyclicalMRRSwitch(topLeftNode, topRightNode, bottomRightNode, bottomLeftNode, i);
		}
		
	}
	
	/* Ensure the number given is valid for a top left node */
	@Override
	public void checkForValidTopLeftNodeNumber(int topLeftNodeNumber) {
		if (topLeftNodeNumber >= coordsToNumberMapping.size()/2 - 1) {
			throw new IllegalArgumentException("Invalid topLeftNodeNumber.");
		}
	}
}
