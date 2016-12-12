package com.inlab.musica;

import com.inlab.MenuActivity;
import com.inlab.R;
import com.inlab.musica.MyBoundService;
import com.inlab.musica.MyBoundService.MyBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MusicaActivity extends MenuActivity {
	
	boolean bound=false;
	MyBoundService servei;
	Handler handler;
	ProgressBar progress; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musica);
        Intent intent=new Intent(getApplicationContext(),com.inlab.musica.MyBoundService.class);
        startService(intent);
    	
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	desconnectarServei();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		connectarServei();
    }
    /*
    @Override
    protected void onPause() {
    	super.onPause();
		desconnectarServei();
    }
    */
    
    public void connectarServei() {
    	if (!bound) {
    		Toast.makeText(this,"Connecto amb el servei",Toast.LENGTH_SHORT).show();		
        	Intent intent=new Intent(getApplicationContext(),com.inlab.musica.MyBoundService.class);
        	getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);    	    		
    	}
    }

    public void desconnectarServei() {
    	if (bound) {
    		Toast.makeText(this,"Desconnecto del servei",Toast.LENGTH_SHORT).show();		    		
    		getApplicationContext().unbindService(connection);
    		//unbindService(connection);
    	}
    }    
    
    private ServiceConnection connection=new ServiceConnection() {
    	public void onServiceConnected(
    			android.content.ComponentName className, 
    			android.os.IBinder service) {
    		MyBinder binder=(MyBinder)service;
    		MusicaActivity.this.servei=binder.getService();
    		MusicaActivity.this.bound=true;
    	}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			MusicaActivity.this.bound=false;
		};
    };

    // Events
    
    public void start(View v) {
    	if (bound) servei.play();
    	else {
    		Toast.makeText(this,"No esta bounded",Toast.LENGTH_SHORT).show();
    		connectarServei();
    	}
    }    
    
    public void stop(View v) {
    	Log.i("serveis","stop");
    	if (bound) servei.stop();
    	else {
    		Toast.makeText(this,"No esta bounded",Toast.LENGTH_SHORT).show();
    		connectarServei();    		    	
    	}
    }
    
    public void pause(View v) {
    	Log.i("serveis","pause");
    	// Aqui ja estem bounded i podem fer pause
    	if (bound) servei.pause();
    	else  {
    		Toast.makeText(this,"No esta bounded",Toast.LENGTH_SHORT).show();
    		connectarServei();
    	}
    }
}
    
