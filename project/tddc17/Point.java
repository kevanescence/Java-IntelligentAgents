package tddc17;

import java.util.Comparator;

public class Point implements Comparable<Point>{
	private int x;
	private int y;
	
	private Point previous;
	private int cost;
	private int distance;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.cost = 0;
		this.distance = 0;
		this.previous = null;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public void setDistance(int distance){
		this.distance = distance;
	}
	
	public void setPrevious(Point previous) {
		this.previous = previous;
	}
	
	public Point getPrevious() {
		return previous;
	}
	
	public int getCost() {
		return cost+distance;
	}
	
	public int getPureCost() {
		return cost;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if ( this == obj ) return true; 
		if ( !(obj instanceof Point) ) return false;
		Point that = (Point)obj;
		return that.x == this.x && that.y == this.y;
	}
	
	@Override
	public int hashCode() {		
		return 100*x + y;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y +",cost:" + cost + ")";
	}

	@Override
	public int compareTo(Point o) {
		
		int tmp = this.cost - o.cost;
		if(tmp < 0)
			return -1;
		if(tmp > 0)
			return 1;
		return 0;
	}



//	@Override
//	public int compare(Point arg0, Point arg1) {
//		// TODO Stub de la méthode généré automatiquement
//		return arg1.cost - arg0.cost;
//	}
	
	
}
