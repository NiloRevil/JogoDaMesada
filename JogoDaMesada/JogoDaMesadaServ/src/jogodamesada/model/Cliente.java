/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author alyso
 */
public class Cliente implements Serializable {

    private String nome;
    private String senha;
    private String ip;
    private String porta;
    private int salaAtual;
    private int ordem;
    private TimerCliente timer = new TimerCliente();

    public Cliente(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.timer = new TimerCliente();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
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

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public TimerCliente getTimer() {
        return timer;
    }

    public void setTimer(TimerCliente timer) {
        this.timer = timer;
    }

}
