package com.desipal.Librerias;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Servidor.EnvioComentario;
import com.desipal.eventu.MainActivity;
import com.desipal.eventu.R;
import com.desipal.eventu.detalleEventoActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class ratingpicker extends DialogFragment {

	private static final String KEY_SAVE_RATING_BAR_VALUE = "KEY_SAVE_RATING_BAR_VALUE";
	public static boolean Enviado;
	private int idEvento;
	private RatingBar mRatingBar;
	private EditText opinion;

	public static ratingpicker newInstance() {
		ratingpicker frag = new ratingpicker();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.rating_dialog, null);
		Enviado = false;
		mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar1);
		opinion = (EditText) view.findViewById(R.id.editComentario);
		this.idEvento = (int) detalleEventoActivity.idEvento;
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_SAVE_RATING_BAR_VALUE)) {
				mRatingBar.setRating(savedInstanceState
						.getFloat(KEY_SAVE_RATING_BAR_VALUE));
			}
		}

		alertDialogBuilder.setView(view);
		// alertDialogBuilder.setTitle(getString(R.string.dialogTitle));
		alertDialogBuilder.setPositiveButton(
				getString(R.string.dialog_positive_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, int which) {
						final Resources res = detalleEventoActivity.act
								.getResources();
						if (!opinion.getText().toString().equals("")) {
							ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
							parametros.add(new BasicNameValuePair("idEvento",
									idEvento + ""));
							parametros
									.add(new BasicNameValuePair(
											"idDispositivo",
											MainActivity.IDDISPOSITIVO));
							parametros.add(new BasicNameValuePair("comentario",
									opinion.getText().toString()));
							parametros.add(new BasicNameValuePair("valoracion",
									mRatingBar.getRating() + ""));
							String URL = "http://desipal.hol.es/app/eventos/nuevoComentario.php";
							final EnvioComentario peticion = new EnvioComentario(
									parametros, getActivity());
							peticion.execute(new String[] { URL });
							final Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									Status s = peticion.getStatus();
									if (s.name().equals("FINISHED")) {
										if (Enviado) {
											Toast.makeText(
													detalleEventoActivity.act,
													res.getString(R.string.dialogComentadoOK),
													Toast.LENGTH_LONG).show();
											dialog.dismiss();
										} else
											Toast.makeText(
													detalleEventoActivity.act,
													res.getString(R.string.dialogComentadoNULL),
													Toast.LENGTH_LONG).show();

									} else
										handler.postDelayed(this, 500);
								}
							}, 500);
						} else {
							Toast.makeText(detalleEventoActivity.act,
									res.getString(R.string.dialogNoComentado),
									Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialogBuilder.setNegativeButton(
				getString(R.string.dialog_negative_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});
		return alertDialogBuilder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putFloat(KEY_SAVE_RATING_BAR_VALUE, mRatingBar.getRating());
		super.onSaveInstanceState(outState);
	}
}
