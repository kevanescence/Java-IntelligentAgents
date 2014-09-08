package tddc17;

public class Direction {
	static final Direction LEFT = new Direction(3);
	static final Direction RIGHT= new Direction(1);
	static final Direction TOP = new Direction(0);
	static final Direction BOTTOM = new Direction(2);
	
	private int current;
				
	Direction(int dir) {
		current = dir;
	}
	
	Direction(Direction d){
		current = d.current;
	}
    
	int getMoveOnX(){
		if(this.equals(LEFT))
			return -1;
		if(this.equals(RIGHT))
			return 1;
		return 0;
	}
	
	int getMoveOnY(){
		if(this.equals(TOP))
			return -1;
		if(this.equals(BOTTOM))
			return 1;
		return 0;
	}
	
	boolean isBottom(){
		return this.equals(Direction.BOTTOM);
	}
	
	boolean isTop(){
		return this.equals(Direction.TOP);
	}
	
	boolean isLeft(){
		return this.equals(Direction.LEFT);
	}
	
	boolean isRight(){
		return this.equals(Direction.RIGHT);
	}
	
	void turnLeft(){
		this.current = this.current - 1;
		if(this.current<0)
			this.current=3;
	}
	
	void turnRight(){
		this.current = (this.current + 1)%4;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		 if (this == obj) return true;
		 if (!(obj instanceof Direction)) return false;
		 Direction d = (Direction) obj;
		 return d.current == this.current;
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
