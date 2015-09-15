import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerThread extends Thread {
	
	Socket socket;
	ServerThread(Socket socket){
		this.socket = socket;
	}
	public void run(){// THREAD RODANDO
		
		try {
			
			String message = null; // STRING QUE RECEBE A MENSAGEM DO CLIENTE
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true); // USADO PARA ESCREVER
			BufferedReader buffereadReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// ENTRADA RECEBIDA DO CLIENTE
			
			System.out.println("User: "+buffereadReader.readLine()+" conectado!!!...");// SABER QUE O CLIENTE FOI CONECTADO COM SUCESSO
			
			printWriter.println("Conectado com Sucesso!!!...");// INFORMA QUE O CLIENTE FOI CONECTADO
			
			while((message = buffereadReader.readLine())!=null){ // ENQUANTO ESTIVER RECEBENDO ENTRADA DO CLIENTE
				System.out.println("Mensagem do Cliente: "+message); // MOSTRA A MENSAGEM RECEBIDA
				printWriter.println("Mensagem Replycada: "+message); // REPLICA A MENSAGEM
			}
			socket.close(); // ENCERRA A THREAD
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}//FIM