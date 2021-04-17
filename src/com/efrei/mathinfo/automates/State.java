package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State implements Cloneable, Comparable<State> {

	// Instances
	private String id; // '1' or 'A'.
	private Map<String, List<State>> links; // keyword gives the state (Map<key, List of state>)
	private List<StateType> types; // Status states: ENTRY, COMMON, EXIT

	public State() {
		this("");
	}

	public State(String id) {
		
		this.id = id;

		this.types = new ArrayList<StateType>();
		this.types.add(StateType.COMMON);
		
		this.links = new HashMap<String, List<State>>();
	}
	
	public State(State... states) {
		this.id = Operations.makeID(states);
		
		List<StateType> temp = new ArrayList<StateType>();
		Map<String, List<State>> tempLinks = new HashMap<String, List<State>>();
		
		for (State state : states) {
			temp = Operations.mergeLists(temp, state.getType());
			tempLinks = Operations.mergeMaps(tempLinks, state.getLinks());
		}
		
		this.types = temp;
		this.links = tempLinks;
	}

	private State(State state) {
		this.id = state.getID();
		
		this.types = new ArrayList<StateType>(state.getType());
		this.links = new HashMap<String, List<State>>(state.getLinks());
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
			if (!this.links.get(what).contains(where)) {
				this.links.get(what).add(where);
				Collections.sort(this.links.get(what));
			}
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
	
	public boolean isEntry() {
		return this.types.contains(StateType.ENTRY);
	}
	
	public boolean isExit() {
		return this.types.contains(StateType.EXIT);
	}
	
	public void mergeWith(State toMerge) {
		
		if (this == toMerge) {
			return;
		}
		
		else if (this.getID().contains(toMerge.getID()) || toMerge.getID().contains(this.id)) {
			return;
		}
						
		if (!this.links.isEmpty() && !toMerge.getLinks().isEmpty()) {
			this.links.putAll(Operations.mergeMaps(this.links, toMerge.getLinks())); // we merge the destinations and keys
		}
		
		else if (this.links.isEmpty() && !toMerge.getLinks().isEmpty()) {
			this.links.putAll(toMerge.getLinks());
		}
		
		if (toMerge.isExit() && !this.isExit()) {
			this.types.add(StateType.EXIT);
		}
		
		this.setID(Operations.removeDuplicates(String.join("", this.id, toMerge.getID())));
	}

	@Override
	public String toString() {
		return this.id;
	}
	
	@Override
	public State clone() {
		return new State(this);
	}

	@Override
	public int compareTo(State o) { // When sorting the list, it will sort the list in ascending order
		
		try {
			return Integer.valueOf(this.getID()) - Integer.valueOf(o.getID());
		} catch (NumberFormatException e) {
			return this.getID().charAt(0) - o.getID().charAt(0);
		}
	}
}
