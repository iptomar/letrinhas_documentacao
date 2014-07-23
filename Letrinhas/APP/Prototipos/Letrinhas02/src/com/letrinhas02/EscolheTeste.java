package com.letrinhas02;

import com.letrinhas02.util.ExecutaTestes;
import com.letrinhas02.util.SystemUiHider;
import com.letrinhas02.util.Teste;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class EscolheTeste extends Activity {
	ImageButton volt, exect;
	public int nTestes;
	boolean modo;
	Teste[] teste;

	/*********************************************************************
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.escolhe_teste);

		volt = (ImageButton) findViewById(R.id.escTVoltar);
		exect = (ImageButton) findViewById(R.id.ibComecar);
		// esconder o title************************************************+
		final View contentView = findViewById(R.id.escTeste);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.hide();

		/************************************************************************
		 * Cria��o de um painel din�mico para os bot�es de selec��o dos testes
		 * existentes.
		 * 
		 * � necess�rio de saber primeiro onde est�o os testes e quantos s�o!
		 * (Comunicar com a BD)
		 * 
		 * aceder � BD local, contar o n� de testes, ex: 
		 * 		nTestes ="Conta(bl�.bl�.bl�)" ; 
		 * 		teste= new Teste[nTestes];
		 * 
		 * e os seus t�tulos guardar essa informa��o num array para se aceder na
		 * constru��o do scroll view. ex:
		 * 			 for(int i=0;i<teste.length i++){ 
		 * 				int tipo= "tipo(bl�.bl�.bl�)"; 
		 * 				String tit = "titulo(bl�bl�bl�)"; 
		 * 				String ender = "endere�o(bl�bl�bl�)"; 
		 * 				teste[i]=new Teste(tip,tit,ender); 
		 * 			}
		 * 
		 */

		
		//teste:::::::::::
		nTestes = 30;
		teste = new Teste[nTestes];
		for (int i = 0; i < teste.length; i++) {
			int tip = i%3; //tipo texto
			String tit = "O t�tulo do teste";
			String ender =  "teste/myteste.txt";
			teste[i] = new Teste(tip, tit, ender);
		}//:::::::::::::::::::::::::::::::::::::::::::

		// Painel din�mico ****************************************************
		LinearLayout ll = (LinearLayout) findViewById(R.id.llescteste);
		// Bot�o original que existe por defeni��o
		ToggleButton tg1 = (ToggleButton) findViewById(R.id.ToggleButton1);

		// Se existirem testes no reposit�rio correspondentes, cria o n� de
		// bot�es referentes ao n� de testes existentes
		if (0 < nTestes) {
			int i = 0;
			// Atribuo o primeiro t�tulo ao primeiro bot�o
			// ********************************+
			// texto por defeito
			tg1.setText(teste[i].getTitulo());
			// texto se n�o seleccionado = "titulo do teste sem numera��o"
			tg1.setTextOff(teste[i].getTitulo());
			// texto se seleccionado = "titulo do teste com numera��o"
			tg1.setTextOn((i + 1) + " - " + teste[i].getTitulo());
			i++;

			// Resto do t�tulos
			while (i < nTestes) {
				// um novo bot�o
				ToggleButton tg = new ToggleButton(getBaseContext());
				// copiar os parametros de layout do 1� bot�o
				tg.setLayoutParams(tg1.getLayoutParams());
				tg.setTextSize(tg1.getTextSize());
				// texto por defeito
				tg.setText(teste[i].getTitulo());
				// texto se n�o seleccionado = "titulo do teste sem numera��o"
				tg.setTextOff(teste[i].getTitulo());
				// texto se seleccionado = "titulo do teste com numera��o"
				tg.setTextOn((i + 1) + " - " + teste[i].getTitulo());
				// inserir no scroll view
				ll.addView(tg);
				i++;
			}
		} else {
			android.app.AlertDialog alerta;
			// Cria o gerador do AlertDialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// define o titulo
			builder.setTitle("Letrinhas 02");
			// define a mensagem
			builder.setMessage("N�o foram encontrados testes no reposit�rio");
			// define um bot�o como positivo
			builder.setPositiveButton("OK", null);
			// cria o AlertDialog
			alerta = builder.create();
			// Mostra
			alerta.show();
			
			//esconder os bot�es
			tg1.setVisibility(View.INVISIBLE);
			exect.setVisibility(View.INVISIBLE);
			
		}
		
		volt = (ImageButton) findViewById(R.id.escTVoltar);
		exect = (ImageButton) findViewById(R.id.ibComecar);

		escutaBotoes();
	}

	/**
	 * Procedimento para voltar a esconder o titulo caso este esteja activo
	 * 
	 * @author Thiago
	 */
	@Override
	public boolean hasWindowFocus() {
		// esconder o title
		final View contentView = findViewById(R.id.escTeste);
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
		return true;
	}

	/**
	 * Procedimento para veirficar os bot�es
	 * 
	 * @author Thiago
	 */
	private void escutaBotoes() {
		exect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				executarTestes();
			}
		});

		volt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {// sair da aplica��o
				finish();
			}
		});
	}
	

	/**************************************************************************
	 * Por Fazer ******************************** executar os testes
	 * selecionados, um de cada vez
	 */
	public void executarTestes() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.llescteste);
		// contar o n� de elementos (testes)
		int nElements = ll.getChildCount();

		int j = 0;
		// contar quantos e quais foram selecionados
		for (int i = 0; i < nElements; i++) {
			// verificar se o teste est� ativo
			if (((ToggleButton) ll.getChildAt(i)).isChecked()) {
				j++;
			}
		}
		Toast.makeText(getApplicationContext(), j + " Testes seleccionados",
				Toast.LENGTH_SHORT).show();
		
		//Copiar os testes seleccionados para uma lista auxiliar
		Teste []lista= new Teste[j];
		j = 0;
		for (int i = 0; i < nElements; i++) {
			if (((ToggleButton) ll.getChildAt(i)).isChecked()) {
				lista[j]=teste[i];
				j++;
			}
		}
		
		// /identificar o modo de apresenta��o de testes, se Professor ou
		// aluno*****************+++++++++++++++
		// Descobrir como passar valores/parametros entre activity's

		// modo =
		// findViewById(R.layout.esc_modo).findViewById(id.rbmod2).isSelected();

		// iniciar os testes.... 
		// Se existe items seleccionados arranca com os testes,
		if (0 < j) {
			ExecutaTestes exect = new ExecutaTestes(this, modo, lista);
			exect.run(); // M�todo run, pois a DVM � burra!!! e n�o funciona
							// muito bem com as threads

		} else {// sen�o avisa que n�o existe nada seleccionado
			android.app.AlertDialog alerta;
			// Cria o gerador do AlertDialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// define o titulo
			builder.setTitle("Letrinhas 02");
			// define a mensagem
			builder.setMessage("N�o existem testes seleccionados!");
			// define um bot�o como positivo
			builder.setPositiveButton("OK", null);
			// cria o AlertDialog
			alerta = builder.create();
			// Mostra
			alerta.show();
		}

	}

}
