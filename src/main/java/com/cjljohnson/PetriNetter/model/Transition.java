/*
 * The properties related to Transition objects.
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

public class Transition implements Cloneable, Serializable {

	private static final long serialVersionUID = 4807890452104246723L;
	
	private int index;
	private String name;
	
	public Transition() {
		
	}
	
	public Transition(int index, String name) {
		this.index = index;
		this.name = name;
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
	
}
