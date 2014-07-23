package com.letrinhas02.util;

/**
 * Objecto que representa um teste, suporte para possibilitar a constru��o das Activity's 
 * dos testes.
 * 
 * @author Thiago
 */
public class Teste {
	int tipo;
	String titulo;
	String endereco;
	byte[] image;
	
	/**
	 * De momento este objeto tem um tipo, um t�tulo e o endere�o/Query do seu conte�do.
	 * 
	 * @param tipo int que apresenta de que forma se ir� construir a Activity (Texto/Palavras/Imagens/etc..)
	 * @param titulo String para identificar o teste.
	 * @param endereco String com o endere�o / Query / comando para retirar o conte�do deste.
	 */
	public Teste(int tipo, String titulo, String endereco) {
		super();
		this.tipo = tipo;
		this.titulo = titulo;
		this.endereco = endereco;
	}
	

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
		
}
