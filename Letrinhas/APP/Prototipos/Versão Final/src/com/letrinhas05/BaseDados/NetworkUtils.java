package com.letrinhas05.BaseDados;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.letrinhas05.ClassesObjs.CorrecaoTeste;
import com.letrinhas05.ClassesObjs.CorrecaoTesteLeitura;
import com.letrinhas05.ClassesObjs.CorrecaoTesteMultimedia;
import com.letrinhas05.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 
 * @author Alexandre
 *
 */
public class NetworkUtils {
    @SuppressWarnings("unused")
	private static final String TAG_NET_UTILS = "net-utils";

    /**
     * Le um ficheiro a partir do url especificado.
     * @param url O url do ficheiro.
     * @return Um array de bytes representando o ficheiro que foi lido, ou null
     * se ocorreu um erro.
     */
    public static byte[] getFile(final String url) {
        AndroidHttpClient client = AndroidHttpClient.newInstance("letrinhas");
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);

            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int size = -1;
            byte[] buf = new byte[1024];
            while ((size = in.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            /// fECHA TODAS AS LIGAÇÕES
            out.close();
            in.close();
            client.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envia um resultado do teste para um deterninado URL
     * Usa uma biblioteca da Apache:
     * http://james.apache.org/download.cgi#Apache_Mime4J
     * @param url O url de destino para enviar os resultados
     * @param correcaoTeste Um objecto correcao de teste com os dados carregados que se pretendem enviar
     *
     *  array de bytes representando o ficheiro que foi lido, ou null
     * se ocorreu um erro.
     */
    @SuppressWarnings("deprecation")
	public static boolean postResultados(final String url, CorrecaoTeste correcaoTeste) {
        AndroidHttpClient client = AndroidHttpClient.newInstance("letrinhas");

        HttpPost postRequest = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        // Construir os campos.
        builder.addTextBody("testId", correcaoTeste.getTestId()+"");
        builder.addTextBody("studentId", correcaoTeste.getIdEstudante()+"");
        builder.addTextBody("executionDate", correcaoTeste.getDataExecucao()+"");
        builder.addTextBody("wasCorrected", correcaoTeste.getEstado()+"");


        String observacoes = "Sem dados";
        String detalhes = "Sem dados";
        if (correcaoTeste instanceof CorrecaoTesteLeitura) {
            CorrecaoTesteLeitura teste = (CorrecaoTesteLeitura) correcaoTeste;
            builder.addTextBody("type", teste.getTipo()+"");
            if (teste.getObservacoes() != null)
                observacoes = teste.getObservacoes();
            if (teste.getDetalhes() != null)
                detalhes = teste.getDetalhes();

            builder.addTextBody("observations", observacoes );
            builder.addTextBody("wpm", teste.getNumPalavrasMin()+"");
            builder.addTextBody("correct", teste.getNumPalavCorretas()+"");
            builder.addTextBody("incorrect", teste.getNumPalavIncorretas()+"");
            builder.addTextBody("precision", teste.getPrecisao()+"");
            builder.addTextBody("expressiveness", teste.getExpressividade()+"");
            builder.addTextBody("rhythm", teste.getRitmo()+"");
            builder.addTextBody("speed", teste.getVelocidade()+"");

            try {
                builder.addPart("details",  new StringBody(detalhes, Charset.forName("UTF-8")) );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//            builder.addTextBody("details", detalhes+"");
                //// Envia Um ficheiro////
            builder.addBinaryBody("audio", Utils.getFileSD2(teste.getAudiourl()), ContentType.APPLICATION_OCTET_STREAM, "cenas.3gp");
        } else {

            CorrecaoTesteMultimedia teste = (CorrecaoTesteMultimedia) correcaoTeste;
            builder.addTextBody("type", teste.getTipo()+"");
            builder.addTextBody("optionChosen", teste.getOpcaoEscolhida()+"");
            if (teste.getOpcaoEscolhida() ==  teste.getCerta())
            builder.addTextBody("isCorrect", "1");
            else
            builder.addTextBody("isCorrect", "0");
            //// Envia Um ficheiro////
        }
        //Faz o post Request
            postRequest.setEntity(builder.build());

        try {
            Log.d("Letrinhas", "Enviando um http request. para os resultados do teste");
            client.execute(postRequest);
            //Fecha a ligação
            client.close();
            return true;
        } catch (Exception e) {
            Log.e("Letrinhas", e.getMessage());
            e.printStackTrace();
            //Fecha a ligação
            client.close();
            return false;
        }

    }
}
