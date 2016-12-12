package com.inlab.calculadora;

import com.inlab.MainActivity;
import com.inlab.MenuActivity;
import com.inlab.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.congrace.exp4j.*;

public class CalculadoraActivity extends MenuActivity implements OnClickListener {

	TextView pantalla;
	int tipusNotificacions=R.id.notificacions_pantalla;
	
	public static final String PREFS_NAME="state";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculadora);

		this.pantalla = (TextView) findViewById(R.id.pantalla);
		TableLayout botonera = (TableLayout) findViewById(R.id.botons);
		assignaListener(botonera);
		registerForContextMenu(botonera);
	}

	@Override
	protected void onResume() {
		super.onResume();
		recuperaEstat();
	}
	
	private void assignaListener(ViewGroup v) {
		for (int i = 0; i < v.getChildCount(); i++) {
			View fill = v.getChildAt(i);
			Log.i("calculadora", "Assigno click a " + v.toString());
			if (fill instanceof Button) fill.setOnClickListener(this);
			if (fill instanceof ViewGroup) assignaListener((ViewGroup)fill);
		}
	}

	public void onClick(View v) {
		Button b = (Button) v;
		char boto = b.getText().charAt(0);
		Log.i("calculadora", "boto:" + boto);
		String text = pantalla.getText().toString();
		switch (boto) {
		case 'c':
			pantalla.setText("");
			break;
		case 'd':
			if (text.length()>0) pantalla.setText(text.substring(0,text.length()-1));
			break;			
		case '=':
			try {
				text=avaluar(text);
				if (text.equals("Infinity")) throw (new Exception("Divisio per zero"));
				if (text.equals("NaN")) throw (new Exception("Valor indeterminat"));
				pantalla.setText(text);
			} catch (Exception e) {
				String m=e.getMessage();
				if (m==null) m="";
				notificaError("Error! "+m);
			}
			break;
		default:
			pantalla.setText(text + boto);
		}
	}
	
	public void notificaError(String missatge) {
		// Hem de mirar la configuraci� a veure com volem les notificacions
		if (tipusNotificacions==R.id.notificacions_pantalla) {
			Context context=getApplicationContext();
			Toast toast=Toast.makeText(context,missatge,Toast.LENGTH_SHORT);
			toast.show();
		} else {
			crearNotificacio(missatge);
		}
	}
	
	public void crearNotificacio(String text) {
		NotificationManager myNManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence texto1 = text;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, texto1,
				when);
		Context context = getApplicationContext();

		CharSequence contentTitle = text;
		CharSequence contentText = text;
		// Aqui posem l'activity on volem anar quan clickem la notificació
		// Si no tinguessim tabs anirniem a la de la calculadora
		Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle,
				contentText, contentIntent);
		// Amb aixo faig que quan clickem a sobre desaparegui
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNManager.notify(1, notification);
    }
	

	public String avaluar(String text) throws Exception  {
		Calculable c = new ExpressionBuilder(text).build();
		return "" + c.calculate();
	}
	
	// Aixo per mantenir l'estat. En aquest cas, el que tenim calculat
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String text = pantalla.getText().toString();
		outState.putString("text",text);
	}
		
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		pantalla.setText(savedInstanceState.getString("text"));		
	}
	
	// Aixi mantenim l'estat fins i tot quan tanquem l'aplicaci� (amb shared preferences)
	// Recuperem al fer el onCreate
		
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("calculadora","Ara paro i guardo");
		guardaEstat();
	}

	public void recuperaEstat() {
		Log.i("calculadora","Recupero");
		SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
		String text=settings.getString("pantalla", "");
		tipusNotificacions=settings.getInt("tipusNotificacions",R.id.notificacions_pantalla);
		pantalla.setText(text);				
	}

	public void guardaEstat() {
		Log.i("calculadora","Guardo");
		SharedPreferences settings=getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor=settings.edit();
		editor.putString("pantalla", pantalla.getText().toString());
		editor.putInt("tipusNotificacions", tipusNotificacions);
		editor.commit();
	}
	
	// Ara fem que la fletxa enrera mostri un dialeg per veure si vols sortir o no realment
	// Dinre dels tabs ho desactivem

	/*
	@Override
	public void onBackPressed() {
		showDialog(0);
	}
	*/
	
	// Aixo no ho executa ningun ara mateix
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// Com que nomes tenim un dialog no hem de fer cap switch
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setMessage("Vols sortir realment?");
	
		builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				CalculadoraActivity.this.finish();
			}
		});
		
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
	
	
	// Menu per poder triar com volem les notificacions
	// Volia fer un contextmenu pero no em surt i faig un de options

	/*
	   @Override
	    public void onCreateContextMenu(ContextMenu menu, View v,
	    		ContextMenuInfo menuInfo) {
	    	super.onCreateContextMenu(menu, v, menuInfo);
	    	MenuInflater inflater=getMenuInflater();
	    	inflater.inflate(R.menu.notificacions,menu);
	    	// Per dir-li el seleccionat hem d'iterar...
	    	for (int i=0;i<menu.size();i++)
	    		if (menu.getItem(i).getItemId()==tipusNotificacions)
	    			menu.getItem(i).setChecked(true);
	    }	   
	   
	   @Override	   	    
	    public boolean onContextItemSelected(MenuItem item) {
	    	item.setChecked(true);
	    	this.tipusNotificacions=item.getItemId();
	    	return true;
	    }
*/
	   
	   // Options menu amb els intents implicits (a part del logout que ja tracta el pare)
	   
		public boolean onOptionsItemSelected(MenuItem item) {
			if (super.onOptionsItemSelected(item)) return true;
			Intent i;
			switch (item.getItemId()) {
				case R.id.obrir_trucar:
					i=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:620535332"));
					startActivity(i);					
					return true;
				case R.id.obrir_navegador:					
					i=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
					startActivity(i);					
					return true;
				case R.id.notificacions_barra:
				case R.id.notificacions_pantalla:
			    	item.setChecked(true);
			    	this.tipusNotificacions=item.getItemId();
			    	guardaEstat();
			    	return true;
			}
			return false;
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater=getMenuInflater();
			inflater.inflate(R.menu.calculadora,menu);
			return true;
		}
		
		// Aqui pinto la opcio seleccionada, que aixo es crida
		// sempre que es mostra el menu, no només al crear-lo
		// Hem d'iterar el submenu perque funcioni.
		
		@Override
		public boolean onPrepareOptionsMenu(Menu menu) {
			Log.i("calculadora","preparemenu "+ tipusNotificacions);
			Menu submenu=menu.getItem(0).getSubMenu();
	    	for (int i=0;i<submenu.size();i++) {
				Log.i("calculadora","itero preparemenu "+ submenu.getItem(i).getItemId());
	    		if (submenu.getItem(i).getItemId()==tipusNotificacions)
	    			submenu.getItem(i).setChecked(true);
	    	}
	    	return true;
		}
	   
}