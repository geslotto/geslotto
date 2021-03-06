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

import com.desipal.eventu.detalleEventoActivity;

import android.os.AsyncTask;

public class asistenciaEvento extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;
	private boolean asiste;

	public asistenciaEvento(ArrayList<NameValuePair> parametros) {
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

				if (total.toString().equals("ok"))
					this.asiste = true;
				else
					this.asiste = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		detalleEventoActivity.asiste = this.asiste;
		return;
	}

}
