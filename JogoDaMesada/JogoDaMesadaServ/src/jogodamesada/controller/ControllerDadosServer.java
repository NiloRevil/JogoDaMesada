/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.controller;

import java.io.BufferedOutputStream;
import jogodamesada.exceptions.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import jogodamesada.model.*;

/**
 *
 * @author Alyson Dantas & Marcelo
 */
public class ControllerDadosServer {

    private static ControllerDadosServer unicaInstancia;

    private ControllerDadosServer() {
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
    
    public void jogar(String nome, String ip, String porta){
        
    }
}
