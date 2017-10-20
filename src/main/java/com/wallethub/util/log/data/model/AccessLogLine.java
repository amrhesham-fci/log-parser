package com.wallethub.util.log.data.model;

import java.util.Date;

public class AccessLogLine {

	private int ID;
	private Date startDate;
	private String IP;
	private Integer reponseCode;
	private String request;
	private String userAgent;

	public AccessLogLine(Date startDate, String iP, Integer reponseCode, String request, String userAgent) {
		super();
		this.startDate = startDate;
		IP = iP;
		this.reponseCode = reponseCode;
		this.request = request;
		this.userAgent = userAgent;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public Integer getReponseCode() {
		return reponseCode;
	}

	public void setReponseCode(Integer reponseCode) {
		this.reponseCode = reponseCode;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String toString() {
		return "AccessLogLine [ID=" + ID + ", startDate=" + startDate + ", IP=" + IP + ", reponseCode=" + reponseCode
				+ ", request=" + request + ", userAgent=" + userAgent + "]";
	}

}
