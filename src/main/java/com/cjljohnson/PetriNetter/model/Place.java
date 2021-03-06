/*
 * The properties related to Place objects.
 * 
 * In the editor these should be treated as immutable so as 
 * not to corrupt the undo history.  There are setters so that 
 * the file codecs can load nets from XML.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.model;

import java.io.Serializable;

public class Place implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 1787401395119188690L;
	
	private int tokens;
	private int capacity;
	private int index;
	private String name;
	
	public Place() {
		
	}
	
	public Place(int tokens, int capacity, int index, String name) {
		this.tokens = tokens;
		this.capacity = capacity;
		this.index = index;
		this.name = name;
	}

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Place clone() {
	    return new Place(tokens, capacity, index, name);
	}
	
	
	
//	public Place(int tokens, int capacity, int index, String name) {
//		
//	}
	
	
}
