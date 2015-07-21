package edu.salisbury.photonic.genetic_algorithm;

import java.util.List;

public abstract class GeneticPopulation <T extends GeneticIndividual>
{
	public abstract void evaluation();
	public abstract List<T> selection();
	public abstract List<T> crossover(List<T> selected);
	public abstract List<T> mutation(List<T> toMutate);
}