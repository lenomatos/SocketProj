import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ServerThread extends Thread {
	
	Socket socket;
	ServerThread(Socket socket){
		this.socket = socket;
	}
	public void run(){
		try {
			String message = null;
			BufferedReader buffereadReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while((message = buffereadReader.readLine())!=null){
				System.out.println("Mensagem do Cliente: "+message);
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}//FIM