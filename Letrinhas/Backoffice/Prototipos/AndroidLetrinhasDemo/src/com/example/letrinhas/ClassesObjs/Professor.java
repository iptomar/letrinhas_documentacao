package com.example.letrinhas.ClassesObjs;

public class Professor {
    // private variables
    private int id;
    private int idEscola;
    private String nome;
    private String username;
    private String password;
    private String telefone;
    private String email;
    private byte[] foto;
    private int estado;

    // Empty constructor
    public Professor() {

    }

    // constructor 1
    public Professor(int id, int idEscola, String nome, String username,
                     String password, String email, String telefone, byte[] foto, int estado) {
        this.id = id;
        this.idEscola = idEscola;
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
        this.estado = estado;


    }

    public int isEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdEscola() {
        return idEscola;
    }

    public void setIdEscola(int idEscola) {
        this.idEscola = idEscola;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
