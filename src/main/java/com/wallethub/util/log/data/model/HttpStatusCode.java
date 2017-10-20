package com.wallethub.util.log.data.model;

public class HttpStatusCode {
	private int id;
	private int code;
	private String message;

	public HttpStatusCode() {
	}

	public HttpStatusCode(int id, int code, String message) {
		super();
		this.id = id;
		this.code = code;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
