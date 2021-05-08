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

	/**
	* Constructor<br>
	* Create a state with its identifier and initialize the other attributes to 0 or null
	* Initialize as a common state (neither input nor output)
	* @param id Identifiers of the state
	*/
	public State(String id) {
		this.id = new Identifier(id);

		this.types = new ArrayList<StateType>();
		this.types.add(StateType.COMMON);

		this.links = new HashMap<String, List<State>>();
	}

	/**
	* Copy constructor<br>
	* No reference of the copied state is re-used
	* @param state State to copy
	*/
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

	/**
	* Create a new transition from one state to another.
	* @param what is the character that the transition will need to be readed
	* @param where is the state this transition will lead to
	**/
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

	/**
	* Add a type to the current state such as Entry type or Exit type
	* @param type that will added to the state
	**/
	public void addType(StateType type) {
		if (!this.types.contains(type)) {
			this.types.add(type);
		}
	}

	/**
	* Remove a type from the current state such as Entry type or Exit type
	* @param type that will disapeer from the state
	**/
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

	/**
	* Function that allow to merge to two states (including all their links)
	* @param toMerge is the state you'll merge with your current state
	**/
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
