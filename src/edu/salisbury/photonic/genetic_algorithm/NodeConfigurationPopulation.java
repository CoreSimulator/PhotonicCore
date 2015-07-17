package edu.salisbury.photonic.genetic_algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.cyclical_core_simulator.CyclicalMappedArchitecture;

public class NodeConfigurationPopulation extends GeneticPopulation
{
	private List<GeneticIndividual> currentPop;
	private CoreLog log;
	private HashMap<Coordinate, Integer> coordsToNumberMapping;
	private int populationSize;
	private int bitsPerFlit;
	private int teardownTime;
	private int mutationsPerGeneration;
	
	private int numberOfParents;
	private int numberOfAllTimeFittestKept = 0;
	
	//TODO add an Array of the most succesful configurations from all time to always add to the pool
	private List<GeneticIndividual> allTimeFittest;
	private int generationNumber = 0; //TODO add generationNumber
	
	
	private HashSet<HashMap<Integer, Integer>> previousCreations = 
			new HashSet<HashMap<Integer, Integer>>();
	
	
	public NodeConfigurationPopulation(CoreLog log, 
			HashMap<Coordinate, Integer> coordsToNumberMapping)
	{
		this(log, coordsToNumberMapping, 64, 1);
	}
	
	public NodeConfigurationPopulation(CoreLog log, 
			HashMap<Coordinate, Integer> coordsToNumberMapping, int bitsPerFlit, int teardownTime)
	{
		this(log, coordsToNumberMapping, bitsPerFlit, teardownTime, 30, 2);
	}
	
	
	public NodeConfigurationPopulation(CoreLog log, 
			HashMap<Coordinate, Integer> coordsToNumberMapping, int bitsPerFlit, int teardownTime, 
			int populationSize, int mutationsPerGeneration)
	{
		
		setPopulationSize(populationSize);
		setMutationsPerGeneration(mutationsPerGeneration);
		setBitsPerFlit(bitsPerFlit);
		setTeardownTime(teardownTime);
		setLog(log);
		setCoordsToNumberMapping(coordsToNumberMapping);
		currentPop = new ArrayList<GeneticIndividual>(populationSize);
		allTimeFittest = new ArrayList<GeneticIndividual>(numberOfAllTimeFittestKept);
		for(int i = 0; i < populationSize; i++)
		{
			currentPop.add(i, generateIndividual());
		}
	}
	
	
	
	public NodeConfiguration generateIndividual()
	{
		HashMap<Integer, Integer> generatedHashMap =  
				new HashMap<Integer, Integer>((int) ( coordsToNumberMapping.size() / 0.75 + 1));
		
		//0 always goes to 0 in NodeConfiguration
		generatedHashMap.put(0, 0);
		
		LinkedList<Integer> intVals = new LinkedList<Integer>();
		
		for(int i = 1; i < coordsToNumberMapping.size(); i++)
		{
			intVals.add(i);
		}
		
		Random randGen = new Random();
		for(int i = 1; i < coordsToNumberMapping.size(); i++)
		{
			generatedHashMap.put(i, intVals.remove(randGen.nextInt(intVals.size())));
		}
		
		while(previousCreations.contains(generatedHashMap))
		{
			swap(generatedHashMap);
		}
		previousCreations.add(generatedHashMap);
		
		CyclicalMappedArchitecture generatedArchitecture = new CyclicalMappedArchitecture(
				bitsPerFlit, teardownTime, coordsToNumberMapping);
		generatedArchitecture.checkAndSetIntegerSwapMap(generatedHashMap);
		
		return new NodeConfiguration(generatedArchitecture, log);
	}
	
	private void swap(HashMap<Integer, Integer> toChange)
	{
		if(toChange.size() != coordsToNumberMapping.size())
		{
			throw new IllegalArgumentException("Should be the same size as coordsToNumberMapping");
		}
		Random randGen = new Random();
		
		int firstIndex = randGen.nextInt(toChange.size() - 1) + 1;
		int secondIndex = randGen.nextInt(toChange.size() - 1) + 1;
		
		while(firstIndex == secondIndex)
		{
			secondIndex = randGen.nextInt(toChange.size() - 1) + 1;
		}
		Integer firstValue = toChange.get(firstIndex);
		toChange.put(firstIndex, toChange.get(secondIndex));
		toChange.put(secondIndex, firstValue);
	}
	
	@Override
	public int[] evaluation(List<GeneticIndividual> population)
	{
		//in our case, the lower the fitness levels the better
		int[] fitnessLevels = new int[populationSize];
		for(int i = 0; i < populationSize; i++)
		{
			System.out.println("Evaluating: " + i);
			fitnessLevels[i] = population.get(i).evaluateFitness();
		}
		return fitnessLevels;
	}
	
	@Override
	public List<GeneticIndividual> selection(int[] fitness)
	{
		if(fitness.length < 2)
		{
			throw new IllegalArgumentException("Needs to have at least two values");
		}
		int fittestIndex = 0;
		int secondFittestIndex = -1;
		
		for(int i = 1; i < fitness.length; i++)
		{
			if(fitness[i] < fitness[fittestIndex])
			{
				secondFittestIndex = fittestIndex;
				fittestIndex = i;
						
			}
			else if(secondFittestIndex == -1 || fitness[i] < fitness[secondFittestIndex])
			{
				secondFittestIndex = i;
			}
		}
		
		ArrayList<GeneticIndividual> fittestIndividuals = new ArrayList<GeneticIndividual>(2); 
		
		for(int i = 0; i < numberOfParents; i++)
		{
			fittestIndividuals.add(0, currentPop.get(fittestIndex));
			fittestIndividuals.add(1, currentPop.get(secondFittestIndex));
		}
		
		return fittestIndividuals;
	}

	@Override
	public List<GeneticIndividual> crossover(List<GeneticIndividual> selected)
	{
		List<GeneticIndividual> newPop = new ArrayList<GeneticIndividual>(populationSize);
		NodeConfiguration fittest = (NodeConfiguration) selected.get(0);
		NodeConfiguration secondFittest = (NodeConfiguration) selected.get(1);
		
		for(int i = 0; i < populationSize; i++)
		{
			NodeConfiguration toAdd = new NodeConfiguration(fittest, secondFittest, bitsPerFlit, 
					teardownTime, coordsToNumberMapping, log);
			
			HashMap<Integer,Integer> possibleSwitchingMap = toAdd.getSwitchingMapRef();
			
			//System.out.println("Stuck");
			while(previousCreations.contains(possibleSwitchingMap))
			{
				System.out.println(possibleSwitchingMap.hashCode());
				swap(possibleSwitchingMap);
			}
			previousCreations.add(possibleSwitchingMap);
			//System.out.println("unstuck");
			newPop.add(i, toAdd);
		}
		return newPop;
	}

	@Override
	public List<GeneticIndividual> mutation(List<GeneticIndividual> toMutate)
	{
		Random randGen = new Random();
		for(int i = 0; i < mutationsPerGeneration; i++)
		{
			HashMap<Integer, Integer> aMap = (
					(NodeConfiguration) toMutate.get(
							randGen.nextInt(toMutate.size()))).getSwitchingMapRef();
			
			previousCreations.remove(aMap);
			
			do 
			{
				swap(aMap);
			}
			while(previousCreations.contains(aMap));
		}
		
		return toMutate;
	}
	
	public void runForGenerations(int numberOfGenerationsToRunFor)
	{
		for(int i = 0; i < numberOfGenerationsToRunFor; i++)
		{
			System.out.println("Evaluating and picking fittest...");
			int[] fitList = evaluation(currentPop);
			List<GeneticIndividual> selectionList = selection(fitList);
			
			
			System.out.println("Generating next generation of configurations via crossover...");
			List<GeneticIndividual> crossoverList = crossover(selectionList);
			System.out.println("Performing mutations");
			currentPop = mutation(crossoverList);
			generationNumber++;
		}
	}

	/**
	 * @return the populationSize
	 */
	public int populationSize()
	{
		return populationSize;
	}
	
	/**
	 * @return the log
	 */
	public CoreLog getLog()
	{
		return log;
	}
	
	/**
	 * Set the log to use in the genetic algorithm.
	 * @param log 
	 */
	public void setLog(CoreLog log)
	{
		if(log == null)
		{
			throw new NullPointerException("The given log cannot be null.");
		}
		this.log = log;
	}

	/**
	 * @return the coordsToNumberMapping
	 */
	public HashMap<Coordinate, Integer> getCoordsToNumberMapping()
	{
		return coordsToNumberMapping;
	}

	/**
	 * Sets the population size, must be greater than 2
	 * @param populationSize
	 */
	public void setPopulationSize(int populationSize)
	{
		if(populationSize <= 2)
		{
			throw new IllegalArgumentException("Needs to be at least 2 individuals in a " +
					"generation.");
		}
		if(populationSize < numberOfParents)
		{
			throw new IllegalArgumentException("Cannot set the number of individuals per " +
					"population under the number of parents");
		}
		
		if(populationSize > this.populationSize)
		{
			while(currentPop.size() < populationSize)
			{
				currentPop.add(generateIndividual());
			}
		}
		else if(populationSize < this.populationSize)
		{
			while(currentPop.size() > populationSize)
			{
				previousCreations.remove(currentPop.remove(currentPop.size() - 1));
			}
			currentPop = currentPop.subList(0, populationSize);
		}
		this.populationSize = populationSize;
	}

	/**
	 * set the number of mutations per generation
	 * @param mutationsPerGeneration
	 */
	public void setMutationsPerGeneration(int mutationsPerGeneration)
	{
		if(mutationsPerGeneration < 0)
		{
			throw new IllegalArgumentException("mutationsPerGeneration needs to be nonNegative.");
		}
		this.mutationsPerGeneration = mutationsPerGeneration;
	}
	
	/*
	 * Sets the coordsToNumberMapping, they cannot be set to null.
	 * @param coordsToNumberMapping
	 */
	private void setCoordsToNumberMapping(HashMap<Coordinate, Integer> coordsToNumberMapping)
	{
		if(coordsToNumberMapping == null)
		{
			throw new NullPointerException("The given log cannot be null.");
		}
		if(coordsToNumberMapping.size() <= 2)
		{
			throw new IllegalArgumentException("There needs to be at least 3 nodes.");
		}
		this.coordsToNumberMapping = coordsToNumberMapping;
	}
	
	/*
	 * Sets bitsPerFlit for all nodeConfigurations, has to be a positive integer 
	 * @param bitsPerFlit
	 */
	private void setBitsPerFlit(int bitsPerFlit) 
	{
		if(bitsPerFlit < 1)
		{
			throw new IllegalArgumentException("bitsPerFlit needs to be a positive integer.");
		}
		this.bitsPerFlit = bitsPerFlit;
	}
	
	/*
	 * sets TeardownTime for all nodeConfigurations, has to be a positive integer 
	 * @param teardownTime
	 */
	private void setTeardownTime(int teardownTime)
	{
		if(teardownTime < 1)
		{
			throw new IllegalArgumentException("teardownTime needs to be a positive integer.");
		}
		this.teardownTime = teardownTime;
	}

	/**
	 * @return the numberOfParents
	 */
	public int getNumberOfParents()
	{
		return numberOfParents;
	}

	/**
	 * @param numberOfParents the numberOfParents to set (must include allTimeFittest)
	 */
	public void setNumberOfParents(int numberOfParents)
	{
		if(numberOfParents < 1)
		{
			throw new IllegalArgumentException("There needs to be at least one parent for a " +
					"genetic algorithm to function.");
		}
		if(numberOfParents < numberOfAllTimeFittestKept)
		{
			throw new IllegalArgumentException("NumberOfAllTimeFittestKept must be accounted for " +
					"in the number of parents that exist");
		}
		if(numberOfParents > populationSize)
		{
			throw new IllegalArgumentException("Number of Parents cannot be set above the number " +
					"of individuals per generation");
		}
		this.numberOfParents = numberOfParents;
	}

	/**
	 * @return the numberOfAllTimeFittestKept
	 */
	public int getNumberOfAllTimeFittestKept()
	{
		return numberOfAllTimeFittestKept;
	}

	/**
	 * the numberOfAllTimeFittestKept that are reintroduced as parents each generation. Needs to be
	 * less than or equal to the number of parents that exist
	 * 
	 * @param 	numberOfAllTimeFittestKept 
	 */
	public void setNumberOfAllTimeFittestKept(int numberOfAllTimeFittestKept)
	{
		if(numberOfAllTimeFittestKept < 0)
		{
			throw new IllegalArgumentException("numberOfAllTimeFittestKept needs to be " +
					"non-negative");
		}
		if(numberOfAllTimeFittestKept > numberOfParents)
		{
			throw new IllegalArgumentException("The numberOfParents cannot be less than the " +
					"numberOfAllTimeFittestKept.");
		}
		this.numberOfAllTimeFittestKept = numberOfAllTimeFittestKept;
		
		while(numberOfAllTimeFittestKept < allTimeFittest.size())
		{
			allTimeFittest.remove(allTimeFittest.size() - 1);
		}
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize()
	{
		return populationSize;
	}

	/**
	 * @return the bitsPerFlit
	 */
	public int getBitsPerFlit()
	{
		return bitsPerFlit;
	}

	/**
	 * @return the teardownTime
	 */
	public int getTeardownTime()
	{
		return teardownTime;
	}

	/**
	 * @return the mutationsPerGeneration
	 */
	public int getMutationsPerGeneration()
	{
		return mutationsPerGeneration;
	}
}
