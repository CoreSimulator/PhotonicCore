package edu.salisbury.photonic.genetic_algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.salisbury.photonic.cyclical_core_simulator.CyclicalMappedArchitecture;
import edu.salisbury.photonic.cyclical_core_simulator.CyclicalSimOverseer;
import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.SortingHelper.SortableValue;


/**
 * A specific mapping of nodes which is modeled as an individual which could be part of a larger 
 * population.
 * @author timfoil
 *
 */
public class NodeConfiguration extends GeneticIndividual implements SortableValue
{
	private CyclicalMappedArchitecture configuration;
	private CoreLog log;
	private Integer fitness;
	
	/**
	 * Creates a nodeconfiguration based on a previous nodeconfiguration
	 * @param 	architecture An already constructed CyclicalMappedArchitecture architecture that 
	 * 			already has a specific switching map (can be null).
	 * @param log
	 */
	public NodeConfiguration(CyclicalMappedArchitecture architecture, CoreLog log)
	{
		if(architecture == null || log == null)
		{
			throw new NullPointerException("Arguments to this constructor should not be null");
		}
		
		configuration = architecture;
		this.log = log;
	}
	/**
	 * Create a node configuration with a specific mapping given 2 parent node configurations
	 * 
	 * @param mostSuccessful most successful parents individual
	 * @param bitsPerFlit number of bits that are in a flit (16, 32, 64)
	 * @param tearDownTime Time it takes to tear down a connection
	 * @param coordsToNumberMapping The mapping used for the coordinate to integer values.
	 * @param log The log used to test this NodeConfiguration
	 */
	public NodeConfiguration(List <NodeConfiguration> mostSuccessful, int bitsPerFlit, 
			int tearDownTime, HashMap <Coordinate, Integer> coordsToNumberMapping, CoreLog log)
	{
		if(log == null)
		{
			throw new NullPointerException("Log should not be null");
		}
		this.log = log;
		
		configuration = new CyclicalMappedArchitecture( 
				bitsPerFlit, tearDownTime, coordsToNumberMapping); 
		
		GenePool pool = new GenePool(mostSuccessful.get(0).getConfiguration().getSwitchingMapCopy(), 
				mostSuccessful.get(0).getConfiguration().getNumberOfCoreNodes());
		
		for(int i = 1; i < mostSuccessful.size(); i++)
		{
			pool.addMapToPool(((NodeConfiguration) mostSuccessful.get(i))
					.getConfiguration().getSwitchingMapCopy());
		}
		
		configuration.checkAndSetIntegerSwapMap(pool.generateSwitchingMap());
	}
	
	
	//TODO One generation of a population could share 1 genepool instead of creating the same new 
	//genePool every time a nodeConfiguration individual is created
	
	/**
	 * A class used to breakdown given Switching maps into a pool of genes.
	 * After creating a pool unique NodeConfigurations can be constructed from the pool.
	 * @author timfoil
	 */
	private static class GenePool
	{
		private HashSet<Coordinate> pool = new HashSet<Coordinate>();
		private List<LinkedList<Coordinate>> organizedPool;
		private int numberOfPoolBuckets;
		
		/**
		 * Initialize with an array of switching maps and the maximum size the switchingMap can
		 * take on for this pool.
		 * 
		 * @param switchingMaps Array of switchingMaps to add to the genepool
		 * @param size of the maximum switching map for this pool
		 */
		@SuppressWarnings("unused")
		public GenePool(List<HashMap<Integer, Integer>> switchingMaps, int size)
		{
			if(switchingMaps.size() <= 0)
			{
				throw new IllegalArgumentException("Cannot have an empty array");
			}
			else if(size < 0)
			{
				throw new IllegalArgumentException("size cannot be a negative number");
			}
			
			numberOfPoolBuckets = size;
			organizedPool = new ArrayList<LinkedList<Coordinate>>(numberOfPoolBuckets);
			
			for(int i = 0; i < numberOfPoolBuckets; i++)
			{
				organizedPool.add(i, null);
			}
			
			for(int i = 0; i < switchingMaps.size(); i++)
			{
				addMapToPool(switchingMaps.get(i));
			}
		}
		
		/**
		 * Creates a genepool from a single switchingMap with the given size number of nodes.
		 * @param 	switchingMap An architecture which is broken down into genes to construct this
		 * 			genepool.
		 * @param 	size Number of (non-head) nodes in the architecture
		 */
		public GenePool(HashMap<Integer, Integer> switchingMap, int size)
		{
			if(size < 0)
			{
				throw new IllegalArgumentException("size cannot be a negative number");
			}
			numberOfPoolBuckets = size;
			organizedPool = new ArrayList<LinkedList<Coordinate>>(numberOfPoolBuckets);
			for(int i = 0; i < numberOfPoolBuckets; i++)
			{
				organizedPool.add(i, null);
			}
			addMapToPool(switchingMap);
		}

		/**
		 * Break down single switchingMap into genes and add it to the genes to the pool if they do
		 * not already exist.
		 * 
		 * @param switchingMaps switchingMap
		 */
		public void addMapToPool(HashMap<Integer, Integer> switchingMap)
		{
			if(switchingMap == null)
			{
				switchingMap = 
						new HashMap<Integer, Integer>(0); //Construct an empty dummy HashMap
			}
			
			if(switchingMap.size() > numberOfPoolBuckets)
			{
				throw new IllegalArgumentException("SwitchingMap is too big for given" +
						"genepool size.");
			}
			
			//take a look at this method
			for(int i = 0; i < numberOfPoolBuckets - 1; i++)
			{
				Integer lowerNodePosition = switchingMap.get(i);
				lowerNodePosition = (lowerNodePosition == null) ? i : lowerNodePosition;
				Integer higherNodePosition = switchingMap.get(i + 1);
				higherNodePosition = (higherNodePosition == null) ? i + 1 : higherNodePosition;
				
				Coordinate toAdd = new Coordinate(lowerNodePosition,higherNodePosition);
				Coordinate reverse = new Coordinate(higherNodePosition, lowerNodePosition);
				if(pool.add(toAdd))
				{
					addToOrganizedPool(toAdd);
				}
				if(pool.add(reverse))
				{
					addToOrganizedPool(reverse);
				}
			}
			
			Integer lowerNode = switchingMap.get(numberOfPoolBuckets - 1);
			lowerNode = (lowerNode == null) ? numberOfPoolBuckets - 1 : lowerNode;
			Integer higherNode = switchingMap.get(0);
			higherNode = (higherNode == null) ? 0 : higherNode;
		}
		
		private void addToOrganizedPool(Coordinate toAdd)
		{
			int x = toAdd.getX();
			int y = toAdd.getY();
			
			if(x < 0 || x >= numberOfPoolBuckets || y < 0 || y >= numberOfPoolBuckets)
			{
				throw new IllegalArgumentException("Coordinate values must be within range");
			}
			if(organizedPool.get(x) == null)
			{
				organizedPool.set(x, new LinkedList<Coordinate>());
			}
			organizedPool.get(x).add(toAdd);
		}
		
		/**
		 * Generates a switchingMap from all the genes that exist in this pool.
		 * @return switchingMap obtained from the genes that exist in this pool
		 */
		public HashMap<Integer, Integer> generateSwitchingMap()
		{
			ArrayList<LinkedList<Coordinate>> clonedList = 
					new ArrayList<LinkedList<Coordinate>>(numberOfPoolBuckets);
			
			
			//first generate a deep copy of shallow copies, this becomes clonedList
			//if anything is removed from cloned list the original copy will not be affected
			for(int i = 0; i < organizedPool.size(); i++)
			{
				clonedList.add(i, new LinkedList<Coordinate>(organizedPool.get(i)));
			}
			
			HashMapHelper mapMaker = new HashMapHelper(numberOfPoolBuckets);
			
			int firstPick = mapMaker.pickRandFromWhatsLeft();
			
			Coordinate firstCoord;
			
			do 
			{
				firstCoord = pop(firstPick, clonedList);
	
				if(firstCoord == null)
				{
					firstCoord = new Coordinate(firstPick, mapMaker.pickRandFromWhatsLeft());
				}
				
			} 
			while(!mapMaker.stillExists(firstCoord.getY()));
			
			mapMaker.put(firstPick, 0, firstCoord);
			
			int prevY = firstCoord.getY();
			
			for(int i = 1; i < numberOfPoolBuckets - 1; i++)
			{
				Coordinate pickedCoord;
				
				do 
				{
					pickedCoord = pop(prevY, clonedList);
		
					if(pickedCoord == null)
					{
						pickedCoord = new Coordinate(prevY, mapMaker.pickRandFromWhatsLeft());
					}
				} 
				while(!mapMaker.stillExists(pickedCoord.getY()));
				mapMaker.put(prevY, 1, pickedCoord);
				prevY = pickedCoord.getY();
			}
			
			mapMaker.put(prevY, 1, new Coordinate(prevY, firstPick));
			
			return mapMaker.generateHashMap();
		}
		
		private Coordinate pop(int index, ArrayList<LinkedList<Coordinate>> toPop)
		{
			if(index >= toPop.size() || index < 0)
			{
				throw new IndexOutOfBoundsException();
			}
			
			if(toPop.get(index) == null || toPop.get(index).isEmpty())
			{
				return null;
			}
			return toPop.get(index).remove(new Random().nextInt(toPop.get(index).size()));
		}




		/**
		 * Helper class for generating switching HashMaps. Used to determine what links have been 
		 * added already and which nodes still need links added to them. Used exclusively in the
		 * generateSwitchingMap() method of GenePool.
		 * 
		 * @author timfoil
		 *
		 */
		private static class HashMapHelper
		{
			private Coordinate[][] hashMapLinks;
			private HashSet<Integer> notPicked;
			
			public HashMapHelper(int size)
			{
				hashMapLinks = new Coordinate[size][2];
				if(size <=1)
				{
					throw new IllegalArgumentException("Needs to be at least 2 in size");
				}
				
				notPicked = new HashSet<Integer>((int) (size / 0.75 + 1));
				
				for(int i = 0; i < size; i++)
				{
					notPicked.add(i);
				}
			}
			
			/**
			 * Determines if the given number has been picked already or not yet 
			 * @param toExamine
			 * @return true if the number has already been picked false otherwise
			 */
			public boolean stillExists(int toExamine)
			{
				return notPicked.contains(toExamine);
			}

			/**
			 * Picks a random integer from the pool that has not been picked yet
			 * @return A random integer that has yet to be picked from the pool
			 */
			public int pickRandFromWhatsLeft()
			{
				if(notPicked.isEmpty())
				{
					throw new RuntimeException("All numbers have already been picked");
				}
				int rand = new Random().nextInt(notPicked.size());
				int i = 0;
				for(Integer pickedInt : notPicked)
				{
					if(i == rand)
					{
						return pickedInt;
					}
					i++;
				}
				throw new RuntimeException("Error: A random number was not picked");
			}
			
			/**
			 * Puts a link into place at the given index. Poolindex should equal the linkToPlace's X
			 * coordinate and linkNumberIndex should equal either 0 or 1 depending on if this is the
			 * first or second link to be added to the given position. Also adds the reverse 
			 * coordinate automatically at the poolindex given by linkToPlace's Y coordinate.
			 * 
			 * @param 	poolIndex should equal the linkToPlace's x coordinate
			 * @param 	linkNumberIndex Either 0 or 1 depending on if this is the first or second link
			 * 			to be added to this position
			 * @param 	linkToPlace The link that will be added at this position
			 */
			public void put(int poolIndex, int linkNumberIndex, Coordinate linkToPlace)
			{
				if(linkToPlace == null)
				{
					throw new NullPointerException("Given coordinate cannot be null");
				}
				
				if(poolIndex >= hashMapLinks.length ||
						poolIndex < 0 ||
						linkNumberIndex >= hashMapLinks[poolIndex].length ||
						linkNumberIndex < 0)
				{
					throw new IndexOutOfBoundsException();
				}
				else if(linkToPlace.getX() != poolIndex)
				{
					throw new IllegalArgumentException("The x value of the coordinate should be " +
							"the same as the poolindex");
				} 
				{
					if(hashMapLinks[poolIndex][linkNumberIndex] == null)
					{
						notPicked.remove(poolIndex);
						hashMapLinks[poolIndex][linkNumberIndex] = linkToPlace;
						addReverse(linkToPlace);
					}
					else
					{
						throw new RuntimeException("There already exists an element in this " +
								"hashmap helper where you are trying to place it");
					}
				}
			}
			
			private void addReverse(Coordinate unReversedCoord)
			{
				Coordinate reversedCoord = 
						new Coordinate(unReversedCoord.getY(), unReversedCoord.getX());
				
				int poolIndex = reversedCoord.getX();
				
				if(notPicked.remove(poolIndex))
				{
					if(hashMapLinks[poolIndex][0] == null)
					{
						hashMapLinks[poolIndex][0] = reversedCoord;
					} 
					else
					{
						throw new RuntimeException("Index should not be null");
					}
				} 
				else
				{
					if(hashMapLinks[poolIndex][1] == null)
					{
						hashMapLinks[poolIndex][1] = reversedCoord;
					}
					else
					{
						throw new RuntimeException("Index should not be null");
					}
				}
			}

			/**
			 * Once two link genes are placed at every index a switchingMap can be formed.
			 * Call this method when all the genes have been put in place to return the hashMap
			 * formed by the genes.
			 * @return The hashMap formed by the genes that have been picked
			 */
			public HashMap<Integer, Integer> generateHashMap()
			{
				if(!notPicked.isEmpty())
				{
					throw new IllegalArgumentException("HashMapHelper has not yet been filled, " +
							"cannot generate HashMap until it has been filled.");
				}
				HashMap<Integer, Integer> generatedHash = 
						new HashMap<Integer, Integer>((int) (hashMapLinks.length / 0.75) + 1);
				
				int nextY = hashMapLinks[0][0].getY();
				int prevY = 0;
				generatedHash.put(0, 0);
				
				for(int i = 1; i < hashMapLinks.length; i++)
				{
					generatedHash.put(i, nextY);
					
					int tmpY = nextY;
					
					if( prevY != hashMapLinks[nextY][0].getY())
					{
						nextY = hashMapLinks[nextY][0].getY();
					}
					else
					{
						nextY = hashMapLinks[nextY][1].getY();
					}
					prevY = tmpY;
				}
				return generatedHash;
			}
		}
	}
	
	
	@Override
	public int evaluateFitness()
	{
		CyclicalSimOverseer simulation = new CyclicalSimOverseer(configuration);
		fitness = simulation.simulateWithLog(log);
		return fitness;
	}
	/**
	 * Returns the fitness score for this object. 
	 * @return null if fitness has not yet been set/evaluated
	 */
	public Integer getFitness()
	{
		return fitness;
	}
	

	/**
	 * @return the configuration
	 */
	public CyclicalMappedArchitecture getConfiguration()
	{
		return configuration;
	}

	/**
	 * @return a reference of the switchingMap from the NodeConfiguration's architecture
	 */
	public HashMap<Integer, Integer> getSwitchingMapRef()
	{
		return configuration.getSwitchingMapRef();
	}
	
	@Override
	public Integer getSortableVal()
	{
		return fitness;
	}
}
