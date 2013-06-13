package com.desipal.EventU;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class galeriaAdapter extends BaseAdapter {
	private Context mContext;
	private List<Drawable> items;

	public galeriaAdapter(Context c, List<Drawable> imagenes) {
		mContext = c;
		items = imagenes;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);
		try {
			imageView.setImageDrawable(items.get(position));		
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		} catch (Exception ex) {
			Toast.makeText(mContext, "Error al cargar las imágenes", Toast.LENGTH_SHORT).show();
		}
		return imageView;
	}
}
