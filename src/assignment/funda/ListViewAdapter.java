package assignment.funda;

import java.util.ArrayList;
import java.util.HashMap;

import assignment.funda.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	//ImageLoader imageLoader;
	HashMap<String, String> resultp = new HashMap<String, String>();

	public ListViewAdapter(Context context,
			ArrayList<HashMap<String, String>> arraylist) {
		this.context = context;
		data = arraylist;
		//imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView name;
		TextView sale;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.listview_item, parent, false);
		// Get the position
		resultp = data.get(position);

		// Locate the TextViews in listview_item.xml
		name = (TextView) itemView.findViewById(R.id.name);
		sale = (TextView) itemView.findViewById(R.id.sale);

		// Locate the ImageView in listview_item.xml
		//path = (ImageView) itemView.findViewById(R.id.path);

		// Capture position and set results to the TextViews
		name.setText(resultp.get(MainActivity.name));
		sale.setText(resultp.get(MainActivity.sale));
		// Capture position and set results to the ImageView
		// Passes path images URL into ImageLoader.class
		//imageLoader.DisplayImage(resultp.get(MainActivity.path), path);
		// Capture ListView item click
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get the position
/*				resultp = data.get(position);
				Intent intent = new Intent(context, SingleItemView.class);
				// Pass all data name
				intent.putExtra("name", resultp.get(MainActivity.name));
				// Pass all data brand
				intent.putExtra("brand", resultp.get(MainActivity.brand));
				// Pass all data price
				intent.putExtra("price",resultp.get(MainActivity.price));
				// Pass all data path
				//intent.putExtra("path", resultp.get(MainActivity.path));
				// Start SingleItemView Class
				context.startActivity(intent);*/
				

			}
		});
		return itemView;
	}
}
