package tddc17;

import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

class MyAgentState {
	public int[][] world = new int[20][20];
	public int initialized = 0;
	final int UNKNOWN = 0;
	final int WALL = 1;
	final int CLEAR = 2;
	final int DIRT = 3;
	final int ACTION_NONE = 0;
	final int ACTION_MOVE_FORWARD = 1;
	final int ACTION_TURN_RIGHT = 2;
	final int ACTION_TURN_LEFT = 3;
	final int ACTION_SUCK = 4;

	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;

	MyAgentState() {
		for (int i = 0; i < world.length; i++)
			for (int j = 0; j < world[i].length; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = CLEAR;
		agent_last_action = ACTION_NONE;
	}

	public void updateWorld(int x_position, int y_position, int info) {
		world[x_position][y_position] = info;
	}

	public void printWorldDebug() {
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[j][i] == UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i] == WALL)
					System.out.print(" # ");
				if (world[j][i] == CLEAR)
					System.out.print(" . ");
				if (world[j][i] == DIRT)
					System.out.print(" D ");
			}
			System.out.println("");
		}
	}

	public int[][] getWorld() {
		return world;
	}

	public boolean mustVisit(int x, int y) {
		assert (x < world.length && y < world[x].length);
		return this.world[x][y] == UNKNOWN;
	}

	public boolean isWall(int x, int y) {
		assert (x < world.length && y < world[x].length);
		return this.world[x][y] == WALL;
	}
}

class MyAgentProgram implements AgentProgram {

	// Here you can define your variables!
	public int iterationCounter = 100;
	public MyAgentState state = new MyAgentState();

	private Stack<Point> next;
	private Direction direction;
	
	public MyAgentProgram() {
		next = new Stack<>();
		next.push((new Point(1,1)));
		direction = new Direction(Direction.RIGHT);
		
		Point p = new Point(0,1);
		p.setCost(3);
		Point p2 = new Point(0,2);
		p2.setCost(6);
		
		if(p.compareTo(p2) < -1) {
			System.out.println("p < p2");
		}
		else {
			System.out.println("p > p2");
		}
			
	}

	@Override
	public Action execute(Percept percept) {

		// This example agent program will update the internal agent state while only moving forward.
		// Note! It works under the assumption that the agent starts facing to the right.

		iterationCounter--;

		if (iterationCounter==0)
			return NoOpAction.NO_OP;

		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean)p.getAttribute("bump");
		Boolean dirt = (Boolean)p.getAttribute("dirt");
		Boolean home = (Boolean)p.getAttribute("home");
		System.out.println("percept: " + p);

		// State update based on the percept value and the last action
		if (state.agent_last_action==state.ACTION_MOVE_FORWARD) {
			if (!bump) {
				 state.agent_x_position+=direction.getMoveOnX();
				 state.agent_y_position+=direction.getMoveOnY();
				 System.out.println("(x,y)" + state.agent_x_position + "," + state.agent_y_position);
			} else {
				state.updateWorld(state.agent_x_position + direction.getMoveOnX(),
								  state.agent_y_position + direction.getMoveOnY(),
								  state.WALL);
			}
		}
		if (dirt)
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
		else
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);

		state.printWorldDebug();


		// Next action selection based on the percept value
		if (dirt) {
			System.out.println("DIRT -> choosing SUCK action!");
			state.agent_last_action=state.ACTION_SUCK;
			return LIUVacuumEnvironment.ACTION_SUCK;
		} 
		else
		{
			if (bump || this.state.agent_x_position==next.lastElement().getX() 
						&& this.state.agent_y_position==next.lastElement().getY()) {
				next.pop();
				//state.agent_last_action=state.ACTION_NONE;
				//return NoOpAction.NO_OP;
			}
			if (next.isEmpty()){
				Stack<Point> explored=new Stack<Point>();
				SortedSet<Point> search = new TreeSet<Point>();	    		    		
				search.add(new Point(this.state.agent_x_position, this.state.agent_y_position));
				Point top = search.last();
				while(!search.isEmpty() && !this.state.mustVisit(top.getX(), top.getY())) {
					explored.push(top);
					search.remove(top);
					Point test=new Point(top.getX()+1,top.getY());
					System.out.println(explored.contains(test));
					if(!explored.contains(test) && !this.state.isWall(test.getX(),test.getY())){
						search.add(test);
						test.setPrevious(top);
						test.setCost(top.getCost()+1);
					}
					test=new Point(top.getX()-1,top.getY());
					if(!explored.contains(test) && !this.state.isWall(test.getX(),test.getY())){
						search.add(test);
						test.setPrevious(top);
						test.setCost(top.getCost()+1);
					}
					test=new Point(top.getX(),top.getY()+1);
					if(!explored.contains(test) && !this.state.isWall(test.getX(),test.getY())){
						search.add(test);
						test.setPrevious(top);
						test.setCost(top.getCost()+1);
					}
					test=new Point(top.getX(),top.getY()-1);
					if(!explored.contains(test) && !this.state.isWall(test.getX(),test.getY())){
						search.add(test);
						test.setPrevious(top);
						test.setCost(top.getCost()+1);
					}
					top=search.last();
				}
				while(top.getPrevious()!=null){					
					System.out.println("Top" + top);
					next.push(top);
					top=top.getPrevious();
				}
			}
			if(next.lastElement().getX()-state.agent_x_position==1){
				System.out.println("To the right !");
				System.out.println(direction.toString());
				if(direction.isRight()){
					state.agent_last_action=state.ACTION_MOVE_FORWARD;					
					return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				else if(direction.isBottom()) {
					state.agent_last_action=state.ACTION_TURN_LEFT;
					this.direction.turnLeft();
					return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				}
				else {					
					state.agent_last_action=state.ACTION_TURN_RIGHT;
					this.direction.turnRight();
					return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			if(next.lastElement().getX()-state.agent_x_position==-1){
				System.out.println("To the left !");
				System.out.println(direction.toString());
				if(direction.isLeft()){
					state.agent_last_action=state.ACTION_MOVE_FORWARD;
					return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				else if(direction.isTop()) {
					state.agent_last_action=state.ACTION_TURN_LEFT;
					this.direction.turnLeft();
					return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				}
				else {
					state.agent_last_action=state.ACTION_TURN_RIGHT;
					this.direction.turnRight();
					return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			
			if(next.lastElement().getY()-state.agent_y_position==1){
				System.out.println("To the bottom !");
				System.out.println(direction.toString());
				if(direction.isBottom()){
					state.agent_last_action=state.ACTION_MOVE_FORWARD;					
					return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				else if(direction.isLeft()) {
					state.agent_last_action=state.ACTION_TURN_LEFT;
					this.direction.turnLeft();
					return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				}
				else {
					state.agent_last_action=state.ACTION_TURN_RIGHT;
					this.direction.turnRight();
					return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			
			if(next.lastElement().getY()-state.agent_y_position==-1){
				System.out.println("To the top !");
				System.out.println(direction.toString());
				if(direction.isTop()){
					state.agent_last_action=state.ACTION_MOVE_FORWARD;
					return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				else if(direction.isRight()) {
					state.agent_last_action=state.ACTION_TURN_LEFT;
					this.direction.turnLeft();
					return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				}
				else {
					state.agent_last_action=state.ACTION_TURN_RIGHT;
					this.direction.turnRight();
					return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			
			state.agent_last_action = state.ACTION_NONE;
			return NoOpAction.NO_OP;
			//Deplacement
			
		}
	}
}

public class MyVacuumAgent extends AbstractAgent {
	public MyVacuumAgent() {
		super(new MyAgentProgram());
	}
}