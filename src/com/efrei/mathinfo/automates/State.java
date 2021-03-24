package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State implements Cloneable {

	// Instances
	private String id; // '1' or 'A'.
	private Map<String, List<State>> links; // keyword gives the state (Map<key, List of state>)
	private List<StateType> types; // Status states: ENTRY, COMMON, EXIT


	// Constructor (memory allocation)
	public State(String id) {
		
		this.id = id;
		this.types = new ArrayList<StateType>();
		this.types.add(StateType.COMMON);
		
		links = new HashMap<String, List<State>>();
	}

	private State(State state) {
		this.id = state.getID();
		this.types = state.getType();
		this.links = state.getLinks();
	}

	public void setID(String id) {
		this.id = id;
	}
	
	// Read and write accessors
	public String getID() {
		return this.id;
	}
	
	public Map<String, List<State>> getLinks() {
		return this.links;
	}
	
	public void addLink(String what, State where) {
		// If the list of this transition is already created
		if (this.links.containsKey(what)) {
			this.links.get(what).add(where);
		}

		// If there is no list yet in the hash map
		else {
			List<State> st = new ArrayList<State>();
			st.add(where);
			this.links.put(what, st);
		}
	}
	
	public List<StateType> getType() {
		return this.types;
	}

	public void addType(StateType type) {
		this.types.add(type);
	}

	public void removeType(StateType type) {
		if (this.types.contains(type)) {
			this.types.remove(type);
		}
	}

	@Override
	public String toString() {
		return this.id;
	}
	
	@Override
	public State clone() {
		return new State(this);
	}
}
