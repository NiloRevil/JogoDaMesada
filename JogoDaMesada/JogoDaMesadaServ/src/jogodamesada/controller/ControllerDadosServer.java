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
import java.util.Iterator;
import java.util.List;
import jogodamesada.model.*;

/**
 *
 * @author Alyson Dantas & Marcelo
 */
public class ControllerDadosServer {

    private static ControllerDadosServer unicaInstancia;
    
    private List<Cliente> clientesAguardando;
    private List<Sala> salasAbertas;
    private List<Sala> salasFechadas;

    private ControllerDadosServer() {
        clientesAguardando = new ArrayList<Cliente>();
        salasAbertas = new ArrayList<Sala>();
        salasFechadas = new ArrayList<Sala>();
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
        if(nome == null || nome.equals("") || senha == null || senha.equals("")){
            throw new CampoVazioException();
        }
        Cliente cliente = getCliente(nome, senha);
        if(salasAbertas.size() == 0){
            Sala sala = new Sala();
            sala.setTamanho(1);
            sala.setCliente1(cliente);
            salasAbertas.add(sala);
            return sala.getId();
        }else if(salasAbertas.size() > 0){
            Sala sala;
            Iterator<Sala> ite = salasAbertas.iterator();
            while(ite.hasNext()){
                sala = ite.next();
                switch (sala.getTamanho()) {
                    case 1:
                        sala.setCliente2(cliente);
                        sala.setTamanho(2);
                        return sala.getId();
                    case 2:
                        sala.setCliente3(cliente);
                        sala.setTamanho(3);
                        return sala.getId();
                    case 3:
                        sala.setCliente4(cliente);
                        sala.setTamanho(4);
                        return sala.getId();
                    case 4:
                        sala.setCliente5(cliente);
                        sala.setTamanho(5);
                        return sala.getId();
                    case 5:
                        sala.setCliente5(cliente);
                        sala.setTamanho(6);
                        sala.setAberta(false);
                        return sala.getId();
                }
            }
        }
        return -1;
    }

    public Sala getSala(int id){
        Iterator<Sala> itera = salasAbertas.iterator();
        Sala sala;
        while(itera.hasNext()){
            sala = itera.next();
            if(sala.getId() == id){
                return sala;
            }
        }
        itera = salasFechadas.iterator();
        while(itera.hasNext()){
            sala = itera.next();
            if(sala.getId() == id){
                return sala;
            }
        }
        
        return null;
    }
    
    public String iniciarJogo(int id) {
        Sala sala = getSala(id);
        salasAbertas.remove(sala);
        salasFechadas.add(sala);
        return "";
    }
    
    public String finalizaJogo(int id, double saldo1, double saldo2, double saldo3, double saldo4, double saldo5, double saldo6){
        
        return "";
    }
}
