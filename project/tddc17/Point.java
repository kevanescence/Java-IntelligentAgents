package tddc17;

public class Point implements Comparable<Point>{
	private int x;
	private int y;
	
	private Point previous;
	private int cost;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.cost = 0;
		this.previous = null;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public void setPrevious(Point previous) {
		this.previous = previous;
	}
	
	public Point getPrevious() {
		return previous;
	}
	
	public int getCost() {
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
	public String toString() {
		return "(" + x + "," + y +")";
	}

	@Override
	public int compareTo(Point o) {
		return this.cost - o.cost;
	}
	
	
}
