package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Identifier implements Comparable<Identifier> {

	private String id = ""; // String value of the identifier
	private int intValue = -1; // By default when it is composed of multiple identifiers

	private List<Identifier> identifiers;

	public Identifier(String id) {
		this.id = id;
		this.identifiers = new ArrayList<Identifier>();

		this.buildIntValue();
	}

	public Identifier(List<Identifier> identifiers) {
		this.identifiers = buildIdentifiers(identifiers, 0);
		Collections.sort(this.identifiers);
		this.id = this.buildID();
		
		if (this.id.length() > 0)
			this.id = this.id.substring(1); // we remove the '.' at the beginning
	}

	private boolean isInteger() {
		try {
			Integer.parseInt(this.id);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

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

			else {
				for (Identifier subID : identifier.getIdentifiers()) {
					possibleID = String.join(".", possibleID, subID.getID());
				}
			}
		}

		return possibleID;
	}

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

	public String getID() {
		return id;
	}

	public int getIntValue() {
		return intValue;
	}

	public List<Identifier> getIdentifiers() {
		return identifiers;
	}

	public void addIdentifier(Identifier id) {
		if (!this.identifiers.contains(id)) {
			this.identifiers.add(id);
			Collections.sort(this.identifiers);
		}
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public int compareTo(Identifier o) {
		return this.intValue - o.getIntValue();
	}
}
