package com.example.legodigitalsonoro1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * class that reads from file and creates list of words
 * @author chiara
 *
 */
public class WordReader {
	
	private List<Word> words;
	private List<String> wordsAsString;
	
	public WordReader(InputStream f){
		words=new ArrayList<Word>();
		wordsAsString=new ArrayList<String>();
		fetchFromFile(f);
	}

	private void fetchFromFile(InputStream f) {
		
		if(f!=null){
			BufferedReader buff=new BufferedReader(new InputStreamReader(f));
			
			String line = null;
			try {
				line=buff.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while(line!=null){
				
				words.add(new Word(line));
				wordsAsString.add(line);
				
				try {
					line=buff.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Word> getSubList(int n) {
		ArrayList<Word> myWords=new ArrayList<Word>();
		
		Random r=new Random();
		int m;
		while(n>0){
			m=words.size();
			myWords.add(words.remove(r.nextInt(m)));
			n--;
		}
		words.clear();
		return myWords;
	}
	
	public ArrayList<String> getWordsAsString(int n){
		ArrayList<String> myWords=new ArrayList<String>();
		
		Random r=new Random();
		while(n>0){
			myWords.add(wordsAsString.remove(r.nextInt(wordsAsString.size())));
			n--;
		}
		wordsAsString.clear();
		return myWords;
	}


}

