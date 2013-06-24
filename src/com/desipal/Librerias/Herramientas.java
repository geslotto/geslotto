package com.desipal.Librerias;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;

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

	public static LatLng ObtenerLocalizacion(Context c) {
		LatLng loca = null;
		try {
			LocationManager lm = (LocationManager) c
					.getSystemService(Context.LOCATION_SERVICE);
			Location loc = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			loca = new LatLng(loc.getLatitude(), loc.getLongitude());

		} catch (Exception e) {
			loca = null;
		}
		return loca;
	}

}
