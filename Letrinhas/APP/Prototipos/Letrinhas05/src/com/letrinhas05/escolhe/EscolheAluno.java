package com.letrinhas05.escolhe;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.letrinhas05.R;
import com.letrinhas05.BaseDados.LetrinhasDB;
import com.letrinhas05.ClassesObjs.Estudante;
import com.letrinhas05.util.SystemUiHider;

public class EscolheAluno extends Activity {

    protected Button btnVoltar;
    protected  int  idEscola, idProfessor, idTurma;
    protected  String nomeEscola, nomeProfessor, fotoNomeProf, nomeTurma;

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

		//////////// Retirar os Extras da janela anterior ////////////////////////
		Bundle b = getIntent().getExtras();
		// escola
		idEscola = b.getInt("Escola_ID");
        nomeEscola = b.getString("Escola");
		// professor
		idProfessor = b.getInt("Professor_ID");
        nomeProfessor = b.getString("Professor");
        fotoNomeProf = b.getString("foto_Professor");
        //Turma
        idTurma = b.getInt("turma_ID");
        nomeTurma = b.getString("Turma");

//////////////////////////////Aceder a objectos Visuais da janela ///////////////////////
        btnVoltar = (Button) findViewById(R.id.escAlbtnVoltar);
        ((TextView) findViewById(R.id.escAlEscola)).setText(nomeEscola);
		((TextView) findViewById(R.id.tvAlProf)).setText(nomeProfessor);
        ImageView imageView = ((ImageView) findViewById(R.id.imgProfEscolhAluno));
        ((TextView) findViewById(R.id.escAlTurma)).setText(nomeTurma);
        final View contentView = findViewById(R.id.escAluno);
   ////////////////////////////////////////////////////////////////////////////////////////////v
		if (fotoNomeProf != null) {
			String imageInSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/School-Data/Professors/" + fotoNomeProf;
			Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
			imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
					100, 100, false));
		}
		// new line faz a rota��o do ecr�n em 180 graus
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
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

           ////////////BOTAO VOLTAR
        btnVoltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {// sair da aplica��o
				finish();
			}
		});

		makeTabela();
	}

	/**
	 * Novo m�todo para criar o painel din�mico para os bot�es de selec��o da
	 * turma
	 * 
	 * @author Thiago
	 */
	@SuppressLint("NewApi")
	private void makeTabela() {

		// Cria o objecto da base de dados
		LetrinhasDB db = new LetrinhasDB(this);
		// ************* Todos os alunos desta turma
		List<Estudante> alunos = db.getAllStudentsByTurmaId(idTurma);
		// *******************************************************************************
		int nAlunos = alunos.size();
		int[] idAluno = new int[nAlunos];
		String nomeAluno[] = new String[nAlunos];
		String fotoAluno[] = new String[nAlunos];
		
		// preenche os arrays s� com a informa��o necess�ria
		for (int i = 0; i < nAlunos; i++) {
			idAluno[i] = alunos.get(i).getIdEstudante();
			nomeAluno[i] = alunos.get(i).getNome();
			fotoAluno[i]= alunos.get(i).getNomefoto();
		}

		for (Estudante cn : alunos) {
			String storage = cn.getNome() + "," + cn.getIdEstudante() + ","
					+ cn.getNomefoto()+ "," + cn.getEstado() + "," + cn.getIdTurma();
		}

		/**
		 * Scroll view com uma tabela de 4 colunas(max)
		 */
		// tabela a editar
		TableLayout tabela = (TableLayout) findViewById(R.id.tblEscolheAl);
		// linha da tabela a editar
		TableRow linha = (TableRow) findViewById(R.id.escAllinha01);
		// 1� bot�o
		Button bt = (Button) findViewById(R.id.AlBtOriginal);
		bt.setText("teste alunos");
		// Contador de controlo
		int cont = 0;
		// criar o n� de linhas a dividir por 4 colunas
		for (int i = 0; i < nAlunos / 4; i++) {
			// nova linha da tabela
			TableRow linha1 = new TableRow(getBaseContext());
			// Copiar os parametros da 1� linha
			linha1.setLayoutParams(linha.getLayoutParams());
			// criar os 4 bot�es da linha
			for (int j = 0; j < 4; j++) {
				// **********************************
				// Nome, id e foto do aluno

				final String alumni = nomeAluno[cont];
				final int idAL = idAluno[cont];
				final String alunFot = fotoAluno[cont];
				// ***********************************

				// novo bot�o
				Button bt1 = new Button(bt.getContext());
				// copiar os parametros do bot�o original
				bt1.setLayoutParams(bt.getLayoutParams());

				// se o aluno tiver foto, vou busca-la
				if (fotoAluno[cont] != null) {
					String imageInSD = Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/School-Data/Students/" + fotoAluno[cont];
					Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
					ImageView imageView = new ImageView(this);

					// ajustar o tamanho da imagem
					imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
							210, 200, false));
					// enviar para o bot�o
					bt1.setCompoundDrawablesWithIntrinsicBounds(null,
							imageView.getDrawable(), null, null);
				} else {
					// sen�o copia a imagem do bot�o original
					bt1.setCompoundDrawables(null,
							bt.getCompoundDrawablesRelative()[1], null, null);
				}

				// addicionar o nome
				bt1.setText(nomeAluno[cont]);
				// Defenir o que faz o bot�o ao clicar
				bt1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Entrar na activity
                     ////////////////CAMPOS PARA A PROXIMA JANELA///////////////////
						Bundle wrap = new Bundle();
						wrap.putString("Escola", nomeEscola);
						wrap.putInt("Escola_ID", idEscola);
						wrap.putString("Professor", nomeProfessor);
						wrap.putInt("Professor_ID", idProfessor);
						wrap.putString("Foto_professor", fotoNomeProf);
						wrap.putString("Turma",nomeTurma);
						wrap.putInt("Turma_ID", idTurma);
						wrap.putString("Aluno",alumni);
						wrap.putInt("Aluno_ID", idAL);
						wrap.putString("Foto_Aluno", alunFot);
						Intent it = new Intent(getApplicationContext(),EscModo.class);
						it.putExtras(wrap);
						startActivity(it);
					}
				});
				// inserir o bot�o na linha
				linha1.addView(bt1);
				// incrementar o contador de controlo
				cont++;
			}
			// inserir a linha criada
			tabela.addView(linha1);
		}
		// resto
		if (nAlunos % 4 != 0) {
			TableRow linha1 = new TableRow(getBaseContext());
			linha1.setLayoutParams(linha.getLayoutParams());
			for (int j = 0; j < nAlunos % 4; j++) {
				// **********************************
				// Nome, id e foto do aluno
				final String alumni = nomeAluno[cont];
				final int idAL = idAluno[cont];
				final String alunFot = fotoAluno[cont];
				// ***********************************
				// novo bot�o
				Button bt1 = new Button(bt.getContext());
				// copiar os parametros do bot�o original
				bt1.setLayoutParams(bt.getLayoutParams());

				// se o aluno tiver foto, vou busca-la
				if (fotoAluno[cont] != null) {
					String imageInSD = Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/School-Data/Students/" + fotoAluno[cont];
					Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
					ImageView imageView = new ImageView(this);

					// ajustar o tamanho da imagem
					imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
							240, 240, false));
					// enviar para o bot�o
					bt1.setCompoundDrawablesWithIntrinsicBounds(null,
							imageView.getDrawable(), null, null);
				} else {
					// sen�o copia a imagem do bot�o original
					bt1.setCompoundDrawables(null,
							bt.getCompoundDrawablesRelative()[1], null, null);
				}

				// addicionar o nome
				bt1.setText(nomeAluno[cont]);
				// Defenir o que faz o bot�o ao clicar
				bt1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Entrar na activity
                        ////////////////CAMPOS PARA A PROXIMA JANELA///////////////////
						Bundle wrap = new Bundle();
						wrap.putString("Escola", nomeEscola);
						wrap.putInt("Escola_ID", idEscola);
						wrap.putString("Professor", nomeProfessor);
						wrap.putInt("Professor_ID", idProfessor);
						wrap.putString("fotoProfs", fotoNomeProf);
						wrap.putString("Turma",nomeTurma);
						wrap.putInt("Turma_ID", idTurma);
						wrap.putString("Aluno",alumni);
						wrap.putInt("Aluno_ID", idAL);
						wrap.putString("Foto_Aluno", alunFot);
						Intent it = new Intent(getApplicationContext(),EscModo.class);
						it.putExtras(wrap);
						startActivity(it);
					}
				});
				// inserir o bot�o na linha
				linha1.addView(bt1);
				// incrementar o contador de controlo
				cont++;
			}
			// inserir a linha criada
			tabela.addView(linha1);
		}
		// por fim escondo a 1� linha
		tabela.removeView(linha);
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

}
