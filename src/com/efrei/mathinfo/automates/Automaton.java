package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Automaton {

	// Attributes
	private List<State> states;
	private Alphabet alphabet;
	private int numTransitions;


	// Constructor
	public Automaton() {
		this.states = new ArrayList<State>();
		this.alphabet = null;
	}
	
	public Automaton(List<State> states) {
		this.states = List.copyOf(states);
	}


	// Read and write accessors
	public List<State> getStates() {
		return this.states;
	}
	
	public void setAlphabet(Alphabet alphabet) {
		this.alphabet = alphabet;
	}
	
	public Alphabet getAlphabet() {
		return this.alphabet;
	}
	
	public void setNumTransitions(int numTransitions) {
		this.numTransitions = Integer.valueOf(numTransitions);
	}
	
	public int getNumTransitions() {
		return this.numTransitions;
	}


	/**
	 * The method checks if a state is already present in the PLC.
	 * @param id Automaton identifier (example = 1 or A)
	 * @return true if the state is already present in the automaton
	 * 		   false if the state is not present in the automaton
	 */
	public boolean containsStateID(String id) {
		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return true;
			}
		}
		
		return false;
	}


	/**
	 * The method allows to retrieve a state from its identifier
	 * @param id State identifier
	 * @return the state with the id
	 * 		   null if the state is not present in the automaton
	 */
	public State getByID(String id) {
		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return state;
			}
		}
		
		return null; // if the state is not present in the automaton
	}


	@Override
	public String toString() {
		String result = "";

		// Alphabet display
		result = this.alphabet.getDictionary().size() + " mots : " + Arrays.toString(this.alphabet.getDictionary().toArray()) + "\n";
		/* toArray() permet de convertir une liste en array*/

		// States display
		result += this.states.size() + " états : " + Arrays.toString(this.states.toArray()) + "\n";
		
		// Inputs displays
		Object[] entries = this.states.stream().filter(state -> state.getType().contains(StateType.ENTRY)).toArray();
		/*stream : allows you to use the filter method
		* filter returns a list containing the filter conditions (<type> -> <filter conditions>)*/
		result += entries.length + " entrées : " + Arrays.toString(entries) + "\n";

		// Output displays
		Object[] exits = this.states.stream().filter(state -> state.getType().contains(StateType.EXIT)).toArray();
		result += exits.length + " sorties : " + Arrays.toString(exits) + "\n";

		// Display of transitions
		result += this.numTransitions + " transitions : \n";
		
		for (State state : this.states) { // for each state among all states
			for (String key : state.getLinks().keySet()) { // for each key among all keys
				result += state.getID() + "->" + key + "->" + Arrays.toString(state.getLinks().get(key).toArray()) + "\n";
			}
		}
		/*use of 'for each'*/
		
		
		
		return result;
	}
}
