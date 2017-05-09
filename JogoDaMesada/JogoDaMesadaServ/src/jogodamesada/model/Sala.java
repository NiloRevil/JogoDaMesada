/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

/**
 *
 * @author Alyson Dantas
 */
public class Sala {

    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente cliente3;
    private Cliente cliente4;
    private Cliente cliente5;
    private Cliente cliente6;
    private int tamanho;
    private boolean aberta;
    private int votos;
    private static Integer serialId=0;//SerialID é quem vai definir o id de cada sala quando for criado
    private int id;//id da sala
    
    public Sala(){
        serialId=serialId+1;//Incrementa o SerialID para que não se repita
	this.id=serialId;//id da sala recebe o serialID da criação
        tamanho = 0;
        aberta = true;
        votos = 0;
    }

    public Cliente getCliente1() {
        return cliente1;
    }

    public void setCliente1(Cliente cliente1) {
        this.cliente1 = cliente1;
    }

    public Cliente getCliente2() {
        return cliente2;
    }

    public void setCliente2(Cliente cliente2) {
        this.cliente2 = cliente2;
    }

    public Cliente getCliente3() {
        return cliente3;
    }

    public void setCliente3(Cliente cliente3) {
        this.cliente3 = cliente3;
    }

    public Cliente getCliente4() {
        return cliente4;
    }

    public void setCliente4(Cliente cliente4) {
        this.cliente4 = cliente4;
    }

    public Cliente getCliente5() {
        return cliente5;
    }

    public void setCliente5(Cliente cliente5) {
        this.cliente5 = cliente5;
    }

    public Cliente getCliente6() {
        return cliente6;
    }

    public void setCliente6(Cliente cliente6) {
        this.cliente6 = cliente6;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public boolean isAberta() {
        return aberta;
    }

    public void setAberta(boolean aberta) {
        this.aberta = aberta;
    }
    
        public void setVotos(int votos) {
        this.votos = votos;
    }

    public int getVotos() {
        return votos;
    }
    
    public int getId() {
        return id;
    }
}
