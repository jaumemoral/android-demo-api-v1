package com.inlab.records;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordsOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "project";
	private static final String RECORDS_TABLE_NAME = "records";
	private static final String RECORDS_TABLE_CREATE = 
		"create table "+RECORDS_TABLE_NAME+" ("+
		" id integer primary key autoincrement, " +
		" user text, " +
		" number integer)";

	public RecordsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(RECORDS_TABLE_CREATE);
		db.execSQL("insert into records (user,number) values ('prova1',100)");
		db.execSQL("insert into records (user,number) values ('prova2',200)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+RECORDS_TABLE_NAME);
		db.execSQL(RECORDS_TABLE_CREATE);
	}

	// I a partir d'aqui els meus m�todes
	
	public void afegir (String user,int number) {
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL(
				"insert into records values (?,?,?)",
				new Object[]{null,user,number}				
		);		
		db.close();
	}

	public void esborrar (String user) {
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL(
				"delete from records where user=?",
				new Object[]{user}				
		);
		db.close();
	}	

	public void esborrarTot () {
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL(
				"delete from records"				
		);
		db.close();
	}	
	
	public Cursor llista(){
		SQLiteDatabase db=this.getReadableDatabase();
		return db.rawQuery("select id _id, user, number from records order by number desc",null);		
	}		
	
	public int getRecord(String user){
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c=db.rawQuery("select min(number) from records where user=?",new String[]{user});
		c.moveToFirst();
		int record=c.getInt(0);
		db.close();		
		return record;		
	}		
		
}
