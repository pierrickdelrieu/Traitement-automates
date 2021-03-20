package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	
	private String id; // '1' or 'A'.
	
	private Map<String, List<State>> links; // keyword gives the state 

	private List<StateType> types;
	
	public State(String id) {
		
		this.id = id;
		this.types = new ArrayList<StateType>();
		
		links = new HashMap<String, List<State>>();
	}
	
	public String getID() {
		return this.id;
	}
	
	public Map<String, List<State>> getLinks() {
		return this.links;
	}
	
	public void addLink(String what, State where) {
		if (this.links.containsKey(what)) {
			this.links.get(what).add(where);
		}
		
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
	
	@Override
	public String toString() {
		return this.id;
	}
}
