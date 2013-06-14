package com.desipal.EventU;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Gallery;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class galeriaActivity extends Activity {

	Gallery galeria;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.galeriacompleta);
		try {
			galeria = (Gallery) findViewById(R.id.galeriaFull);
			galeria.setAdapter(new galeriaAdapter(this, detalleEventoActivity.imagenes,true));
		} catch (Exception ex) {
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT).show();
		}
	}

}
