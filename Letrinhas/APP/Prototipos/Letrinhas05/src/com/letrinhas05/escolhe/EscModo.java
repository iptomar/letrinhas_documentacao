package com.letrinhas05.escolhe;
import com.letrinhas05.R;
import com.letrinhas05.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Escolher modo
 *
 */
public class EscModo extends Activity {
    protected Button btnModoAluno, btnModoProf, btnVoltar;
    protected int idEscola, idProfessor, idTurma, idAluno, iDs[];
    protected String nomeEscola, nomeProfessor, nomeTurma, nomeAluno, Nomes[];
    protected String fotoNomeProf, fotoAluno;

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
		setContentView(R.layout.esc_modo);
		///////////////////// Retirar os Extras da janela Anterior /////////////////
		Bundle b = getIntent().getExtras();
		// escola
		idEscola = b.getInt("Escola_ID");
        nomeEscola = b.getString("Escola");
		// professor
		idProfessor = b.getInt("Professor_ID");
        nomeProfessor = b.getString("Professor");
        fotoNomeProf = b.getString("fotoProfs");
        //Turma
        nomeTurma = b.getString("Turma");
        idTurma = b.getInt("Turma_ID");
        //Aluno
        nomeAluno = b.getString("Aluno");
        idAluno = b.getInt("Aluno_ID");
        fotoAluno = b.getString("Foto_Aluno");
        ////////////////Aceder a objectos visuais da janela///////////////////
        btnModoAluno = (Button) findViewById(R.id.btModoAluno);
        btnModoProf = (Button) findViewById(R.id.btModoProf);
        btnVoltar = (Button) findViewById(R.id.escMbtnVoltar);
        ((TextView) findViewById(R.id.escMEscola)).setText(nomeEscola);
		((TextView) findViewById(R.id.tvMProf)).setText(nomeProfessor);
        ((TextView) findViewById(R.id.tvMTurma)).setText(nomeTurma);
        ((TextView) findViewById(R.id.tvMAluno)).setText(nomeAluno);
        final View contentView = findViewById(R.id.escModo);
/////////////////////////////////////////////////////////////////////////////////
		// se professor tem uma foto, usa-se
		if (fotoNomeProf != null) {
			ImageView imageView = ((ImageView) findViewById(R.id.imgProfEscmODO));
			String imageInSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/School-Data/Professors/" + fotoNomeProf;
			Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
			imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100,
					100, false));
		}
		// se o aluno tem uma foto, usa-se
		if (fotoAluno != null) {
			ImageView imageView = ((ImageView) findViewById(R.id.ivMAluno));
			String imageInSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/School-Data/Students/" + fotoAluno;
			Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
			imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100,
					100, false));
		}

		// new line faz a rota��o do ecr�n em 180 graus
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}

        // juntar tudo num array, para simplificar o c�digo
        // String's - Escola, Professor, fotoProf, Turma, Aluno, fotoAluno
        Nomes = new String[6];
        Nomes[0] = nomeEscola;
        Nomes[1] = nomeProfessor;
        Nomes[2] = fotoNomeProf;
        Nomes[3] = nomeTurma;
        Nomes[4] = nomeAluno;
        Nomes[5] = fotoAluno;

        // int's - idEscola, idProfessor, idTurma, idAluno
        iDs = new int[4];
        iDs[0] = idEscola;
        iDs[1] = idProfessor;
        iDs[2] = idTurma;
        iDs[3] = idAluno;

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
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
    //////////////////////////////
		escutaBotoes();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Trigger the initial hide() shortly after the activity has been
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

	private void escutaBotoes() {
        btnModoAluno.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				modAluno();
			}
		});
        btnModoProf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				modProf();
			}
		});
        btnVoltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	public void modAluno() {
		((Button) findViewById(R.id.btModoAluno)).setTextColor(Color.GREEN);
		((Button) findViewById(R.id.btModoProf)).setTextColor(Color.rgb(0x5d,
				0xdf, 0xff));
	////////////////enviar os parametros necesserios para a proxima janela ////////////
		Bundle wrap = new Bundle();
		wrap.putStringArray("Nomes", Nomes);
		wrap.putIntArray("IDs", iDs);
		// iniciar a pagina 2 (escolher Dsiciplina)
		Intent it = new Intent(EscModo.this, EscolheDisciplina.class);
		it.putExtras(wrap);
		startActivity(it);
	}

	public void modProf() {
		((Button) findViewById(R.id.btModoProf)).setTextColor(Color.GREEN);
		((Button) findViewById(R.id.btModoAluno)).setTextColor(Color.rgb(0x5d,
				0xdf, 0xff));
////////////////enviar os parametros necesserios para a proxima janela ////////////
		Bundle wrap = new Bundle();
		// String's - Escola, Professor, fotoProf, Turma, Aluno, fotoAluno
		wrap.putStringArray("Nomes", Nomes);
		// int's - idEscola, idProfessor, idTurma, idAluno
		wrap.putIntArray("IDs", iDs);
		// iniciar a pagina (escolher testes a corrigir)
		Intent it = new Intent(EscModo.this, ListarSubmissoes.class);
		it.putExtras(wrap);
		startActivity(it);
	}

}
