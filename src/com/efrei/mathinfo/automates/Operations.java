package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
		if (isAsync(automaton)) {

			Map<State, List<State>> epsilonStates = new HashMap<State, List<State>>();
			List<State> epsilonDestinations = new ArrayList<State>();
			List<State> newStates = new ArrayList<State>();

			for (State state : automaton.getStates()) {
				fillEpsilonStates(epsilonDestinations, state);
				epsilonStates.put(state, new ArrayList<State>(epsilonDestinations));
				epsilonDestinations.clear();

				state.getLinks().remove("*");
			}

			epsilonStates.forEach((key, value) -> {
				//System.out.println(key + "->" + Arrays.toString(value.toArray()));
				State mergedState = mergeStates(value.toArray(new State[0]));

				newStates.add(mergedState);
				value.clear();
				value.add(mergedState);
				System.out.println(key.getID() + "*" + " == " + mergedState.getID());
				mergedState.getIdentifier().setId(key.getID() + "*");
			});

			for (State state : newStates) {
				for (String key : state.getLinks().keySet()) {
					List<State> destinations = state.getLinks().get(key);
					List<State> copy = new ArrayList<State>(destinations);
					
					for (State destination : copy) {
						destinations.add(epsilonStates.get(destination).get(0));
						destinations.remove(destination);
					}
				}
			}

			//newStates.forEach(s -> System.out.println(s));
			Collections.sort(newStates);
			automaton.changeStates(newStates);
			automaton.getAlphabet().removeWord("*");
			automaton.display();
		}

		else {
			System.err.println("Votre automate est déjà synchrone");
		}
	}

	public static void fillEpsilonStates(List<State> result, State state) {

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

	public static void determinize(Automaton automaton) {

		if (!isDeterministic(automaton)) {

			System.out.println("\nDéterminisation de votre automate \n");

			synchronize(automaton); // Only does it if it's asynchronous

			State[] entries = automaton.getStatesByType(StateType.ENTRY);
			State newEntry = mergeStates(entries);

			Stack<State> toDetermine = new Stack<State>();
			toDetermine.add(newEntry);

			List<State> newStates = new ArrayList<State>();

			int transitions = 0;

			while (!toDetermine.isEmpty()) {
				State current = toDetermine.pop(); // we retrieve the state to determine

				if (newStates.stream().noneMatch(s -> s.getID().equals(current.getID()))) {
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

					newStates.add(current);
				}
			}

			automaton.setNumTransitions(transitions);

			// Remove all the other entries but not the first one (which is the only one we
			// need)
			for (State entry : newStates) {
				if (entry != newStates.get(0)) {
					entry.removeType(StateType.ENTRY);
				}
			}

			automaton.setNumTransitions(transitions);
			automaton.changeStates(newStates);

			automaton.display();
			System.out.println("Déterminisation terminée !");
		}

		else {
			System.err.println("\nVotre automate est déja déterministe");
		}
	}

	private static boolean isDeterministic(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// deterministic
			return false;
		}

		else {
			for (State state : automaton.getStates()) { // For all the states
				for (String key : state.getLinks().keySet()) { // We check all the transitions
					if (state.getLinks().get(key).size() > 1) { // and check if one returns more than one state as its
																// destination
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void complete(Automaton automaton) {
		determinize(automaton);
		
		if (!isCompleted(automaton)) {
			System.out.println("\nComplétion de votre automate \n");

			if (isAsync(automaton)) {
				synchronize(automaton);
			}

			State bin = new State("P"); // we create the 'bin' state
			for (String key : automaton.getAlphabet().getDictionary()) {
				bin.addLink(key, bin); // we add as many loops on 'bin' as there are letters in the alphabet
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

			automaton.display();
			System.out.println("Complétion terminée ! ");
		}

		else {
			System.err.println("\nVotre automate est déjà complet");
		}
	}

	private static boolean isCompleted(Automaton automaton) {

		// we check if the automaton is deterministic, if not -> it isn't completed
		if (!isDeterministic(automaton)) {
			return false;
		}

		else {
			for (State state : automaton.getStates()) {

				int transitions = state.getLinks().keySet().size();

				// we check if it has the same number of keys as the number of letter in the
				// alphabet
				if (transitions < automaton.getAlphabet().getDictionary().size()) {
					return false;
				}
			}
		}

		return true;
	}

	public static void standardize(Automaton automaton) {
		if (!isStandard(automaton)) {

			synchronize(automaton);

			System.out.println("\nStandardisation de votre automate : \n");

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
			System.out.println("Votre automate est désormais standardisé ! \n");
		}

		else {
			System.err.println("Votre automate est déjà standardisé ! \n");
		}
	}

	private static boolean isStandard(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// standard
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
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void minimize(Automaton automaton) {

		if (!isCompleted(automaton)) {
			System.err.println("Votre automate doit être complet pour être minimisé, nous le complétons");
			complete(automaton);
		}

		List<List<State>> finalTheta = getUnmergedLastTheta(automaton);

		List<State> newStates = new ArrayList<State>();

		if (finalTheta.size() == automaton.getStates().size()) {
			System.err.println("Votre automate était déjà minimal");
		}

		else {

			int count = 0;

			// TODO : fixer le problème des transitions des transitions qui ne changent pas
			// TODO : fixer les problèmes, tout court.

			for (List<State> states : finalTheta) {
				String simplifiedID = String.valueOf((char) ('A' + count));
				State[] statesArr = states.toArray(new State[0]);
				State state = mergeStates(states.toArray(new State[0]));

				if (states.size() > 1) {
					System.out.println(simplifiedID + " == " + Arrays.toString(statesArr));
					state.getIdentifier().setId(simplifiedID);
					states.forEach(st -> st.setIdentifier(new Identifier(simplifiedID)));
					count++;
				}

				if (!newStates.contains(state)) {
					newStates.add(state);
				}

			}

			automaton.changeStates(newStates);
			System.out.println("Minimisation terminée");
			automaton.display();
		}
	}

	private static List<List<State>> getUnmergedLastTheta(Automaton automaton) {

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

		// System.out.println(Arrays.toString(thetaCurrent.toArray()));

		int count = 0;

		do {
			System.out.println("theta (" + count + "): " + Arrays.toString(thetaCurrent.toArray()));
			thetaLast.clear();
			;
			thetaLast.addAll(thetaCurrent);
			// System.out.println("last : " + Arrays.toString(thetaLast.toArray()));
			thetaCurrent.clear();

			for (List<State> part : thetaLast) {

				// System.out.println("part : " + Arrays.toString(part.toArray()));

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
								j--;

								Collections.sort(state1);
							}
						}

						if (!subTheta.contains(state1)) {
							subTheta.add(state1);
						}
					}

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

		return thetaCurrent;
	}

	private static boolean compareDestinations(State s1, State s2, List<List<State>> theta) {
		boolean same = true;
		int check = s1.getLinks().size() - 1;

		for (String key : s1.getLinks().keySet()) {
			List<State> dStates1 = s1.getLinks().get(key);
			List<State> dStates2 = s2.getLinks().get(key);

			// because the automaton is deterministic and complete
			State dState1 = dStates1.get(0);
			State dState2 = dStates2.get(0);

			if (check == 0) {
				return true;
			}

			for (List<State> partition : theta) {
				// System.out.println(Arrays.toString(partition.toArray()));
				boolean foundInPartition1 = foundInPartition(dState1, partition);
				boolean foundInPartition2 = foundInPartition(dState2, partition);

				// System.out.println(s1 + ":" + foundInPartition1 + " && " + s2 + ":" +
				// foundInPartition2);

				if (foundInPartition1 && foundInPartition2) {
					same = (same && true);
					check--;
				}

				else {
					same = false;
				}
			}
		}

		return same;
	}

	private static boolean foundInPartition(State d1, List<State> partition) {

		for (State state : partition) {
			if (state.getID().equals(d1.getID())) {
				return true;
			}
		}
		return false;
	}

	public static Automaton getComplementary(Automaton automaton) {

		Automaton complementary = automaton.clone();

		System.out.println("Création de l'automate complémentaire à votre automate \n");

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
		System.out.println("Création de l'automate complémentaire terminée ! \n");

		return complementary;
	}

	protected static State mergeStates(State... states) {

		if (states.length == 0) {
			return null;
		}

		else if (states.length == 1) {
			return states[0];
		}

		State current = new State(states[0]);

		for (int i = 1; i < states.length; i++) {
			State next = states[i];

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

		if (possibleState != null) {
			return possibleState;
		}

		else {
			return mergeStates(states);
		}
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
			map2.forEach((key, destinationStates) -> {
				newMap.merge(key, destinationStates, (v1, v2) -> mergeLists(v1, v2));
			});
		}

		return newMap;
	}

	protected static String makeID(State... states) {
		List<Identifier> statesIds = new ArrayList<Identifier>();

		for (State state : states) {
			statesIds = mergeLists(statesIds, state.getIdentifier().getIdentifiers());
		}

		return new Identifier(statesIds).getID();
	}

	private void fixDestinations(Automaton automaton) {

		for (State state : automaton.getStates()) {

			for (String key : state.getLinks().keySet()) {

				List<State> destinations = state.getLinks().get(key);

				for (State destination : destinations) {

				}
			}
		}
	}
}
