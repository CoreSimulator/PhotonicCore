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
	private int mutationsPerPop;
	private int generationNumber = 0;
	private HashSet<HashMap<Integer, Integer>> previousCreations = 
			new HashSet<HashMap<Integer, Integer>>(3000); //TODO Consider adding a different number
	
	public NodeConfigurationPopulation(int bitsPerFlit,	int teardownTime, CoreLog log, 
			HashMap<Coordinate, Integer> coordsToNumberMapping, int populationSize, 
			int mutationsPerPop)
	{
		if(log == null || coordsToNumberMapping == null)
		{
			throw new NullPointerException("The given log cannot be null.");
		}
		if(coordsToNumberMapping.size() <= 2)
		{
			throw new IllegalArgumentException(" needs to be at least 3 nodes.");
		}
		if(populationSize <= 2)
		{
			throw new IllegalArgumentException(" needs to be at least individuals in a generation.");
		}
		if(bitsPerFlit < 1)
		{
			throw new IllegalArgumentException("bitsPerFlit needs to be at least 1.");
		}
		if(teardownTime < 1)
		{
			throw new IllegalArgumentException("teardownTime needs to be at least 1.");
		}
		if(mutationsPerPop < 1)
		{
			throw new IllegalArgumentException("mutationsPerPop needs to be at least 1.");
		}
		this.mutationsPerPop = mutationsPerPop;
		this.bitsPerFlit = bitsPerFlit;
		this.teardownTime = teardownTime;
		this.log = log;
		this.coordsToNumberMapping = coordsToNumberMapping;
		currentPop = new ArrayList<GeneticIndividual>(coordsToNumberMapping.size());
		for(int i = 0; i < currentPop.size(); i++)
		{
			currentPop.set(i, generateIndividual());
		}
	}
	
	public NodeConfiguration generateIndividual()
	{
		HashMap<Integer, Integer> generatedHashMap =  
				new HashMap<Integer, Integer>((int) ( coordsToNumberMapping.size() / 0.75 + 1));
		
		//0 always goes to 0 in NodeConfiguration
		generatedHashMap.put(0, 0);
		
		List<Integer> intVals = new LinkedList<Integer>();
		
		for(int i = 1; i < coordsToNumberMapping.size(); i++)
		{
			intVals.add(i);
		}
		
		Random randGen = new Random();
		for(int i = 1; i < coordsToNumberMapping.size(); i++)
		{
			int indexToRemove = intVals.get(randGen.nextInt(intVals.size()));
			generatedHashMap.put(i, intVals.remove(indexToRemove));
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
		toChange.put(firstIndex, toChange.get(firstIndex));
		toChange.put(secondIndex, toChange.get(secondIndex));
	}
	
	@Override
	public int[] evaluation(List<GeneticIndividual> population)
	{
		//in our case, the lower the fitness levels the better
		int[] fitnesslevels = new int[population.size()];
		for(int i = 0; i < population.size(); i++)
		{
			fitnesslevels[i] = population.get(i).evaluateFitness();
		}
		return fitnesslevels;
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
		
		List<GeneticIndividual> fittestIndividuals = new ArrayList<GeneticIndividual>(2); 
		
		fittestIndividuals.set(0, currentPop.get(fittestIndex));
		fittestIndividuals.add(1, currentPop.get(secondFittestIndex));
		return fittestIndividuals;
	}

	@Override
	public List<GeneticIndividual> crossover(List<GeneticIndividual> selected)
	{
		List<GeneticIndividual> newPop = new ArrayList<GeneticIndividual>();
		NodeConfiguration fittest = (NodeConfiguration) selected.get(0);
		NodeConfiguration secondFittest = (NodeConfiguration) selected.get(1);
		
		for(int i = 0; i < newPop.size(); i++)
		{
			NodeConfiguration toAdd = new NodeConfiguration(fittest, secondFittest, bitsPerFlit, 
					teardownTime, coordsToNumberMapping);
			
			HashMap<Integer,Integer> possibleSwitchingMap = toAdd.getSwitchingMapRef();
			while(previousCreations.contains(possibleSwitchingMap))
			{
				swap(possibleSwitchingMap);
			}
			previousCreations.add(possibleSwitchingMap);
			newPop.set(i, toAdd);
		}
		return newPop;
	}

	@Override
	public List<GeneticIndividual> mutation(List<GeneticIndividual> toMutate)
	{
		Random randGen = new Random();
		for(int i = 0; i < mutationsPerPop; i++)
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
			List<GeneticIndividual> selectionList = selection(evaluation(currentPop));
			
			System.out.println("Fittest individuals of generation "+ generationNumber + " (Lower " +
					"scores are better): ");
			
			for(int j = 0; j < selectionList.size(); j++)
			{
				System.out.println( (j+1) + ": " + selectionList.get(j));
			}
			
			System.out.println("Generating next generation of configurations via crossover...");
			List<GeneticIndividual> crossoverList = crossover(selectionList);
			System.out.println("Performing mutations");
			currentPop = mutation(crossoverList);
			generationNumber++;
		}
	}

	/**
	 * @return the generationSize
	 */
	public int generationSize()
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
	 * @return the coordsToNumberMapping
	 */
	public HashMap<Coordinate, Integer> getCoordsToNumberMapping()
	{
		return coordsToNumberMapping;
	}



}
