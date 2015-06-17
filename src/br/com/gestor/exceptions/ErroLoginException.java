package br.com.gestor.exceptions;

public class ErroLoginException extends RuntimeException {

	private String msg;
	
	public ErroLoginException(String msg){
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return this.msg;
	}
	 
	
}
