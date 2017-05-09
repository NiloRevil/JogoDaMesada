/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.controller.threads;

import java.util.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import javax.swing.JTextArea;
import jogodamesada.controller.*;
import jogodamesada.model.Pacote;

/**
 *
 * @author Alyson Dantas
 */
public class ThreadServidorConexao extends Thread {
	private Socket cliente;//socket do cliente
	private ServerSocket server;//socket do servidor
	private JTextArea textField;//para atualizar a interface
	private ControllerDadosServer controller = ControllerDadosServer.getInstance();//instancia do controller

	/**
	 * Construtor
	 * @param server Servidor que aceita os clientes
	 * @param textField Area de log na interface
	 * @param cliente Cliente que ja foi aceito
	 */
	public ThreadServidorConexao(ServerSocket server, JTextArea textField, Socket cliente) {//recebe o socket server e o textArea
		this.server = server; 
		this.cliente = cliente;
		this.textField = textField;
	}

	/**
	 * Metodo Run da Thread
	 */
	public void run() {
		try {
			//Inicia thread do cliente aceitando clientes

			//ObjectInputStream para receber o nome do arquivo
			ObjectInputStream   entrada = new ObjectInputStream(cliente.getInputStream());//cria um objeto de entrada
			ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());//cria um objeto de saida
			Pacote pack = (Pacote) entrada.readObject();//obtem o pacote de entrada
			int opcao = pack.getOpcao();//recebe a opcao que o cliente mandou
			List<String> informacoes = pack.getLista();//recebe a lista de informações
			Iterator<String> itera = informacoes.iterator();
			Pacote enviarResultado = new Pacote();//cria novo pacote para envio
			List<String> result = new ArrayList<String>();//cria uma nova lista de informaçõoes para envio
			String s = "erro";//string de log com erro
			switch(opcao){
			case 0://Cadastro de novo jogador
				String nome = itera.next();//recebe as informações para cadastro
				String senha = itera.next();

				try{
					controller.cadastrarConta(nome,  senha);//controller cadastra cliente
					s = "Criar cadastro " + nome;//log
					saida.writeObject("concluido");//envia resposta concluido
				}catch(Exception e){
					saida.writeObject("camponaopreenchido");//erro de campo nao preenchido
				}
				saida.flush();
				break;
			}
			System.out.println("\nCliente atendido com sucesso: " + s + cliente.getRemoteSocketAddress().toString());
			textField.setText(textField.getText() + "\nCliente atendido com sucesso: " + s + cliente.getRemoteSocketAddress().toString());//coloca o log no textArea

			entrada.close();//finaliza a entrada
			saida.close();//finaliza a saida
			cliente.close();//fecha o cliente
		}

		catch(Exception e) {//caso alguma exceção seja lançada
			System.out.println("Excecao ocorrida na thread: " + e.getMessage());
			textField.setText(textField.getText() + "\nExcecao ocorrida na thread: " + e.getMessage());//caso alguma exceção desconheciada seja lançada ela encerra a thread e é exibida
			try {
				cliente.close();   //finaliza o cliente
			}catch(Exception ec) {
				textField.setText(textField.getText() + "\nErro fatal cliente não finalizado: " + e.getMessage());//cliente não foi finalizado
			}     
		}
	}
}
