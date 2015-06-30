package edu.salisbury.cyclical_core_simulator;

/**
 * In a cyclical network a task can either take a clockwise or counterclockwise path. However, until
 * that direction has been assigned to the task, the direction assigned to that task should be 
 * undetermined.
 * @author timfoil
 *
 */
public enum CyclicalDirection
{
	CLOCKWISE, COUNTERCLOCKWISE, UNDETERMINED
}
