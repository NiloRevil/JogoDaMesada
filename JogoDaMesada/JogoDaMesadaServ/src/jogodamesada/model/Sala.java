/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alyson Dantas
 */
public class Sala {

    private List<Cliente> clientes;
    private boolean aberta;
    private List<Cliente> votosSim;
    private static Integer serialId=0;//SerialID é quem vai definir o id de cada sala quando for criado
    private int id;//id da sala
    
    public Sala(){
        serialId=serialId+1;//Incrementa o SerialID para que não se repita
	this.id=serialId;//id da sala recebe o serialID da criação
        clientes = new ArrayList<Cliente>();
        votosSim = new ArrayList<Cliente>();
        aberta = true;
    }

    public int getTamanho() {
        return clientes.size();
    }

    public boolean isAberta() {
        return aberta;
    }

    public void setAberta(boolean aberta) {
        this.aberta = aberta;
    }
    
    
    public int getId() {
        return id;
    } 

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> getVotosSim() {
        return votosSim;
    }

    public void setVotosSim(List<Cliente> votosSim) {
        this.votosSim = votosSim;
    }
    
    
}
