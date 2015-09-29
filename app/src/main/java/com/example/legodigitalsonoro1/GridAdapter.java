package com.example.legodigitalsonoro1;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * class to manage the content of the grid view
 * @author chiara
 *
 */
public class GridAdapter extends ArrayAdapter<Syllable>{
	
	Context context;
	ArrayList<Syllable> currentSyllables;
	int index;
	int cardHeight;
	int cardWidth;
	
	public GridAdapter(Context context, int gridItem, ArrayList<Syllable> newSylls, int height, int width) {
		super(context, gridItem);
		this.context=context;
		currentSyllables=newSylls;
		index=-1;
		cardHeight=(height/21)*10;
		cardWidth=cardHeight/2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;
		convertView = LayoutInflater.from(context).inflate(R.layout.grid_item,
				null);
		imageView = (ImageView) convertView.findViewById(R.id.image);
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(cardWidth, cardHeight);
		params.gravity=Gravity.CENTER;
		imageView.setLayoutParams(params);

		Syllable s = getItem(position);
		imageView.setImageResource(context.getResources().getIdentifier(
				s.getImage(), "drawable", context.getPackageName()));

		if (index == position) {
			Animation pulse = AnimationUtils.loadAnimation(context,
					R.anim.pulse);
			imageView.setAnimation(pulse);
			imageView.startAnimation(pulse);
		} else {
			imageView.clearAnimation();
		}

		return convertView;
		/*
		 * TextView text; if(convertView==null){ text=new TextView(context);
		 * }else{ text=(TextView) convertView; } Syllable s = getItem(position);
		 * text.setText(s.getMySyll()); return text;
		 */
	}
	
	@Override
	public Syllable getItem(int position) {
		return currentSyllables.get(position);
	}

	@Override
	public int getCount() {
		super.getCount();
		return currentSyllables.size();
	}

	public void setSelected(int position) {
		index=position;
		notifyDataSetChanged();
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(currentSyllables.get(position).isEmptySyll()){
			return false;
		}else{
			return true;
		}
	}

}
