import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static final int PORT = 7070;
	
	public static void main(String[] args) throws IOException{
		new Server().runServer();
	}
	
	public void runServer() throws IOException{
		ServerSocket serverScoket = new ServerSocket(PORT);
		System.out.println("Server Ativo e Pronto para Conectar!!!...");
		
		while(true){
			Socket socket = serverScoket.accept();
			new ServerThread(socket).start();
		}
		
	}
}//FIM