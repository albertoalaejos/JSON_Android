package assignment.funda;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import assignment.funda.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	// Declare Variables
	
	volatile boolean stop = false;
	boolean flag = true;
	static Handler actHandler;
	static Handler actHandler2;
	JSONObject jsonobject;
	JSONArray jsonarray;
	ListView listview;
	ListViewAdapter adapter;
	ProgressDialog mProgressDialog;
	ArrayList<HashMap<String, String>> arraylist;
	static String name = "name";
	static String sale = "sale";
	String loadingList = "Amsterdam...";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from listview_main.xml
		setContentView(R.layout.listview_main);
		// Execute DownloadJSON AsyncTask - send the URL as parameter (Amsterdam)
		new DownloadJSON().execute("http://partnerapi.funda.nl/feeds/Aanbod.svc/json/005e7c1d6f6c4f9bacac16760286e3cd/?type=koop&zo=/amsterdam");
	}

	// DownloadJSON AsyncTask
	private class DownloadJSON extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressdialog
			mProgressDialog = new ProgressDialog(MainActivity.this);
			// Set progressdialog title
			mProgressDialog.setTitle("Funda Test");
			// Set progressdialog message
			mProgressDialog.setMessage(loadingList);
			mProgressDialog.setIndeterminate(false);
			// Show progressdialog
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... urls) {
			
			//Call function to find the top 10 for the given url
			top10Makelaars(urls[0]);	
			return null;
		}
		
		private void top10Makelaars(String URL){
			// Create an arraylist to show the information in a listView
			arraylist = new ArrayList<HashMap<String, String>>();
			// Retrieve JSON Objects from the given URL address - first page
			jsonobject = JSONfunctions
					.getJSONfromURL(URL+"&page=1&pagesize=25");
		
			try {
				// Retrieve Paging JSON Object
				JSONObject jsonobject2 = jsonobject.getJSONObject("Paging");
				//Get the number of pages
				int pages = Integer.parseInt(jsonobject2.getString("AantalPaginas"));
				//HashMap to keep the number of sales for each Makelaar
				HashMap<String, Integer> salesMakelaars = new HashMap<String, Integer>();
				//An auxiliary arraylist to have a list of the Makelaar names. 
				ArrayList<String> listMakelaars = new ArrayList<String>();
				
				//loop until it reaches the last page
				for (int i = 1; i <= pages; i++) {
					// Retrieve Paging JSON Object
					jsonobject = JSONfunctions
							.getJSONfromURL(URL+"&page="+i+"&pagesize=25");
					// Retrieve Objects JSON Array
					jsonarray = jsonobject.getJSONArray("Objects");
					
					//loop while there are more Objects in the array
		            for (int j = 0; j < jsonarray.length(); j++) {
		            	// Retrieve each JSON Object in the Objects array
						jsonobject2 = jsonarray.getJSONObject(j);
						//if the Makelaar is already in the list, we increase the value of sales in one (+1)
						//It counts the number of sales for each Makelaar
						if(salesMakelaars.containsKey(jsonobject2.getString("MakelaarNaam"))){
							salesMakelaars.put(jsonobject2.getString("MakelaarNaam"), salesMakelaars.get(jsonobject2.getString("MakelaarNaam"))+1);
						}
						//if the Makelaar name does not exist yet, we create it with 1 as value (1 sale)
						else{
							listMakelaars.add(jsonobject2.getString("MakelaarNaam"));
							salesMakelaars.put(jsonobject2.getString("MakelaarNaam"),1);
						}
		            }
		            
				}
				
				//Using the Arraylist of Makelaars, we find the max value, which means the Makelaar with more sales.
				//We add the Makelaar to the HashMap, to be shown in the UI, and remove the Makelaar from the ArrayList.
				//We loop 10 times, to find the top 10.
	            for(int counter=0;counter<10;counter++){
	            	HashMap<String, String> map = new HashMap<String, String>();
		            int max = 0;
		            for(int c=0;c<listMakelaars.size();c++){
		            	if(salesMakelaars.get((String)listMakelaars.get(c)) > salesMakelaars.get((String)listMakelaars.get(max))){
		            		max = c;
		            	}
		            }
		            
					map.put("name", (String)listMakelaars.get(max));
					map.put("sale", "" + salesMakelaars.get((String)listMakelaars.get(max))  );
					
					arraylist.add(map);
		            
		            listMakelaars.remove(max);
	            }

			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}			
		}

		@Override
		protected void onPostExecute(Void args) {
			// Locate the listview in listview_main.xml
			listview = (ListView) findViewById(R.id.listview);
			// Pass the results into ListViewAdapter.java
			adapter = new ListViewAdapter(MainActivity.this, arraylist);
			// Set the adapter to the ListView
			listview.setAdapter(adapter);
			
			actHandler=new Handler(){
	            public void handleMessage(android.os.Message msg)
	            {
	                super.handleMessage(msg);
	             // Execute DownloadJSON AsyncTask - send the URL as parameter (Amsterdam)
	                new DownloadJSON().execute("http://partnerapi.funda.nl/feeds/Aanbod.svc/json/005e7c1d6f6c4f9bacac16760286e3cd/?type=koop&zo=/amsterdam");  	                
	            }
	        };
			actHandler2=new Handler(){
	            public void handleMessage(android.os.Message msg)
	            {
	                super.handleMessage(msg);
	             // Execute DownloadJSON AsyncTask - send the URL as parameter (Amsterdam - Tuin)
	                new DownloadJSON().execute("http://partnerapi.funda.nl/feeds/Aanbod.svc/json/005e7c1d6f6c4f9bacac16760286e3cd/?type=koop&zo=/amsterdam/tuin");
	            }
	        };
	        new Thread(){
	            public void run(){
	                try{
	                	//Every 100 seconds it shows a different list.
	                	/*The Lists are:
	                		-Top 10 Makelaars - Amsterdam
	                		-Top 10 Makelaars - Amsterdam with Tuin
	                	*/
	                	Thread.sleep(100000);
	                	if(!stop){
		                    if(flag){
		                    	loadingList = "Amsterdam - Tuin...";
		                    	actHandler2.sendEmptyMessage(0);
		                    }
		                    else{
		                    	loadingList = "Amsterdam...";
		                    	actHandler.sendEmptyMessage(0);	                    	
		                    }
		                    flag = !flag;
	                	}
	                     
	                }
	                catch(Exception ex){}
	               
	            	}
	        }.start();
	            	

			((BaseAdapter) listview.getAdapter()).notifyDataSetChanged(); 
			// Close the progressdialog
			mProgressDialog.dismiss();
			
		}
	}
	
	@Override
	public void onDestroy() {
	    stop = true;
	    super.onDestroy();

	}	
	
}