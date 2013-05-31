package com.desipal.EventU;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class crearEventoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crearevento);

		Spinner spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.categorias, R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);
		Spinner spiEdad = (Spinner) findViewById(R.id.spiEdad);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				this, R.array.edad, R.layout.spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiEdad.setAdapter(adapter2);

		Button btnInicio = (Button) findViewById(R.id.btnInicio);
		btnInicio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(crearEventoActivity.this,
						MainActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Apéndice de método generado automáticamente
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
}
