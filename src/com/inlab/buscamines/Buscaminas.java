package com.inlab.buscamines;

import java.util.Date;

import android.util.Log;

public class Buscaminas {
	
	boolean enMarxa=false;
	long tempsInicial;
	char[][] camp;
	char[][] tapades;
	int ndestapades=0;
	int mida;
	int mines;
	
	Buscaminas (int mida, int mines) {
		tempsInicial=new Date().getTime();
		enMarxa=true;
		this.mida=mida;
		this.mines=mines;
		camp=new char[mida][mida];
		tapades=new char[mida][mida];
				
		for (int i=0;i<mida;i++)
			for (int j=0;j<mida;j++) {
				camp[i][j]='0';		
				tapades[i][j]='X';
			}
		
		// Coloca
		
		int colocades=mines;
		while (colocades>0) {
			int x=(int)(Math.random()*(float)mida);
			int y=(int)(Math.random()*(float)mida);
			if (camp[x][y]=='0') {
				camp[x][y]='*';
				colocades--;
			}
		}
		
		// I preprocessa els numeros
		
		for (int i=0;i<mida;i++)
			for (int j=0;j<mida;j++)
				if (camp[i][j]!='*') camp[i][j]=minesVoltant(i,j);
		
	}	
		
	public void atura() {
		enMarxa=false;
	}
	
	public boolean enMarxa() {
		return (tempsInicial>0);		
	}		
	
	public char contingut(int x, int y) {
		if ((x<0)||(x>=mida)||(y<0)||(y>=mida)) return ' ';
		return camp[x][y];	
	}
	
	public char destapa(int n) {
    	int x=n/mida;
    	int y=n%mida;    	
    	return destapa(x,y);
	}
	
	public char destapa(int x, int y) {
		if ((x<0)||(x>=mida)||(y<0)||(y>=mida)) return ' ';
		if (tapades[x][y]!='X') return ' ';
		tapades[x][y]=' ';
		ndestapades++;
		if (contingut(x,y)=='0') {
			destapa(x-1,y-1);destapa(x-1,y);destapa(x-1,y+1);
			destapa(x,y-1);                 destapa(x,y+1);
			destapa(x+1,y-1);destapa(x+1,y);destapa(x+1,y+1);
    	}
    	return contingut(x,y);
	}
	
	public void destapaTot() {
		for (int i=0;i<mida;i++)
			for (int j=0;j<mida;j++)		
				tapades[i][j]=' ';			
	}	
	
	public void marca (int x, int y) {
		tapades[x][y]='F';
	}

	public boolean teBandera (int n) {
    	int x=n/mida;
    	int y=n%mida;
    	return teBandera(x,y);
	}
	
	public void posaBandera (int n) {		
		int x=n/mida;
    	int y=n%mida;
    	tapades[x][y]='F';
	}

	public void treuBandera (int n) {		
		int x=n/mida;
    	int y=n%mida;
    	tapades[x][y]='X';
	}	
	
	public boolean teBandera (int x, int y) {
		return (tapades[x][y]=='F');
	}

	
	public int quedenPerDestapar() {
		return (mida*mida)-mines-ndestapades;		
	}
	
	public boolean destapada (int n) {		
		int x=n/mida;
    	int y=n%mida;
    	return destapada (x,y);
	}

	public boolean destapada(int x, int y) {
		return tapades[x][y]==' ';
	}
	
	public char contingut(int i) {
		return camp[i/mida][i%mida];	
	}
	
	public int getMida() {
		return mida;
	}
	
	public int getSegons() {
		return (int)(new Date().getTime()-tempsInicial)/1000;
	}
		
	public char minesVoltant(int x,int y) {
		int i=0;
		if (contingut(x-1,y-1)=='*') i++;
		if (contingut(x-1,y)  =='*') i++;
		if (contingut(x-1,y+1)=='*') i++;
		if (contingut(x,y-1)  =='*') i++;
		if (contingut(x,y+1)  =='*') i++;
		if (contingut(x+1,y-1)=='*') i++;
		if (contingut(x+1,y)  =='*') i++;
		if (contingut(x+1,y+1)=='*') i++;
		return (""+i).charAt(0);
	}
		
	public String toString() {
		StringBuffer res=new StringBuffer();
		for (int i=1;i<=mida;i++) {
			res.append("\n");
			for (int j=1;j<=mida;j++)
				res.append(camp[i][j]);
		}
		
		return res.toString();
	}
				
}
