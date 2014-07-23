package com.letrinhas02.util;

import com.letrinhas02.EscModo;
import com.letrinhas02.EscolheTeste;
import com.letrinhas02.PagInicial;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Classe para lan�ar as activity's de um modo sequ�ncial de acordo com a lista
 * recebida de testes a realizar.
 * vai verificar o tipo de teste a realizar e montar a activity de acordo com o seu cont�do
 * 
 * 
 * 
 * @author Thiago
 * 
 */
public class ExecutaTestes extends Thread {
	Activity act; 
	boolean modo; 
	Teste lista[];
	
	
	/**
	 * Construtor
	 * 
	 * @param act
	 *            Activity base para poder lan�ar as novas activity's, (Testes)
	 * @param modo
	 *            Boolean para identificar o modo de execu��o dos testes  Aluno/Professor
	 * @param lista
	 *            Array com a identifica��o de cada um dos testes seleccionado anteriormente           
	 *            
	 */
	public ExecutaTestes(Activity act, boolean modo, Teste lista[]) {
		this.act = act;
		this.modo = modo;
		this.lista = lista;
	}

	
	public void run() {
		
			
		
		
		for (int i = 0; i < lista.length; i++) {
			switch (lista[i].tipo){
			case 0 :
				Toast.makeText(act.getApplicationContext(),(i+1)+" - Texto",
						Toast.LENGTH_SHORT).show();
				//lan�ar a nova activity do tipo texto, e o seu conte�do
				//
				//
				
				break;
			case 1 :
				Toast.makeText(act.getApplicationContext(),(i+1)+" - Palavras",
						Toast.LENGTH_SHORT).show();
				//lan�ar a nova activity do tipo Palavras, e o seu conte�do
				//
				//Intent it = new Intent(act.getApplicationContext(),texto.class);
				//act.startActivity(it);
				
				//esperar que esta termine 
				//while (!act.isDestroyed());
				
				break;
			case 2 :
				Toast.makeText(act.getApplicationContext(),(i+1)+" - Poemas",
						Toast.LENGTH_SHORT).show();
				//lan�ar a nova activity do tipo Poema, e o seu conte�do
				//
				//
				
				break;
			case 3 :
				Toast.makeText(act.getApplicationContext(),(i+1)+" - Imagens",
						Toast.LENGTH_SHORT).show();
				//lan�ar a nova activity do tipo imagem, e o seu conte�do
				//
				//
				
				
				break;
			default:
				Toast.makeText(act.getApplicationContext(),(i+1)+" - Tipo n�o defenido",
						Toast.LENGTH_SHORT).show();
				//n�o lan�ar nada e continuar
				
				break;
			}
			
			
		}
		//Verifica o modo.
		

	}

}
