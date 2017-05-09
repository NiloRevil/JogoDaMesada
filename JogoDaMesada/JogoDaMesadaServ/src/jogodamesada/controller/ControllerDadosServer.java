/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.controller;

import jogodamesada.exceptions.CampoVazioException;

/**
 *
 * @author Alyson Dantas & Marcelo
 */
public class ControllerDadosServer {
    private static ControllerDadosServer unicaInstancia;

	private ControllerDadosServer(){
	}

	/**
	 * controla o instanciamento de objetos Controller
	 * @return unicaInstancia
	 */
	public static synchronized ControllerDadosServer getInstance(){
		if(unicaInstancia==null){
			unicaInstancia = new ControllerDadosServer();
		}
		return unicaInstancia;
	}

	/**
	 * reseta o objeto Controller ja instanciado
	 */
	public static void zerarSingleton (){
		unicaInstancia = null;
	}
        
        public void cadastrarConta(String nome, String senha) throws CampoVazioException{
            if(nome == null || nome.equals("") || senha == null || senha.equals("")){
                throw new CampoVazioException();
            }
            
            
        }
}
