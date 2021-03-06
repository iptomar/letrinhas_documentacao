package com.letrinhas03;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.letrinhas03.BaseDados.JSONParser;
import com.letrinhas03.BaseDados.LetrinhasDB;
import com.letrinhas03.BaseDados.NetworkUtils;
import com.letrinhas03.ClassesObjs.*;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 03/05/2014.
 * ESTA CLASSE VAI FAZER A SINCRON DAS TABELAS EM BACKGROUND
 */
public class SincAllBd  extends AsyncTask<String,String,String> {

    public Context context;
    @Override
    protected String doInBackground(String[] strings) {
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        lerSynProfessores(strings[0]);
        lerSynEscolas(strings[0]);
        lerSynEstudante(strings[0]);
        lerSynTestes(strings[0]);
        return null;
    }

    public SincAllBd(Context context) {
        this.context = context;
    }

    /**
     *  Vai por HTTP buscar toda a informacao sobre os Professor e no final
     *  chama  o metodo para guardar na base dados
     *
     */
    protected void lerSynProfessores(String URlString) {

        String url = URlString + "professors/";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // JSONObject json = jParser.getJSONObject(url, params);
        JSONObject json = JSONParser.getJSONObject(url, params);

        try {

            JSONArray profs = json.getJSONArray("professors");
            Professor[] arrProf = new Professor[profs.length()];

            // For (loop)looping
            for (int i = 0; i < profs.length(); i++) {
                JSONObject c = profs.getJSONObject(i);
                // Armazenar cada item json nas vari�veis
                arrProf[i] = new Professor(
                        c.getInt("id"),
                        c.getInt("schoolId"), c.getString("name"),
                        c.getString("username"), c.getString("password"),
                        c.getString("emailAddress"), c.getString("telephoneNumber"),
                        NetworkUtils.getFile(URlString + c.getString("photoUrl")),
                        c.getInt("isActive")
                );
            }
            guardarProfBD(arrProf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *  Vai por HTTP buscar toda a informacao sobre os escolas e no final
     *  chama  o metodo para guardar na base de dados
     *
     */
    protected void lerSynEscolas(String URlString) {
        String url = URlString + "schools/";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = JSONParser.getJSONObject(url, params);

        try {

            JSONArray escola = json.getJSONArray("schools");
            Escola[] arrEscolas = new Escola[escola.length()];

            // For (loop)looping
            for (int i = 0; i < escola.length(); i++) {
                JSONObject c = escola.getJSONObject(i);
                // Armazenar cada item json nas variaveis
                arrEscolas[i] = new Escola(
                        c.getInt("id"),
                        c.getString("schoolName"),
                        NetworkUtils.getFile(URlString + c.getString("schoolLogoUrl")),
                        c.getString("schoolAddress")
                );
            }
            guardarEscolaBD(arrEscolas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Vai por HTTP buscar toda a informacao sobre os estudantes e no final
     *  chama  o metodo para guardar na base de dados
     *
     */
    protected void lerSynEstudante(String URlString) {
        String url = URlString + "students/";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = JSONParser.getJSONObject(url, params);

        try {

            JSONArray estudante = json.getJSONArray("students");
            Estudante[] arrEstudantes = new Estudante[estudante.length()];

            // For (loop)looping
            for (int i = 0; i < estudante.length(); i++) {
                JSONObject c = estudante.getJSONObject(i);
                // Armazenar cada item json nas vari�veis
                arrEstudantes[i] = new Estudante(
                        c.getInt("id"),
                        c.getInt("classId"),
                        c.getString("name"),
                        NetworkUtils.getFile(URlString + c.getString("photoUrl")),
                        c.getInt("isActive")
                );
            }
            guardarEstudantesBD(arrEstudantes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *  Vai por HTTP buscar toda a informacao sobre os TestesLeitura e no final
     *  chama  o metodo para guardar na base de dados
     */
    protected void lerSynTestes(String URlString) {
        String url = URlString + "tests?type=0";  //// type 0 -> leitura | 1 -> multimédia | 2 -> lista | 3 -> poema
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
        JSONParser jParser = new JSONParser();
        JSONArray  tests = jParser.getJSONArray(url, params);
        try {
            // For (loop)looping atraves de todos os Testes
            for (int i = 0; i < tests.length(); i++) {
                TesteLeitura testeleitura = new TesteLeitura();
                JSONObject c = tests.getJSONObject(i);
                ///////// Preencher um objecto do tipo teste com a informaçao    ///////////////
                testeleitura.setIdTeste(c.getInt("id"));
                testeleitura.setAreaId(c.getInt("areaId"));
                testeleitura.setProfessorId(c.getInt("professorId"));
                testeleitura.setTitulo(c.getString("title"));
                testeleitura.setTexto(c.getString("mainText"));
                testeleitura.setDataInsercaoTeste(c.getLong("creationDate"));
                testeleitura.setGrauEscolar(c.getInt("grade"));
                testeleitura.setTipos(0);
                ////////////////////
                testeleitura.setConteudoTexto(c.getString("textContent"));
                testeleitura.setProfessorAudioUrl(c.getString("professorAudioUrl"));
                 guardarTestesLeituraBD(testeleitura);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }




    /**
     *  Guarda um array de  ObJECTOS professores na Base de dados
     * @param profs Array com  Professores para se guardar
     */
    public void guardarProfBD(Professor[] profs) {
       LetrinhasDB db = new LetrinhasDB(context);
        db.deleteAllItemsProf();
        Log.d("DB", "Inserir Dados na base de dados dos Professores ..");
        for (int i = 0; i < profs.length; i++) {
            db.addNewItemProf(profs[i]);
        }
        db.close();
        Log.d("DB", "Tudo inserido nos Professores");
        /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA//////////////
        List<Professor> dadosImg = db.getAllProfesors();
        Log.d("BDDADOS: ", "***********PROFESSORES******************");
        for (Professor cn : dadosImg) {
            String logs = "Id: " + cn.getId() +
                    ", idEscola: " + cn.getIdEscola() +
                    "  , nome: " + cn.getNome() +
                    "  , username: " + cn.getUsername() +
                    "  , password: " + cn.getPassword() +
                    "  , telefone: " + cn.getTelefone() +
                    "  , email: " + cn.getEmail() +
                    "  , foto: " + cn.getFoto();
            // Writing Contacts to log
            Log.d("BDDADOS: ", logs);
        }
    }

    /**
     *  Guarda um array de  ObJECTOS escola na Base de dados
     * @param escolas Array com  escolas para se guardar
     */
    public void guardarEscolaBD(Escola... escolas) {
        LetrinhasDB db = new LetrinhasDB(context);
        db.deleteAllItemsEscola();
        Log.d("DB", "Inserir Dados na base de dados das Escolas ..");
        for (int i = 0; i < escolas.length; i++) {
            db.addNewItemEscolas(escolas[i]);
        }
        db.close();
        Log.d("DB", "Tudo inserido nas Escolas");
        /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA//////////////
        List<Escola> dadosImg = db.getAllSchools();
        Log.d("BDDADOS: ", "********ESCOLAS********************");
        for (Escola cn : dadosImg) {
            String logs = "Id: " + cn.getIdEscola() +
                    ", nome: " + cn.getNome() +
                    ", logotipo: " + cn.getLogotipo() +
                    ", morada: " + cn.getMorada();
            // Writing Contacts to log
            Log.d("BDDADOS: ", logs);
        }
    }

    /**
     *  Guarda um array de  ObJECTOS estudantes na Base de dados
     * @param estudantes Array com  estudantres para se guardar
     */
    public void guardarEstudantesBD(Estudante... estudantes) {
        LetrinhasDB db = new LetrinhasDB(context);
        db.deleteAllItemsEstudante();
        Log.d("DB", "Inserir Dados na base de dados dos Estudantes ..");
        for (int i = 0; i < estudantes.length; i++) {
            db.addNewItemEstudante(estudantes[i]);
        }
        db.close();
        Log.d("DB", "Tudo inserido nas Estudantes");
        /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA//////////////
        List<Estudante> dados = db.getAllStudents();
        Log.d("BDDADOS: ", "*********Estudantes********************");
        for (Estudante cn : dados) {
            String logs = "IdEstudante:" + cn.getIdEstudante() +
                    ", IdTurma: " + cn.getIdTurma() +
                    ", nome: " + cn.getNome() +
                    "  , foto: " + cn.getFoto() +
                    "  , estado: " + cn.getEstado();
            Log.d("BDDADOS: ", logs);
        }
    }

    /**
     *  Guarda um array de  ObJECTOS testesLeitura na Base de dados
     * @param testeLeitura Array com  testesLeitura para se guardar
     */
    public void guardarTestesLeituraBD(TesteLeitura... testeLeitura) {
        LetrinhasDB db = new LetrinhasDB(context);
        db.deleteAllItemsTests();
        db.deleteAllItemsTestsLeitura();

        Log.d("DB", "Inserir Dados na base de dados dos TESTES ..");
        for (int i = 0; i < testeLeitura.length; i++) {
            db.addNewItemTestesLeitura(testeLeitura[i]);
        }
        db.close();
        Log.d("DB", "Tudo inserido nas Testes");
        /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA//////////////
        List<Teste> dados = db.getAllTeste();
        Log.d("BDDADOS: ", "*********Testes********************");
        for (Teste cn : dados) {
            String logs = "getIdTeste:   " + cn.getIdTeste() +
                    ",getTitulo:   " + cn.getTitulo() +
                    ",getTexto:    " + cn.getTexto() +
                    ", getDataInsercaoTeste:    " + cn.getDataInsercaoTeste() +
                    ", getGrauEscolar:    " + cn.getGrauEscolar() +
                    ", getTipo:    " + cn.getTipo();

            // Writing Contacts to log

            Log.d("BDDADOS: ", logs);
        }


        /////PARA EFEITOS DE DEBUG E LOGO  O CODIGO A FRENTE APENAS MOSTRA O CONTEUDO DA TABELA//////////////
        List<TesteLeitura> dados2 = db.getAllTesteLeitura();
        Log.d("BDDADOS: ", "\n*********testesleitura********************");
        for (TesteLeitura cn : dados2) {
            String cenas = "getIdTeste:" + cn.getIdTeste() +
                    ", getConteudoTexto: " + cn.getConteudoTexto() +
                    ", getProfessorAudioUrl: " + cn.getProfessorAudioUrl();
            // Writing Contacts to log
            Log.d("BDDADOS: ", cenas);
        }
    }
}

