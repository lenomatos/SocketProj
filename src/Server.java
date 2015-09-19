import java.awt.List;
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
	ArrayList<PrintWriter> writers = new ArrayList();
	
	public void sendToAll(String msg){ // ENVIAR MSG PARA TODOS
		for(PrintWriter w: writers){
			try{
				w.println(msg);
				w.flush();
			} catch(Exception e){}
		}
	}
	
	public static void main(String[] args) throws IOException{
		new Server().runServer();
	}
	
	public void runServer() throws IOException{
		ServerSocket serverScoket = new ServerSocket(PORT);
		System.out.println("Server Ativo e Pronto para Conectar!!!...");
		
		while(true){
			Socket socket = serverScoket.accept();// NOVA CONEXAO
			new ServerThread(socket).start(); // STARTA UMA THREAD PARA CADA CONEXAO
			PrintWriter p = new PrintWriter(socket.getOutputStream(), true);
			writers.add(p);
		}
		
	}
	
	public class ServerThread extends Thread {
		
		Socket socket;

		ServerThread(Socket socket){
			this.socket = socket;
			
		}
		
	synchronized public void run(){// THREAD RODANDO
			
			try {
				Player p = new Player();// JOGADOR
				int aux; // VARIAVEL AUXILIAR
				String n = null; // PARA O NOME
				String message = null; // STRING QUE RECEBE A MENSAGEM DO CLIENTE
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true); // USADO PARA ESCREVER
				BufferedReader buffereadReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// ENTRADA RECEBIDA DO CLIENTE
				
				n = buffereadReader.readLine();// PEGAR O NOME DO JOGADOR
				System.out.println("User: "+n+" conectado!!!...");// SABER QUE O CLIENTE FOI CONECTADO COM SUCESSO
	
				p.setName(n); // SETA O NOME DO JOGADOR
				p.setStatus("Inocente");// TODOS INOCENTES NO INICIO DO JOGO
				players.add(p);// ADICIONA JOGADOR A LISTA
				
				if((players.size())==5){ // LIMITAR O NUMERO DE JOGADORES
					distributeRoles(); // PARA MAXIMO 4 JOGADORES
				}
				
				printWriter.println("Conectado com Sucesso!!!...");// INFORMA QUE O CLIENTE FOI CONECTADO
				
				// COMUNICANDO
				while((message = buffereadReader.readLine())!=null){ // ENQUANTO ESTIVER RECEBENDO ENTRADA DO CLIENTE
					System.out.println("Mensagem Recebida do "+p.getNome()+": "+message); // MOSTRA A MENSAGEM RECEBIDA
					if(p.getStatus().equals("Morto")){
						socket.close();
					}
					if(message.equals("Suspeitar")){
						
						if(p.getPapel().equals("Detetive")){
							printWriter.println("Diginte o nome do Jogador");
							n = buffereadReader.readLine();
							aux = buscaPos(n);
							
							if(aux>=0){
								suspect(players.get(aux));
							}
						}
					}
					if(message.equals("Acusar")){
						
						if(p.getPapel().equals("Detetive")){
							printWriter.println("Diginte o nome do Jogador");
							n = buffereadReader.readLine();
							aux = buscaPos(n);		
							if(aux>=0){
								if(!players.get(aux).getStatus().equals("Suspeito")){// SO PODE ACUSAR 
									System.out.println("PRECISA ACUSAR ALGUEM");
									printWriter.println("PRECISA ACUSAR ALGUEM");
								}
								accuse(players.get(aux));
							}
						}
					}
					if(message.equals("Ameacar")){
			
						if(p.getPapel().equals("Assassino")){
							printWriter.println("Diginte o nome do Jogador");
							n = buffereadReader.readLine();
							aux = buscaPos(n);				
							if(aux>=0){
								threaten(players.get(aux));
							}
						}
					}
					if(message.equals("Matar")){
						
						if(p.getPapel().equals("Assassino")){
							printWriter.println("Diginte o nome do Jogador");
							n = buffereadReader.readLine();
							aux = buscaPos(n);
							if(aux>=0){
								if(!players.get(aux).getStatus().equals("Ameacado")){// SO PODE ACUSAR 
									System.out.println("PRECISA AMEACAR ALGUEM");
									printWriter.println("PRECISA AMEACAR ALGUEM");
								}
								blink(players.get(aux));
							}
						}
					}
					
					printWriter.println("VOCE: "+p.getNome()+" -PAPEL: "+p.getPapel()+" -STATUS: "+p.getStatus()); // REPLICA A MENSAGEM -- OPCIONAL
					
					System.out.println("TODOS JOGADORES: PAPEL: STATUS");// TIRAR -- USANDO COMO DEBUGGER
					for(int i=0; i<players.size(); i++){// TIRAR -- USANDO COMO DEBUGGER
						System.out.println(players.get(i).getNome() +": "+players.get(i).getPapel()+": "+players.get(i).getStatus());
					}
					System.out.println("------------------------");// TIRAR -- USANDO COMO DEBUGGER
				}
				socket.close(); // ENCERRA A SOCKET E THREAD
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void distributeRoles(){ // DISTRIBUI PAPEIS PARA OS JOGADORES -- OK
			Random number = new Random();
			int num1 = number.nextInt((players.size())); // SORTEIA POSIÇÃO 
			int num2 = number.nextInt((players.size())); // SORTEIA POSIÇÃO
			
			while(num1==num2){ // UM UNICO DETETIVE E ASSASSINO
				num2 = number.nextInt((4)+1);
				num1 = number.nextInt((4)+1);
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
			
			if(p.getStatus().equals("Suspeito")){
				if(p.getPapel().equals("Assassino")){
					sendToAll("DETETIVE GANHOU");// CHAMAR VENCEDOR DETETIVE
				}
				else{
					p.setStatus("Inocente");
				}
			}

		}// FIM DO ACUSAR
		
		// SOMENTE ASSASSINO CHAMA ESTE METODO, AMEACANDO ALGUEM
		private void threaten(Player p){
			p.setStatus("Ameacado");
		}//  FIM DO AMEACAR
		
		//  METODO UTILIZADO PELO ASSASSINO
		private void blink(Player p){
			
			if((p.getStatus().equals("Ameacado"))&&(p.getPapel().equals("Detetive"))){// AMEACOU O DETETIVE 
				System.out.println("DETETIVE GANHOU");// DETETIVE GANHA
				sendToAll("DETETIVE GANHOU");
			}
			
			if(p.getStatus().equals("Ameacado")){// MATANDO AMEACADO
				p.setStatus("Morto");
				sendToAll("Jogador: "+p.getNome()+" foi morto!");//	INFORMAR A TODOS QUE O PLAYER MORREU
				
				int pos = buscaPos(p.getNome());
				if(pos>=0){
					players.remove(pos);
				}
				if(players.size()<=3){// ASSASSINO VENCEDOR
					System.out.println("ASSASSINO GANHOU");
					sendToAll("ASSASSINO GANHOU");
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