package com.inlab.records;

import com.inlab.R;
import com.inlab.MenuActivity;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecordsActivity extends MenuActivity {

	EditText user;
	EditText number;
    ListView listView;	
	RecordsOpenHelper bd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.records);

		bd=new RecordsOpenHelper(this);		
		listView = (ListView) findViewById(R.id.listView1);
		assignaAdapter();
	}
	
	// Tornem a refrescar quan mostrem
	@Override
	protected void onResume() {
		super.onResume();
		assignaAdapter();
	}
	
	public void assignaAdapter() {
		Cursor cursor=bd.llista();
	    startManagingCursor(cursor);	    
	    String[] from = { "user","number" };
	    int[] to = { R.id.textUser, R.id.textNumber }	;
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
	    		R.layout.row, cursor, from, to);
	    listView.setAdapter(adapter);
	    bd.close();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item)) return true;
		if (item.getItemId()==R.id.opcio_reset) {
			bd.esborrarTot();
			assignaAdapter();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.records,menu);
		return true;
	}		

}