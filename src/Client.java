import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static final int PORT = 7070;
	// ESTE PROGRAMA DEVE SER COMPIADO PELO CMD
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		if(args.length == 1){ // CASO NÃO RECEBA UMA IDENTIFICAÇÃO DO USER
			
			String name = args[0]; // NOME DO USER
			Socket socket = new Socket("localhost", PORT); // IP E PORTA
			BufferedReader buffereadReaderClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));// TUDO QUE O CLIENTE RECEBE
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true); // COMO O CLIENTE ESCREVE
			BufferedReader bufferedReaderCMD = new java.io.BufferedReader(new InputStreamReader(System.in));// ENTRADA DO CMD
			
			printWriter.println(name); // MANDANDO PARA O SERVER QUEM ESTÁ CONECTADO
			
			while(true){//  ENQUANTO A CONEXAO FOR VERDADEIRA
				String readerInput = bufferedReaderCMD.readLine(); // RECEBE MENSAGEM DO CMD
				printWriter.println(name+": "+readerInput); // ENVIA MENSAGEM PARA O SERVER
				System.out.println(buffereadReaderClient.readLine());/* RECEBE A REPLY DO SERVER*/
			}
			//System.out.println("CONEXAO ENCERRADA");
		}
		else{
			System.out.println("ERRO!!! Tente EX: Client useXYZ");
		}
	}

}//FIM