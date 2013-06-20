package com.desipal.eventu;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.eventu.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private Context mContext;
	protected List<adaptadorEventoEN> items;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", MainActivity.currentLocale);
	protected ArrayList<View> ArList;

	public GridViewAdapter(Context c, List<adaptadorEventoEN> eventos) {
		mContext = c;
		this.items = eventos;
		this.ArList = new ArrayList<View>(eventos.size());
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return items.get(position).getIdEvento();
	}

	// create a new ImageView for each item referenced by the Adapter

	public View getView(int position, View convertView, ViewGroup parent) {
		View vista = convertView;
		View a;
		try {
			a = this.ArList.get(position);
		} catch (Exception e) {
			a = null;
		}
		if (a != null) {
			vista = a;
			return a;
		}
		try {
			adaptadorEventoEN item = items.get(position);
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vista = inflater.inflate(R.layout.gridview_item, null);

			ImageView imgEvento = (ImageView) vista.findViewById(R.id.imgEvento);
			imgEvento.setImageDrawable(item.getImagen());

			TextView txtFecha = (TextView) vista.findViewById(R.id.txtFechaEvento);
			String fecha = dateFormat.format(item.getFecha());
			txtFecha.setText(fecha);

			TextView txtDist = (TextView) vista.findViewById(R.id.txtDistEvento);
			String distancia = new DecimalFormat("#.##").format(item.getDistancia());
			txtDist.setText(distancia + " Km");

			TextView txtNombre = (TextView) vista.findViewById(R.id.txtTituloEvento);
			txtNombre.setText(item.getNombre());

		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return vista;
	}
}
