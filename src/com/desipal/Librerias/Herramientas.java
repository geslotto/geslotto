package com.desipal.Librerias;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.desipal.Entidades.categoriaEN;
import com.desipal.eventu.MainActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Herramientas {

	public static Bitmap disminuirImagen(Bitmap bm, int nuevaMedida) {
		Bitmap resizedBitmap;
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		float aspect = 0;
		if (height > nuevaMedida || width > nuevaMedida) {
			if (height > width) {
				aspect = (float) height / width;
				scaleHeight = nuevaMedida;
				scaleWidth = scaleHeight / aspect;

			} else if (width > height) {
				aspect = (float) width / height;
				scaleWidth = nuevaMedida;
				scaleHeight = scaleWidth / aspect;
			} else if (width == height) {
				scaleWidth = nuevaMedida;
				scaleHeight = nuevaMedida;
			}
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth / width, scaleHeight / height);
			resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, true);
		} else
			resizedBitmap = bm;
		return resizedBitmap;
	}

	public static Bitmap aumentarImagen(Bitmap bm, int nuevaMedida) {
		Bitmap resizedBitmap;
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		float aspect = 0;

		if (height > width) {
			aspect = (float) height / width;
			scaleHeight = nuevaMedida;
			scaleWidth = scaleHeight / aspect;

		} else if (width > height) {
			aspect = (float) width / height;
			scaleWidth = nuevaMedida;
			scaleHeight = scaleWidth / aspect;
		} else if (width == height) {
			scaleWidth = nuevaMedida;
			scaleHeight = nuevaMedida;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth / width, scaleHeight / height);
		resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);

		return resizedBitmap;
	}

	public static int calcularZoom(int ratio) {
		int zoom = 10;
		if (ratio < 2)
			zoom = 14;
		else if (ratio < 4)
			zoom = 13;
		else if (ratio < 10)
			zoom = 12;
		else if (ratio < 15)
			zoom = 11;
		else if (ratio < 26)
			zoom = 10;
		else if (ratio < 56)
			zoom = 9;
		else if (ratio < 101)
			zoom = 8;

		return zoom;
	}

	public static List<categoriaEN> Obtenercategorias(Activity act) {
		BufferedReader fin;
		List<categoriaEN> lista = new ArrayList<categoriaEN>();
		try {
			fin = new BufferedReader(new InputStreamReader(
					act.openFileInput(MainActivity.fCategorias)));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = fin.readLine()) != null) {
				total.append(line);
			}
			fin.close();
			if (!fin.toString().equals("")) {
				JSONArray o = new JSONArray(total.toString());
				for (int i = 0; o.length() > i; i++) {
					categoriaEN ca = new categoriaEN();
					JSONObject jobj = o.getJSONObject(i);
					ca.setIdCategoria(jobj.getInt("idCategoria"));
					ca.setTexto(jobj.getString("texto"));
					ca.setDescripcion(jobj.getString("descripcion"));
					lista.add(ca);
				}
			} else
				lista = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lista = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lista = null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lista = null;
		}

		return lista;

	}

}
