package com.inlab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.inlab.raco.Raco;
import com.inlab.raco.User;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RacoOAuthActivity extends Activity implements View.OnClickListener {
		
    private OAuthProvider provider = new DefaultOAuthProvider(
        "https://raco.fib.upc.edu/oauth/request_token",
        "https://raco.fib.upc.edu/oauth/access_token",
        "https://raco.fib.upc.edu/oauth/protected/authorize");	

    String callback="raco://raco";
    String key="xxxxxxxxxxxx";
    String secret="yyyyyyyyyyyyy";

	private DefaultOAuthConsumer consumer;
	SharedPreferences prefs;
	TextView username;
	TextView nom;
	TextView cognoms;
	TextView mail;
	Button botoEntrar;
	boolean loguejat;
				
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Quan creem l'activity, mirem si ja tenim el token i es valid. També haurem catxejar la info de l'usuari
		// Si ja el tenim, mostrem qui som i un botó de logout
		// Si no el tenim, mostrem un botó de login que iniciara "The Dance"
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		View v=findViewById(R.id.login);
		v.setOnClickListener(this);

		consumer = new DefaultOAuthConsumer(key,secret);
		recuperaTokens();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		username=(TextView)findViewById(R.id.username);
		nom=(TextView)findViewById(R.id.nom);
		cognoms=(TextView)findViewById(R.id.cognoms);
		mail=(TextView)findViewById(R.id.mail);
		botoEntrar=(Button)findViewById(R.id.entrar);

		Raco r=Raco.getInstance();
		if (r.isLogged()) {
			// Ja tenim les dades de l'usuari
			mostraDadesUsuari(r.getUser());
		} else if (prefs.getBoolean("tokensOk",false)) {
			// Tenim el token pero no les dades de l'usuari
			new DemanaUserAsync().execute();			
		}
		// Si no tenim res, doncs esperem a fer login (tenim el boto de continuar desactivat)
	}
	
	public void mostraDadesUsuari(User u) {
	    username.setText(u.getUsername());
	    nom.setText(u.getNom());
	    cognoms.setText(u.getCognoms());		    
	    mail.setText(u.getMail());
	    botoEntrar.setEnabled(true);
	}

	protected void recuperaTokens() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String token = prefs.getString("token", null);
		String tokenSecret = prefs.getString("tokenSecret", null);		
		if (token != null && tokenSecret != null) {
			consumer.setTokenWithSecret(token, tokenSecret);			
			Raco.getInstance().setConsumer(consumer);
		}
		// Si tenim cache de l'usuari, el carreguem
		String username = prefs.getString("username", null);
		if (username!= null) {
			User u=new User();
			u.setUsername(username);
			u.setNom(prefs.getString("nom", null));
			u.setCognoms(prefs.getString("cognoms", null));
			u.setMail(prefs.getString("mail", null));
			Raco.getInstance().setUser(u);
		}
	}

	protected void guardaTokens() {
		guardaTokens(false);
	}
	
	protected void guardaTokens(boolean access) {
		SharedPreferences.Editor prefsEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		prefsEdit.putString("token",consumer.getToken());
		prefsEdit.putString("tokenSecret",consumer.getTokenSecret());
		prefsEdit.putBoolean("tokensOk",access); 
		prefsEdit.commit();
		Log.d("oauth","guardo token:"+consumer.getToken());
		Log.d("oauth","guardo tokenSecret:"+consumer.getTokenSecret());
		Log.d("oauth","guardo tokensOk:"+access);
	}

	protected void guardaUsuari(User u) {
		SharedPreferences.Editor prefsEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		prefsEdit.putString("username",u.getUsername());
		prefsEdit.putString("nom",u.getNom());
		prefsEdit.putString("cognoms",u.getCognoms());
		prefsEdit.putString("mail",u.getMail());
		prefsEdit.commit();
	}	
	
	protected void esborraTokens() {
		SharedPreferences.Editor prefsEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		prefsEdit.putString("token",null);
		prefsEdit.putString("tokenSecret",null);
		prefsEdit.putBoolean("tokensOk",false); 
		prefsEdit.putString("username",null);
		prefsEdit.putString("nom",null);
		prefsEdit.putString("cognoms",null);
		prefsEdit.putString("mail",null);		
		prefsEdit.commit();
	}	
	
	// Demano el token de forma asincrona per evitar l'excepcio     
	// android.os.NetworkOnMainThreadException
	
	@Override
	public void onClick(View v) {
		Log.i("oauth","Click!!");
		// Si tinc no tinc usuari, començo el proces. Si ja el tinc, faig logout
		if (!Raco.getInstance().isLogged()) {
			new DemanaRequestTokenAsync().execute();
		} else {
			esborraTokens();
			Raco.getInstance().logout();
			username.setText(R.string.no_login);
			nom.setText("");
			cognoms.setText("");
			mail.setText("");
		    botoEntrar.setEnabled(false);			
		}
	}
	
	class DemanaRequestTokenAsync extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			String authURL=null;
			try {
		        authURL = provider.retrieveRequestToken(consumer, callback);
				guardaTokens();
			} catch (Exception e) {
		   		e.printStackTrace();
			}
	        return authURL;
		}

		// Una vegada tinc el token obro ja el navegador. D'event en event...
		// Important! Abans de continguar he de guardar els tokens (no se perque, pero cal fer-ho)
		
		@Override
		protected void onPostExecute(String authURL) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
		}
	}
	
	// Un cop hem validat el token, arribem a l'activitat una altra vegada
	// per recollir-lo
    
    @Override
    public void onResume() {
	    super.onResume();
	    Uri uri = this.getIntent().getData();
        recuperaTokens();

	    // Aixo es el cas en que tornem amb token. Si no, no fem res
	    if (uri != null && uri.toString().startsWith(callback)) {
			new DemanaAccessTokenAsync().execute();
	    }
    }
    
	class DemanaAccessTokenAsync extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				provider.retrieveAccessToken(consumer,null);
				guardaTokens(true);
			} catch (Exception e) {
				Log.d("oauth","ha petat al access token");
			}
			return null;
		}

		// Una vegada tinc el access token i secret, ja puc fer feina...
		
		@Override
		protected void onPostExecute(Void result) {
		    // Aqui el consumer s'ha actualitzat amb els 2 access tokens que ja li permeten fer les peticions
		    // als nostres webservices. Aquests valor s'haurien de guardar i abans de tot fer un 
		    // consumer.setTokenWithSecret si existeixen. Amb aixo i demanant la info ja sabem qui som
			Raco r=Raco.getInstance();
			r.setConsumer(consumer);			
			new DemanaUserAsync().execute();
		}
	}
    
    
	class DemanaUserAsync extends AsyncTask<Void, Void, User> {
		
		@Override
		protected User doInBackground(Void... params) {
			return Raco.getInstance().getUser();
		}	    
		
		@Override
		protected void onPostExecute(User u) {
		    Log.i("resultat",u.getUsername());		    
		    mostraDadesUsuari(u);
			guardaUsuari(u);		    
		}
	}
	
	// Quan clickem per continuar l'aplicacio, obrim l'activity dels tabs
	
	public void entrar(View v) {
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
	}
}    
	
