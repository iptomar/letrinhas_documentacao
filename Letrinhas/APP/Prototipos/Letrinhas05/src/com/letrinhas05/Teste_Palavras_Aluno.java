package com.letrinhas05;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.letrinhas05.BaseDados.LetrinhasDB;
import com.letrinhas05.ClassesObjs.CorrecaoTeste;
import com.letrinhas05.ClassesObjs.CorrecaoTesteLeitura;
import com.letrinhas05.ClassesObjs.Teste;
import com.letrinhas05.ClassesObjs.TesteLeitura;
import com.letrinhas05.util.SystemUiHider;

public class Teste_Palavras_Aluno extends Activity{
	// flags para verificar os diversos estados do teste
			boolean modo, gravado, recording, playing;
			// objetos
			ImageButton record, play, voltar, cancelar, avancar,voicePlay;
			// variaveis contadoras para a avalia��o
			int numero=0,nTestes,testeId;
			private MediaRecorder gravador;
			private MediaPlayer reprodutor = new MediaPlayer();
			String endereco,yo,yo1,yo2,profSound,tempo,path;
			String uuid = UUID.randomUUID().toString();
			LetrinhasDB db;
			CorrecaoTesteLeitura ctl;
			
			TesteLeitura teste;
			int tipo, idTesteAtual;
			String[] Nomes;
			int[] iDs, testesID;
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
				db = new LetrinhasDB(this);
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
				Bundle b = getIntent().getExtras();
				inicia(b);
				/**
				 * 
				 * 
				 * 
				 */
				endereco = Environment.getExternalStorageDirectory().getAbsolutePath() + "/School-Data/CorrectionReadTest/"+uuid+".mp3";
				path =  "/School-Data/CorrectionReadTest/"+uuid+".mp3";
				profSound = Environment.getExternalStorageDirectory().getAbsolutePath() + "/School-Data/ReadingTests/SOM1.mp3";

				Log.d("Debug-ButtonRecord", String.valueOf(findViewById(R.id.txtRecordPalavras)));
				Log.d("Debug-ButtonComeca", String.valueOf(findViewById(R.id.txtVoicePlay)));
				record = (ImageButton) findViewById(R.id.txtRecordPalavras);
				play = (ImageButton) findViewById(R.id.txtDemoPlay);
				play.setVisibility(View.INVISIBLE);
				voicePlay = (ImageButton) findViewById(R.id.txtVoicePlay);
				voltar = (ImageButton) findViewById(R.id.txtVoltar);
				cancelar = (ImageButton) findViewById(R.id.txtCancel);
				avancar = (ImageButton) findViewById(R.id.txtAvaliar);
				escutaBotoes();
			}

			/**
			 * m�todo para iniciar os componetes, que dependem do conteudo passado
			 * por parametros (extras)
			 * 
			 * @param b Bundle, cont�m informa��o da activity anterior
			 */
			public void inicia(Bundle b){
				// Compor novamente e lista de testes
				testesID = b.getIntArray("ListaID");
				// String's - Escola, Professor, fotoProf, Turma, Aluno, fotoAluno
				Nomes = b.getStringArray("Nomes");
				// int's - idEscola, idProfessor, idTurma, idAluno
				iDs = b.getIntArray("IDs");

				/** Consultar a BD para preencher o conte�do.... */
				LetrinhasDB bd = new LetrinhasDB(this);
				teste =  bd.getTesteLeituraById(testesID[0]);
				Log.d("Debug-Iniciar(b)", "testesID->"+String.valueOf(testesID[0])+" teste->"+teste.getTexto());
				Log.d("Debug-getTitulo()", teste.getTitulo());
				String[] yo = teste.getTexto().split("[ ]");
				String yo2 = "";
				((TextView) findViewById(R.id.textTitulo)).setText(teste.getTitulo());
				for(int i = 0; i<yo.length;i++){
					yo2 += yo[i]+"\n";
				}
				((TextView) findViewById(R.id.TextView01)).setText(yo2);
				((TextView) findViewById(R.id.TextView02)).setText(yo2);
				((TextView) findViewById(R.id.TextView03)).setText(yo2);

				// **********************************************************************************************

				idTesteAtual = testesID[0];
				// descontar este teste da lista.
				int[] aux = new int[testesID.length - 1];
				for (int i = 1; i < testesID.length; i++) {
					aux[i - 1] = testesID[i];
				}
				testesID = aux;
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
						startPlay("gravacao");
					}
				});
				voicePlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startPlay("vozProf");
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
						submit();
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
			
			/**
			 * 
			 * @return yyyy-MM-dd HH:mm:ss formate date as string
			 */
			@SuppressLint("SimpleDateFormat")
			public static String getCurrentTimeStamp(){
			    try {

			        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

			        return currentTimeStamp;
			    } catch (Exception e) {
			        e.printStackTrace();

			        return null;
			    }
			}
			
			/**
			 * znyt
			 * znrdy
			 * xdgh
			 * dnyx
			 * dm
			 * @author D�rio
			 */
			public void submit(){
				ctl = new CorrecaoTesteLeitura();
				File file = new File(endereco);
				if(!file.exists()){
					Toast.makeText(getApplicationContext(),"N�o gravou nada",Toast.LENGTH_SHORT).show();
				}else{
					long time = System.currentTimeMillis() / 1000;
					String aux = idTesteAtual + iDs[3] + time + "";
					ctl.setIdCorrrecao(Long.parseLong(aux));
					ctl.setAudiourl(path);
					ctl.setDataExecucao(time);
					ctl.setTipo(2);// pois estou num teste texto
					ctl.setEstado(0);
					ctl.setTestId(idTesteAtual);
					ctl.setIdEstudante(iDs[3]);
					Toast.makeText(getApplicationContext(),ctl.getAudiourl(),Toast.LENGTH_SHORT).show();
					Toast.makeText(getApplicationContext(),getCurrentTimeStamp(),Toast.LENGTH_SHORT).show();
					Toast.makeText(getApplicationContext(),String.valueOf(ctl.getIdEstudante()),Toast.LENGTH_SHORT).show();
					Toast.makeText(getApplicationContext(),String.valueOf(ctl.getTestId()),Toast.LENGTH_SHORT).show();
					db.addNewItemCorrecaoTesteLeitura(ctl);
					
					List<CorrecaoTeste> data1 = db.getAllCorrecaoTeste();
					Log.d("CheckInserts: ", "***********Testes******************");
					for (CorrecaoTeste cn : data1) {
						String logs = "Id: " + cn.getIdCorrrecao() + ", idEstudante: "
								+ cn.getIdEstudante() + "  , estado: " + cn.getEstado()
								+ "  , testeId: " + cn.getTestId() + "  , tipo: "
								+ cn.getTipo() + "  , data: " + cn.getDataExecucao();
						// Writing Contacts to log
						Log.d("CheckInserts: ", logs);
					}
					finaliza();
					
					Bundle wrap = new Bundle();
					wrap.putInt("IDTeste", idTesteAtual);// id do teste atual
					wrap.putInt("IDAluno", iDs[3]); //id do aluno
					// listar submiss�es anteriores do mesmo teste
					 Intent it = new Intent(getApplicationContext(),
					 ResumoSubmissoes.class);
					 it.putExtras(wrap);
					 startActivity(it);
				}
			}
			
			public void delete(){
				File file = new File(endereco);
				if (file.exists()) {
					file.delete();
				}		
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
						// handler para controlar a GUI do android e a thread seguinte
						play_handler = new Handler() {
							@SuppressLint("HandlerLeak")
							public void handleMessage(Message msg) {
								switch (msg.what) {
								case PARADO:
									String m,s;
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
									tempo =  m + s;
									Toast.makeText(getApplicationContext(), "Tempo: "+ m + s,Toast.LENGTH_SHORT).show();
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
			private void startPlay(String path) {
				if (!playing) {
					play.setImageResource(R.drawable.play_on);
					record.setVisibility(View.INVISIBLE);
					playing = true;
					try {
						reprodutor = new MediaPlayer();
						Toast.makeText(getApplicationContext(),"Tipo de som a ser reproduzido - "+path.toString(),Toast.LENGTH_SHORT).show();
						if(path=="vozProf"){
							Log.d("Debug-path", path);
							Log.d("Debug-vozProf", profSound);
							reprodutor.setDataSource(profSound);
						}else if(path=="gravacao"){
							reprodutor.setDataSource(endereco);
						}
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
			 * Prepara a finalizacao da activity, descobrindo qual o proximo teste
			 * a realizar Este metodo devera ser usado em todas as paginas de teste.
			 */
			private void finaliza() {
				if (testesID.length != 0) {
					// enviar o parametro de modo
					Bundle wrap = new Bundle();
					wrap.putIntArray("ListaID", testesID);// id's dos testes
					wrap.putStringArray("Nomes", Nomes);
					wrap.putIntArray("IDs", iDs);
					// identifico o tipo do pr�ximo teste
					LetrinhasDB bd = new LetrinhasDB(this);
					Teste tst = bd.getTesteById(testesID[0]);
					tipo = tst.getTipo();
					switch (tipo) {
					case 0:
						// lan�ar a nova activity do tipo texto,
						Intent it = new Intent(getApplicationContext(),Teste_Texto.class);
						it.putExtras(wrap);
						startActivity(it);
						break;
					case 1:// lan�ar a nova activity do tipo imagem
						Intent ip = new Intent(getApplicationContext(),Teste_Imagem.class);
						ip.putExtras(wrap);
						startActivity(ip);
						break;
					case 2: // lan�ar a nova activity do tipo Palavras
						Intent ipm = new Intent(getApplicationContext(),Teste_Palavras_Aluno.class);
						ipm.putExtras(wrap);
						startActivity(ipm);
						break;
					case 3: // lan�ar a nova activity do tipo poema
						Intent ipp = new Intent(getApplicationContext(),Teste_Palavras_Aluno.class);
						ipp.putExtras(wrap);
						startActivity(ipp);
						break;
					default:
						Toast.makeText(getApplicationContext(), " - Tipo n�o defenido",Toast.LENGTH_SHORT).show();
						// retirar o teste errado e continuar
						// descontar este teste da lista.
						int[] aux = new int[testesID.length - 1];
						for (int i = 1; i < testesID.length; i++) {
							aux[i - 1] = testesID[i];
						}
						testesID = aux;
						finaliza();
						break;
					}
				}
				finish();
			}
		}
