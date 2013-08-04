package com.example.randomwordapp;

import java.util.ArrayList;

import com.example.randomwordapp.MainActivity.RandomWord;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
	
	Activity activity;
	ArrayList<RandomWord> words;
	LayoutInflater inflater = null;
	boolean showTranslation = false;

	public CustomAdapter(Activity activity, ArrayList<RandomWord> words, boolean showTranslation){
		this.activity = activity;
		this.words = words;
		this.showTranslation = showTranslation;
	}//CustomAdapter()
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View finalView = (convertView == null) ?
					inflater.inflate(R.layout.list_row, null) : convertView;
					
		TextView mainText = (TextView) finalView.findViewById(R.id.mainWord);
		TextView minorText = (TextView) finalView.findViewById(R.id.definition);
		
		if(!showTranslation){
			mainText.setText(words.get(position).getKanji());
			minorText.setText(words.get(position).getHiragana());
		} else {
			mainText.setText(words.get(position).getHiragana());
			minorText.setText(words.get(position).getEnglish());
		}//else
		
		return finalView;
	}

}
