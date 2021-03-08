package com.efrei.mathinfo.automates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	
	private String id; // '1' or 'A' 
	
	private Map<String[], State> links; // keyword gives the state 

	private List<StateType> types;
	
	public State(String id, StateType... types) {
		
		this.id = id;
		this.types = Arrays.asList(types);
		
		links = new HashMap<String[], State>();
	}
	
	public String getID() {
		return this.id;
	}
	
	public Map<String[], State> getLinks() {
		return this.links;
	}
	
	public void addLink(State where, String... what) {
		this.links.put(what, where);
	}
	
	public List<StateType> getType() {
		return this.types;
	}
	
	public void addType(StateType type) {
		this.types.add(type);
	}
}
