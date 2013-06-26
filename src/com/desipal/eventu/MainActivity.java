package com.desipal.eventu;

import java.util.Locale;

import com.desipal.Librerias.TestFragmentAdapter;
import com.desipal.eventu.R;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

	static Locale currentLocale = new Locale("es", "ES");

	boolean bloquearPeticion = false;// bandera que bloquea para no poder jhacer la peticion
	boolean finPaginadoCerca = false;// bandera para saber que no tiene que hacer mas peticiones en cerca

	Button btnCrearEvento;

	TestFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		mIndicator.setViewPager(mPager);

		btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);

		btnCrearEvento.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, crearEventoActivity.class);
				startActivity(i);
			}
		});
	}
}