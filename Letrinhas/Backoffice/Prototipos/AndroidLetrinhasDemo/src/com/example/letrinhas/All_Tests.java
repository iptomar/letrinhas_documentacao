package com.example.letrinhas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.example.androidhive.R;
import com.example.letrinhas.ClassesObjs.Teste;
import com.example.letrinhas.ClassesObjs.TesteLeitura;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class All_Tests extends Activity {

    ////////////////////////////////////////////////////////////////////////////////
    // JSON Nome dos campos
    private static final String TAG_ID = "id";
    private static final String TAG_AREAID = "areaId";
    private static final String TAG_PROFESSORID = "professorId";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TEXT = "mainText";
    private static final String TAG_CREATING_DATE = "creationDate";
    private static final String TAG_GRADE = "grade";
    private static final String TAG_TYPE= "type";
    private static final String TAG_TEXTCONTENT = "textContent";
    private static final String TAG_PROFAUDIOURL = "professorAudioUrl";


    // URL do Servidor  Nota: Recebido em baixo//////////////////////////////
    private static String url_all_tests = ""; ////
    /////Variaveis que vai guardar o ip e porta recebido da janela anterior/////
    public String ip;
    public String porta;
    // Criar o objecto JSON Parser
    JSONParser jParser = new JSONParser();
    //Array list de testes
    //Ficheiro JSONArray dos testes
    JSONArray tests = null;
    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__tests);
        // Obtendo IP E PORTA
        Intent i = getIntent();
        ip = i.getStringExtra("IP");
        porta = i.getStringExtra("PORTA");


        Button btnSinAllTest = (Button) findViewById(R.id.btnSinAllTest);
        // view Click no botao, vai chamar a thread de loadTests
        btnSinAllTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText  txtano = (EditText) findViewById(R.id.txtAnoTestsSinc);
                url_all_tests = "http://" + ip + ":" + porta + "/tests?grade="+txtano.getText()+ "&type=0";  //// type 0 - leitura !!  1 - multimedia
                new LoadAllTests().execute();
            }
        });
    }

    /**
     * Faz up http request onde recebe um json com a informacao pedida
     * e começa a inserir essa informacao nas tabelas sqlLite
     */
    public void inserirNaBd()
    {
        LetrinhasDB db = new LetrinhasDB(this);
        ////////////Apagar dados das tabelas //////////////////
        db.deleteAllItemsTests();
        db.deleteAllItemsTestsLeitura();
        Log.d("BDDADOS: ", "*********BUSCAR JSON********************");
        // Construi os Parametros
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
      tests = jParser.getJSONArray(url_all_tests, params);
        try {

            // For (loop)looping atraves de todos os Testes
            for (int i = 0; i < tests.length(); i++) {
                TesteLeitura testeleitura = new TesteLeitura();
                JSONObject c = tests.getJSONObject(i);
              ///////// Preencher um objecto do tipo teste com a informaçao    ///////////////
                testeleitura.setIdTeste(c.getInt(TAG_ID));
                testeleitura.setAreaId(c.getInt(TAG_AREAID));
                testeleitura.setProfessorId(c.getInt(TAG_PROFESSORID));
                testeleitura.setTitulo(c.getString(TAG_TITLE));
                testeleitura.setTexto(c.getString(TAG_TEXT));
                testeleitura.setDataInsercaoTeste(c.getLong(TAG_CREATING_DATE));
                testeleitura.setGrauEscolar(c.getInt(TAG_GRADE));
                testeleitura.setTipos(0);
                //////////////////// inforamacao relativa aos testes de leitura que vai para outra tabela
                testeleitura.setConteudoTexto(c.getString(TAG_TEXTCONTENT));
                testeleitura.setProfessorAudioUrl(c.getString(TAG_PROFAUDIOURL));
                //////INSERIR NA BASE DE DADOS OS CAMPOS RECEBIDOS
                db.addNewItemTestesLeitura(testeleitura);
            }

            /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA TESTES//////////////
            List<Teste> dados = db.getAllTeste();
            Log.d("BDDADOS: ", "*********Testes********************");
            for (Teste cn : dados) {
                String logs = "getIdTeste:   " + cn.getIdTeste() +
                        ",getTitulo:   " + cn.getTitulo() +
                        ",getTexto:    " + cn.getTexto() +
                        ", getDataInsercaoTeste:    " + cn.getDataInsercaoTeste() +
                        ", getGrauEscolar:    " + cn.getGrauEscolar() +
                        ", getTipo:    " + cn.getTipo();
                        Log.d("BDDADOS: ", logs);
            }



            /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA TESTELEITURA//////////////
            List<TesteLeitura> dados2 = db.getAllTesteLeitura();
            Log.d("BDDADOS: ", "\n*********testesleitura********************");
            for (TesteLeitura cn : dados2) {
                String cenas = "getIdTeste:" + cn.getIdTeste() +
                        ", getConteudoTexto: " + cn.getConteudoTexto() +
                        ", getProfessorAudioUrl: " + cn.getProfessorAudioUrl();
                Log.d("BDDADOS: ", cenas);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async Task para carregar todos os Testes por HTTP Request
     */
    class LoadAllTests extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar a Thread Bacground aparece a progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(All_Tests.this);
            pDialog.setMessage("A Carregar Testes. Por Favor aguarde...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Faz em Background
         */
        protected String doInBackground(String... args) {
            inserirNaBd();
            return null;
        }

        /**
         * Depois de completar tarefa de background Fechar a Progress Dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // fechar a janela de Progress Dialog depois de receber todos os
            // Tests
            pDialog.dismiss();
            // Actualizar a UI a partir da Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /////////nao faz nada ////
                }
            });

        }

    }

}
