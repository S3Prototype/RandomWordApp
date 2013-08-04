package com.example.randomwordapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.randomwordapp.MainActivity.RandomWord;

public class CustomAdapter extends BaseAdapter {
	
	ArrayList<RandomWord> words;
	LayoutInflater inflater = null;
	boolean showTranslation = false;
	private Context context;

	public CustomAdapter(Context context, ArrayList<RandomWord> words, boolean showTranslation){
		this.context = context;
		this.words = words;
		this.showTranslation = showTranslation;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		
        TextView mainText = null;
        TextView subText = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, parent, false);
        }
        mainText = (TextView) convertView.findViewById(R.id.mainWord);
        subText = (TextView) convertView.findViewById(R.id.definition);

        RandomWord word = words.get(position);
        mainText.setText(word.getKanji());
        subText.setText(word.getHiragana());
        // Same for description and link
        return convertView;
/*		View finalView = (convertView == null) ?
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
		
		return finalView;*/
	}

}
