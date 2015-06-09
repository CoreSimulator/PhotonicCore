package edu.salisbury.core_basic_layout;

/**
 * coordinates class for the basic layout test scheme
 * increasing Y indicates cores in downward direction
 * increasing X indicates cores in rightward direction
 * Due to this the origin of the coord system is at the top leftmost corner
**/
public class Coordinates 
{
	private int coordX;
	private int coordY;
	
	//constructor, takes an x and y coordinate
	//x must be between 0 and 8 and y must be between 0 and 1
	public Coordinates(int x, int y)
	{
		if(x < 0 || x > 8) throw new IndexOutOfBoundsException("x value must be between 0 and 8");
		if(y < 0 || y > 1) throw new IndexOutOfBoundsException("y value must be between 0 and 1");
		coordX = x;
		coordY = y;
	}
	
	//Constructor with no args defaults to a coordintate value of (0,0)
	public Coordinates()
	{
		coordX = 0;
		coordY = 0;
	}
	
	//getter for X value
	public int getX()
	{
		return coordX;
	}
	
	//getter for Y value
	public int getY()
	{
		return coordY;
	}
	
	//Given an integer between 0 and 15 inclusive, returns a coordinates object
	public static Coordinates intToCoords(int toCoords)
	{
		if(toCoords < 0 || toCoords > 15)
		{
			throw new IndexOutOfBoundsException("Must give an integer between 0 and 15 inclusive");
		}
		
		if(toCoords < 8)
		{
			return new Coordinates(toCoords,0);
		} else 
		{
			return new Coordinates(7 - (toCoords % 8),1);
		}
	}
	
	//Converts a coordinates of a core to an integer from 0 to 15 inclusive
	//Cores are labeled starting at 0 in the top left of the grid and then with increasing as the numbers
	public static int coordinatesToInt(Coordinates coords)
	{	
		if(coords.coordY > 0){
			return 7 - coords.coordX + (coords.coordY * 8);
		} else return coords.coordX;
	}
	
	//Converts a coordinates of a core to an integer from 0 to 15 inclusive
	public int coordinatestoInt()
	{
		return(coordinatesToInt(this));
	}
	
	public String toString()
	{
		return "(" + this.coordX + ", " + this.coordY + ")";
	}
}
