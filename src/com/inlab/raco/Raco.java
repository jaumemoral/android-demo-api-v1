package com.inlab.raco;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;
import oauth.signpost.OAuthConsumer;

public class Raco {
	
	User user;
	private static Raco raco=null;	
	
	private OAuthConsumer consumer;
    String URL_INFO_PERSONAL="https://raco.fib.upc.edu/api-v1/info-personal.json";

	
	public static Raco getInstance() {		
		if (raco==null) {
			raco=new Raco();
		}
		return raco;
	}
	
	public void setConsumer(OAuthConsumer consumer) {
		this.consumer=consumer;
	}

	public boolean isLogged() {
		return (user!=null);		
	}
	
	public void logout() {
		user=null;		
	}	

	public void setUser(User user) {
		this.user=user;
	}	
	
	public User getUser() {
		if (user!=null) return user;
		try {
			String json=demana(URL_INFO_PERSONAL);
			JSONObject jObject = new JSONObject(json);
			User u=new User();			
			u.setUsername(jObject.getString("username"));
			u.setMail(jObject.getString("mail"));
			u.setNom(jObject.getString("nom"));
			u.setCognoms(jObject.getString("cognoms"));
			return u;
		} catch (Exception e) {
			return null;
		}
	}
		
	private String demana (String url) {
		StringBuffer aux=new StringBuffer();
		try {
		URL u = new URL(url);
		HttpURLConnection request = (HttpURLConnection) u.openConnection();
		consumer.sign(request);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				request.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			aux.append(inputLine);
		}
		} catch (Exception e) {
			Log.i("oauth",e.getMessage());
		}
		return aux.toString();

	}
	
}
