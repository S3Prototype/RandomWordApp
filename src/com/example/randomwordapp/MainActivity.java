package com.example.randomwordapp;


import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements; 


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	ListView wordListViewLV;
	Button moreWords;
	
	ArrayList <RandomWord> words = null;
	boolean showTranslation = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        moreWords = (Button) findViewById(R.id.moreWords);
        wordListViewLV = (ListView) findViewById(R.id.wordList);
        
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
			  
			 //each dt contains a word, kanij, and hiragana
			 Elements kanjiTags = webDoc.select("font[size*=7]");
			 
			 tempWords = new RandomWord[5];
			 int i = 0;
			 
			 for(Element currTag : kanjiTags){
				 String kanji = currTag.select("font").first().text();
				 tempWords[i] = new RandomWord();
				 tempWords[i].setKanji(kanji);
				 Log.d(tag, tempWords[i].getKanji());
				 if(++i >= 5) break;
			 }//for
			 
			 	//The following code gets hiragana and english from tangorin
			 String firstHalf = "http://jisho.org/words?jap=";
			 String secondHalf = "&eng=&dict=edict";
			 
			 for(i = 0; i < 5; i++){
				 try{
					 jisho = Jsoup.connect("http://www.tangorin.com/general/" + tempWords[i].getKanji()).get();
				 } catch (Exception e){
					 Log.d("JISHO CONNECTION:", e.getMessage());
				 }
				 
				 Element kana = null;
				 Element meaning = null;
				  
				 try{
					 kana = jisho.getElementsByClass("kana").first();
					 //meaning = jisho.select("td.meanings_column").first();
				 } catch (Exception e){
					 Log.d(tag, e.getMessage()); 
				 }
					 String hiragana = "NONE";
					 try{
						 hiragana = kana.text();
					 } catch (Exception e){
						 Log.d(tag, e.getMessage());
					 }
					 String english = "hello ";//meaning.text();
					 Log.d(tag, hiragana);
					 
					 tempWords[i].setEnglish(english);
					 tempWords[i].setHiragana(hiragana);
			 }//for
			 
			return tempWords;
		}//doInBackground
		
		@Override
		protected void onPostExecute(RandomWord[] result) {
			Log.d("ONPOSTEXECUTE", "Was called");
			for(RandomWord currResult : result){ 
				words.add(currResult);
				Log.d("RESULT INFO:", currResult.getEnglish());
			}//for()
			
			if(result == null){
				Log.d("RESULT WORD ARRAY WAS NULL", "ERROR!");
				return;
			}
			
			CustomAdapter adapter = new CustomAdapter(MainActivity.this, words, showTranslation);
			wordListViewLV.setAdapter(adapter);
		}//onPostExecute()
    	
    }//GetWordsTask class
    
}//MainActivity class
