package com.example.legodigitalsonoro1;

/**
 * 
 * @author chiara
 *
 */
public abstract class AbstractWord {
	
	protected String myword;
	protected String image;
	
	/**
	 * @return the myword
	 */
	public String getMyword() {
		return myword;
	}

	/**
	 * @param myword the myword to set
	 */
	public abstract void setMyword(String myword);
	
	public abstract boolean isEmptyWord();
	
	public abstract String getImage();
	
}
