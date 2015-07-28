package edu.salisbury.photonic.simulation_gui;

import java.util.HashMap;
import java.util.List;

import edu.salisbury.photonic.core_simulator.Coordinate;
import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.genetic_algorithm.NodeConfiguration;
import edu.salisbury.photonic.genetic_algorithm.NodeConfigurationPopulation;

public class GeneticAlgorithm implements Runnable
{
	private CoreLog coreLog = new CoreLog();
	private HashMap<Coordinate, Integer> coordsToNumberMapping;
	private int bitsPerFlit;
	private int teardownTime;
	private int populationSize;
	private int mutationsPerGeneration;
	private int numberOfGenerations = 15;
	private int numberOfParents = 2;
	private int numberOfAllTimeFittestKept = 1;
	private NodeConfigurationPopulation primordialSoup;
	
	public GeneticAlgorithm (String filename, int startingSection, int endingSection, int[] nodeArrangement, int bitsPerFlit, int teardownTime,
			int populationSize, int mutationsPerGeneration) {
		coreLog = LogReader.readLogIgnoreRepeaters(filename);
		coreLog = coreLog.subLog(startingSection, endingSection);
		coordsToNumberMapping = getCoordsToNumberMapping();
		this.bitsPerFlit = bitsPerFlit;
		this.teardownTime = teardownTime;
		this.populationSize = populationSize;
		this.mutationsPerGeneration = mutationsPerGeneration;
	}
	
	@Override
	public void run() {
		primordialSoup = new NodeConfigurationPopulation(coreLog, coordsToNumberMapping, 
				bitsPerFlit, teardownTime, populationSize, mutationsPerGeneration, numberOfParents);
		primordialSoup.setMutationsPerGeneration(mutationsPerGeneration);
		primordialSoup.setNumberOfAllTimeFittestKept(numberOfAllTimeFittestKept);
		runGenerations(numberOfGenerations);
	}
	
	public void runGenerations(int numOfGenerations)
	{
		for(int i = 0; i < numOfGenerations; i++)
		{
		 	System.out.println("Evaluating configurations");
	    	primordialSoup.evaluation();
			List<NodeConfiguration> selectionList = primordialSoup.selection();
			addToDataSets(selectionList, i);
			primordialSoup.crossover(selectionList);
			primordialSoup.mutation();
		}
    }
	 
	public void addToDataSets(List<NodeConfiguration> toAdd, int genNumber)
    {
		System.out.println("Best fitness for generation " + genNumber);
    	for(int i = 0; i < toAdd.size(); i++)
    	{
    		System.out.println("parent"+ i + ": " + toAdd.get(i).getFitness());
    	}
    }
	
	private HashMap<Coordinate, Integer> getCoordsToNumberMapping() {
		HashMap<Coordinate, Integer> switchingMap =  new HashMap<Coordinate, Integer>();
		for(int i = 0; i < 8; i++)
		{
			switchingMap.put(new Coordinate(1,i), i);
		}
		for(int i = 7; i >= 0; i--)
		{
			switchingMap.put(new Coordinate(2,7-i), 8+i);
		}
		return switchingMap;
	}

	public void setNumberOfGenerations(int numOfGenerations) {
		numberOfGenerations = numOfGenerations;
	}
	
	public void setNumberOfParents(int numOfParents) {
		numberOfParents = numOfParents;
	}
	
	public void setNumberOfAllTimeFittestKept (int numOfFittest) {
		numberOfAllTimeFittestKept = numOfFittest;
	}	
}
