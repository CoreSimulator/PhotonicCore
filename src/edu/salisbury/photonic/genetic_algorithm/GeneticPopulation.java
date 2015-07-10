package edu.salisbury.photonic.genetic_algorithm;

public abstract class GeneticPopulation
{
	public abstract int[] evaluation(GeneticIndividual[] population);
	public abstract GeneticIndividual[] selection(int[] fitness);
	public abstract GeneticIndividual[] crossover(GeneticIndividual[] selected);
	public abstract GeneticIndividual[] mutation(GeneticIndividual[] toMutate);
}