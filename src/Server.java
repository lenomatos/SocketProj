import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {
	
	public static final int PORT = 7070;
	ArrayList<Player> players = new ArrayList();
	public static void main(String[] args) throws IOException{
		new Server().runServer();
	}
	
	public void runServer() throws IOException{
		ServerSocket serverScoket = new ServerSocket(PORT);
		System.out.println("Server Ativo e Pronto para Conectar!!!...");
		
		while(true){
			Socket socket = serverScoket.accept();// NOVA CONEXAO
			new ServerThread(socket).start(); // STARTA UMA THREAD PARA CADA CONEXAO
		}
		
	}
	
	public class ServerThread extends Thread {
		
		Socket socket;
		Boolean nova_rodada = false;
		Boolean rodada = false;
		
		ServerThread(Socket socket){
			this.socket = socket;
		}
		
		public void run(){// THREAD RODANDO
			
			try {
				Player p = new Player();
				String n = null; // PARA O NOME
				String message = null; // STRING QUE RECEBE A MENSAGEM DO CLIENTE
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true); // USADO PARA ESCREVER
				BufferedReader buffereadReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// ENTRADA RECEBIDA DO CLIENTE
				n = buffereadReader.readLine();
				System.out.println("User: "+n+" conectado!!!...");// SABER QUE O CLIENTE FOI CONECTADO COM SUCESSO
				
				p.setName(n); // SETA O NOME DO JOGADOR
				players.add(p);// ADICIONA JOGADOR A LISTA
				
				if(players.size()==4){ // LIMITAR O NUMERO DE JOGADORES
					distributeRoles(); // PARA MAXIMO 4 JOGADORES
				}
				// INFORMA AO JOGADOR QUE FOI CONECTADO
				printWriter.println("Conectado com Sucesso!!!...");// INFORMA QUE O CLIENTE FOI CONECTADO
				
				// COMUNICANDO
				while((message = buffereadReader.readLine())!=null){ // ENQUANTO ESTIVER RECEBENDO ENTRADA DO CLIENTE
					System.out.println("Mensagem Recebida do "+p.getNome()+": "+message); // MOSTRA A MENSAGEM RECEBIDA
					
					/*
					if(message.equals("Suspeitar")){
						printWriter.println("Comando: "+message); // REPLICA A MENSAGEM
					}
					if(message.equals("Acusar")){
						printWriter.println("Comando: "+message); // REPLICA A MENSAGEM
					}
					if(message.equals("Ameacar")){
						printWriter.println("Comando: "+message); // REPLICA A MENSAGEM
					}
					if(message.equals("Matar")){
						printWriter.println("Comando: "+message); // REPLICA A MENSAGEM
					}
					else{
						printWriter.println("Recebido"); // REPLICA A MENSAGEM
					}*/
					
					for(int i=0; i<players.size(); i++){
						System.out.println("TODOS JOGADORES: "+players.get(i).getNome() +": "+players.get(i).getPapel());
					}
				}
				socket.close(); // ENCERRA A SOCKET E THREAD
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void distributeRoles(){ // DISTRIBUI PAPEIS PARA OS JOGADORES
			Random number = new Random();
			int num1 = number.nextInt((4)+1); // SORTEIA POSIÇÃO 
			int num2 = number.nextInt((4)+1); // SORTEIA POSIÇÃO
			
			while(num1==num2){ // UM UNICO DETETIVE E ASSASSINO
				num2 = number.nextInt((4)+1);
			}
			
			players.get(num1).setRole("Detetive"); // COLOCANDO PAPEL
			players.get(num2).setRole("Assassino");// COLOCANDO PAPEL
			
			for(int i=0; i<players.size(); i++){// OUTROS COMO VITIMA
				if(players.get(i).getPapel()==null){
					players.get(i).setRole("Vitima");
				}
			}// FIM DO FOR
		}// FIM DA DISTRIBUICAO DE PAPEIS
		
		
		// SOMENTE O DETETIVE CHAMA ESTE METODO EM UM PLAYER QUALQUER
		private void suspect(Player p){
			p.setStatus("Suspeito");
		}// FIM SUSPEITO
		
		// SOMENTE O DETETIVE CHAMA ESTE METODO EM UM PLAYER SUSPEITO
		private void accuse(Player p){
			System.out.println("ACUSANDO");

			if(p.getStatus().equals("Suspeito")){
				if(p.getPapel().equals("Assassino")){
					// CHAMAR VENCEDOR DETETIVE
					System.out.println("DETETIVE GANHOU");
				}
				else{
					System.out.println("SUSPEITO INOCENTE");
				}
			}
			else{
				System.out.println("PRECISA ACUSAR ALGUEM");
			}

		}// FIM DO ACUSAR
		
		// SOMENTE ASSASSINO CHAMA ESTE METODO, AMEACANDO ALGUEM
		private void threaten(Player p){
			p.setStatus("Ameacado");
		}//  FIM DO AMEACAR
		
		//  METODO UTILIZADO PELO ASSASSINO
		private void blink(Player p){
			// AMEACOU O DETETIVE 
			if((p.getStatus().equals("Ameacado"))&&(p.getPapel().equals("Detetive"))){
				// DETETIVE GANHA
				System.out.println("DETETIVE GANHOU");
			}
			// MATANDO AMEACADO
			else if(p.getStatus().equals("Ameacado")){
				p.setStatus("Morto");
				//	INFORMAR A TODOS QUE O PLAYER MORREU
				int pos = buscaPos(p.getNome());
				if(pos>=0){
					players.remove(pos);
				}
				if(players.size()<=3){
					// ASSASSINO VENCEDOR
					System.out.println("ASSASSINO GANHOU");
				}
			}
		}// FIM DO METODO MATAR
		
		//	 BUSCA A POSIÇÃO DO JOGAODR NA LISTA POR NOME
		private int buscaPos(String name) {
			int i;
			for(i=0 ; i<players.size(); i++){
				if(players.get(i).getNome().equals(name)){
					return i;
				}
			}
			return -1;// ERRO
		} // FIM DO METODO DE BUSCA
		
		
	}//FIM
	
}//FIM