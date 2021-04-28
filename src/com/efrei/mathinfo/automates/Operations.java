package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Operations {

	/**
	 * Checks if an automaton is asynchronous
	 * 
	 * @param automaton the automaton to check
	 * 
	 * @return true if it is async false if not
	 */
	private static boolean isAsync(Automaton automaton) {
		for (State state : automaton.getStates()) {
			for (String key : state.getLinks().keySet()) {
				if (key.equals("*")) { // "" is an epsilon transition, if one state has such transition, the automaton
										// is asynchronous
					System.err.println("Votre automate est asynchrone à cause de la transition " + state + "->" + key
							+ "->" + state.getLinks().get(key));
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Synchronizes an automaton
	 * 
	 * @param automaton the automaton to synchronize
	 * 
	 */
	public static void synchronize(Automaton automaton) {

		System.out.println("\n------ Tentative de synchronisation de l'automate ------\n");

		if (isAsync(automaton)) {

			System.out.println("\n\tTentative d'élimination des transitions epsilons\n");

			// The purpose of this map is to store one state as a key and a value that is
			// its "epsilon cloture"
			// that way we can access it easier when we will be creating the new merged
			// states
			Map<State, List<State>> epsilonStates = new HashMap<State, List<State>>();

			// Empty list that we are going to fill and then put in epsilonStates map
			List<State> epsilonDestinations = new ArrayList<State>();

			// Will contain all the new states we will be creating
			List<State> newStates = new ArrayList<State>();

			for (State state : automaton.getStates()) {
				
				fillEpsilonStates(epsilonDestinations, state);
				Operations.removeDuplicates(epsilonDestinations);
				epsilonStates.put(state, new ArrayList<State>(epsilonDestinations));
				epsilonDestinations.clear();

				state.getLinks().remove("*"); // for each state we remove the epsilon transition
			}

			// creating all the new states, and naming them with a simple name
			System.out.println("\nEpsilon clotures obtenues : \n");
			epsilonStates.forEach((key, value) -> {
				State mergedState = mergeStates(value.toArray(new State[0]));

				newStates.add(mergedState);

				value.clear();
				value.add(mergedState);

				System.out.println(key.getID() + "*" + " == " + mergedState.getID());
				mergedState.setIdentifier(new Identifier(key.getID() + "*"));
			});

			System.out.println();

			for (State state : newStates) {

				for (String key : state.getLinks().keySet()) {
					List<State> destinations = state.getLinks().get(key);
					List<State> copy = new ArrayList<State>(destinations);

					for (State destination : copy) {

						if (epsilonStates.containsKey(destination)) {
							destinations.remove(destination);
							destinations.add(epsilonStates.get(destination).get(0));
						}
					}
				}
			}

			Collections.sort(newStates);
			automaton.changeStates(newStates);
			automaton.getAlphabet().removeWord("*");
			automaton.display();
			System.out.println("\n------ Votre automate est désormais synchrone ------\n");
		}

		else {
			System.err.println("\n------ Votre automate est déjà synchrone ------\n");
		}
	}

	/**
	 * Fills a list with all the states that <strong>state</strong> state can travel
	 * to with epsilon transitions
	 * 
	 * @param result The list to fill
	 * @param state  The starting state
	 */
	private static void fillEpsilonStates(List<State> result, State state) {

		if (!result.contains(state)) {
			result.add(state);
		}

		List<State> destinations = state.getLinks().get("*");

		if (destinations == null) {
			return;
		}

		for (State destination : destinations) {

			if (!result.contains(destination)) {
				fillEpsilonStates(result, destination);
			}
		}
	}

	/**
	 * Determinizes an automaton <br>
	 * 
	 * For example, if one state has 0->a->[1,2], the function will change the
	 * destinations to 0->a[1.2] with <strong>1.2</strong> being the combination of
	 * state 1 and 2. The function then does that for all the states in the
	 * automaton creating a new set of states if necessary
	 * 
	 * @param automaton The automaton to determinize
	 */
	public static void determinize(Automaton automaton) {

		if (!isDeterministic(automaton)) {

			System.out.println("\n------ Tentative de déterminisation de votre automate ------\n");

			if (isAsync(automaton))
				synchronize(automaton); 

			System.out.println("\n------ Début de la déterminisation ------\n");

			State[] entries = automaton.getStatesByType(StateType.ENTRY);
			State newEntry = mergeStates(entries);

			Stack<State> toDetermine = new Stack<State>();
			toDetermine.add(newEntry);

			List<State> newStates = new ArrayList<State>();
			automaton.changeStates(newStates);

			int transitions = 0;

			while (!toDetermine.isEmpty()) {
				State current = toDetermine.pop(); // we retrieve the state to determine

				if (!automaton.getStates().contains(current)) {
					for (String key : current.getLinks().keySet()) {
						transitions++;
						List<State> destinations = current.getLinks().get(key);
						State mergedState = findOrMerge(automaton, destinations.toArray(new State[0]));

						destinations.clear();
						destinations.add(mergedState);

						if (!newStates.contains(mergedState)) {
							toDetermine.add(mergedState);
						}
					}

					automaton.getStates().add(current);
				}
			}

			for (State entry : newStates) {
				if (!entry.equals(newEntry)) {
					entry.removeType(StateType.ENTRY);
				}
			}

			automaton.setNumTransitions(transitions);

			automaton.display();
			System.out.println("\n------ Déterminisation terminée ! ------\n");
		}

		else {
			System.err.println("\n------ Votre automate est déja déterministe ------\n");
		}
	}

	/**
	 * Checks if <strong>automaton</strong> is deterministic or not <br><br>
	 * 
	 * By definition, an automaton is deterministic if it has one entry 
	 * and if all of its states have transitions that lead to one destination <br>
	 * 
	 * For example, if the automaton has a state <strong>0</strong> with
	 * a transition such as <strong>0->a->[1,2]</strong> that would make the 
	 * automaton not deterministic
	 * 
	 * @param automaton The automaton we want to check
	 * @return {@code true} if it is deterministic, {@code false} otherwise
	 * */
	private static boolean isDeterministic(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// deterministic
			System.err.println("Votre automate n'est pas déterministe car il possède " + entries.length + " entrées");
			return false;
		}

		else {
			for (State state : automaton.getStates()) {
				for (String key : state.getLinks().keySet()) {
					if (state.getLinks().get(key).size() > 1) {

						System.err.println("Votre automate n'est pas déterministe à cause de la transition : " + state
								+ "->" + key + "->" + state.getLinks().get(key));
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Completes the <strong>automaton</strong> by filling all of its empty
	 * transitions. <br><br>
	 * 
	 * It creates a new state <strong>P</strong> known as a "bin state" or "garbage state"
	 * which will replace all of the empty transitions of the automaton's states 
	 * 
	 * @param automaton The automaton to complete
	 * */
	public static void complete(Automaton automaton) {

		System.out.println("\n------ Tentative de complétion de votre automate ------\n");

		determinize(automaton);

		if (!isCompleted(automaton)) {
			System.out.println("\n------ Complétion de votre automate ------- \n");

			if (isAsync(automaton)) {
				synchronize(automaton);
			}

			State bin = new State("P");
			
			for (String key : automaton.getAlphabet().getDictionary()) {
				bin.addLink(key, bin);
				automaton.setNumTransitions(automaton.getNumTransitions() + 1);
			}

			for (State state : automaton.getStates()) {
				Set<String> keys = state.getLinks().keySet();

				for (String word : automaton.getAlphabet().getDictionary()) {
					if (!keys.contains(word)) {
						automaton.setNumTransitions(automaton.getNumTransitions() + 1);
						state.addLink(word, bin);
					}
				}
			}

			automaton.getStates().add(bin);
			Collections.sort(automaton.getStates());

			automaton.display();
			System.out.println("\n------ Complétion terminée ! ------\n");
		}

		else {
			System.err.println("------ \nVotre automate est déjà complet ------\n");
		}
	}

	/**
	 * Checks if the <strong>automaton</strong> is complete or not <br><br>
	 * 
	 * @param automaton The automaton to complete
	 * @return {@code true} if complete, {@code false} otherwise
	 * */
	private static boolean isCompleted(Automaton automaton) {

		// we check if the automaton is deterministic, if not -> it isn't completed
		if (!isDeterministic(automaton)) {
			System.err.println("Votre état n'est pas complet car celui-ci n'est pas déterministe");
			return false;
		}

		else {
			for (State state : automaton.getStates()) {

				int transitions = state.getLinks().keySet().size();

				// we check if it has the same number of keys as the number of letter in the
				// alphabet
				if (transitions < automaton.getAlphabet().getDictionary().size()) {
					System.err.println("Votre automate n'est pas complet à cause de l'état " + state);

					if (transitions == 0) {
						System.err.println("En effet celui-ci n'a pas de transitions");
					}

					else {
						System.err.println("En effet celui-ci n'a des transitions que pour "
								+ Arrays.toString(state.getLinks().keySet().toArray()));
					}
					return false;
				}
			}
		}

		return true;
	}

	public static void standardize(Automaton automaton) {
		if (!isStandard(automaton)) {

			synchronize(automaton);

			System.out.println("\n------ Tentative de standardisation de votre automate ------ \n");

			State newEntry = new State("I");
			newEntry.addType(StateType.ENTRY);

			State[] entries = automaton.getStatesByType(StateType.ENTRY);

			for (State entry : entries) {
				for (String key : entry.getLinks().keySet()) {

					List<State> destinations = entry.getLinks().get(key);

					for (State state : destinations) {
						newEntry.addLink(key, state);
					}
				}

				entry.removeType(StateType.ENTRY);
			}

			automaton.getStates().add(newEntry);

			automaton.display();
			System.out.println("------ Fin de la standardisation ------ ! \n");
		}

		else {
			System.err.println("------ Votre automate est déjà standardisé ! ------ \n");
		}
	}

	public static boolean isStandard(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// standard

			System.err.println("Votre automate n'est pas standard car il contient " + entries.length + " entrées");
			return false;
		}

		else {

			State entry = (State) entries[0]; // We have only one entry, so we know it is at index 0

			for (State state : automaton.getStates()) { // We check all the states of the automaton
				for (String key : state.getLinks().keySet()) { // With all their transitions

					// to see if one state has a transition that brings it back to the entry
					Object[] listOfTransitions = state.getLinks().get(key).stream()
							.filter(destination -> destination.getID().equals(entry.getID())).toArray();

					if (listOfTransitions.length >= 1) {
						System.err.println("Votre automate n'est pas standard car " + "l'état " + state
								+ " possède une/des transition(s) revenant vers l'état d'entrée " + entries[0]);
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void minimize(Automaton automaton) {

		System.out.println("------ Tentative de minimisation de votre automate ------\n");

		if (!isCompleted(automaton)) {
			System.err.println("Avant d'être minimisé, votre automate doit être complet, nous le complétons");
			complete(automaton);
		}

		System.out.println("\n------ Début de la minimisation ------\n");

		List<List<State>> finalTheta = getUnmergedLastTheta(automaton);
		List<State> newStates = new ArrayList<State>();

		if (finalTheta.size() == automaton.getStates().size()) {
			System.err.println("Votre automate était déjà minimal");
			System.out.println("------ Fin de la minimisation ------");
			return;
		}

		else {

			int count = 0;

			for (List<State> states : finalTheta) {
				String simplifiedID = String.valueOf((char) ('A' + count));
				State[] statesArr = states.toArray(new State[0]);

				State state = mergeStates(statesArr);

				if (states.size() > 1) {
					System.out.println("L'état " + simplifiedID + " == " + Arrays.toString(statesArr) + "\n");

					state.getIdentifier().setId(simplifiedID);
					System.out.println(state + " " + state.getIdentifier().getIdentifiers());

					for (State st : states) {
						for (String key : st.getLinks().keySet()) {
							for (State des : st.getLinks().get(key)) {
								
								if (st.equals(des))
									des.setIdentifier(state.getIdentifier());
							}
						}
						
						st.setIdentifier(state.getIdentifier());
					}

					count++;
				}

				if (!newStates.contains(state)) {
					newStates.add(state);
				}

			}

			int numTransitions = 0;

			Collections.sort(newStates);
			automaton.changeStates(newStates);

			for (State state : newStates) {
				for (String key : state.getLinks().keySet()) {

					numTransitions++;

					List<State> destinations = state.getLinks().get(key);
					List<State> nd = Operations.removeDuplicates(destinations);

					destinations.clear();
					destinations.addAll(nd);
				}
			}

			automaton.setNumTransitions(numTransitions);
			automaton.display();
			System.out.println("------ Fin de la minimisation ------");
		}
	}

	private static List<List<State>> getUnmergedLastTheta(Automaton automaton) {

		System.out.println("\n------ Récupération de la partition finale ------\n");

		List<List<State>> thetaCurrent = new ArrayList<List<State>>();
		List<List<State>> thetaLast = new ArrayList<List<State>>();
		List<State> part1 = new ArrayList<State>(List.of(automaton.getStatesByType(StateType.EXIT)));
		List<State> part2 = new ArrayList<State>(List.of(automaton.getAllStatesButType(StateType.EXIT)));

		Collections.sort(part1);
		Collections.sort(part2);

		// Adding the first two partitions to our theta
		if (!part1.isEmpty()) {
			thetaCurrent.add(part1);
		}

		if (!part2.isEmpty()) {
			thetaCurrent.add(part2);
		}

		int count = 0;

		do {
			System.out.println("theta (" + count + "): " + Arrays.toString(thetaCurrent.toArray()));
			thetaLast.clear();
			thetaLast.addAll(thetaCurrent);

			thetaCurrent.clear();

			for (List<State> part : thetaLast) {

				if (part.size() > 1) {

					List<List<State>> subTheta = new ArrayList<List<State>>();

					part.forEach(state -> subTheta.add(new ArrayList<State>(List.of(state))));

					for (int i = 0; i < subTheta.size(); i++) {

						List<State> state1 = subTheta.get(i);

						for (int j = 0; j < subTheta.size(); j++) {

							if (i == j)
								continue;

							List<State> state2 = subTheta.get(j);

							boolean v = compareDestinations(state1.get(0), state2.get(0), thetaLast);

							if (v) {
								subTheta.remove(state1);
								subTheta.remove(state2);
								state1 = Operations.mergeLists(state1, state2);
								j--; // come back one step because we removed one state

								Collections.sort(state1);
							}
						}

						if (!subTheta.contains(state1)) {
							subTheta.add(state1);
						}
					}

					// Adding all the partitions we made in subTheta in thetaCurrent
					subTheta.forEach(partition -> {
						thetaCurrent.add(partition);
					});
				}

				else {
					thetaCurrent.add(part);
				}
			}

			count++;

		} while (thetaCurrent.size() != thetaLast.size());

		System.out.println("theta (final): " + Arrays.toString(thetaCurrent.toArray()));

		System.out.println("\n----- Fin de la récupération ------\n");
		return thetaCurrent;
	}

	private static boolean compareDestinations(State s1, State s2, List<List<State>> theta) {
		boolean same = true;
		int check = s1.getLinks().size();

		for (String key : s1.getLinks().keySet()) {
			List<State> dStates1 = s1.getLinks().get(key);
			List<State> dStates2 = s2.getLinks().get(key);

			State dState1 = dStates1.get(0);
			State dState2 = dStates2.get(0);

			for (List<State> partition : theta) {

				boolean foundInPartition1 = foundInPartition(dState1, partition);
				boolean foundInPartition2 = foundInPartition(dState2, partition);

				if (foundInPartition1 && foundInPartition2) {
					same = (same && true);
					check--;
				}

				else {
					same = false;
				}
			}

			if (check == 0) {
				return true;
			}
		}

		return same;
	}

	private static boolean foundInPartition(State d1, List<State> partition) {

		for (State state : partition) {
			if (state.getIdentifier().equals(d1.getIdentifier())) {
				return true;
			}
		}
		return false;
	}

	public static Automaton getComplementary(Automaton automaton) {

		Automaton complementary = automaton.clone();

		System.out.println("\n------ Création de l'automate complémentaire à votre automate ------\n");

		for (State state : complementary.getStates()) {
			if (!state.getType().contains(StateType.EXIT)) {
				state.addType(StateType.EXIT);
			}

			else {
				state.removeType(StateType.EXIT);
			}
		}

		System.out.println(Arrays.toString(complementary.getStatesByType(StateType.EXIT)));

		complementary.display();
		System.out.println("\n------ Automate complémentaire terminée ! ------\n");

		return complementary;
	}

	public static State mergeStates(State... states) {

		if (states.length == 0) {
			return null;
		}

		else if (states.length == 1) {
			return states[0];
		}

		State current = states[0].clone();

		for (int i = 1; i < states.length; i++) {
			State next = states[i].clone();

			if (next.equals(current)) {
				continue;
			}

			current.mergeWith(next);
		}

		return current;
	}

	protected static State findOrMerge(Automaton automaton, State... states) {

		String possibleID = makeID(states);
		State possibleState = automaton.getByID(possibleID);

		// System.out.println("Possible : " + possibleID);

		if (possibleState != null) {
			return possibleState;
		}

		else {
			return mergeStates(states);
		}
	}

	protected static <T> List<T> removeDuplicates(List<T> list) {
		List<T> noDuplicates = new ArrayList<T>();

		for (T t : list) {
			if (!noDuplicates.contains(t)) {
				noDuplicates.add(t);
			}
		}

		return noDuplicates;
	}

	protected static <T> List<T> mergeLists(List<T> list1, List<T> list2) {

		List<T> newList = new ArrayList<T>();

		newList.addAll(list1);

		for (T t : list2) {
			if (!newList.contains(t)) {
				newList.add(t);
			}
		}

		return newList;
	}

	protected static Map<String, List<State>> mergeMaps(Map<String, List<State>> map1, Map<String, List<State>> map2) {

		Map<String, List<State>> newMap = new HashMap<String, List<State>>();

		if (map1.isEmpty() && !map2.isEmpty()) {
			newMap.putAll(map2);
		}

		else if (map2.isEmpty()) {
			newMap.putAll(map1);
		}

		else if (!map1.isEmpty() && !map2.isEmpty()) {
			newMap.putAll(map1);

			for (String key : map2.keySet()) {

				if (newMap.containsKey(key)) {
					List<State> newList = new ArrayList<State>();
					newList.addAll(mergeLists(newMap.get(key), map2.get(key)));

					removeDuplicates(newList);

					newMap.get(key).clear();
					newMap.get(key).addAll(newList);
				}

				else {
					newMap.put(key, map2.get(key));
				}
			}
		}

		return newMap;
	}

	protected static Map<String, List<State>> copyOf(Map<String, List<State>> map) {
		Map<String, List<State>> newMap = new HashMap<String, List<State>>();

		map.forEach((key, value) -> {
			newMap.put(key, new ArrayList<State>(value));
		});

		return newMap;
	}

	protected static String makeID(State... states) {
		List<Identifier> statesIds = new ArrayList<Identifier>();

		for (State state : states) {

			if (state.getIdentifier().getIdentifiers().isEmpty()) {
				if (!statesIds.contains(state.getIdentifier())) {
					statesIds.add(state.getIdentifier());
				}
			}

			else {
				statesIds = mergeLists(statesIds, state.getIdentifier().getIdentifiers());
			}
		}

		return new Identifier(statesIds).getID();
	}

}
