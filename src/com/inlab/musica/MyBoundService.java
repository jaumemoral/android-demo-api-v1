package com.inlab.musica;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyBoundService extends Service implements OnCompletionListener{
	
	// Tot aixo es necessari pel tema del bound service
	private final IBinder binder=new MyBinder();
	public class MyBinder extends Binder {
		MyBoundService getService() {
			return MyBoundService.this;
		}
	}
	
	String mp3="journey.mp3";
	MediaPlayer mediaPlayer;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer=new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);					
		inicialitza();
	}
	
	public void inicialitza() {
		try {			
			File sdCard=Environment.getExternalStorageDirectory();
			File song= new File (sdCard.getAbsolutePath()+"/music/"+mp3);
			mediaPlayer.setDataSource(song.getAbsolutePath());
			mediaPlayer.prepare();		
		} catch (Exception e) {
			Log.i("serveis","Peto!!!! " +e.getMessage());
		}			
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		Toast.makeText(this,"Ja ha acabat la musica",Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this,"Bind correcte!!",Toast.LENGTH_SHORT).show();
		return binder;
	}	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this,"Start command",Toast.LENGTH_SHORT).show();		
		return START_STICKY;
	}	
	
	public void play () {
		Toast.makeText(this,"Play!!",Toast.LENGTH_SHORT).show();
		mediaPlayer.start();
	}
	
	public void pause() {
		Toast.makeText(this,"Pause!!",Toast.LENGTH_SHORT).show();
		
		if (mediaPlayer.isPlaying()) 
			mediaPlayer.pause();
		else
			mediaPlayer.start();
	}
	
	public void stop() {
		Toast.makeText(this,"Stop!!",Toast.LENGTH_SHORT).show();		
		mediaPlayer.stop();
		inicialitza();
	}
	
	@Override
	public void onDestroy() {
		mediaPlayer.stop();
		mediaPlayer.release();
		Toast.makeText(this,"Stop!!",Toast.LENGTH_SHORT).show();
	}
	
}
