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

public class NodeConfiguration extends GeneticIndividual
{
	private CyclicalMappedArchitecture configuration;
	private CoreLog log;
	
	public NodeConfiguration(CyclicalMappedArchitecture architecture, CoreLog log)
	{
		if(architecture == null || log == null)
		{
			throw new NullPointerException("Arguments to this constructor should not be null");
		}
		
		configuration = architecture;
		this.log = log;
	}
	
	public NodeConfiguration(NodeConfiguration mostSuccessful, 
			NodeConfiguration secondMostSuccesful, int bitsPerFlit, int tearDownTime, 
			HashMap <Coordinate, Integer> coordsToNumberMapping)
	{
		configuration = new CyclicalMappedArchitecture( 
				bitsPerFlit, tearDownTime, coordsToNumberMapping); 
		
		GenePool pool = new GenePool(mostSuccessful.getConfiguration().getSwitchingMapCopy(), 
				mostSuccessful.getConfiguration().getNumberOfCoreNodes());
		pool.addMapToPool(secondMostSuccesful.getConfiguration().getSwitchingMapCopy());
		
		configuration.checkAndSetIntegerSwapMap(pool.generateSwitchingMap());
	}
	
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
			
			for(int i = 0; i < switchingMaps.size(); i++)
			{
				addMapToPool(switchingMaps.get(i));
			}
		}
		
		public GenePool(HashMap<Integer, Integer> switchingMap, int size)
		{
			if(size < 0)
			{
				throw new IllegalArgumentException("size cannot be a negative number");
			}
			numberOfPoolBuckets = size;
			organizedPool = new ArrayList<LinkedList<Coordinate>>(numberOfPoolBuckets);
			addMapToPool(switchingMap);
		}

		public void addMapToPool(HashMap<Integer, Integer> switchingMap)
		{
			if(switchingMap == null)
			{
				//if switchingMap is null that means the default values must be added
				//Since no switching has occured
				addDefaultValues();
			}
			
			if(switchingMap.size() > numberOfPoolBuckets)
			{
				throw new IllegalArgumentException("SwitchingMap is too big for given" +
						"genepool size.");
			}
			
			for(int i = 0; i < numberOfPoolBuckets; i++)
			{
				Integer value = switchingMap.get(i);
				if(value == null)
				{
					Coordinate toAdd = new Coordinate(i,i);
					if(pool.add(toAdd))
					{
						addToOrganizedPool(toAdd);
					}
				} 
				else
				{
					Coordinate toAdd = new Coordinate(i, value);
					if(pool.add(toAdd))
					{
						addToOrganizedPool(toAdd);
					}
				}
			}
		}
		
		private void addDefaultValues()
		{
			for(int i = 0; i < numberOfPoolBuckets; i++)
			{
				Coordinate toAdd = new Coordinate(i,i);
				if(pool.add(toAdd))
				{
					addToOrganizedPool(toAdd);
				}
			}
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
			
			if(toPop.get(index) == null)
			{
				return null;
			}
			
			return toPop.get(index).remove(new Random().nextInt(toPop.get(index).size()));
		}




		/**
		 * Helper class for generating switching HashMaps
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
			
			public boolean stillExists(int toExamine)
			{
				return notPicked.contains(toExamine);
			}

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
		return simulation.simulateWithLog(log);
	}

	/**
	 * @return the configuration
	 */
	public CyclicalMappedArchitecture getConfiguration()
	{
		return configuration;
	}

	public HashMap<Integer, Integer> getSwitchingMapRef()
	{
		return configuration.getSwitchingMapRef();
	}
}
