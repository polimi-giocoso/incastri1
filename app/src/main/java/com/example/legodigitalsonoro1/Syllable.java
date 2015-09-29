package com.example.legodigitalsonoro1;

/**
 * 
 * @author chiara
 *
 */
public class Syllable {
	
	private static final String first="_sx";
	private static final String second="_dx";
	
	private String myword;
	private String mySyll;
	protected String image;
	
	private boolean isFirstSyll=false;
	
	public Syllable(String word, String syll, boolean isfirst) {
		myword=word;
		setMySyll(syll);
		if(isfirst){
			setImage(myword+first);
			setFirstSyll(true);
		}else{
			setImage(myword+second);
		}
	}
	
	public String getWord(){
		return myword;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the firstSyll
	 */
	public boolean isFirstSyll() {
		return isFirstSyll;
	}

	/**
	 * @param firstSyll the firstSyll to set
	 */
	public void setFirstSyll(boolean firstSyll) {
		this.isFirstSyll = firstSyll;
	}
	
	public boolean isEmptySyll(){
		return false;
	}

	/**
	 * @return the mySyll
	 */
	public String getMySyll() {
		return mySyll;
	}

	/**
	 * @param mySyll the mySyll to set
	 */
	public void setMySyll(String mySyll) {
		this.mySyll = mySyll;
	}

}
