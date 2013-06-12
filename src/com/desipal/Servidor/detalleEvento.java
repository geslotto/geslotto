package com.desipal.Servidor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.desipal.Entidades.eventoEN;
import com.desipal.EventU.detalleEventoActivity;

import android.os.AsyncTask;

public class detalleEvento extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("es", "ES"));
	private eventoEN evento;

	public detalleEvento(ArrayList<NameValuePair> parametros) {
		this.parametros = parametros;
	}

	@Override
	protected Void doInBackground(String... urls) {
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(parametros));

				HttpResponse execute = client.execute(httppost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader r = new BufferedReader(new InputStreamReader(content));
				StringBuilder total = new StringBuilder();

				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				
				if (!total.toString().equals("null")) {									
					JSONObject jobj = new JSONObject(total.toString());
					eventoEN e = new eventoEN();
					e.setIdEvento(jobj.getInt("idEvento"));
					e.setIdCreador(jobj.getString("idCreador"));
					e.setNombre(jobj.getString("nombre"));
					e.setDescripcion(jobj.getString("descripcion"));
					e.setLatitud(jobj.getDouble("latitud"));
					e.setLongitud(jobj.getDouble("longitud"));
					e.setAsistencia(jobj.getInt("asistencia"));
					e.setValidado(jobj.getBoolean("validado"));
					e.setComentarios(jobj.getBoolean("comentarios"));
					e.setDireccion(jobj.getString("direccion"));
					e.setIdCategoria(jobj.getInt("idCategoria"));
					e.setFechaInicio(dateFormat.parse(jobj.getString("fechaInicio")));
					e.setFechaFin(dateFormat.parse(jobj.getString("fechaFin")));
					e.setTodoElDia(jobj.getBoolean("todoElDia"));
					e.setUrl(jobj.getString("url"));
					this.evento = e;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		detalleEventoActivity.evento = this.evento;
		return;
	}

}
