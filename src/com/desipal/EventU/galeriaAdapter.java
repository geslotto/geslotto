package com.desipal.EventU;

import java.util.List;

import com.desipal.Librerias.Herramientas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class galeriaAdapter extends BaseAdapter {
	private Context mContext;
	private List<Drawable> items;
	boolean pantallaCompleta;
	private Display pantalla;
	int MedidaMaxima;

	public galeriaAdapter(Context c, List<Drawable> imagenes, boolean pantCompleta, Display display) {
		mContext = c;
		items = imagenes;
		pantallaCompleta = pantCompleta;
		pantalla = display;
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
				Bitmap bitmap = ((BitmapDrawable) items.get(position)).getBitmap();
				int anchura = pantalla.getWidth();
				int altura = pantalla.getHeight();
				if (anchura > altura)
					MedidaMaxima = anchura;
				else
					MedidaMaxima = altura;
				Drawable d = new BitmapDrawable(mContext.getResources(), Herramientas.aumentarImagen(bitmap,
						MedidaMaxima));
				imageView.setImageDrawable(d);
			}
			// }
			// imageView.setLayoutParams(new ImageView.LayoutParams(30,30));

			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		} catch (Exception ex) {
			Toast.makeText(mContext, "Error al cargar las imágenes", Toast.LENGTH_SHORT).show();
		}
		return imageView;
	}
}
