package tddc17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import aima.core.environment.liuvacuum.*;
import aima.core.util.datastructure.PriorityQueue;
import aima.core.util.datastructure.Queue;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

class MyAgentState {
	public int[][] world = new int[22][22];
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
					System.out.print(" 0 ");
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
	private boolean end=false;
	public MyAgentState state = new MyAgentState();
	private Stack<Point> next;
	private Set<Point> explored;
	private Queue<Point> search;
	private Direction direction;
	
	public MyAgentProgram() {
		next = new Stack<Point>();
		next.push((new Point(1,1)));//		
		direction = new Direction(Direction.RIGHT);
		explored = new HashSet<Point>();
		PointComparator comp = new PointComparator();		
		search = new PriorityQueue<Point>(100,comp);
	}
	private void addSearchPoint(Point p, Point previous){
		this.addSearchPoint(p, previous, false);
	}
	
	private void addSearchPoint(Point p, Point previous, boolean aStar){
		System.out.println(p);
		if(!explored.contains(p)
				&& !search.contains(p) 
				&& !this.state.isWall(p.getX(),p.getY())){
			
			if(aStar){
				p.setDistance(Math.abs(p.getX()-1)+Math.abs(p.getY()-1));
				p.setCost(previous.getPureCost()+1);
			}
			else {
				p.setCost(previous.getCost()+1);
			}
				
			p.setPrevious(previous);
			search.add(p);														
		}
	}
	
	private void addNeighbours(Point p){
		//Square on the right		
		Point test=new Point(p.getX()+1,p.getY());
		this.addSearchPoint(test, p);
		//Square on the left
		test=new Point(p.getX()-1,p.getY());
		this.addSearchPoint(test, p);
		//Square above
		test=new Point(p.getX(),p.getY()+1);
		this.addSearchPoint(test, p);
		//Square below
		test=new Point(p.getX(),p.getY()-1);
		this.addSearchPoint(test, p);
	}
	@Override
	public Action execute(Percept percept) {

		if (end && next.size()==1)
			return NoOpAction.NO_OP;

		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean)p.getAttribute("bump");
		Boolean dirt = (Boolean)p.getAttribute("dirt");
		Boolean home = (Boolean)p.getAttribute("home");
		System.out.println("percept: " + p);

		// State update based on the percept value and the last action
		if (state.agent_last_action==state.ACTION_MOVE_FORWARD) {
			//The robot managed to walk ?
			if (!bump) {
				 state.agent_x_position += direction.getMoveOnX();
				 state.agent_y_position += direction.getMoveOnY();
				 System.out.println("(x,y)" + state.agent_x_position + "," + state.agent_y_position);
			} else {
				//There is a wall forward
				state.updateWorld(state.agent_x_position + direction.getMoveOnX(),
								  state.agent_y_position + direction.getMoveOnY(),
								  state.WALL);
			}
		}
		this.state.printWorldDebug();
		//Is there something to clean ?
		if (dirt) {
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
			System.out.println("DIRT -> choosing SUCK action!");
			state.agent_last_action=state.ACTION_SUCK;
			return LIUVacuumEnvironment.ACTION_SUCK;
		}
		else {			
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
			if (bump ||    (this.state.agent_x_position == next.lastElement().getX() 
						&& this.state.agent_y_position == next.lastElement().getY())) {
				next.pop();
			}
			if (next.isEmpty()){
				search.clear();				
				explored.clear();				
				search.add(new Point(this.state.agent_x_position, this.state.agent_y_position));
				Point top = search.peek();
				while(!end && !search.isEmpty() && !this.state.mustVisit(top.getX(), top.getY())) {
					explored.add(top);					
					search.poll();
					this.addNeighbours(top);
					Point test;
					if(search.isEmpty()){
						end=true;
						search.clear();				
						explored.clear();
						search.add(new Point(this.state.agent_x_position, this.state.agent_y_position));
						top = search.peek();
						while(!(top.getX()==1 && top.getY()==1)) {//							
							explored.add(top);
							search.poll();
							this.addNeighbours(top);
							top = search.peek();
						}
					}
					top = search.peek();
				}
				while(top.getPrevious()!=null){						
					next.push(top);
					top=top.getPrevious();
				}
			}
			if(!next.isEmpty()){
				if(next.lastElement().getX()-state.agent_x_position==1){
					System.out.println("To the right !");
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