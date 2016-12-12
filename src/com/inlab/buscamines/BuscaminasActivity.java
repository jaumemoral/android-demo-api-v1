package com.inlab.buscamines;

import com.inlab.MenuActivity;
import com.inlab.R;
import com.inlab.records.RecordsActivity;
import com.inlab.records.RecordsOpenHelper;
import com.inlab.buscamines.BuscaminasActivity;
import com.inlab.raco.Raco;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BuscaminasActivity extends MenuActivity implements OnClickListener, OnLongClickListener {
	
	Buscaminas buscaminas;
	Button[][] botons;
	int mida=8;
	int mines=10;
	TextView crono;
	Thread actualitzadorCrono=null;
	boolean cronoAturat=false;
	Handler handler=new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected void onResume() {
		super.onResume();
		if (buscaminas==null || !buscaminas.enMarxa()) dialogInici();
	}    
    
    public void inicialitza() {
        setContentView(R.layout.buscamines);
        crono=(TextView)findViewById(R.id.crono);    	
        crono.setText("0s");        
        botons=new Button[mida][mida];
        buscaminas=new Buscaminas(mida,mines);        
        TableLayout camp=(TableLayout)findViewById(R.id.camp);
    	for (int i=0;i<mida;i++) {
    		TableRow tr=new TableRow(this);
    		camp.addView(tr);
    		for (int j=0;j<mida;j++) {
    			Button b=new Button(this);
    			b.setTextSize(25);
    			b.setText("   ");
    			b.setId(i*mida+j);
        		b.setOnClickListener(this);
        		b.setOnLongClickListener(this);
        		tr.addView(b);
        		botons[i][j]=b;
    		}
    	}
    	engegaCrono();
    }    
    
    public void engegaCrono() {
    	cronoAturat=false;
    	Log.i("serveis","comencem thread...");
    	actualitzadorCrono=new Thread(new Runnable() {
    		public void run() {
    			while (true) {
    				try {Thread.sleep(1000);} catch (Exception e) {;}
    				if (cronoAturat) break;
    				handler.post(new Runnable(){
    					public void run(){
    						crono.setText(buscaminas.getSegons()+"s");
    					}
    				});
    			}
    		}
    	});
    	actualitzadorCrono.start(); 
    }
    
    public void aturaCrono() {
    	cronoAturat=true;
    }
   
    public void repinta() {        
    	for (int i=0;i<mida;i++) {
    		for (int j=0;j<mida;j++) {
    			Button boto=botons[i][j];
    			if (buscaminas.destapada(i,j)) {     				    				
    				boto.setText(""+buscaminas.contingut(i,j));
    				switch (buscaminas.contingut(i,j)) {
						case '0': boto.setEnabled(false);
								  break;
    					case '1': boto.setTextColor(Color.BLACK); break;
    					case '2': boto.setTextColor(Color.BLUE); break;
    					case '3': boto.setTextColor(Color.GREEN); break;
    					case '4': boto.setTextColor(Color.YELLOW); break;
    					case '*': boto.setTextColor(Color.RED); 
    				}
    				boto.setClickable(false);
    			} else  if (buscaminas.teBandera(i,j)) {
    				boto.setText("*");
    				boto.setTextColor(Color.WHITE);
    	        	boto.setClickable(false);
    			}
    		}
    	}
    }

        
    @Override
    public void onClick(View v) {
    	Log.i("buscaminas", "id:"+v.getId());
    	char casella=buscaminas.destapa(v.getId());
    	repinta();
    	Log.d("buscamines","queden per destapar "+buscaminas.quedenPerDestapar());
    	Log.d("buscamines","destapo -"+casella+"-");
    	
    	if (casella=='*') {
    		Log.d("buscamines","booom!");
    		aturaCrono();    		
    		buscaminas.atura();
    		dialogFinalitzacio("Has trepitjat una mina!");
    	} else if (buscaminas.quedenPerDestapar()==0) {
    		int temps=buscaminas.getSegons();
    		aturaCrono();
    		buscaminas.atura();    		
    		dialogFinalitzacio("Has acabat el joc en "+temps+"s");
    		RecordsOpenHelper bd=new RecordsOpenHelper(this);
    		bd.afegir(Raco.getInstance().getUser().getUsername(),temps);
    	}
    }
    
    void dialogFinalitzacio(String missatge) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(missatge)
    	   .setCancelable(false)
           .setPositiveButton("Nova partida", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   dialog.cancel();
            	   inicialitza();
               }
           })
           .setNegativeButton("Sortir", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
            		buscaminas.destapaTot();
            		repinta();
               }
           });
           
        AlertDialog alert = builder.create();
        alert.show();
    }

    void dialogInici() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Preparat per buscar mines?")
    	   .setCancelable(false)
           .setPositiveButton("Endavant!", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   dialog.cancel();
            	   inicialitza();
               }
           });
           
        AlertDialog alert = builder.create();
        alert.show();
    }
    

    @Override
    public boolean onLongClick(View v) {
    	
    	Log.i("buscaminas", "long id:"+v.getId());
    	if (buscaminas.destapada(v.getId())) return true;
    	Button b=(Button)v;

    	// Anem avisant
    	Context context=getApplicationContext();
    	if (!buscaminas.teBandera(v.getId())) {
        	b.setClickable(false);
        	b.setTextColor(Color.WHITE);
        	b.setText("*");
        	buscaminas.posaBandera(v.getId());
    		Toast toast=Toast.makeText(context,"Bandera posada",Toast.LENGTH_SHORT);
    		toast.show();    		
    	} else {
    		buscaminas.treuBandera(v.getId());
        	b.setClickable(true);
        	b.setText("");
    		Toast toast=Toast.makeText(context,"Bandera treta",Toast.LENGTH_SHORT);
    		toast.show();    		
    	}
    	// Aixo es que no faci un click normal
    	return true;
    }
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item)) return true;
		if (item.getItemId()==R.id.opcio_restart) {
			inicialitza();
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.buscamines,menu);
		return true;
	}

	// Aixo es perque no giri. Canviant el xml no m'ha fet cas 
	// Sembla que crida al onCreate i no se com fer que no sigui aixi :(	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	
}