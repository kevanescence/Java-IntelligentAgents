package tddc17;

import tddc17.Direction;
import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;


class MyAgentState
{
	private int[][] world = new int[20][20];
	private int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int ACTION_NONE 		= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = CLEAR;
		agent_last_action = ACTION_NONE;
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
			}
			System.out.println("");
		}
	}
	
	public int[][] getWorld(){
		return world;
	}
	
	public boolean mustVisit(int x, int y){
		assert(x < world.length && y < world[x].length);
		return this.world[x][y] == UNKNOWN;
	}
	
	public int getCell(int x, int y){
		return world[x][y];
	}
	
}

class Cell{
	private Cell parent;
	private int x,y;
	private int cost;
	public Cell(Cell parent,int cost, int x, int y){
		this.parent=parent;
		this.cost=cost;
		this.x=x;
		this.y=y;
	}
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public Cell getParent(){
		return this.parent;
	}
}


class MyAgentProgram implements AgentProgram {
	// Here you can define your variables!
	private int iterationCounter = 100;
	private MyAgentState state = new MyAgentState();
	
	private Direction direction = Direction.RIGHT;
	
	private Direction chooseDirection(Boolean bump){
		if(direction.isRight() && bump) {			
			return exploreDirection(Direction.BOTTOM);
		}
		if(direction.isBottom() && bump) {
			return exploreDirection(Direction.LEFT);
		}
		if(direction.isLeft() && bump) {
			return exploreDirection(Direction.TOP);
		}
		if(direction.isTop() && bump) {
			return exploreDirection(Direction.RIGHT);
		}		
		return exploreDirection(this.direction);
	}
	
	private Direction exploreDirection(Direction dir) {
		int newX = this.state.agent_x_position + dir.getMoveOnX();
		int newY = this.state.agent_y_position + dir.getMoveOnY();
		if(this.state.mustVisit(newX, newY)) {
			return dir;
		}		
		for(Direction d: Direction.DIRECTIONS) {			
			if(!d.equals(dir)){
				newX = this.state.agent_x_position + d.getMoveOnX();
				newY = this.state.agent_y_position + d.getMoveOnY();
				if(this.state.mustVisit(newX, newY)){
					return d;
				}
			}
		}
		return dir;
	}
	
	private int chooseAction(Direction newDirection, Boolean dirt) {
		if(dirt){
			return state.ACTION_SUCK;
		}
		if(direction.isBottom() && newDirection.isRight())
			return state.ACTION_TURN_LEFT;
		if(direction.isBottom() && newDirection.isLeft())
			return state.ACTION_TURN_RIGHT;
		if(direction.isTop() && newDirection.isLeft())
			return state.ACTION_TURN_LEFT;
		if(direction.isTop() && newDirection.isRight())
			return state.ACTION_TURN_RIGHT;
		if(direction.isLeft() && newDirection.isTop())
			return state.ACTION_TURN_RIGHT;
		if(direction.isLeft() && newDirection.isBottom())
			return state.ACTION_TURN_LEFT;
		if(direction.isRight() && newDirection.isBottom())
			return state.ACTION_TURN_RIGHT;
		if(direction.isRight() && newDirection.isTop())
			return state.ACTION_TURN_LEFT;
		return state.ACTION_MOVE_FORWARD;
	}
	private Action stateToAction(int state) {
		if(state == this.state.ACTION_MOVE_FORWARD)
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		if(state == this.state.ACTION_TURN_LEFT)
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		if(state == this.state.ACTION_TURN_RIGHT)
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		if(state == this.state.ACTION_SUCK)
			return LIUVacuumEnvironment.ACTION_SUCK;
		return NoOpAction.NO_OP;
	}
	
	private void computeCoordinates(Direction newDirection) {
		int moveX = 0;
		int moveY = 0;
		
		if(newDirection.isTop())			
			moveY = -1;
		else if(newDirection.isBottom())			
			moveY = 1;
		else if(newDirection.isLeft())			
			moveX = -1;
		else if(newDirection.isRight())
			moveX = 1;
		
		this.state.agent_x_position += moveX;
		this.state.agent_y_position += moveY;
		
	}
	
	@Override
	public Action execute(Percept percept) {		
		//Artifact to avoid infinite loops
	    iterationCounter--;
	    if (iterationCounter==0) {
	    	return NoOpAction.NO_OP;
	    }
	    
//	    this.state.printWorldDebug();
	    
	    
	    //Retrieve all the attributes
	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	   
	    System.out.println("percept: " + p);
	    
	    int action;
		//Update of the map	    
//	    if (state.agent_last_action == state.ACTION_MOVE_FORWARD){
	    	
	    	Direction d = this.chooseDirection(bump);	    	
	    	action = this.chooseAction(d, dirt);	    	
	    	this.state.agent_last_action = action;
	    	this.direction = d;	    	
    		if (!bump){
    			state.agent_x_position+=d.getMoveOnX();
    			state.agent_y_position+=d.getMoveOnY();    			
    		} 
    		else {
    			System.out.println("Mur as " + state.agent_x_position +"," + state.agent_y_position);
    			state.updateWorld(state.agent_x_position,
    							  state.agent_y_position,state.WALL);    			
    		}
//	    }
	    // Next action selection based on the percept value
	    if (dirt) {
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    	System.out.println("Action:SUCK");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    }
	    else {
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
//	    	return stateToAction(this.state.agent_last_action);
	    	if(bump) {	    		
	    		return stateToAction(this.state.agent_last_action);
	    	}
	    	else {
	    		state.agent_last_action = state.ACTION_MOVE_FORWARD;
	    	}
	    }
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
