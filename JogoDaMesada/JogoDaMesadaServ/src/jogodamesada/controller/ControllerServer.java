/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.controller;

import java.net.ServerSocket;
import jogodamesada.controller.threads.*;

/**
 *
 * @author alyso
 */
public class ControllerServer {
    private ServerSocket server;
    private ThreadServidorConexao thread;
    private static ControllerServer unicaInstancia;
    
    
        /**
	 * Construtor
	 */
	private ControllerServer(){
		//controller.getInstance();
	}
	/**
	 * controla o instanciamento de objetos Controller
	 * @return unicaInstancia
	 */
	public static synchronized ControllerServer getInstance(){
		if(unicaInstancia==null){
			unicaInstancia = new ControllerServer();
		}
		return unicaInstancia;
	}

	/**
	 * reseta o objeto Controller ja instanciado
	 */
	public static void zerarSingleton (){
		unicaInstancia = null;
	}

    
    
}
