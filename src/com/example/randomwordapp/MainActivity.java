package com.example.randomwordapp;


import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	ListView wordListViewLV;
	Button moreWords;
	
	Context context;
	
	ArrayList <RandomWord> words = null;
	boolean showTranslation = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        
        moreWords = (Button) findViewById(R.id.moreWords);
        
        moreWords.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new GetWordsTask().execute();
			} 
        });
    
    }//onCreate()

    @Override
	protected void onResume() {
    	super.onResume();
    	new GetWordsTask().execute();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
    
    public class RandomWord{
    	private String kanji;
    	private String hiragana;
    	private String english;
    	
    	public RandomWord(String kanji, String hiragana, String english) {
    		super();
    		this.kanji = kanji;
    		this.hiragana = hiragana;
    		this.english = english;
    	}
    	
		public RandomWord() {
			// TODO Auto-generated constructor stub
		}

		public String getKanji() {
			return kanji;
		}

		public void setKanji(String kanji) {
			this.kanji = kanji;
		}

		public String getHiragana() {
			return hiragana;
		}

		public void setHiragana(String hiragana) {
			this.hiragana = hiragana;
		}

		public String getEnglish() { 
			return english;
		}

		public void setEnglish(String english) {
			this.english = english;
		} 
    }//RandomWord class
    
    public void fillList(RandomWord[] list){
    	
    	int i = 0;
    	for(RandomWord currResult : list){ 
    		words.add(currResult);
    		Log.d("RESULT INFO:", words.get(i).getEnglish());
    		i++;
    	}//for()
    	
    	if(words == null){
    		Log.d("ERROR!", "RESULT WORD ARRAY WAS NULL");
    		return;
    	}
    	
    	Activity currActivity = MainActivity.this;
    	CustomAdapter adapter = new CustomAdapter(currActivity.getApplicationContext(), words, showTranslation);
    	Log.d("IS IT NULL?", "" + (adapter == null));
    	wordListViewLV.setAdapter(adapter);
    }
    
    private class GetWordsTask extends AsyncTask <Void, Void, RandomWord[]> {
    	Document webDoc = null;
    	Document jisho = null;
		@Override
		protected void onPreExecute() {
			words = new ArrayList<RandomWord>();
		}

		@Override
		protected RandomWord[] doInBackground(Void... arg0) {
			RandomWord tempWords[] = null;
			String tag = "JSOUP INFO";
			try {
				webDoc = Jsoup.connect("http://www.manythings.org/c/r2.cgi/edict").post();
			} catch (Exception e) {
				Log.d("JSOUP ERROR", e.getMessage());
			}
			
			Elements kanjiTags = webDoc.select("font[size*=7]");
			Elements selectTags = webDoc.select("select");
			
			tempWords = new RandomWord[5];
			int x = 0;
			for(int i = 0; i < 10; i += 2){
				tempWords[x] = new RandomWord(kanjiTags.get(x).text(),
											  selectTags.get(i).getElementsByTag("option").get(1).ownText(),
											  selectTags.get(i + 1).getElementsByTag("option").get(1).ownText());
				x++;
			}//for()
			
			Log.d("WORD CHOSEN:", "#4 - " + tempWords[4].getKanji() + " : " + tempWords[4].getHiragana());
			
			return tempWords;
		}//doInBackground
		
		@Override
		protected void onPostExecute(RandomWord[] result) {
			Log.d("ONPOSTEXECUTE", "Was called");
			//fillList(result);
			for(int i = 0; i < 5; i++){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout listRow = (LinearLayout) inflater.inflate(R.layout.list_row, null);
				TextView kanji = (TextView) listRow.findViewById(R.id.mainWord);
				TextView def = (TextView) listRow.findViewById(R.id.definition);
				
				kanji.setText(result[i].getKanji());
				def.setText(result[i].getHiragana());
				
				TableRow newRow = new TableRow(context);
				
				newRow.addView(listRow);
				
				TableLayout viewTable = (TableLayout) findViewById(R.id.viewTable);
				viewTable.addView(newRow);
			}//for
		}//onPostExecute()
    	
    }//GetWordsTask class
    
}//MainActivity class
