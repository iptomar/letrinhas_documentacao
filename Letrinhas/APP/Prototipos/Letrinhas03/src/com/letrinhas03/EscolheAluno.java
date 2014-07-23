package com.letrinhas03;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.letrinhas03.BaseDados.LetrinhasDB;
import com.letrinhas03.ClassesObjs.Escola;
import com.letrinhas03.ClassesObjs.Estudante;
import com.letrinhas03.ClassesObjs.Professor;
import com.letrinhas03.util.SystemUiHider;

public class EscolheAluno extends Activity {

	ImageButton volt, exect;
	public int nAlunos;
	List<Estudante> estudantes;
	LetrinhasDB db;
	Escola escola;
	Professor prof;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
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
		setContentView(R.layout.activity_escolhe_aluno);
		
		
		
		//Cria o objecto da base de dados
		db = new LetrinhasDB(this);
		estudantes = db.getAllStudents();
		nAlunos = estudantes.size();

		// esconder o title************************************************+
		final View contentView = findViewById(R.id.escAluno);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		/************************************************************************
		 * Cria��o de um painel din�mico para os bot�es de selec��o das escolas
		 * existentes.
		 * 
		 * 
		 */

		// Painel din�mico ****************************************************
		LinearLayout ll = (LinearLayout) findViewById(R.id.llescAluno);
		// Bot�o original que existe por defeni��o
		Button bt1 = (Button) findViewById(R.id.Button_aluno);

		// Cria o n� de bot�es referentes ao n� de escolas presentes na lista
		if (0 < nAlunos) {
			int i = 0;
			// Atribir o primeiro aluno ao primeiro bot�o
			// ********************************+
			// Nome do Aluno
			bt1.setText(estudantes.get(i).getNome());
			i++;
			// Resto dos alunos
			while (i <= nAlunos) {
				// um novo bot�o
				Button bt = new Button(getBaseContext());
				// copiar os parametros de layout do 1� bot�o
				bt.setLayoutParams(bt1.getLayoutParams());
				bt.setTextSize(bt1.getTextSize());
				// Nome do Aluno
				bt.setText(estudantes.get(i).getNome());
				// inserir no scroll view
				ll.addView(bt);
				i++;
			}
		} else {
			// esconder os bot�es
			bt1.setVisibility(View.INVISIBLE);
			exect.setVisibility(View.INVISIBLE);

			android.app.AlertDialog alerta;
			// Cria o gerador do AlertDialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// define o titulo
			builder.setTitle("Letrinhas 03");
			// define a mensagem
			builder.setMessage("N�o foram encontradas alunos no sistema");
			// define um bot�o como positivo
			builder.setPositiveButton("OK", null);
			// cria o AlertDialog
			alerta = builder.create();
			// Mostra
			alerta.show();
			
		}

		volt = (ImageButton) findViewById(R.id.escTVoltar_aluno);
		exect = (ImageButton) findViewById(R.id.ibComecar_aluno);

		escutaBotoes();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(1000);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
				//executarTestes();
				Toast.makeText(getApplicationContext(), " - Tipo n�o defenido",
				Toast.LENGTH_SHORT).show();
			}
		});

		volt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {// sair da aplica��o
				finish();
			}
		});
	}
}
