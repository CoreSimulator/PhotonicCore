package edu.salisbury.photonic.genetic_algorithm;

import java.util.List;

public abstract class GeneticPopulation <T extends GeneticIndividual>
{
	public abstract List<T> evaluation(List<T> population);
	public abstract List<T> selection(List<T> fitness);
	public abstract List<T> crossover(List<T> selected);
	public abstract List<T> mutation(List<T> toMutate);
}