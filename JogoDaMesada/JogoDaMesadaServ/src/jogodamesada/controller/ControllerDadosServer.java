/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.controller;

import jogodamesada.exceptions.SenhaIncorretaException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import jogodamesada.exceptions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jogodamesada.model.*;

/**
 *
 * @author Alyson Dantas & Marcelo
 */
public class ControllerDadosServer {

    private static ControllerDadosServer unicaInstancia;

  //  private List<Cliente> clientesAguardando;
    private List<Sala> salasAbertas;
    private List<Sala> salasFechadas;
    private List<Cliente> clientesOnline;
    private List<Cliente> clientesOciosos;

    private ControllerDadosServer() {
        //clientesAguardando = new ArrayList<Cliente>();
        salasAbertas = new ArrayList<Sala>();
        salasFechadas = new ArrayList<Sala>();
        clientesOciosos = new ArrayList<Cliente>();
        clientesOnline = new ArrayList<Cliente>();
    }

    /**
     * controla o instanciamento de objetos Controller
     *
     * @return unicaInstancia
     */
    public static synchronized ControllerDadosServer getInstance() {
        if (unicaInstancia == null) {
            unicaInstancia = new ControllerDadosServer();
        }
        return unicaInstancia;
    }

    /**
     * reseta o objeto Controller ja instanciado
     */
    public static void zerarSingleton() {
        unicaInstancia = null;
    }

    public void cadastrarConta(String nome, String senha) throws CampoVazioException, CadastroJaExistenteException, IOException {
        if (nome == null || nome.equals("") || senha == null || senha.equals("")) {
            throw new CampoVazioException();
        }
        criaCaminho();//cria o caminho
        File arquivos = new File("/jogoDaMesada/clientes/");
        File todosarquivos[] = arquivos.listFiles();//verifica todos os arquivos que ja estão na pasta
        int cont = 0;
        for (int i = todosarquivos.length; cont < i; cont++) {//procura na pasta por algum cliente ja cadastrado para não conflitar
            File arquivo = todosarquivos[cont];
            if (arquivo.getName().equals(nome + ".dat")) {
                throw new CadastroJaExistenteException();
            }
            System.out.println(arquivo.getName());
        }

        Cliente cliente = new Cliente(nome, senha);
        escreveCliente(cliente);//escreve o cliente

    }

    /**
     * Metedo que cria as pastas de acesso caso não ja existam
     */
    private void criaCaminho() {
        File caminhoCliente = new File("\\jogoDaMesada\\clientes"); // verifica se a pasta existe
        if (!caminhoCliente.exists()) {
            caminhoCliente.mkdirs(); //caso não exista cria a pasta
        }
    }

    /**
     * Metodo que escreve o arquivo cliente
     *
     * @param cliente Cliente
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void escreveCliente(Cliente cliente) throws IOException {
        criaCaminho();//cria o caminho caso ele não exista
        ObjectOutputStream objectOutC = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("/jogoDaMesada/clientes/" + cliente.getNome() + ".dat")));	//grava o objeto no caminho informado		
        objectOutC.writeObject(cliente);//escreve o arquivo
        objectOutC.flush();
        objectOutC.close();
    }

    public Cliente getCliente(String nome, String senha) throws CampoVazioException, FileNotFoundException, IOException, ClassNotFoundException, SenhaIncorretaException {
        criaCaminho();//cria o caminho
        if (nome == null || nome.equals("") || senha == null || senha.equals("")) {
            throw new CampoVazioException();//lança exceção caso uma dos campos estejam vazios
        }
        ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("/jogoDaMesada/clientes/" + nome + ".dat")));//recupera a conta
        Cliente cliente = (Cliente) objectIn.readObject();
        objectIn.close();

        String a = cliente.getSenha();

        if (a.equals(senha)) {//se a agencia for a mesma retorna a conta
            return cliente;
        } else {//caso não retorna uma exceção
            throw new SenhaIncorretaException();
        }
    }

    public int jogar(String nome, String senha, String ip, int porta) throws CampoVazioException, IOException, FileNotFoundException, ClassNotFoundException, SenhaIncorretaException {
        if (nome == null || nome.equals("") || senha == null || senha.equals("")) {
            throw new CampoVazioException();
        }
        Cliente cliente = getCliente(nome, senha);
        cliente.setIp(ip);
        cliente.setPorta("" + porta);
        clientesOnline.add(cliente);
        if (salasAbertas.size() == 0) {
            Sala sala = new Sala();
            List<Cliente> clientes = sala.getClientes();
            clientes.add(cliente);
            salasAbertas.add(sala);
            return sala.getId();
        } else {
            Sala sala;
            Iterator<Sala> ite = salasAbertas.iterator();
            while (ite.hasNext()) {
                sala = ite.next();
                List<Cliente> clientes = sala.getClientes();
                if (sala.getTamanho() == 5) {
                    clientes.add(cliente);
                    sala.setAberta(false);
                    return sala.getId();
                } else {
                    clientes.add(cliente);
                    return sala.getId();
                }
            }
        }
        return -1;
    }

    public Sala getSala(int id) {
        Iterator<Sala> itera = salasAbertas.iterator();
        Sala sala;
        while (itera.hasNext()) {
            sala = itera.next();
            if (sala.getId() == id) {
                return sala;
            }
        }
        itera = salasFechadas.iterator();
        while (itera.hasNext()) {
            sala = itera.next();
            if (sala.getId() == id) {
                return sala;
            }
        }

        return null;
    }

    public String iniciarJogo(int id) {
        Sala sala = getSala(id);
        salasAbertas.remove(sala);
        sala.setAberta(false);
        salasFechadas.add(sala);
        return conexoes(id);
    }

    public String conexoes(int id) {
        Iterator<Sala> itera = salasFechadas.iterator();
        Sala sala;
        List<Cliente> clientes;
        Iterator<Cliente> iteraCliente;
        Cliente cliente;
        String todasConexoes = "";
        while(itera.hasNext()){
            sala = itera.next();
            if(sala.getId() == id){
                iteraCliente = sala.getClientes().iterator();
                List<Integer> ordem = new ArrayList<Integer>();
                for(int a = 0; a<sala.getClientes().size() ; a++){
                    ordem.add(a+1);
                }
                Collections.shuffle(ordem);
                int i = 0;
                while(iteraCliente.hasNext()){
                    cliente = iteraCliente.next();
                    todasConexoes = todasConexoes + "|" + cliente.getNome() + "$" + cliente.getIp() + "$" + cliente.getPorta() + "$" + ordem.get(i);
                    System.out.println("chegou aqui hihi");
                    i++;
                }
                return todasConexoes;
            }
        }
        return null;
    }
    
    public void removerClienteDaSala(String nomeCliente, int id){
        Sala sala = getSala(id);
        Cliente clienteaux = null;
        Iterator<Cliente> itera = sala.getClientes().iterator();
        while(itera.hasNext()){
            clienteaux = itera.next();
            if(clienteaux.getNome().equals(nomeCliente)){
                break;
            }
        }
        List<Cliente> clientes = sala.getClientes();
        List<Cliente> clientesVotos = sala.getVotosSim();
        if(clienteaux!=null){
            clientes.remove(clienteaux);
            clientesVotos.remove(clienteaux);
            clientesOnline.remove(clienteaux);
        }
        
    }

    public String finalizaJogo(int id, double saldo1, double saldo2, double saldo3, double saldo4, double saldo5, double saldo6) {

        return "";
    }
    
    public int verificaUser(String nome) {//adicionar eles nas listas...
        Cliente cliente = null;
        Iterator<Cliente> itera = clientesOnline.iterator();
        int opcao = 0;
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                return opcao = 1;
            }
        }
        itera = clientesOciosos.iterator();
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                return opcao = 2;
            }
        }
        
        return opcao;//0-usuario não esta online / 1-usuario esta online / 2-usuario estava ocioso
    }
    
    public void votar(String nomeAcesso,String senhaAcesso, int idSala, String voto){
        Cliente aux;
        Sala sala = getSala(idSala);
        List<Cliente> clientes = sala.getClientes();
        Iterator<Cliente> itera = clientes.iterator();
        while(itera.hasNext()){
            aux = itera.next();
            if(aux.getNome().equals(nomeAcesso)){
                List<Cliente> clientesVotos = sala.getVotosSim();
                Iterator<Cliente> iteraVotos = clientesVotos.iterator();
                Cliente aux2 = null;
                int naoEsta = 0;
                while(iteraVotos.hasNext()){
                    aux2 = iteraVotos.next();
                    if(aux2.getNome().equals(aux.getNome())){
                        //aux2 = aux;
                        naoEsta = 1;
                        break;
                    }
                }
                if(naoEsta == 0 && voto.equals("Sim")){
                    clientesVotos.add(aux);
                    break;
                }else if(naoEsta == 1 && voto.equals("Nao")){
                    if(aux2!=null){
                        clientesVotos.remove(aux2);
                    }
                }
            }
        }
        
    }
}
