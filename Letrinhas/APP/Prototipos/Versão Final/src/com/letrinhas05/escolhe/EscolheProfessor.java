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
import com.letrinhas05.ClassesObjs.Professor;
import com.letrinhas05.util.Autenticacao;
import com.letrinhas05.util.SystemUiHider;

/**
 * Classe de apoio à Pagina de escolha de Professor
 * 
 * @author Thiago
 */
public class EscolheProfessor extends Activity {

	// /Varivaveis/////////////////
	protected Button btnVoltar;
	protected int nProfs, idEscola, idProf;
	protected String escolaNome, profNome, fotoProf;
	protected List<Professor> listaProfs;
	protected String[] arrNomesFotosProfs;
	protected LetrinhasDB db;
	protected int[] arridProfs;
	protected String[] username;

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
		setContentView(R.layout.activity_escolhe_professor);

		// Ir buscar os extras a janela anterior
		Bundle b = getIntent().getExtras();
		idEscola = b.getInt("Escola_ID");
		escolaNome = b.getString("Escola");

		// ///////////Aceder a objectos visuais da janela///////
		btnVoltar = (Button) findViewById(R.id.btnVoltarProf);
		((TextView) findViewById(R.id.escPEscola)).setText(escolaNome);
		final View contentView = findViewById(R.id.escProf);
		// ////////////////////////////////////////////////////////////

		// new line faz a rotação do ecrãn em 180 graus
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

		// Botao de voltar
		btnVoltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent it = new Intent(getApplicationContext(),
						EscolheEscola.class);
				startActivity(it);
				finish();
			}
		});
		makeTabela();
	}

	/**
	 * Novo método para criar o painel dinâmico para os botões de
	 * seleção do professor
	 * 
	 * @author Thiago
	 */
	@SuppressLint("NewApi")
	private void makeTabela() {

		// Cria o objecto da base de dados
		db = new LetrinhasDB(this);
		listaProfs = db.getAllProfesorsBySchool(idEscola);
		nProfs = listaProfs.size();
		arridProfs = new int[listaProfs.size()];
		username = new String[listaProfs.size()];
		arrNomesFotosProfs = new String[listaProfs.size()];

		for (int i = 0; i < nProfs; i++) {
			username[i] = listaProfs.get(i).getNome();
			arridProfs[i] = listaProfs.get(i).getId();
			arrNomesFotosProfs[i] = listaProfs.get(i).getFotoNome();
		}

		/**
		 * Scroll view com uma table de 4 colunas(max)
		 */
		// tabela a editar
		TableLayout tabela = (TableLayout) findViewById(R.id.tblEscolheProf);
		// linha da tabela a editar
		TableRow linha = (TableRow) findViewById(R.id.Proflinha01);
		// 1� bot�o
		Button bt = (Button) findViewById(R.id.PrfBtOriginal);
		bt.setText("teste professores");

		// Contador de controlo
		int cont = 0;
		int largura = getResources().getDimensionPixelSize(R.dimen.dim240);
		for (int i = 0; i < nProfs / 4; i++) {
			// nova linha da tabela
			TableRow linha1 = new TableRow(getBaseContext());
			// Copiar os parametros da 1� linha
			linha1.setLayoutParams(linha.getLayoutParams());
			for (int j = 0; j < 4; j++) {

				// **********************************
				// id do professor
				final int idPrf = arridProfs[cont];
				// ***********************************

				// novo bot�o
				Button bt1 = new Button(bt.getContext());
				// copiar os parametros do bot�o original
				bt1.setLayoutParams(bt.getLayoutParams());

				// se a professor tiver foto, vou busca-la
				if (arrNomesFotosProfs[cont] != null) {
					String imageInSD = Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/School-Data/Professors/"
							+ arrNomesFotosProfs[cont];
					Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
					ImageView imageView = new ImageView(this);
					// ajustar o tamanho da imagem
					imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
							largura, largura, false));
					// enviar para o bot�o
					bt1.setCompoundDrawablesWithIntrinsicBounds(null,
							imageView.getDrawable(), null, null);
				} else {
					// senao copia a imagem do botao original
					bt1.setCompoundDrawables(null,
							bt.getCompoundDrawablesRelative()[1], null, null);
				}

				// addicionar o nome
				bt1.setText(username[cont]);
				// /////////////////////////BOTAO DE CLICAR DA
				// LISTA////////////////////////
				bt1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LetrinhasDB bd = new LetrinhasDB(
								getApplicationContext());
						Professor prf = bd.getProfessorById(idPrf);

						String PIN = prf.getPassword();
						Bundle wrap = new Bundle();
						wrap.putString("PIN", PIN);

						// iniciar a pagina (Autentica��o)
						Intent at = new Intent(getApplicationContext(),
								Autenticacao.class);
						at.putExtras(wrap);
						startActivityForResult(at, 1);
						idProf = prf.getId();
						profNome = prf.getNome();
						fotoProf = prf.getFotoNome();

					}
				});
				// inserir o botAo na linha
				linha1.addView(bt1);
				// incrementar o contador de controlo
				cont++;
			}
			// inserir a linha criada
			tabela.addView(linha1);
		}

		// resto
		if (nProfs % 4 != 0) {
			TableRow linha1 = new TableRow(getBaseContext());
			linha1.setLayoutParams(linha.getLayoutParams());
			for (int j = 0; j < nProfs % 4; j++) {
				// **********************************
				// id do professor
				final int idPrf = arridProfs[cont];
				// ***********************************

				// novo bot�o
				Button bt1 = new Button(bt.getContext());
				// copiar os parametros do bot�o original
				bt1.setLayoutParams(bt.getLayoutParams());

				// se a professor tiver foto, vou busca-la
				if (arrNomesFotosProfs[cont] != null) {
					String imageInSD = Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/School-Data/Professors/"
							+ arrNomesFotosProfs[cont];
					Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
					ImageView imageView = new ImageView(this);

					// ajustar o tamanho da imagem
					imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
							largura, largura, false));
					// enviar para o bot�o
					bt1.setCompoundDrawablesWithIntrinsicBounds(null,
							imageView.getDrawable(), null, null);
				} else {
					// sen�o copia a imagem do bot�o original
					bt1.setCompoundDrawables(null,
							bt.getCompoundDrawablesRelative()[1], null, null);
				}

				// addicionar o nome
				bt1.setText(username[cont]);
				// Defenir o que faz o bot�o ao clicar
				bt1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LetrinhasDB bd = new LetrinhasDB(
								getApplicationContext());
						Professor prf = bd.getProfessorById(idPrf);

						String PIN = prf.getPassword();
						Bundle wrap = new Bundle();
						wrap.putString("PIN", PIN);

						// iniciar a pagina (Autentica��o)
						Intent at = new Intent(getApplicationContext(),
								Autenticacao.class);
						at.putExtras(wrap);
						startActivityForResult(at, 1);
						idProf = prf.getId();
						profNome = prf.getNome();
						fotoProf = prf.getFotoNome();
					}
				});
				// inserir o bot�o na linha
				linha1.addView(bt1);
				// incrementar o contador de controlo
				cont++;
			}
			// inserir a linha criada com o resto dos bot�es
			tabela.addView(linha1);
		}
		// por fim escondo a 1� linha
		tabela.removeView(linha);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data.getExtras().getBoolean("Resultado")) {
			entrar();
		}
	}

	private void entrar() {
		// ///////////PARAMETROS PARA A PROXIMA JANELA/////////////////
		Bundle wrap = new Bundle();
		wrap.putString("Escola", escolaNome);
		wrap.putInt("Escola_ID", idEscola);
		wrap.putString("Professor", profNome);
		wrap.putString("foto_Professor", fotoProf);
		wrap.putInt("Professor_ID", idProf);
		Intent it = new Intent(getApplicationContext(), EscolheTurma.class);
		it.putExtras(wrap);
		startActivity(it);
		finish();
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
