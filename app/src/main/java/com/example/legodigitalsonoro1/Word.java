package com.example.legodigitalsonoro1;

import java.util.StringTokenizer;

/**
 * 
 * @author chiara
 *
 */
public class Word extends AbstractWord{

	private Syllable first;
	private Syllable second;
	private String eng;
	
	public Word(String line) {
		
		StringTokenizer tokenizer=new StringTokenizer(line," ");
		
		String word=tokenizer.nextToken();
		String syll1=tokenizer.nextToken();
		String syll2=tokenizer.nextToken();
		setEng(tokenizer.nextToken(";"));
		
		setMyword(word);
		setImage(word);
		setFirst(new Syllable(word, syll1, true));
		setSecond(new Syllable(word, syll2,false));
		
	}

	/**
	 * @return the first
	 */
	public Syllable getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(Syllable first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public Syllable getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(Syllable second) {
		this.second = second;
	}

	/**
	 * @return the myword
	 */
	public String getMyword() {
		return myword;
	}

	/**
	 * @param myword the myword to set
	 */
	public void setMyword(String myword) {
		this.myword = myword;
	}

	@Override
	public boolean isEmptyWord() {
		return false;
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
	 * @return the eng
	 */
	public String getEng() {
		return eng;
	}

	/**
	 * @param eng the eng to set
	 */
	public void setEng(String eng) {
		this.eng = eng;
	}
	

}
