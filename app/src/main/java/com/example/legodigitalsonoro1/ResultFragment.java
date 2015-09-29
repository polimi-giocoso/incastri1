package com.example.legodigitalsonoro1;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;

/**
 * 
 * @author chiara
 *
 */
public class ResultFragment extends Fragment {
	
	private MatchManager matchManager;

	private ResAdapter adapter;
	private ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.result_fragment, null);
		
		list=(ListView) v.findViewById(R.id.result_list);
		list.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				 // Ensure you call it only once :
			    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			        list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			    }
			    else {
			        list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			    }
			    
			    int width=list.getWidth();
			    adapter=new ResAdapter(getActivity(), R.layout.res_item, matchManager.getCurrentResults(),width);
				list.setAdapter(adapter);
				list.setDivider(null);
			}
		});
		return v;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		matchManager=MatchManager.getMatchManager();
	}

	public void updateResult() {
		if(matchManager.getLastSelectedWord()!=null){
			MyTextToSpeechManager.getTextToSpeech().speak(matchManager.getLastResultWord().getMyword(), TextToSpeech.QUEUE_ADD, null);
		}
		adapter.notifyDataSetChanged();
		
	}

}
