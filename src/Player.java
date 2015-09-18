
public class Player {
	
	private String name;
	private String papel;
	private String comando;
	private String status;
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setRole(String papel){
		this.papel = papel;
	}
	public void setComando(String comando){
		this.comando = comando;
	}
	public void setStatus(String status){
		this.status = status;
	}
	public String getNome(){
		return this.name;
	}
	
	public String getPapel(){
		return this.papel;
	}
	
	public String getComando(){
		return this.comando;
	}
	
	public String getStatus(){
		return this.status;
	}
}
