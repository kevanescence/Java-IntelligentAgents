package tddc17;


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

	public int getCell(int x, int y){
		return world[x][y];
	}
}

class Cell{
	private Cell parent;
	private int x,y;
	public Cell(Cell parent,int cost, int x, int y){
		this.parent=parent;
		this.cost=cost;
		this.x=x;
		this.y=y;
	}
	public int getX(){
		return this.x;
	}
	public int getX(){
		return this.y;
	}
	public int getParent(){
		return this.parent;
	}
}

class MyAgentProgram implements AgentProgram {

	// Here you can define your variables!
	private int iterationCounter = 100;
	private MyAgentState state = new MyAgentState();

	int dir=0;
	
	@Override
	public Action execute(Percept percept) {

	    iterationCounter--;
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	   
	    System.out.println("percept: " + p);
	    
	    

		//Update of the map
	    if (state.agent_last_action==state.ACTION_MOVE_FORWARD){
		int moveX=0;int moveY=0;
	 	switch(dir){
	 		case 0:
				moveX = 1;					
				break;
			case 2:
				moveX = -1;					
				break;
			case 1:
				moveY = 1;					
				break;
			case 3:
				moveY = -1;
				break;
	    	}
	    	if (!bump){
			state.agent_x_position+=moveX;
			state.agent_y_position+=moveY;	
	    	} 
		else{
	    		state.updateWorld(state.agent_x_position+moveX,state.agent_y_position+moveY,state.WALL);
	    	}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    	//Fin update of the map

	    if (dirt){
	    	System.out.println("Action:SUCK");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 

	    else
	    {
	    	/*if (bump)
	    	{
	    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    		if(dir<3)
	    			dir++;
	    		else
	    			dir=0;
		    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    	}
	    	else
	    	{
			if(dir_old==1 && state.agent_last_action==state.ACTION_MOVE_FORWARD) {
	    			dir=2;
				state.agent_last_action=state.ACTION_TURN_RIGHT;
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			}
			else {			
		    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
			}
	    	}*/
		//Shortest path to unvisited cell
		Stack<Cell> search;
		Stack<Cell> visited;
		search.push(new Cell(null,this.x,this.y));
		while(state.getCell(search.get(0).getX(),search.get(0).getY())!=0 && !search.isEmpty){
			for(int i=0;i<4;i++){
				if(state.getCell(search.get(0).getX(),search.get(0).getY()))
			}
			search.pop();
		}
	    }
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}(
