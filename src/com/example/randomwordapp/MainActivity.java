package com.example.randomwordapp;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	boolean moreWordsPressed = false;
	
	LayoutInflater inflater;
	
	DisplayMetrics metric;
	int screenWidth;
	int screenHeight;
	
	boolean hasBeenInitialized = false;
	
	int addWordsCount = 0;//Number of times user added new words
	
	ArrayList <RandomWord> words = null;
	boolean showTranslation = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        moreWords = (Button) findViewById(R.id.moreWords);
        
        moreWords.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				moreWordsPressed = true;
				new GetWordsTask().execute();
			} 
        });
    
    }//onCreate()

    @Override
	protected void onResume() {
    	super.onResume();
    	metric = getResources().getDisplayMetrics();
    	screenWidth = metric.widthPixels;
    	screenHeight = metric.heightPixels;
    		//This ensures it won't get new words every time onResume is called
    	if(!hasBeenInitialized){
    		new GetWordsTask().execute();
    		hasBeenInitialized = true;
    	}
    	
    	setBackground();
	}
    
    public void setBackground(){
    	TableLayout main = (TableLayout) findViewById(R.id.table);
    	main.setBackgroundResource(R.drawable.bg);
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
    	private boolean showDef;
    	
    	public boolean ShowDef() {
			return showDef;
		}

		public void setShowDef(boolean showDef) {
			this.showDef = showDef;
		}

		public RandomWord(String kanji, String hiragana, String english) {
    		super();
    		this.kanji = kanji;
    		this.hiragana = hiragana;
    		this.english = english;
    		showDef = false;
    	}
    	
		public RandomWord() {
			// TODO Auto-generated constructor stub
			showDef = false;
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
    	ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			words = new ArrayList<RandomWord>();
			dialog = ProgressDialog.show(MainActivity.this, "お待ちください", "We're getting your words ...");
		}
		
		private String formatEnglish(String englishVal){
			String english = englishVal;
			if(english.length() > 30){
				//If the string is too long, gotta break it up
				int startingPoint = 0;
				for(int i = 1; i <= english.length()/30; i++){
					/** 
					 * This loop will cut a string at every 30 char interval.
					 */
					int closestSpaceIndex = english.substring(startingPoint, (i * 30)).lastIndexOf(" ");
					if(closestSpaceIndex > 0){
						String tempEng, tempEng1;
						tempEng = english.substring(startingPoint, startingPoint + closestSpaceIndex);
						tempEng1 = english.substring(startingPoint + closestSpaceIndex);
						String result = tempEng + "\n" + tempEng1;
						//When startPoing != 0, we need to remember to append the first part of english
						//as well, or we'll lose it
						english = (startingPoint == 0) ? result : english.substring(0, startingPoint) + result;
						startingPoint = i * 30;//Next iteration will cut from here to i * 30
					}//if
				}//for()
			}//if(english.length > 30)
			return english;
		}//formatEnglish()

		@Override
		protected RandomWord[] doInBackground(Void... arg0) {
			RandomWord tempWords[] = null;
			String tag = "JSOUP INFO";
			webDoc = null;
			boolean successfullyConnected = false;
			while(!successfullyConnected){
				try{
					webDoc = Jsoup.connect("http://www.manythings.org/c/r2.cgi/edict").post();
					if(webDoc != null)
						successfullyConnected = true;
				} catch (MalformedURLException e){
					
				} catch (HttpStatusException e){
					
				} catch (UnsupportedMimeTypeException e){
					
				} catch (SocketTimeoutException e){
					
				} catch (IOException e){
					
				}
			}//while()
			
			Elements kanjiTags = webDoc.select("font[size*=7]");
			Elements selectTags = webDoc.select("select");
			
			tempWords = new RandomWord[5];
			int x = 0;
			for(int i = 0; i < 10; i += 2){
				String english = formatEnglish(selectTags.get(i + 1).getElementsByTag("option").get(1).ownText());
				
				
				tempWords[x] = new RandomWord(kanjiTags.get(x).text(),
											  selectTags.get(i).getElementsByTag("option").get(1).ownText(),
											  english);
				x++;
			}//for()
			
			Log.d("WORD CHOSEN:", "#4 - " + tempWords[4].getKanji() + " : " + tempWords[4].getHiragana());
			
			return tempWords;
		}//doInBackground 
		
		@Override
		protected void onPostExecute(RandomWord[] result) {
			Log.d("ONPOSTEXECUTE", "Was called");
			//fillList(result);
			if(moreWordsPressed){
				addWordsCount++;
				moreWordsPressed = false;
			}
			for(int i = 0; i < 5; i++){
				LinearLayout listRow = (LinearLayout) inflater.inflate(R.layout.list_row, null);
				TextView kanji = (TextView) listRow.findViewById(R.id.mainWord);
				TextView def = (TextView) listRow.findViewById(R.id.definition); 
				TextView number = (TextView) listRow.findViewById(R.id.number); 
				
				kanji.setText(result[i].getKanji());
				def.setText(result[i].getHiragana());
				
				Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/epmgobld.ttf");
				
				kanji.setTypeface(font);
				//kanji.setTextColor(Color.RED);
				kanji.setShadowLayer(5, 2, 3, Color.BLACK);
				def.setTypeface(font);
				
				number.setTypeface(font);
				number.setTextColor(Color.BLACK);
				
				TableRow newRow = new TableRow(context);
				
				newRow.addView(listRow);
				int actualIndex = i + (addWordsCount * 5);
				newRow.setId(actualIndex);
				def.setTag(newRow.getId());
				
				number.setText("(" + (actualIndex + 1) + ")");
				
				final TableLayout viewTable = (TableLayout) findViewById(R.id.viewTable);
				final RandomWord wordToPass = result[i];
				
				newRow.setOnClickListener(new OnClickListener(){
					public void onClick(View row) {
						TextView defText = (TextView) viewTable.findViewWithTag(row.getId());
						if(!wordToPass.ShowDef()){
							defText.setText(wordToPass.getHiragana() + "\n" + wordToPass.getEnglish());
							defText.setTextColor(Color.BLACK);
							wordToPass.setShowDef(true);
						} else {
							defText.setText(wordToPass.getHiragana());
							defText.setTextColor(Color.WHITE);
							wordToPass.setShowDef(false);
						}//else
					}//onClick()
				});
				
				viewTable.addView(newRow);
			}//for
			
			//finally dismiss the progress dialog
			dialog.dismiss();
		}//onPostExecute()
    	
    }//GetWordsTask class
    
}//MainActivity class
