package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Automaton implements Cloneable {

	// Attributes
	private List<State> states;
	private Alphabet alphabet;
	private int numTransitions;
	private String filePath;

	// Constructor
	public Automaton() {
		this.states = new ArrayList<State>();
		this.alphabet = null;
	}

	public Automaton(List<State> states) {
		this.states = new ArrayList<State>(states);
	}

	public String getFilePath() {
		return this.filePath;
	}

	// Read and write accessors
	public List<State> getStates() {
		return this.states;
	}

	public void changeStates(List<State> newStates) {
		this.states = newStates;
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
	 * 
	 * @param id Automaton identifier (example = 1 or A)
	 * @return true if the state is already present in the automaton false if the
	 *         state is not present in the automaton
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
	 * 
	 * @param id State identifier
	 * @return the state with the id null if the state is not present in the
	 *         automaton
	 */
	public State getByID(String id) {

		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return state;
			}
		}

		return null; // if the state is not present in the automaton
	}

	public State[] getStatesByType(StateType type) {

		List<State> filteredList = this.states.stream().filter(state -> state.getType().contains(type))
				.collect(Collectors.toList());
		State[] filteredArray = new State[] {};

		filteredArray = filteredList.toArray(filteredArray);

		return filteredArray;
	}

	public Automaton clone() {

		List<State> states = new ArrayList<State>();
		this.states.stream().forEach(state -> states.add(state.clone()));

		Alphabet alphabet = new Alphabet(this.alphabet.getDictionary());
		int transitions = Integer.valueOf(this.numTransitions);

		Automaton clone = new Automaton(states);
		clone.setAlphabet(alphabet);
		clone.setNumTransitions(transitions);

		return clone;
	}

	public void display() {
		System.out.println(this);
	}

	public boolean recognizesWord(String word) {

		State[] entries = this.getStatesByType(StateType.ENTRY);

		for (State entry : entries) {
			
			System.out.println("Checking validity for " + entry);

			if (recognizesWordFromState(word, 0, entry)) {
				return true;
			}
		}

		return false;
	}

	private boolean recognizesWordFromState(String word, int index, State current) {
		
		String letter = String.valueOf(word.charAt(index));
		
		if (!this.alphabet.getDictionary().contains(letter)) {
			return false;
		}

		else if (index == word.length()) {
			System.out.println(current + " last state");
			return current.isExit();
		}
		
		else {
			List<State> destinations = current.getLinks().get(letter);
			
			if (destinations == null) {
				return false;
			}
			
			int i = Integer.valueOf(index + 1);
			
			for (State destination : destinations){

				System.out.println("index : " + i + "/" + (word.length()-1));
				//System.out.println(current + " (" + letter + ")" + " going to " + destination);
				
				if(this.recognizesWordFromState(word, i, destination)) {
					return true;
				}
				
				else {
					//System.out.println("C'est pas bon " + current + " (" + letter + ") " + destination);
				}
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		String result = "";

		// Alphabet display
		result = this.alphabet.getDictionary().size() + " mots : "
				+ Arrays.toString(this.alphabet.getDictionary().toArray()) + "\n";
		/* toArray() permet de convertir une liste en array */

		// States display
		result += this.states.size() + " états : " + Arrays.toString(this.states.toArray()) + "\n";

		// Inputs displays
		State[] entries = this.getStatesByType(StateType.ENTRY);
		/*
		 * stream : allows you to use the filter method filter returns a list containing
		 * the filter conditions (<type> -> <filter conditions>)
		 */
		result += entries.length + " entrées : " + Arrays.toString(entries) + "\n";

		// Output displays
		State[] exits = this.getStatesByType(StateType.EXIT);
		result += exits.length + " sorties : " + Arrays.toString(exits) + "\n";

		// Display of transitions
		result += this.numTransitions + " transitions : \n";

		for (State state : this.states) { // for each state among all states

			Set<String> allKeys = state.getLinks().keySet();

			for (String key : this.alphabet.getDictionary()) { // for each key among all keys
				if (!allKeys.contains(key)) {
					result += state.getID() + "->" + key + "->[x] \n";
				}

				else {
					result += state.getID() + "->" + key + "->" + Arrays.toString(state.getLinks().get(key).toArray())
							+ "\n";
				}
			}
		}

		return result;
	}
}
