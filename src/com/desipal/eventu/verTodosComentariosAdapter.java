package com.desipal.eventu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.desipal.Entidades.comentarioEN;
import com.desipal.eventu.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class verTodosComentariosAdapter extends BaseAdapter {
	private Context mContext;
	protected List<comentarioEN> items;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", MainActivity.currentLocale);
	protected ArrayList<View> ArList;

	public verTodosComentariosAdapter(Context c, List<comentarioEN> eventos) {
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
		return position;
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
			comentarioEN item = items.get(position);
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vista = inflater.inflate(R.layout.item_comentario, null);

			TextView txtFecha = (TextView) vista.findViewById(R.id.txtFecha);
			String fecha = dateFormat.format(item.getFecha());
			txtFecha.setText(fecha);

			RatingBar ratingBar1 = (RatingBar) vista
					.findViewById(R.id.ratingBar1);
			ratingBar1.setEnabled(false);
			ratingBar1.setRating((float) item.getValoracion());

			TextView txtComentario = (TextView) vista
					.findViewById(R.id.txtComentario);
			txtComentario.setText(item.getComentario());
			/*View vi = (View) vista.findViewById(R.id.hr);
			if (items.size() == position + 1)
				vi.setVisibility(View.GONE);*/
			this.ArList.add(position, vista);
			return vista;

		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return vista;
	}
}
