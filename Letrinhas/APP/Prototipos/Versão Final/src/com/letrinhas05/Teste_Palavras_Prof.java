package com.letrinhas05;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.letrinhas05.BaseDados.LetrinhasDB;
import com.letrinhas05.ClassesObjs.CorrecaoTesteLeitura;
import com.letrinhas05.util.Avaliacao;
import com.letrinhas05.util.SystemUiHider;

/**
 * Classe para apoiar a activity de corrigir um teste de lista de palavras
 * 
 * @author Dário
 * 
 */
public class Teste_Palavras_Prof extends Activity {
	boolean playing;
	// objetos
	ImageButton play, voltar, cancelar, avancar;
	int plvErradas = 0, id_teste, totalDePalavras = 0;
	private MediaPlayer reprodutor = new MediaPlayer();
	private String endereco, uuidAudio;
	CorrecaoTesteLeitura ctl;
	LetrinhasDB db;
	String[] nomes;
	int[] ids;
	String text;
	TextView valueWord;
	int numPalavCorretas = 0, numPalavIncorretas = 0;
	long idCorrrecao;
	float numPalavrasMin = 0, precisao = 0, velocidade = 0, expressividade = 0,
			ritmo = 0;
	int segundos, minutos, auxiliar;
	String observacoes = "empty", detalhes = "empty", stringAuxForType,
			dataDeExecucao;
	Avaliacao eval;
	Chronometer chrono;
	ProgressBar pbDuracao;

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
		setContentView(R.layout.teste_palavras_prof);
		// new line faz a rotacao do ecran 180 graus
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
		// / esconder o title************************************************+
		final View contentView = findViewById(R.id.corecaoPalavras);
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
		// buscar os parametros
		Bundle b = getIntent().getExtras();
		nomes = b.getStringArray("Nomes");
		ids = b.getIntArray("IDs");
		id_teste = b.getInt("ID_teste");

		idCorrrecao = b.getLong("ID_Correcao");
		dataDeExecucao = b.getString("dataDeExecucao");
		db = new LetrinhasDB(this);

		// ////////////CORRIGIDO AQUI/////////////////////
		CorrecaoTesteLeitura correcaoTesteLeitura = db
				.getCorrecaoTesteLeirutaById(Long.parseLong("" + idCorrrecao));
		auxiliar = correcaoTesteLeitura.getTipo();
		uuidAudio = correcaoTesteLeitura.getAudiourl();
		text = db.getTesteLeituraById(id_teste).getConteudoTexto();

		endereco = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ uuidAudio;
		// Log.d("Debug-pathAudio", endereco);
		valueWord = (TextView) findViewById(R.id.ValueWord);
		valueWord.setText("0");

		// ordenacao do texto nas tres colunas de forma a preencher toda de
		// seguida a 1 e so depois passa para as outras
		int lenght;
		String[] ar = text.split("[ ]");
		int[] restoVal = new int[2];
		lenght = ar.length / 3;
		// Log.d("Debug-length_totil", String.valueOf(ar.length));
		// Log.d("Debug-length", String.valueOf(ar.length / 3));
		// Log.d("Debug-Resto", String.valueOf(ar.length % 3));
		if (ar.length % 3 == 2) {
			restoVal[0] = 1;
			restoVal[1] = 1;
		} else if (ar.length % 3 == 1) {
			restoVal[0] = 1;
			restoVal[1] = 0;
		} else if (ar.length % 3 == 0) {
			restoVal[0] = 0;
			restoVal[1] = 0;
		}
		String[] texto, texto1, texto2;
		if (ar.length == 1) {
			texto = new String[lenght + restoVal[0]];
			texto1 = new String[0];
			texto2 = new String[0];
			for (int i = 0; i < ((ar.length) / 3) + restoVal[0]; i++) {
				texto[i] = ar[i];
			}
		} else if (ar.length == 2) {
			texto = new String[lenght + restoVal[0]];
			texto1 = new String[lenght + restoVal[1]];
			texto2 = new String[0];
			int var = 0, resto, support = 0;
			for (int i = 0; i < ((ar.length) / 3) + restoVal[0]; i++) {
				texto[i] = ar[i];
				var = i;
			}
			resto = ar.length - (ar.length) / 3;
			for (int j = var + 1; j < resto + restoVal[1]; j++) {
				texto1[support] = ar[j];
				support++;
			}
		} else {
			texto = new String[lenght + restoVal[0]];
			texto1 = new String[lenght + restoVal[1]];
			texto2 = new String[lenght];
			int var = 0, var1 = 0, resto, support = 0, support1 = 0;
			for (int i = 0; i < (lenght) + restoVal[0]; i++) {
				texto[i] = ar[i];
				var = i;
			}
			resto = ar.length - lenght;
			/*
			 * Log.d("Debug-Resto_in", String.valueOf(resto));
			 * Log.d("Debug-Resto_var", String.valueOf(var));
			 * Log.d("Debug-value", String.valueOf(resto + restoVal[1]));
			 */for (int j = var + 1; j < resto; j++) {
				texto1[support] = ar[j];
				var1 = j;
				support++;
			}
			for (int k = var1 + 1; k < ar.length; k++) {
				texto2[support1] = ar[k];
				support1++;
			}
		}
		initSetup(getResources(), R.id.tlpScroll, R.id.tlpToggleButton,
				R.id.tlplllayer, texto);
		initSetup(getResources(), R.id.tlpScroll1, R.id.tlpToggleButton1,
				R.id.tlplllayer1, texto1);
		initSetup(getResources(), R.id.tlpScroll2, R.id.tlpToggleButton2,
				R.id.tlplllayer2, texto2);
		// **********************************************************************************************

		play = (ImageButton) findViewById(R.id.tlpVoicePlay);
		voltar = (ImageButton) findViewById(R.id.tlpVoltar);
		cancelar = (ImageButton) findViewById(R.id.tlpCancel);
		avancar = (ImageButton) findViewById(R.id.tlpAvaliar);
		chrono = (Chronometer) findViewById(R.id.tlpcrom);
		pbDuracao = (ProgressBar) findViewById(R.id.pbPalavreas);

		try {
			reprodutor.setDataSource(endereco);
			reprodutor.prepare();
			pbDuracao.setMax(reprodutor.getDuration());
			segundos = ((reprodutor.getDuration() / 1000) % 60);
			minutos = ((reprodutor.getDuration() / 1000) / 60);
		} catch (Exception ex) {
		}
		chrono.setText(n2d(minutos) + ":" + n2d(segundos));
		escutaBotoes();
	}

	/**
	 * Eliminar a correção
	 * 
	 * @author Thiago
	 */
	public void elimina() {
		File file = new File(endereco);
		if (file.exists()) {
			db.deleteCorrecaoLeituraByid(ctl.getIdCorrrecao());
			file.delete();
		}
		finish();
	}

	/**
	 * método para acrescentar um 0 nas casas das dezenas, caso o numero seja
	 * inferior a 10
	 * 
	 * @param n
	 *            Numero inteiro a ser verificado
	 * @return String adaptada.
	 * @author Thiago
	 */
	private String n2d(int n) {
		String num;
		if (n / 10 == 0) {
			num = "0" + n;
		} else {
			num = "" + n;
		}
		return num;
	}

	/**
	 * forçar a paragem da reprodução do audio!
	 * 
	 * @author Thiago
	 */
	private void stopPlay() {
		if (playing) {
			reprodutor.stop();
			reprodutor.release();
		}
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
	 * temos de manter o onDestroy, devido a existir a possibilidade de fazer
	 * finhish() através da barra de sistema!
	 * 
	 * @author Thiago
	 */
	@Override
	protected void onDestroy() {
		if (playing) {
			reprodutor.stop();
			reprodutor.release();
		}
		super.onDestroy();
	}

	/** Método para defenir o Touchlistener de cada botão
	 * @author Thiago
	 */
	private void escutaBotoes() {
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startPlay();
			}
		});
		avancar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// voltar para pag inicial
				startAvalia();
				finish();
			}
		});
		voltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// voltar para pag inicial
				stopPlay();
				finish();
			}
		});

		cancelar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				android.app.AlertDialog alerta;
				// Cria o gerador do AlertDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(view
						.getContext());
				// define o titulo
				builder.setTitle("Letrinhas");
				// define a mensagem
				builder.setMessage("Tens a certeza que queres abandonar este teste?");
				// define os botoes
				builder.setNegativeButton("Nao", null);

				builder.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								stopPlay();
								finish();
							}
						});
				// cria o AlertDialog
				alerta = builder.create();
				// Mostra
				alerta.show();

			}
		});
	}

	int minuto, segundo;

	/**
	 * este metodo serve para enviar todos os dados para a tabela respectiva e
	 * preparar os dados para a activity seguinte
	 * 
	 * @author Dario
	 */
	public void submit() {
		ctl = new CorrecaoTesteLeitura();
		try {
			db.updateCorrecaoTesteLeitura(idCorrrecao, observacoes,
					numPalavrasMin, numPalavCorretas, numPalavIncorretas,
					precisao, velocidade, (int) expressividade, (int) ritmo,
					detalhes);
		} catch (Exception ex) {
		}
		lancaResultados();
	}

	/**
	 * Método para verificar se existe mais do que um resultado deste aluno,
	 * sobre este teste e caso exista apresente o mais recente para comparar com
	 * este.
	 * 
	 * @author Thiago
	 */
	private void lancaResultados() {
		ctl = db.getCorrecaoTesteLeirutaById(idCorrrecao);

		List<CorrecaoTesteLeitura> crt = db
				.getAllCorrecaoTesteLeitura_ByIDaluno_TestID(
						ctl.getIdEstudante(), ctl.getTestId());

		// Se o resultado for superior a 1 ent�o vai procurar o mais recente
		if (1 < crt.size()) {
			// recebe o primeiro para comparar
			int contador = 0, ponteiro = 0;
			long data = crt.get(0).getDataExecucao();

			// corre o resto da lista e procura o mais recente, anterior a este
			for (int i = 0; i < crt.size(); i++) {
				if (crt.get(i).getEstado() == 1) {// estado
					if (data < crt.get(i).getDataExecucao()) {// Data mais
																// recente
						if (ctl.getIdCorrrecao() != crt.get(i).getIdCorrrecao()) { // Corre��o_ID
							ponteiro = i;
							data = crt.get(i).getDataExecucao();
							contador++;
						}
					}
				}
			}
			if (contador > 0) {
				long id1, id2;
				id1 = crt.get(ponteiro).getIdCorrrecao();
				id2 = ctl.getIdCorrrecao();
				Bundle wrap = new Bundle();
				// ID da correcao antiga
				wrap.putLong("ID1", id1);
				// ID desta correcao
				wrap.putLong("ID2", id2);
				// Mostrar os resultados das correcoe dos testes
				Intent it = new Intent(getApplicationContext(),
						RelatasCorrection.class);
				it.putExtras(wrap);
				startActivity(it);
				finish();
			} else {// teste do resultado!
				Bundle wrap = new Bundle();
				// ID desta correcao
				wrap.putLong("ID", ctl.getIdCorrrecao());
				// Mostrar o resultado da correcao do teste
				Intent it = new Intent(getApplicationContext(), Resultado.class);
				it.putExtras(wrap);
				startActivity(it);
				finish();
			}

		}// Sen�o apresenta apenas este.
		else {
			// teste do resultado!
			Bundle wrap = new Bundle();
			// ID desta correcao
			wrap.putLong("ID", ctl.getIdCorrrecao());
			// Mostrar o resultado da correcao do teste
			Intent it = new Intent(getApplicationContext(), Resultado.class);
			it.putExtras(wrap);
			startActivity(it);
			finish();
		}

	}

	private final int PARADO = 2, ANDANDO = 1;
	private int tSegundo = segundos, tMinuto = minutos;
	private Handler play_handler, play_handler2;;

	/**
	 * serve para a aplicacao reproduzir ou parar o som
	 * 
	 * @author Dario Jorge
	 */
	@SuppressLint("HandlerLeak")
	private void startPlay() {
		if (!playing) {
			resetTimer();
			play.setImageResource(R.drawable.play_on);
			playing = true;
			try {
				reprodutor = new MediaPlayer();
				reprodutor.setDataSource(endereco);
				reprodutor.prepare();
				reprodutor.start();
				final ImageView img2 = new ImageView(this);
				img2.setImageResource(R.drawable.play);
				// espetar aqui uma thread, para caso isto pare
				// handler para controlara a GUI do android e a thread seguinte
				play_handler = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case PARADO:
							play.setImageResource(R.drawable.palyoff);
							playing = false;
							try {
								reprodutor.stop();
								reprodutor.release();
							} catch (Exception ex) {
							}
							resetTimer();
							break;
						default:
							break;
						}
					}
				};
				new Thread(new Runnable() {
					public void run() {
						Message msg = new Message();
						while (reprodutor.isPlaying())
							;
						msg.what = PARADO;
						play_handler.sendMessage(msg);
					}
				}).start();
				play_handler2 = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case ANDANDO:
							pbDuracao.setProgress(reprodutor
									.getCurrentPosition());
							chrono.setText(n2d(tMinuto) + ":" + n2d(tSegundo));
							break;
						default:
							break;
						}
					}
				};
				new Thread(new Runnable() {
					public void run() {
						Message msg = new Message();
						while (playing) {
							msg = new Message();
							msg.what = ANDANDO;
							play_handler2.sendMessage(msg);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							tSegundo--;
							if (tSegundo == (-1)) {
								tMinuto--;
								tSegundo = 59;
							}
						}
					}
				}).start();
			} catch (Exception ex) {
				play.setImageResource(R.drawable.palyoff);
				resetTimer();
				playing = false;
			}
		} else {
			play.setImageResource(R.drawable.palyoff);
			playing = false;
			try {
				reprodutor.stop();
				reprodutor.release();
			} catch (Exception ex) {
			}
			resetTimer();
		}
	}

	/** Método para reposicionar a progress bar no inicio e voltar 
	 * a colocar o timer no inicio.
	 * @author Thiago
	 */
	protected void resetTimer() {
		try {
			reprodutor.setDataSource(endereco);
			reprodutor.prepare();
			pbDuracao.setMax(reprodutor.getDuration());
			pbDuracao.setProgress(0);
		} catch (Exception ex) {
		}
		tSegundo = segundos;
		tMinuto = minutos;
		chrono.setText(n2d(tMinuto) + ":" + n2d(tSegundo));
	}

	/** Método para iniciar o processo de avaliar a submissão
	 * @author Thiago, adaqptado por Dário
	 */
	private void startAvalia() {
		File file = new File(endereco);
		if (file.exists()) { // se ja fez uma gravacao
			// uma pop-up ou activity para determinar o valor de
			// exprecividade da leitura
			// usar a classe Avaliacao para calcular os resultados.
			resetTimer();
			eval = new Avaliacao(totalDePalavras, 0, plvErradas);
			numPalavCorretas = eval.palavrasCertas();
			velocidade = eval.VL(tMinuto, tSegundo);
			// Log.d("Debug-vl", tMinuto + "min - sec" + tSegundo);
			precisao = eval.PL();
			switch (auxiliar) {
			case 0:
				stringAuxForType = "Texto";
				break;
			case 1:
				stringAuxForType = "Multimedia";
				break;
			case 2:
				stringAuxForType = "Palavras";
				break;
			case 3:
				stringAuxForType = "Poema";
				break;
			default:
				stringAuxForType = "Indefenido";
				break;
			}
			numPalavrasMin = eval.PLM(tMinuto, tSegundo);
			// Log.d("Debug-PLM", numPalavrasMin + " ");
			submit();
		} else {
			android.app.AlertDialog alerta;
			// Cria o gerador do AlertDialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// define o titulo
			builder.setTitle("Letrinhas");
			// define a mensagem
			builder.setMessage(" Nao existe o ficheiro audio!");
			// define um botao como positivo
			builder.setPositiveButton("OK", null);
			// cria o AlertDialog
			alerta = builder.create();
			// Mostra
			alerta.show();
		}
	}

	/**
	 * Neste metodo irao criar o primeiro butao, que irao servir de modelo para
	 * os restantes
	 * @author Dário
	 */
	public void initSetup(Resources res, int list, int toggle, int layout,
			String[] ar) {
		ToggleButton tg = (ToggleButton) findViewById(toggle);
		tg.setTextColor(Color.DKGRAY);
		tg.setTextSize(22);
		tg.setBackgroundColor(Color.DKGRAY);
		tg.setTextColor(Color.WHITE);
		totalDePalavras++;
		tg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CompoundButton) v).isChecked()) {
					v.setBackgroundColor(Color.RED);
					plvErradas++;
					// Log.d("Debug-plvErradas", String.valueOf(plvErradas));
					valueWord.setText(String.valueOf(plvErradas));
				} else {
					v.setBackgroundColor(Color.DKGRAY);
					plvErradas--;
					valueWord.setText(String.valueOf(plvErradas));
				}
			}
		});
		tg.setText(ar[0]);
		tg.setTextOn(ar[0]);
		tg.setTextOff(ar[0]);
		LinearLayout ll = (LinearLayout) findViewById(layout);
		// Resto do titulos
		for (int i = 1; i < ar.length; i++) {
			buttonSetUp(ar, i, ll, tg);
			totalDePalavras++;
		}
	}

	/**
	 * Esta metodo serve para a criacao de todos os outros butoes
	 * 
	 * @param teste
	 * @param i
	 * @param ll
	 * @param tg1
	 * @author Dário
	 */
	public void buttonSetUp(String[] teste, int i, LinearLayout ll,
			ToggleButton tg1) {
		ToggleButton tg = new ToggleButton(getBaseContext());
		tg.setLayoutParams(tg1.getLayoutParams());
		tg.setBackgroundColor(Color.DKGRAY);
		tg.setTextColor(Color.WHITE);
		tg.setTextSize(22);
		tg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CompoundButton) v).isChecked()) {
					v.setBackgroundColor(Color.RED);
					plvErradas++;
					valueWord.setText(String.valueOf(plvErradas));
				} else {
					v.setBackgroundColor(Color.DKGRAY);
					plvErradas--;
					valueWord.setText(String.valueOf(plvErradas));
				}
			}
		});
		tg.setText(teste[i]);
		tg.setTextOn(teste[i]);
		tg.setTextOff(teste[i]);
		ll.addView(tg);
	}
}