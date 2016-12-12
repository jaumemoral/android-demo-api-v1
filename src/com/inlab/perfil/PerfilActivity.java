package com.inlab.perfil;

import java.io.File;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inlab.MenuActivity;
import com.inlab.R;
import com.inlab.RacoOAuthActivity;
import com.inlab.raco.Raco;
import com.inlab.raco.User;
import com.inlab.records.RecordsOpenHelper;

public class PerfilActivity extends MenuActivity implements OnClickListener {

	private static final int SELECT_PICTURE = 1;
	private final String AVATAR="imatgeavatar";

	private static final String TAG = "perfil";
	TextView nom;
	TextView username;
	TextView record;
	TextView mail;
	ImageView avatar;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.perfil);
		nom=(TextView)findViewById(R.id.textView1);
		username=(TextView)findViewById(R.id.textView2);
		mail=(TextView)findViewById(R.id.textView3);
		record=(TextView)findViewById(R.id.textView4);
		avatar=(ImageView)findViewById(R.id.avatar);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i(TAG,"Ja tinc un shared");
		String selectedImage= prefs.getString(AVATAR, null);
		
		if (selectedImage != null) {
			Log.i(TAG,"Recupero del shared");
			avatar.setImageURI(Uri.parse(selectedImage));
		}
		
		User u=Raco.getInstance().getUser();
		nom.setText(u.getUsername());
		username.setText(u.getNom()+ " "+ u.getCognoms());
		mail.setText(u.getMail());
		RecordsOpenHelper bd=new RecordsOpenHelper(this);
		int punts=bd.getRecord(u.getUsername());
		if (punts>0) {
			record.setText("record: "+ punts);			
		} else {
			record.setText("No té cap puntuació");
		}
		
		Button b=(Button)findViewById(R.id.canvia_foto);
		b.setOnClickListener(this);		

		b=(Button)findViewById(R.id.opcio_logout);
		b.setOnClickListener(this);		

	}

	@Override
	public void onClick(View v) {	
		switch (v.getId()) {
		case R.id.canvia_foto:
			Intent intent = new Intent();
		    intent.setType("image/*");
		    intent.setAction(Intent.ACTION_GET_CONTENT);
		    startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
		    break;
		case R.id.opcio_logout:
			Intent intent1=new Intent(this,RacoOAuthActivity.class);
			startActivity(intent1);
			break;
		}
	}
			

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode == RESULT_OK) {  
	    switch(requestCode){    
	         case SELECT_PICTURE:
	        	  Log.i(TAG,"Ja tinc la picture");
	              Uri selectedImageUri = data.getData();
	              avatar.setImageURI(selectedImageUri);
	              SharedPreferences.Editor editor=prefs.edit();
	              editor.putString(AVATAR, selectedImageUri.toString());
	              editor.commit();
	              break;
	        }  
	    }        
	}
	
}