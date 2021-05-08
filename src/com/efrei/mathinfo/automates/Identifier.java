package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Identifier implements Comparable<Identifier> {

	private String id = ""; // String value of the identifier
	private int intValue = -1; // By default 

	private List<Identifier> identifiers;

	/**
	* Constructor<br>
	* The identifier is made up of itself
	* @param id Automaton identifier
	*/
	public Identifier(String id) {
		this.id = id;
		this.identifiers = new ArrayList<Identifier>();

		this.buildIntValue();
	}
	
	/**
	* Copy constructor
	* @param identifier Identifier to copy
	*/
	public Identifier(Identifier identifier) {
		this.id = identifier.getID();
		this.identifiers = new ArrayList<Identifier>(identifier.getIdentifiers());
		
		this.buildIntValue();
	}

	/**
	* Copy constructor for different identifier
	* @param identifiers The different identifiers to merge
	*/
	public Identifier(List<Identifier> identifiers) {
		this.identifiers = buildIdentifiers(identifiers, 0);
		Collections.sort(this.identifiers);
		this.id = this.buildID();
		
		if (this.id.length() > 0)
			this.id = this.id.substring(1); // we remove the '.' at the beginning
		
		this.buildIntValue();
	}
	
	/**
	* The method checks if a element is a integer
	* @return {@code true} if integer {@code false} if not
	*/
	private boolean isInteger() {
		try {
			Integer.parseInt(this.id);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	* Method to create the ID that is the name of a state. </br>
	* Merge IDs contain in identifier by a "."
	* @return String
	*/
	private String buildID() {
		if (this.id != "") {
			System.err.println("La création d'un nouvel identifiant n'est pas nécessaire");
			return this.id;
		}

		String possibleID = "";

		for (Identifier identifier : this.identifiers) {
			if (identifier.getIdentifiers().size() == 0) {
				possibleID = String.join(".", possibleID, identifier.getID());
			}
		}

		return possibleID;
	}
	/**
	* Method that builds a list of identifier, that will be contained in an this object.
	* @param ids list of automaton identifer
	* @param index int value to move easily into ids
	* @return List<Identifier>
	*/
	public static List<Identifier> buildIdentifiers(List<Identifier> ids, int index) {
		List<Identifier> subIds = new ArrayList<Identifier>();

		if (index < ids.size()) {

			if (ids.get(index).getIdentifiers().isEmpty()) {
				subIds.add(ids.get(index));
				return Operations.mergeLists(subIds, buildIdentifiers(ids, index + 1));
			}

			else {
				subIds = Operations.mergeLists(subIds, buildIdentifiers(ids.get(index).getIdentifiers(), 0));
				return Operations.mergeLists(subIds, buildIdentifiers(ids, index + 1));
			}
		}
		
		return subIds;
	}

	/**
	* This method changes the intValue of an Identifier Object. </br>
	* If the id is composed of characters, will turn them into int.
	*/
	private void buildIntValue() {
		if (isInteger()) {
			this.intValue = Integer.parseInt(this.id);
		}

		else {
			this.id.chars().forEach(c -> {
				this.intValue += c;
			});
		}
	}

	/**
	* This method returns the id of a Identifier object.
	* @return String
	*/
	public String getID() {
		return id;
	}
	
	/**
	* Change the id of an Identifier object
	* @param id String that will be used to create the new ID of the Identifier object
	*/
	public void setId(String id) {
		this.id = id;
		this.buildIntValue();
	}

	/**
	* Return the value of the Identifier object
	* @return int 
	*/
	public int getIntValue() {
		return intValue;
	}

	/**
	* Return the list of Identifiers contained by a Identifier object.
	* @return List of Identifier
	*/
	public List<Identifier> getIdentifiers() {
		return identifiers;
	}
	
	/**
	* Method that checks if an Identifier is contained by another one.
	* @return {@code true} if the id is in Identifiers </br>
	*		  {@code false} if the id is not in Identifiers
	*/
	public boolean isSubIdOf(Identifier id) {
		return id.getIdentifiers().contains(this);
	}
	/**
	* toString method
	* @return String id, the id of the Identifier object. 
	*/
	@Override
	public String toString() {
		return this.id;
	}

	/**
	* Method that compare two Identifier objects.
	* @return int difference, the difference between of the two IDs. 
	*/
	@Override
	public int compareTo(Identifier o) {
		return this.intValue - o.getIntValue();
	}
	
	/**
	* Method that compare if two Identifier objects are the same.
	* @return {@code true} if the two are equals. </br>
	* 		  {@code false} if not.
	*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Identifier identifier = (Identifier) o;
        return this.getID().equals(identifier.getID());
    }
}
