package com.letrinhas04;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.letrinhas04.BaseDados.LetrinhasDB;
import com.letrinhas04.ClassesObjs.Teste;
import com.letrinhas04.util.SystemUiHider;

public class Teste_Palavras_Aluno extends Activity{
	// flags para verificar os diversos estados do teste
			boolean modo, gravado, recording, playing;
			// objetos
			ImageButton record, play, voltar, cancelar, avancar,voicePlay;
			TextView pnt, vcl, frg, slb, rpt, pErr;
			Chronometer chrono;
			// variaveis contadoras para a avalia��o
			int plvErradas, pontua, vacil, fragment, silabs, repeti,tipoDeTextView, numero=0,nTestes;
			private MediaRecorder gravador;
			private MediaPlayer reprodutor = new MediaPlayer();
			String endereco,yo;
			LetrinhasDB db;
			List<Teste> testePalavras;
			String[] texto;
			String[] texto2;
			String[] titulo;
			String[] titulo2;
			int[] tipo;
			int[] tipo2;
			TextView auxiliar;
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
				setContentView(R.layout.teste_palavras_aluno);
				//new line faz a rota��o do ecr�n 180 graus
				int currentOrientation = getResources().getConfiguration().orientation;
				if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				}else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				}
				// / esconder o title************************************************+
				final View contentView = findViewById(R.id.testPalavras);
				// Set up an instance of SystemUiHider to control the system UI for
				// this activity.
				mSystemUiHider = SystemUiHider.getInstance(this, contentView,HIDER_FLAGS);
				mSystemUiHider.setup();
				mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mShortAnimTime;
							@Override
							@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
							public void onVisibilityChange(boolean visible) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
									if (mShortAnimTime == 0) {
										mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
									}
								}
								if (visible && AUTO_HIDE) {
									// Schedule a hide().
									delayedHide(AUTO_HIDE_DELAY_MILLIS);
								}
							}
						});
				// buscar os parametros
				//Bundle b = getIntent().getExtras();
				// Compor novamnete e lista de testes
				//int lstId[] = b.getIntArray("ListaID");
				//int[] lstTipo = b.getIntArray("ListaTipo");
				//String[] lstTitulo = b.getStringArray("ListaTitulo");
				//String[] lstTexto = b.getStringArray("ListaTexto");
				
				db = new LetrinhasDB(this);
				testePalavras = db.getAllTeste();
				nTestes = testePalavras.size();
				texto = new String[nTestes];
				texto2 = new String[nTestes];
				titulo = new String[nTestes];
				titulo2 = new String[nTestes];
				tipo = new int[nTestes];
				tipo2 = new int[nTestes];
				//tudo = new String[nTestes];
				for(Teste cn: testePalavras){
					//tudo[numero] = cn.getTexto()+","+cn.getTipo()+","+cn.getTitulo();
					//Log.d("debug-Palavras", tudo[numero]);
					texto[numero] = cn.getTexto();
					tipo[numero] = cn.getTipo();
					titulo[numero] = cn.getTitulo();
					numero++;
				}
				// Consultar a BD para preencher o conte�do....
				String[] var;
				for(int i = 0; i<tipo.length; i++){
					var = texto[i].split("[ ]");
					for(int j = 1; j< var.length; j++){
						yo += var[j]+"\n";
						//Log.d("debug-EXtrs", yo);
					} 
					//Log.d("debug-EndText", yo);
				}
				Log.d("debug-EndText", yo);
				((TextView) findViewById(R.id.TextView01)).setText(yo);
				((TextView) findViewById(R.id.TextView02)).setText(yo);
				((TextView) findViewById(R.id.TextView03)).setText(yo);
				//endereco = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + b.getString("Professor") + "/" + b.getString("Aluno") + "/" + titulo[0] + ".3gpp";
				//endereco = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + b.getString("Professor") + "/" + b.getString("Aluno") + "/" + titulo[0] + ".3gpp";
				// descontar este teste da lista.
				//tudo = new String[nTestes - 1];
				//String[] aux = tudo;
				for (int i = 1; i < nTestes; i++) {
					//aux[i - 1] = tudo[i];
				}
				//tudo = aux;
				record = (ImageButton) findViewById(R.id.txtRecord);
				play = (ImageButton) findViewById(R.id.txtPlay);
				play.setVisibility(View.INVISIBLE);
			
				//	voicePlay = (ImageButton) findViewById(R.id.voicePlay);
				
				voltar = (ImageButton) findViewById(R.id.txtVoltar);
				cancelar = (ImageButton) findViewById(R.id.txtCancel);
				avancar = (ImageButton) findViewById(R.id.txtAvaliar);
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

			public void setUp() {
				gravador = new MediaRecorder();
				gravador.setAudioSource(MediaRecorder.AudioSource.MIC);
				gravador.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				gravador.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
				// construir as pastas caso necess�rio
				File file = new File(endereco);
				if (file.getParent() != null && !file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				gravador.setOutputFile(endereco);
			}

			private void escutaBotoes() {
				record.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startGrava();
					}

				});
				play.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startPlay();
					}
				});
				voicePlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						//startPlay();
					}
				});
				cancelar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// salta a avalia��o e vai para o pr�ximo teste descurando a
						// grava��o gerada
						/*File file = new File(endereco);
						if (file.exists()) {
							file.delete();
						}*/
						finaliza();
					}
				});
				avancar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// voltar para pag inicial
						//startAvalia();
					}
				});
				voltar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// voltar para pag inicial
						finish();
					}
				});
			}
			int minuto, segundo;
			/**
			 * Serve para come�ar ou parar o recording do audio
			 * 
			 * @author D�rio Jorge
			 */
			@SuppressLint("HandlerLeak")
			private void startGrava() {
				if (!recording) {
					record.setImageResource(R.drawable.stop);
					play.setVisibility(View.INVISIBLE);
					cancelar.setVisibility(View.INVISIBLE);
					voltar.setVisibility(View.INVISIBLE);
					avancar.setVisibility(View.INVISIBLE);
					recording = true;
					try {
						setUp();
						gravador.prepare();
						gravador.start();
						Toast.makeText(getApplicationContext(), "A gravar.",Toast.LENGTH_SHORT).show();
						// O cronometro n�o funciona assim t�o bem no seu modo
						// original...
						chrono = (Chronometer) findViewById(R.id.cromTxt);
						// handler para controlar a GUI do android e a thread seguinte
						play_handler = new Handler() {
							@SuppressLint("HandlerLeak")
							public void handleMessage(Message msg) {
								switch (msg.what) {
								case PARADO:
									String m,
									s;
									if (minuto < 10) {
										m = "0" + minuto + ":";
									} else {
										m = minuto + ":";
									}
									if (segundo < 10) {
										s = "0" + segundo;
									} else {
										s = "" + segundo;
									}
									chrono.setText(m + s);
									break;
								default:
									break;
								}
							}
						};
						// pequena thread, para interagir com o cronometro
						new Thread(new Runnable() {
							public void run() {
								minuto = 0;
								segundo = 0;
								while (recording) {
									Message msg = new Message();
									msg.what = PARADO;
									play_handler.sendMessage(msg);

									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									segundo++;
									if (segundo == 60) {
										minuto++;
										segundo = 0;
									}
								}
							}
						}).start();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(),"Erro na grava��o.\n" + e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				} else {
					record.setImageResource(R.drawable.record);
					play.setVisibility(View.VISIBLE);
					cancelar.setVisibility(View.VISIBLE);
					voltar.setVisibility(View.VISIBLE);
					avancar.setVisibility(View.VISIBLE);
					recording = false;
					try {
						gravador.stop();
						gravador.release();
						Toast.makeText(getApplicationContext(),"Grava��o efetuada com sucesso!", Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(),"Tempo de leitura: " + minuto + ":" + segundo,Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(),"Erro na grava��o.\n" + e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				}
			}
			private final int PARADO = 2;
			private Handler play_handler;
			/**
			 * serve para a aplica��o reproduzir ou parar o som
			 * 
			 * @author D�rio Jorge
			 */
			@SuppressLint("HandlerLeak")
			private void startPlay() {
				if (!playing) {
					play.setImageResource(R.drawable.play_on);
					record.setVisibility(View.INVISIBLE);
					playing = true;
					try {
						reprodutor = new MediaPlayer();
						reprodutor.setDataSource(endereco);
						reprodutor.prepare();
						reprodutor.start();
						Toast.makeText(getApplicationContext(), "A reproduzir.",Toast.LENGTH_SHORT).show();
						// espetar aqui uma thread, para caso isto pare
						// handler para controlara a GUI do androi e a thread seguinte
						play_handler = new Handler() {
							public void handleMessage(Message msg) {
								switch (msg.what) {
								case PARADO:
									play.setImageResource(R.drawable.palyoff);
									record.setVisibility(View.VISIBLE);
									playing = false;
									try {
										reprodutor.stop();
										reprodutor.release();
										Toast.makeText(getApplicationContext(),"Fim da reprodu��o.",Toast.LENGTH_SHORT).show();
									} catch (Exception ex) {
									}
									break;
								default:
									break;
								}
							}
						};
						new Thread(new Runnable() {
							public void run() {
								while (reprodutor.isPlaying());
								Message msg = new Message();
								msg.what = PARADO;
								play_handler.sendMessage(msg);
							}
						}).start();
					} catch (Exception ex) {
						Toast.makeText(getApplicationContext(),"Erro na reprodu��o.\n" + ex.getMessage(),Toast.LENGTH_SHORT).show();
					}
				} else {
					play.setImageResource(R.drawable.palyoff);
					record.setVisibility(View.VISIBLE);
					playing = false;
					try {
						reprodutor.stop();
						reprodutor.release();

						Toast.makeText(getApplicationContext(),"Reprodu��o interrompida.", Toast.LENGTH_SHORT).show();
					} catch (Exception ex) {
						Toast.makeText(getApplicationContext(),"Erro na reprodu��o.\n" + ex.getMessage(),Toast.LENGTH_SHORT).show();
					}
				}
			}

			/**
			 * Prepara a finaliza��o da activity, descobrindo qual o pr�ximo teste a
			 * realizar Este m�todo dever� ser usado em todas as paginas de teste.
			 */
			private void finaliza() {
				if (tipo.length != 0) {
					// Decompor o array de teste, para poder enviar por parametros
					//Log.d("debug-SetBack", tipo[0]);
					for (int i = 0; i < tipo.length; i++) {
						String[] bach = texto; 
						for(int j=0;j<bach.length;j++){
							Log.d("debug-CheckType", j+":"+bach[j]);
						}
						texto2[i] = texto[i];
						tipo2[i] = tipo[i];
						titulo2[i] = titulo[i];
					}
					Bundle wrap = new Bundle();
					// teste, a depender das informa��es da BD
					// ***********************************************************+
					//wrap.putStringArray("ListaID", texto);
					//wrap.putIntArray("ListaTipo", tipo);
					//wrap.putStringArray("ListaTitulo", titulo);
					// identifico o tipo de teste
					switch (tipo[0]) {
						case 0:
							// lan�ar a nova activity do tipo texto,
							// iniciar a pagina 2 (escolher teste)
							Intent it = new Intent(getApplicationContext(),Teste_Texto_Aluno.class);
							it.putExtras(wrap);
							//startActivity(it);
							break;
						case 1:// lan�ar a nova activity do tipo Palavras, e o seu conte�do
							Intent ip = new Intent(getApplicationContext(),Teste_Palavras_Aluno.class);
							ip.putExtras(wrap);
							startActivity(ip);
							break;
						case 2: // lan�ar a nova activity do tipo Poema, e o seu conte�do
							//
							Intent ipm = new Intent(getApplicationContext(),Teste_Poema_Aluno.class);
							ipm.putExtras(wrap);
							//startActivity(ipm);
							break;
						case 3: // lan�ar a nova activity do tipo imagem, e o seu conte�do
							//
							// Intent it = new Intent(getApplicationContext(),
							// Teste_Texto.class);
							// it.putExtras(wrap);
							// startActivity(it);
							break;
						default:
							Toast.makeText(getApplicationContext(), " - Tipo n�o defenido",Toast.LENGTH_SHORT).show();
							// retirar o teste errado e continuar
							/*int k = 0;
							Teste aux[] = new Teste[lista.length - 1];
							for (int i = 1; i < lista.length; i++) {
								aux[k] = lista[i];
								k++;
							}
							lista = aux;*/
							finaliza();
							break;
					}
				}
				finish();
			}
		}
