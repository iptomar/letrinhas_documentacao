package com.letrinhas04;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import com.letrinhas04.R;
import com.letrinhas04.BaseDados.LetrinhasDB;
import com.letrinhas04.BaseDados.SincAllBd;
import com.letrinhas04.ClassesObjs.Escola;
import com.letrinhas04.escolhe.EscolheEscola;
import com.letrinhas04.util.SystemUiHider;
import com.letrinhas04.util.coneccaoW;

/**
 * P�gina Inicial
 * 
 * com um exemplo de uma actividade full-screen, que mostra e esconde o User
 * Interface de sistema (i.e. status bar and navigation/system bar) com a
 * intera��o do utilizador
 * 
 * @see SystemUiHider
 * 
 * @author Thiago
 */
public class PaginaInicial extends Activity {
	public Button bentrar, exper; // bot�o para aceder ao menu
	ImageButton ibotao;// bot�o para sair da app
    public ProgressBar prog;
    public TextView txtViewMSG;
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
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

		setContentView(R.layout.pagina_inicial);

        //Declaracao de objectos da janela
        prog = (ProgressBar) findViewById(R.id.progressBarLoad);
        prog.setVisibility(View.INVISIBLE);
        txtViewMSG = (TextView) findViewById(R.id.txtMsg);
        txtViewMSG.setVisibility(View.INVISIBLE);
        findViewById(R.id.bEntrar1).setOnTouchListener(mDelayHideTouchListener);
        bentrar = (Button) findViewById(R.id.bEntrar1);
        ibotao = (ImageButton) findViewById(R.id.iBSair);
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);


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
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});



		/**************
		 * Por fazer,
		 * ##############################################################
		 * Verificar se sexiste algum professor "logado", se sim, retira as suas
		 * credenciais e avan�a sem que seja necess�rio fazer a
		 * autentica��o se n�o, executa a escolha da escola, do professor,
		 * requer a autentica��o do professor, que posteriormente carrega
		 * turmas / alunos ao seu encargo.
		 * 
		 * o utilizador (professor) escolhe o aluno que vai executar o teste e o
		 * seu modo (Aluno = treino) ou (Professor=avalia��o).
		 * 
		 * 
		 * //###################################################################
		 * #################################### //###### Iniciar uma classe do
		 * tipo thread para detetar a liga��o, sincronizar / carregar a BD,
		 * desativar //###### a barra de progresso e ativar o bot�o para
		 * entrar.~
		 * 
		 */
		// DE acordo com a ultima aula ficou decidido que n�o vamos ligar o
		// WI.fi
		// coneccaoW con = new coneccaoW(this);
		// con.run();//M�todo run, pois a DVM � burra!!! e n�o funciona
		// muito bem com as threads no m�todo Start()

		// bloqueiar o bot�o Entrar
		bentrar.setEnabled(false);

		// verifica se existe alguma coisa na BD local, vou � primeira tabela
		// necess�ria
		LetrinhasDB db = new LetrinhasDB(this);
		List<Escola> escolas = db.getAllSchools();

		// Se n�o existir, tentamos aqui ligar ao servidor
		if (escolas.size() == 0) {
			try {
				Toast.makeText(this,"Sem informacao na Base de dados local!\n"
								+ "A descarregar BASE DADOS do servidor!\n",
						Toast.LENGTH_LONG).show();
                prog.setVisibility(View.VISIBLE);
                txtViewMSG.setVisibility(View.VISIBLE);
				// /////////////////////////////////////////////////////////////////////////////////////////
				// //////////CHAMA EM BAKCGORUND A SINCRO DE TABELAS E INSERE NA
				// BASE DE DADOS /////////////
				String ip = "code.dei.estt.ipt.pt"; // //TROCAR ISTO POR
													// VARIAVEIS
				// COM OS ENDERE�OS IP QUE NAO SEI ONDE TEM/////////
				String porta = "80";
				// Forma o endere�o http
				String URlString = "http://" + ip + ":" + porta + "/";

				String[] myTaskParams = { URlString, URlString, URlString };
				new SincAllBd(this, this).execute(myTaskParams);

				// //////////////PODEM VER EM LOGCAT A INSERIR TODOS OS DADOS NA
				// /////////////////////////////////////////////////////////////////////////////////////////
			} catch (Exception e2) {
				Toast.makeText(this, "Nao foi possivel aceder ao servidor!\n"
						+ "Debug: " + e2, Toast.LENGTH_LONG).show();

			}
		} else {
			// desbloqueia botao de entrar
            prog.setVisibility(View.INVISIBLE);
            txtViewMSG.setVisibility(View.INVISIBLE);
			bentrar.setEnabled(true);
		}

		escutaBotoes();
	}

	private void escutaBotoes() {

		bentrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// *****************************************************************************
				// *****************************************************************************
				// *****************************************************************************
				// iniciar a pagina 2 (escolher escola ou ent�o passar logo para
				// a pagina escolher turma(brevemente))
				Intent it = new Intent(PaginaInicial.this, EscolheEscola.class);// Autenticacao.class);
				startActivity(it);
			}
		});

		ibotao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// sair da aplica��o
				java.lang.System.exit(RESULT_OK);
			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(2000);
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
