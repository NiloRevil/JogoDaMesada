/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Alyson Dantas
 */
public class Pacote implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int opcao;
	private boolean b;
	private boolean c;
	private List<String> lista;
	private List<String> lista2;
	public int getOpcao() {
		return opcao;
	}
	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public List<String> getLista() {
		return lista;
	}
	public void setLista(List<String> lista) {
		this.lista = lista;
	}
	public boolean isC() {
		return c;
	}
	public void setC(boolean c) {
		this.c = c;
	}
	public List<String> getLista2() {
		return lista2;
	}
	public void setLista2(List<String> lista2) {
		this.lista2 = lista2;
	}
	
}
