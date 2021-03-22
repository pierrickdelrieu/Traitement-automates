package com.efrei.mathinfo.automates;

public class Operations {

    // No instance

    public static void determinize(Automaton automaton) {
    	
    }

    public static boolean isDeterministic(Automaton automaton) {
    	
    	Object[] entries = automaton.getStatesByType(StateType.ENTRY);
    	
    	if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't deterministic
    		return false;
    	}
    	
    	else {
    		for (State state : automaton.getStates()) { // For all the states
    			for (String key : state.getLinks().keySet()) { // We check all the transitions
    				if (state.getLinks().get(key).size() > 1) { // and check if one returns more than one state as its destination
    					return false;
    				}
    			}
    		}
    	}
    	
    	return true;
    }


    public static void complete(Automaton automaton) {

    }

    public static boolean isCompleted(Automaton automaton) {
        return false;
    }

    public static void standardize(Automaton automaton) {

    }

    public static boolean isStandard(Automaton automaton) {
    	
    	// We are unable to convert it to a State, but it does not matter, we just need the string value of the objet, which is the id of the state 
    	Object[] entries = automaton.getStatesByType(StateType.ENTRY);
    	    	
    	if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't standard
    		return false;
    	}
    	
    	else {
    		
    		State entry = (State) entries[0]; // We have only one entry, so we know it is at index 0
    		
			for (State state : automaton.getStates()) { // We check all the states of the automaton
				for (String key : state.getLinks().keySet()) { // With all their transitions
					
					// to see if one state has a transition that brings it back to the entry
					Object[] listOfTransitions = state.getLinks().get(key).stream().filter(destination -> destination.getID().equals(entry.getID())).toArray();
					    					
					if (listOfTransitions.length >= 1) {
						return false; 
					}
				}
			}
    	}
    	
        return true;
    }


    public static void minimize(Automaton automaton) {

    }

    public static boolean isMinimized(Automaton automaton) {
        return false;
    }


    public static Automaton getComplementary(Automaton automaton) {
        return null;
    }

}
