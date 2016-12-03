package com.rcw.pojo;

public class BaseInfo {
	private String ipaddress;
	private int port;
	private int localport;

	public BaseInfo() {
		this.ipaddress = null;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLocalport() {
		return localport;
	}

	public void setLocalport(int localport) {
		this.localport = localport;
	}

}
