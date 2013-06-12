package com.desipal.EventU;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.desipal.Entidades.adaptadorEventoEN;

import android.annotation.SuppressLint;
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
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
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

			RelativeLayout relLayot = (RelativeLayout) vista.findViewById(R.id.rel_gridView_item);

			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion > android.os.Build.VERSION_CODES.HONEYCOMB)
				relLayot.setBackground(item.getImagen());
			else
				relLayot.setBackgroundDrawable(item.getImagen());

			TextView txtFecha = (TextView) vista.findViewById(R.id.txtFecha_evento);
			String fecha = dateFormat.format(item.getFecha());
			txtFecha.setText(fecha);

			TextView txtDist = (TextView) vista.findViewById(R.id.txtDist_evento);
			String distancia = new DecimalFormat("#.##").format(item.getDistancia());
			txtDist.setText(distancia + " Km");

			TextView txtNombre = (TextView) vista.findViewById(R.id.txtTitulo_evento);
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
