package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Automate {

	private List<State> states;
	private Language language;
	private int numTransitions;
		
	public Automate() {
		this.states = new ArrayList<State>();
		this.language = null;
	}
	
	public Automate(List<State> states) {
		this.states = List.copyOf(states);
	}
	
	public List<State> getStates() {
		return this.states;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public void setNumTransitions(int numTransitions) {
		this.numTransitions = Integer.valueOf(numTransitions);
	}
	
	public int getNumTransitions() {
		return this.numTransitions;
	}
	
	public boolean containsStateID(String id) {
		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	public State getByID(String id) {
		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return state;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result = this.language.getDictionary().size() + " mots : " + Arrays.toString(this.language.getDictionary().toArray()) + "\n";
		result += this.states.size() + " états : " + Arrays.toString(this.states.toArray()) + "\n";
		
		
		Object[] entries = this.states.stream().filter(state -> state.getType().contains(StateType.ENTRY)).toArray();
		Object[] exits = this.states.stream().filter(state -> state.getType().contains(StateType.EXIT)).toArray();
		
		result += entries.length + " entrées : " + Arrays.toString(entries) + "\n";
		result += exits.length + " sorties : " + Arrays.toString(exits) + "\n";
		
		result += this.numTransitions + " transitions : \n";
		
		for (State state : this.states) {
			for (String key : state.getLinks().keySet()) {
				result += state.getID() + "->" + key + "->" + Arrays.toString(state.getLinks().get(key).toArray()) + "\n";
			}
		}
		
		
		
		return result;
	}
}
