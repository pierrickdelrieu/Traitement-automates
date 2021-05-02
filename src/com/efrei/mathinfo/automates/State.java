package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State implements Cloneable, Comparable<State> {

	private Identifier id; // '1' or 'A'.
	private Map<String, List<State>> links; // keyword gives the state (Map<key, List of state>)
	private List<StateType> types; // Status states: ENTRY, COMMON, EXIT

	public State() {
		this("");
	}

	public State(String id) {
		this.id = new Identifier(id);

		this.types = new ArrayList<StateType>();
		this.types.add(StateType.COMMON);

		this.links = new HashMap<String, List<State>>();
	}

	public State(State state) {
		this.id = new Identifier(state.getIdentifier());

		this.types = new ArrayList<StateType>(state.getTypes());
		this.links = Operations.copyOf(state.getLinks());
	}

	public void setIdentifier(Identifier id) {
		this.id = id;
	}

	public Identifier getIdentifier() {
		return this.id;
	}

	public String getID() {
		return this.id.getID();
	}

	public Map<String, List<State>> getLinks() {
		return this.links;
	}
	
	public void transformTo(State state) {
		this.id = state.getIdentifier();
		this.types = state.getTypes();
		this.links = state.getLinks();
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

	public List<StateType> getTypes() {
		return this.types;
	}

	public void addType(StateType type) {
		if (!this.types.contains(type)) {
			this.types.add(type);
		}
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

		if (this.equals(toMerge)) {
			return;
		}
		
		if (toMerge.getIdentifier().isSubIdOf(this.getIdentifier())) {
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
		
		if (toMerge.isEntry() && !this.isEntry()) {
			this.types.add(StateType.ENTRY);
		}

		this.id = new Identifier(List.of(this.id, toMerge.getIdentifier()));
	}

	@Override
	public String toString() {
		return this.id.getID();
	}

	@Override
	public State clone() {
		return new State(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		State state = (State) o;
		return this.getIdentifier().equals(state.getIdentifier());
	}

	@Override
	public int compareTo(State o) { // When sorting the list, it will sort the list in ascending order
		return this.getIdentifier().compareTo(o.getIdentifier());
	}
}
