package edu.salisbury.photonic.genetic_algorithm;

import java.util.List;

public abstract class GeneticPopulation
{
	public abstract int[] evaluation(List<GeneticIndividual> population);
	public abstract List<GeneticIndividual> selection(int[] fitness);
	public abstract List<GeneticIndividual> crossover(List<GeneticIndividual> selected);
	public abstract List<GeneticIndividual> mutation(List<GeneticIndividual> toMutate);
}