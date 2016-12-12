package com.inlab;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import com.inlab.R;
import com.inlab.buscamines.BuscaminasActivity;
import com.inlab.musica.MusicaActivity;
import com.inlab.records.RecordsActivity;
import com.inlab.calculadora.CalculadoraActivity;
import com.inlab.perfil.PerfilActivity;

public class MainActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TabHost tabs = getTabHost();
		tabs.setup();

		TabHost.TabSpec spec;

		
		spec = tabs.newTabSpec("records");
		Intent records= new Intent(this, RecordsActivity.class);
        spec.setContent(records);
        spec.setIndicator("Records");
		tabs.addTab(spec);						
		
		spec = tabs.newTabSpec("buscaminas");
		Intent buscamines= new Intent(this, BuscaminasActivity.class);
        spec.setContent(buscamines);
        spec.setIndicator("Buscamines");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("perfil");
		Intent perfil= new Intent(this, PerfilActivity.class);
        spec.setContent(perfil);
        spec.setIndicator("Perfil");
		tabs.addTab(spec);		
		
		spec = tabs.newTabSpec("musica");
		Intent musica= new Intent(this, MusicaActivity.class);
        spec.setContent(musica);
        spec.setIndicator("Musica");
		tabs.addTab(spec);		

		spec = tabs.newTabSpec("calculadora");
		Intent calculadora= new Intent(this, CalculadoraActivity.class);
        spec.setContent(calculadora);
        spec.setIndicator("Calculadora");
		tabs.addTab(spec);					
		
		tabs.setCurrentTab(0);
	}
}
