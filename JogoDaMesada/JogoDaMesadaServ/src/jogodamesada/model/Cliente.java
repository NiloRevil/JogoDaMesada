/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

import java.io.Serializable;

/**
 *
 * @author alyso
 */
public class Cliente implements Serializable{
    private String nome;
    private String senha;
    private String ip;
    private String porta;
    private int salaAtual;
    
    public Cliente(String nome, String senha){
        this.nome = nome;
        this.senha = senha;
    }
    
    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public String getSenha(){
        return senha;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }

    public String getIp() {
        return ip;
    }

    public String getPorta() {
        return porta;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public int getSalaAtual() {
        return salaAtual;
    }

    public void setSalaAtual(int salaAtual) {
        this.salaAtual = salaAtual;
    }
    
    
}
