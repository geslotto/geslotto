package com.desipal.EventU;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class galeriaAdapter extends BaseAdapter {
	private Context mContext;
	private List<Drawable> items;
	boolean pantallaCompleta;

	public galeriaAdapter(Context c, List<Drawable> imagenes, boolean pantCompleta) {
		mContext = c;
		items = imagenes;
		pantallaCompleta = pantCompleta;
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
			if (pantallaCompleta) {
				imageView.setLayoutParams(new LayoutParams(50, 50)); 
			}
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		} catch (Exception ex) {
			Toast.makeText(mContext, "Error al cargar las imágenes", Toast.LENGTH_SHORT).show();
		}
		return imageView;
	}
}
