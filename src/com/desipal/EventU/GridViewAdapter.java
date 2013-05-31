package com.desipal.EventU;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private Context mContext;

	public GridViewAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return mThumbIds.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View gridView = new View(mContext);
		try {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				gridView = inflater.inflate(R.layout.gridview_item, null);
				RelativeLayout relLayot = (RelativeLayout) gridView
						.findViewById(R.id.rel_gridView_item);
				relLayot.setBackgroundResource(mThumbIds[position]);
				TextView txtTitulo = (TextView) gridView
						.findViewById(R.id.txtTiutlo_evento);
				txtTitulo.setText("Concierto Macaco");
				TextView txtDesc = (TextView) gridView
						.findViewById(R.id.txtDesc_evento);
				txtDesc.setText("Concierto Macaco");
			} else
				gridView = (View) convertView;

		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return gridView;
		/*
		 * ImageView imageView; if (convertView == null) { // if it's not
		 * recycled, initialize some // attributes imageView = new
		 * ImageView(mContext); imageView.setLayoutParams(new
		 * GridView.LayoutParams(140, 125));
		 * imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		 * //imageView.setPadding(3, 3, 3, 3); } else { imageView = (ImageView)
		 * convertView; }
		 * 
		 * imageView.setImageResource(mThumbIds[position]); return imageView;
		 */
	}

	// references to our images
	private Integer[] mThumbIds = { R.drawable.grid_1, R.drawable.grid_2,
			R.drawable.grid_3, R.drawable.grid_4, R.drawable.grid_1,
			R.drawable.grid_2, R.drawable.grid_3, R.drawable.grid_4 };
}
