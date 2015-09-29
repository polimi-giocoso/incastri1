package com.example.legodigitalsonoro1;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * 
 * @author chiara
 *
 */
public class MyTextToSpeechManager {

	private static TextToSpeech tts;
	private static TextToSpeech englishTTS;
	
	private static MyTextToSpeechManager myTTSmanager;


	private MyTextToSpeechManager(Context applicationContext) {
		
		englishTTS = new TextToSpeech(applicationContext, new OnInitListener() {

			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.ERROR) {
					englishTTS.setLanguage(Locale.ENGLISH);
					englishTTS.setSpeechRate(0.7f);
				}
			}
		});
		
		tts = new TextToSpeech(applicationContext, new OnInitListener() {

			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.ITALIAN);
					tts.setSpeechRate(0.7f);
				}
			}
		});
	}
	
	public static void InitializeTextToSpeech(Context context){
		myTTSmanager=new MyTextToSpeechManager(context);
	}

	public static TextToSpeech getEnglishTextToSpeech() {
		return englishTTS;
	}

	public static MyTextToSpeechManager getMyTextToSpeechManager() {
		return myTTSmanager;
	}
	public static TextToSpeech getTextToSpeech() {
		return tts;
	}

}
