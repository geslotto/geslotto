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

import com.desipal.Librerias.ratingpicker;
import android.app.Activity;
import android.os.AsyncTask;

public class EnvioComentario extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;
	Activity act;

	public EnvioComentario(ArrayList<NameValuePair> parametros, Activity act) {
		this.parametros = parametros;
		this.act = act;
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
				BufferedReader r = new BufferedReader(new InputStreamReader(
						content));
				StringBuilder total = new StringBuilder();

				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				if (total.toString().equals("ok")) {
					ratingpicker.Enviado = true;
				} else {
					ratingpicker.Enviado = false;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
