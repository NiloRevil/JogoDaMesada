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
import java.util.Comparator;
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
    private List<Sala> salasFinalizando;
    private List<Cliente> clientesOnline;
    private List<Cliente> clientesOciosos;

    private ControllerDadosServer() {
        //clientesAguardando = new ArrayList<Cliente>();
        salasAbertas = new ArrayList<Sala>();
        salasFechadas = new ArrayList<Sala>();
        salasFinalizando = new ArrayList<Sala>();
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

    /**
     * Metodo de jogar
     * @param nome
     * @param senha
     * @param ip
     * @param porta
     * @return
     * @throws CampoVazioException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws SenhaIncorretaException 
     */
    public int jogar(String nome, String senha, String ip, int porta) throws CampoVazioException, IOException, FileNotFoundException, ClassNotFoundException, SenhaIncorretaException {
        if (nome == null || nome.equals("") || senha == null || senha.equals("")) {
            throw new CampoVazioException();
        }
        Cliente cliente = getCliente(nome, senha);
        cliente.setIp(ip);
        cliente.setPorta("" + porta);
        clientesOnline.add(cliente);
        if (salasAbertas.isEmpty()) {//se não tiver salas
            Sala sala = new Sala();//inicia uma sala nova
            List<Cliente> clientes = sala.getClientes();
            cliente.setSalaAtual(sala.getId());
            clientes.add(cliente);//obtem o id da sala e o coloca no cliente
            salasAbertas.add(sala);//adiciona o cliente na sala
            return sala.getId();
        } else {//caso contrario
            Sala sala;
            Iterator<Sala> ite = salasAbertas.iterator();
            while (ite.hasNext()) {//procura uma sala livre
                sala = ite.next();
                List<Cliente> clientes = sala.getClientes();
                if (sala.getTamanho() == 5) {//se ela tiver tamanho 5 então ela vai fechar em seguida
                    clientes.add(cliente);
                    sala.setAberta(false);//avisa que a sala fechou
                    cliente.setSalaAtual(sala.getId());
                    return sala.getId();
                } else {//caso contrario so add o cliente na sala
                    clientes.add(cliente);
                    cliente.setSalaAtual(sala.getId());
                    return sala.getId();
                }
            }
        }
        return -1;//retorna erro
    }

    /**
     * Metodo que obtem a sala
     * @param id
     * @return 
     */
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

    /**
     * Metodo que inicia o jogo
     * @param id
     * @return 
     */
    public String iniciarJogo(int id) {
        Sala sala = getSala(id);
        salasAbertas.remove(sala);
        sala.setAberta(false);
        salasFechadas.add(sala);//coloca a sala em fechadas

        Iterator<Cliente> iteraCliente;
        Cliente cliente;
        iteraCliente = sala.getClientes().iterator();
        List<Integer> ordem = new ArrayList<>();
        for (int a = 0; a < sala.getClientes().size(); a++) {
            ordem.add(a + 1);//faz uma ordem para os clientes
        }
        Collections.shuffle(ordem);
        int i = 1;
        while (iteraCliente.hasNext()) {//coloca a ordem neles
            cliente = iteraCliente.next();
            cliente.setOrdem(i);
            i++;
        }

        return conexoes(id);//retorna as conexões dessa sala
    }

    /**
     * Metodo que cria as conexões
     * @param id
     * @return 
     */
    public String conexoes(int id) {
        Iterator<Sala> itera = salasFechadas.iterator();//obtem a sala
        Sala sala;
        List<Cliente> clientes;
        Iterator<Cliente> iteraCliente;
        Cliente cliente;
        String todasConexoes = "";
        while (itera.hasNext()) {
            sala = itera.next();
            if (sala.getId() == id) {
                iteraCliente = sala.getClientes().iterator();
                while (iteraCliente.hasNext()) {
                    cliente = iteraCliente.next();//coloca as informações dos clientes na string
                    todasConexoes = todasConexoes + "|" + cliente.getNome() + "$" + cliente.getIp() + "$" + cliente.getPorta() + "$" + cliente.getOrdem();
                }
                return todasConexoes;
            }
        }
        return null;
    }

    /**
     * Metodo que remove um cliente da sala
     * @param nomeCliente
     * @param id 
     */
    public void removerClienteDaSala(String nomeCliente, int id) {
        Sala sala = getSala(id);
        Cliente clienteaux = null;
        Iterator<Cliente> itera = sala.getClientes().iterator();
        while (itera.hasNext()) {
            clienteaux = itera.next();
            if (clienteaux.getNome().equals(nomeCliente)) {
                break;
            }
        }
        List<Cliente> clientes = sala.getClientes();
        List<Cliente> clientesVotos = sala.getVotosSim();
        if (clienteaux != null) {
            clientes.remove(clienteaux);
            clientesVotos.remove(clienteaux);
            clientesOnline.remove(clienteaux);
        }

    }

    /**
     * Metodo que verifica se o usuario esta online
     * @param nome
     * @return 
     */
    public int verificaUser(String nome) {
        Cliente cliente = null;
        Iterator<Cliente> itera = clientesOnline.iterator();
        int opcao = 0;
        while (itera.hasNext()) {
            cliente = itera.next();
            if (cliente.getNome().equals(nome)) {
                return opcao = 1;
            }
        }
        itera = clientesOciosos.iterator();
        while (itera.hasNext()) {
            cliente = itera.next();
            if (cliente.getNome().equals(nome)) {
                return opcao = 2;
            }
        }

        return opcao;//0-usuario não esta online / 1-usuario esta online / 2-usuario estava ocioso
    }

    /**
     * Metodo que relaiza a votação de uma partida
     * @param nomeAcesso
     * @param senhaAcesso
     * @param idSala
     * @param voto 
     */
    public void votar(String nomeAcesso, String senhaAcesso, int idSala, String voto) {
        Cliente aux;
        Sala sala = getSala(idSala);
        List<Cliente> clientes = sala.getClientes();
        Iterator<Cliente> itera = clientes.iterator();
        while (itera.hasNext()) {
            aux = itera.next();
            if (aux.getNome().equals(nomeAcesso)) {//apos encontrar o usuario que esta votando
                List<Cliente> clientesVotos = sala.getVotosSim();
                Iterator<Cliente> iteraVotos = clientesVotos.iterator();
                Cliente aux2 = null;
                int naoEsta = 0;
                while (iteraVotos.hasNext()) {//verifica todos os votos e verifica se ele ja votou
                    aux2 = iteraVotos.next();
                    if (aux2.getNome().equals(aux.getNome())) {
                        naoEsta = 1;
                        break;
                    }
                }
                if (naoEsta == 0 && voto.equals("Sim")) {//se não votou e vai votar agora é sim
                    clientesVotos.add(aux);//add o voto
                    break;
                } else if (naoEsta == 1 && voto.equals("Nao")) {//se votou e vai votar com não
                    if (aux2 != null) {
                        clientesVotos.remove(aux2);//remove o cliente da lista de quem votou sim
                    }
                }
            }
        }
    }
    
    /**
     * Metodo que avisa que um cliente esta ausente
     * @param nome
     * @throws CampoVazioException
     * @throws ClienteNaoEncontradoException 
     */
    public void clienteAusente(String nome) throws CampoVazioException, ClienteNaoEncontradoException{
        if(nome == null || nome.equals("")){
            throw new CampoVazioException();
        }
        System.out.println("Cliente que vai ficar ausente: " + nome);
        Cliente cliente = null;
        Iterator<Cliente> itera = clientesOnline.iterator();
        int opcao = 0;
        boolean verifica = false;
        while (itera.hasNext()) {
            cliente = itera.next();
            if (cliente.getNome().equals(nome)) {
                System.out.println(cliente.getNome() + " igual " + nome);
                verifica = true;
                clientesOnline.remove(cliente);
                TimerCliente timer = new TimerCliente();
                cliente.setTimer(timer);
                System.out.println("Cliente que ficou ausente: " + cliente.getNome());
                clientesOciosos.add(cliente);
                break;
            }
        }
    }
    
    /**
     * Metodo que obtem a sala em andamento
     * @param nome
     * @return
     * @throws ClienteNaoEncontradoException 
     */
    public String getSalaAndamento(String nome) throws ClienteNaoEncontradoException{
        Cliente cliente = null;
        Iterator<Cliente> itera = clientesOciosos.iterator();
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                System.out.println("encontrou ele");
                break;
            }
        }
        if(cliente != null){
            boolean onLine = verificaClienteOnline(cliente);
            if(onLine){
                String conexoes = conexoes(cliente.getSalaAtual());
                clientesOciosos.remove(cliente);
                clientesOnline.add(cliente);
                TimerCliente timer = new TimerCliente();
                cliente.setTimer(timer);
                return conexoes;
            }else{
                clientesOciosos.remove(cliente);
                return "salanaoencontrada";
            }
            
            
        }else{
            return "salanaoencontrada";
        }
    }
    
    /**
     * Metodo que obtem o cliente que esta on line
     * @param nome
     * @return 
     */
    public Cliente getClienteOnline(String nome){
        Iterator<Cliente> itera = clientesOnline.iterator();
        Cliente cliente;
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                return cliente;
            }
        }
        return null;
    }
    
    /**
     * Metodo que obtem o cliente ausente
     * @param nome
     * @return 
     */
    public Cliente getClienteAusente(String nome){
        Iterator<Cliente> itera = clientesOciosos.iterator();
        Cliente cliente;
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                return cliente;
            }
        }
        return null;
    }
    
    /**
     * Metodo que renova o timer de um cliente que esta on
     * @param nome
     * @throws CampoVazioException 
     */
    public void renovaTimerClienteOn(String nome) throws CampoVazioException{
        if(nome == null || nome.equals("")){
            throw new CampoVazioException();
        }
        Iterator<Cliente> itera = clientesOnline.iterator();
        Cliente cliente;
        while(itera.hasNext()){
            cliente = itera.next();
            if(cliente.getNome().equals(nome)){
                TimerCliente timer = new TimerCliente();
                cliente.setTimer(timer);
            }
        }
    }
    
    /**
     * Metodo que verifica se um cliente esta on pelo timer
     * @param cliente
     * @return 
     */
    public boolean verificaClienteOnline(Cliente cliente){
        TimerCliente timerCliente = cliente.getTimer();
        TimerCliente timerAtual = new TimerCliente();
        if(timerAtual.getHora()-timerCliente.getHora() < 0){
            return false;
        }else if(timerCliente.getHora()-timerAtual.getHora() > 1){
            return false;
        }else if(timerAtual.getMinuto()-timerCliente.getMinuto() > 15){
            return false;
        }
        return true;
    }
    
    /**
     * Metodo que obtem a sala que esta fechanda
     * @param id
     * @return 
     */
    public Sala getSalaFechando(int id) {
        Iterator<Sala> itera = salasFinalizando.iterator();
        Sala sala;
        while (itera.hasNext()) {
            sala = itera.next();
            if (sala.getId() == id) {
                return sala;
            }
        }
       
        return null;
    }
    
    /**
     * Metodo que sinaliza que uma sala esta no fim
     * @param cliente
     * @param saldo
     * @throws SalaNaoEncontradaException 
     */
    public synchronized void sinalizaFimDeSala(Cliente cliente, int saldo) throws SalaNaoEncontradaException{
        if(cliente == null){
            System.out.println("Erro cliente nulo");
        }
        int idSala = cliente.getSalaAtual();
        Sala sala = getSalaFechando(idSala);
        List<Cliente> votos = new ArrayList<Cliente>();
        
        if(sala == null){
            sala = getSala(idSala);
            if(sala == null){
                throw new SalaNaoEncontradaException();
            }
            salasAbertas.remove(sala);
            sala.setVotosSim(votos);
            System.out.println("1Remove de salas abertas " + cliente.getNome());
            sala.setAberta(true);
            votos = sala.getVotosSim();
            cliente.setSaldo(saldo);
            votos.add(cliente);
            salasFinalizando.add(sala);
            System.out.println("1Adiciona em salas finalizando " + cliente.getNome() + " sala id " + sala.getId());
            
        }else{
            
            votos = sala.getVotosSim();
            cliente.setSaldo(saldo);
            votos.add(cliente);
            System.out.println("2Adiciona na lista de votos para finalizar " + cliente.getNome());
            
        }
    }
    
    /**
     * Metodo que finaliza a sala e avisa que finalizou
     * @param idSala
     * @return 
     */
    public boolean finalizaSala(int idSala){
        System.out.println("finaliza sala + " + idSala);
        Sala sala = getSalaFechando(idSala);
        int votos = sala.getVotosSim().size();
        int qtdClientes = sala.getTamanho();
        Iterator<Cliente> itera = sala.getClientes().iterator();
        Cliente aux;
        Cliente aux2;
        Iterator<Cliente> iteraOciosos;
        while(itera.hasNext()){
            aux = itera.next();
            iteraOciosos = clientesOciosos.iterator();
            while(iteraOciosos.hasNext()){
                aux2 = iteraOciosos.next();
                if(aux2.getNome().equals(aux.getNome())){
                    qtdClientes = qtdClientes - 1;
                    System.out.println("Esta ausente no final da partida " + aux2.getNome());
                }
            }
        }
        
        System.out.println("quantidade de votos " + votos + " quantidade de clientes" + qtdClientes);
        if(votos == qtdClientes){
            salasFechadas.add(sala);
            System.out.println("retornou true sala off pronta");
            return true;
        }
        
        return false;      
    }
    
    /**
     * Metodo que finaliza o jogo e retorna para os clientes a string de fim
     * @param idSala
     * @return 
     */
    public String finalizaJogo(int idSala) {
        Sala sala = getSalaFechando(idSala);
        List<Cliente> clientes = sala.getClientes();
        Collections.sort (clientes, new Comparator() {//ordena pelo saldo que cada um tem
            public int compare(Object o1, Object o2) {
                Cliente p1 = (Cliente) o1;
                Cliente p2 = (Cliente) o2;
                return p1.getSaldo() < p2.getSaldo() ? -1 : (p1.getSaldo() > p2.getSaldo() ? +1 : 0);
            }
        });
        Iterator<Cliente> itera = clientes.iterator();
        Cliente cliente;
        String fim = "01";
        while(itera.hasNext()){
            cliente = itera.next();
            removeClienteSalas(cliente);
            fim = fim + "|" + cliente.getNome() + "|" + cliente.getSaldo();
        }
        return fim;
    }
    
    /**
     * Metodo que remove o cliente das salas
     * @param cliente 
     */
    public void removeClienteSalas(Cliente cliente){
        clientesOciosos.remove(cliente);
        clientesOnline.remove(cliente);
    }
}
