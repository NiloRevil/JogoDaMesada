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
import java.net.SocketException;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import jogodamesada.controller.*;
import jogodamesada.exceptions.*;
import jogodamesada.model.Cliente;
import jogodamesada.model.Pacote;
import jogodamesada.model.Sala;

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
     *
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
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());//cria um objeto de entrada
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());//cria um objeto de saida
            String pack = (String) entrada.readObject();//obtem o pacote de entrada
            String informacoes[] = pack.split(Pattern.quote("|"));
            int opcao = Integer.parseInt(informacoes[0]);//recebe a opcao que o cliente mandou
            String s = "erro";//string de log com erro
            switch (opcao) {
                case 0://Cadastro de novo jogador
                    String nome = informacoes[1];//recebe as informações para cadastro
                    String senha = informacoes[2];
                    try {
                        controller.cadastrarConta(nome, senha);//controller cadastra cliente
                        s = "Criar cadastro " + nome;//log
                        saida.writeObject("concluido");//envia resposta concluido
                    } catch (CampoVazioException e) {
                        saida.writeObject("camponaopreenchido");//erro de campo nao preenchido
                    } catch (CadastroJaExistenteException e) {
                        saida.writeObject("cadastrojaexistente");//erro de campo nao preenchido
                    }
                    saida.flush();
                    break;
                case 1://acessa uma sala
                    String nomeAcesso = informacoes[1];
                    String senhaAcesso = informacoes[2];

                    int estaEmSala = controller.verificaUser(nomeAcesso);//criar switch case para os casos...

                    switch (estaEmSala) {
                        case 0://ele não estava on line antes, pode prosseguir.
                            String ipAcesso = cliente.getInetAddress().getHostAddress();
                            int portaAcesso = cliente.getPort();
                            Sala salaAcesso;
                            System.out.println("atendendo o " + ipAcesso);
                            int idSala = -1;
                            try {
                                System.out.println("entrando no jogo");
                                idSala = controller.jogar(nomeAcesso, senhaAcesso, ipAcesso, portaAcesso);
                                System.out.println("sala criada id:" + idSala);
                                if (idSala > -1) {
                                    Sala salaAtual = controller.getSala(idSala);
                                    if (salaAtual.getTamanho() == 6) {
                                        String conexao = "1" + controller.iniciarJogo(idSala);
                                        System.out.println("sala fechou na thread");
                                        saida.writeObject(conexao);
                                        saida.flush();
                                    } else {
                                        while (salaAtual.isAberta()) {
                                            Thread.sleep(1000);
                                            int tamanho = salaAtual.getTamanho();
                                            String informacao = "0|" + tamanho;

                                            System.out.println("pessoas na sala:" + informacao);
                                            if (tamanho > 1) {
                                                int votos = salaAtual.getVotosSim().size();
                                                if(votos == salaAtual.getTamanho()){
                                                    controller.iniciarJogo(idSala);
                                                    salaAtual.setAberta(false);
                                                }
                                                informacao = informacao + "|podeiniciar";
                                                
                                            } else {
                                                informacao = informacao + "|aindanao";
                                            }
                                            saida.writeObject(informacao);//0 é resposta de que ainda esta procurando jogadores
                                            saida.flush();
                                            String votacao = (String) entrada.readObject();//obtem o pacote de entrada
                                            String pessoaVoto[] = votacao.split(Pattern.quote("|"));
                                            System.out.println("Pessoa:" + pessoaVoto[0] + "Voto:" + pessoaVoto[1]);
                                            controller.votar(nomeAcesso,senhaAcesso, idSala, pessoaVoto[1]);
                                        }
                                        System.out.println("A sala: " + idSala + " fechou com: " + controller.conexoes(idSala));
                                        saida.writeObject("1" + controller.conexoes(idSala));
                                        saida.flush();
                                    }
                                }
                                s = "Entrar em sala: " + nomeAcesso;//log
                            } catch (SocketException e) {
                                System.out.println("cliente desconectou do nada: " + nomeAcesso);
                                textField.setText(textField.getText() + "\nErro cliente desconectou inesperadamente: " + nomeAcesso);//cliente não foi finalizado
                                try {
                                    controller.removerClienteDaSala(nomeAcesso, idSala);
                                } catch (Exception ea) {
                                    System.out.println("deu ruim aqui " + e);
                                }

                            } catch (Exception e) {
                                System.out.println("conexao erro " + e);
                            }
                            break;
                        case 1://ele esta online não pode prosseguir
                            s = "Cliente Já online" + nomeAcesso;//log
                            Cliente clienteOn = controller.getClienteOnline(nomeAcesso);
                            boolean verifica = controller.verificaClienteOnline(clienteOn);
                            if(verifica){
                                saida.writeObject("2|" + "jaestaonline");//2 indica que o usuario esta on line no momento
                                saida.flush();
                            }else{
                                controller.removerClienteDaSala(nomeAcesso, clienteOn.getSalaAtual());
                                saida.writeObject("4|" + "clienteDesconectado");//2 indica que o usuario esta on line no momento
                                saida.flush();
                            }
                            break;
                        case 2://usuario ausente
                            s = "Eentrando na partida em andamento " + nomeAcesso;//log
                            try{ 
                                String conexoesAusentes = controller.getSalaAndamento(nomeAcesso);
                                saida.writeObject("3" + conexoesAusentes);
                            }catch(ClienteNaoEncontradoException e){
                                saida.writeObject("clientenaoestaOn");//erro de campo nao preenchido
                            }
                            break;
                    }

                    break;
                case 2:
                    String nomeAusente = informacoes[1];//recebe as informações para cadastro
                    try {
                        controller.clienteAusente(nomeAusente);
                        s = "Avisa que esta ausente: " + nomeAusente;//log
                        saida.writeObject("concluido");//envia resposta concluido
                    } catch (CampoVazioException e) {
                        saida.writeObject("camponaopreenchido");//erro de campo nao preenchido
                    } catch (ClienteNaoEncontradoException e) {
                        saida.writeObject("clientenaoestaOn");//erro de campo nao preenchido
                    }
                    saida.flush();
                    break;
                case 3:
                    String nomeRenova = informacoes[1];
                    try{
                        controller.renovaTimerClienteOn(nomeRenova);
                        s = "Timer resetado: " + nomeRenova;//log
                        saida.writeObject("concluido");//concluido
                    }catch(CampoVazioException e){
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
        }catch(SocketException e){
            System.out.println("Filanizou o atendimento.");
            textField.setText(textField.getText() + "\nAtendimento foi finalizado.");//caso alguma exceção desconheciada seja lançada ela encerra a thread e é exibida
            try {
                cliente.close();   //finaliza o cliente
            } catch (Exception ec) {
                textField.setText(textField.getText() + "\nErro fatal cliente não finalizado: " + ec.getMessage());//cliente não foi finalizado
            }
        }catch (Exception e) {//caso alguma exceção seja lançada
            System.out.println("Excecao ocorrida na thread: " + e);
            textField.setText(textField.getText() + "\nExcecao ocorrida na thread: " + e.getMessage());//caso alguma exceção desconheciada seja lançada ela encerra a thread e é exibida
            try {
                cliente.close();   //finaliza o cliente
            } catch (Exception ec) {
                textField.setText(textField.getText() + "\nErro fatal cliente não finalizado: " + ec.getMessage());//cliente não foi finalizado
            }
        }
    }
}
