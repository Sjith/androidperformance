package com.android.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.R;
import com.android.Session;
import com.android.helpers.ServiceHelper;
import com.android.helpers.ThreadPoolHelper;
import com.android.listeners.BaseResponseListener;
import com.android.models.Device;
import com.android.models.GPS;
import com.android.models.Item;
import com.android.models.Measurement;
import com.android.models.Ping;
import com.android.models.Throughput;
import com.android.models.Usage;
import com.android.models.WifiNeighbor;
import com.android.services.PerformanceService;
import com.android.tasks.MeasurementTask;
import com.android.ui.ListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;


public class RunActivity extends Activity 
{
	//private LinearLayout table;

	private ThreadPoolHelper serverhelper;
	private Session session = null;
	private Activity activity;
	private boolean firstPing=true;
	public String serviceTag = "PerformanceService";
	private Button backButton;
	//private ProgressBar progress;
	//private ProgressBar progressBar;
	public ArrayList<Item> items;
	public ListView listview;
	public ListAdapter listadapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual_test);

		activity = this;

		serverhelper = new ThreadPoolHelper(5,10);
		backButton=(Button)findViewById(R.id.back);
		//progress=(ProgressBar)findViewById(R.id.spinningBar);
		//progressBar=(ProgressBar)findViewById(R.id.progressBar);

		//table = (LinearLayout)findViewById(R.id.measurementslayout);
		listview = (ListView) findViewById(R.id.allview);
		
		backButton.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {	
				finish();
			}
		});

		ServiceHelper.processStopService(this,serviceTag);
		
		items = new ArrayList<Item>();
		listadapter = new ListAdapter(activity,R.layout.item_view,items);
		serverhelper.execute(new MeasurementTask(activity,new HashMap<String,String>(), new MeasurementListener()));
		listview.setAdapter(listadapter);

	}


	private void newLineTable(){
		View row = new View(this);
		row.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, 
				1));
		row.setBackgroundColor(Color.WHITE);

		/*table.addView(row,new LayoutParams(
				LayoutParams.FILL_PARENT,
				1));*/
	}

	private void initPingTable(){
		LinearLayout row = new LinearLayout(this);
		row.isVerticalScrollBarEnabled();
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView cell2 = new TextView(this);
		cell2.setText("Max");
		cell2.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));
		row.addView(cell2,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell3 = new TextView(this);
		cell3.setText("Min");
		cell3.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));
		row.addView(cell3,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell4 = new TextView(this);
		cell4.setText("StdDev");
		cell4.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));
		row.addView(cell4,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell5 = new TextView(this);
		cell5.setText("Avr");
		cell5.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));
		row.addView(cell5,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));        

		/*table.addView(row,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));*/

		newLineTable();
	}

	private void newPingTable(Ping p){
		LinearLayout row = new LinearLayout(this);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView cell2 = new TextView(this);
		cell2.setText(String.valueOf(p.getMeasure().getMax()));
		cell2.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 
				(float) 0.25));
		row.addView(cell2,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell3 = new TextView(this);
		cell3.setText(String.valueOf(p.getMeasure().getMin()));
		cell3.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 
				(float) 0.25));
		row.addView(cell3,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell4 = new TextView(this);
		cell4.setText(String.valueOf(p.getMeasure().getStddev()));
		cell4.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 
				(float) 0.25));
		row.addView(cell4,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		TextView cell5 = new TextView(this);
		cell5.setText(String.valueOf(p.getMeasure().getAverage()));
		cell5.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 
				(float) 0.25));
		row.addView(cell5,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT,
				(float) 0.25));

		/*table.addView(row,new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		*/
		newLineTable();
	}

	private void newDeviceTable(Device d){

		JSONObject obj=d.toJSON();
		Iterator keyes=obj.keys();
		LinearLayout row;

		for (String key=(String)keyes.next();keyes.hasNext();key=(String)keyes.next()){

			row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			name.setText(key);
			name.setHorizontallyScrolling(true);
			name.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					(float) 0.5));
			row.addView(name,new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT, 
					(float) 0.5));

			try {
				TextView nameV = new TextView(this);
				//nameV.setHorizontallyScrolling(true);
				//nameV.setGravity(Gravity.RIGHT);
				nameV.setText((String)obj.get(key));
				nameV.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT, 
						(float) 0.5));
				row.addView(nameV,new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, 
						(float) 0.5));
			} catch (JSONException e) {e.printStackTrace();}


			/*table.addView(row,new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));*/

			newLineTable();
		}     
	}

	private void newMeasurementTable(Measurement m){
		JSONObject obj=m.toJSON();
		Iterator keyes=obj.keys();
		LinearLayout row;

		for (String key=(String)keyes.next();keyes.hasNext();key=(String)keyes.next()){
			if (key.equals("pings"))
				continue;

			row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			name.setText(key);
			name.setHorizontallyScrolling(true);
			name.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT, 
					(float) 0.5));
			row.addView(name,new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT, 
					(float) 0.5));

			try {
				TextView nameV = new TextView(this);
				//nameV.setGravity(Gravity.RIGHT);
				//nameV.setHorizontallyScrolling(true);
				nameV.setText((String)obj.get(key));
				nameV.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT, 
						(float) 0.5));
				row.addView(nameV,new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, 
						(float) 0.5));
			} catch (JSONException e) {e.printStackTrace();}


			/*table.addView(row,new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));*/

			newLineTable();
		}  
	}

	public class MeasurementListener extends BaseResponseListener{

		public void onCompletePing(Ping response) {
			Message msg=Message.obtain(pingHandler, 0, response);
			pingHandler.sendMessage(msg);
			Message msg2=Message.obtain(UIHandler, 0, new Item("Ping",response.toJSON()));
			UIHandler.sendMessage(msg2);
		}

		public void onCompleteDevice(Device response) {
			Message msg=Message.obtain(deviceHandler, 0, response);
			deviceHandler.sendMessage(msg);		
		}

		public void onCompleteMeasurement(Measurement response) {
			Message msg=Message.obtain(measurementHandler, 0, response);
			measurementHandler.sendMessage(msg);
		}

		public void onComplete(String response) {

		}

		public void onUpdateProgress(int val){
			Message msg=Message.obtain(progressBarHandler, 0, val);
			progressBarHandler.sendMessage(msg);
		}

		public void onCompleteGPS(GPS gps) {
			// TODO Auto-generated method stub

		}

		public void makeToast(String text) {
			Message msg=Message.obtain(toastHandler, 0, text);
			toastHandler.sendMessage(msg);

		}

		public void onCompleteSignal(int signalStrength) {

		}
		public void onCompleteUsage(Usage usage) {
			// TODO Auto-generated method stub

		}

		public void onCompleteThroughput(Throughput throughput) {
			// TODO Auto-generated method stub

		}

		public void onCompleteWifi(List<ScanResult> wifiList) {
			// TODO Auto-generated method stub

		}
	}

	private Handler toastHandler = new Handler() {
		public void  handleMessage(Message msg) {
			try {
				String text = (String)msg.obj;
				Toast.makeText(activity, text, 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler pingHandler = new Handler() {
		public void  handleMessage(Message msg) {
			try {
				Ping p=(Ping)msg.obj;
				if (firstPing){
					initPingTable();
					firstPing=false;
				}
				newPingTable(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler deviceHandler = new Handler() {
		public void  handleMessage(Message msg) {
			try {

				Device d=(Device)msg.obj;
				newDeviceTable(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler measurementHandler = new Handler() {
		public void  handleMessage(Message msg) {
			try {

				//progress.setVisibility(View.GONE);
				//progressBar.setVisibility(View.GONE);
				Measurement m=(Measurement)msg.obj;
				newMeasurementTable(m);


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	private Handler UIHandler = new Handler(){
		public void  handleMessage(Message msg) {
			
			Item ui = (Item)msg.obj;
			items.add(ui);
			System.out.println(items.size());
			listadapter.add(ui);
			System.out.println(listadapter.getCount());
			listadapter.notifyDataSetChanged();		
			
		}
	};

	private Handler progressBarHandler = new Handler() {

		public void  handleMessage(Message msg) {
			try {
				int value=(Integer)msg.obj;
				//progressBar.setProgress(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}