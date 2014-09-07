package tddc17;

public class Direction {
	static final Direction LEFT = new Direction(-1,0);
	static final Direction RIGHT = new Direction(1,0);
	static final Direction TOP = new Direction(0,-1);
	static final Direction BOTTOM = new Direction(0,1);
	static final Direction[] DIRECTIONS = {TOP,RIGHT, LEFT, BOTTOM};
	private int moveOnX;
	private int moveOnY;
			
	private Direction(int moveOnX, int moveOnY) {
		this.moveOnX = moveOnX;
		this.moveOnY = moveOnY;
	}
    
	int getMoveOnX(){
		return this.moveOnX;
	}
	
	int getMoveOnY(){
		return this.moveOnY;
	}
	
	boolean isBottom(){
		return this.equals(BOTTOM);
	}
	
	boolean isTop(){
		return this.equals(TOP);
	}
	
	boolean isLeft(){
		return this.equals(LEFT);
	}
	
	boolean isRight(){
		return this.equals(RIGHT);
	}
	
	@Override
	public boolean equals(Object obj) {
		 if (this == obj) return true;
		 if (!(obj instanceof Direction)) return false;
		 Direction d = (Direction) obj;
		 return d.moveOnX == this.moveOnX && d.moveOnY == this.moveOnY;
	}
	
	@Override
	public String toString() {
		if(this.isLeft()){
			return "LEFT";
		}
		if(this.isRight()){
			return "RIGHT";
		}
		if(this.isBottom()){
			return "BOTTOM";
		}
		return "TOP";
	}
	
}
