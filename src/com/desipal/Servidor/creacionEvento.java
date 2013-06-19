package com.desipal.Servidor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.desipal.Entidades.seleccionUbicacionEN;
import com.desipal.EventU.crearEventoActivity;

import android.os.AsyncTask;

public class creacionEvento extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;

	public creacionEvento(ArrayList<NameValuePair> parametros) {
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
				if (total.toString().equals("ok")) {
					crearEventoActivity.Creacionsatisfactoria = 0;
				} else {
					if (total.toString().equals("ERROR_NO_RESULTS_FOUND"))
						crearEventoActivity.Creacionsatisfactoria = 2;
					else if (total.toString().contains("ERROR_TOO_MANY_RESULTS")) {
						JSONObject jobj = new JSONObject(total.toString());
						JSONArray jlist = new JSONArray(jobj.getString("list"));
						crearEventoActivity.opcionesDireccion = new seleccionUbicacionEN[jlist.length()];
						for (int i = 0; i < jlist.length(); i++) {
							JSONObject item = new JSONObject(jlist.get(i).toString());
							seleccionUbicacionEN ubi = new seleccionUbicacionEN();
							ubi.setDireccion(item.getString("direccion"));
							ubi.setLongitud(Double.valueOf(item.getString("longitud")));
							ubi.setLatitud(Double.valueOf(item.getString("latitud")));
							crearEventoActivity.opcionesDireccion[i] = ubi;
						}
						crearEventoActivity.Creacionsatisfactoria = 3;

					} else if (total.toString().equals("ERROR_NO_RESULT_VERIFIED"))
						crearEventoActivity.Creacionsatisfactoria = 4;
					else
						crearEventoActivity.Creacionsatisfactoria = 1;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
