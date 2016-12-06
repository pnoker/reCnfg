package com.rcw.pojo;

/**
 * @author Pnoker
 * @mail peter-no@foxmail.com
 * @date 2016年12月6日
 * @description
 */

public class Result {
	private boolean success;
	private boolean string;
	private String result;

	public Result() {
		this.success = false;
		this.string = false;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isString() {
		return string;
	}

	public void setString(boolean string) {
		this.string = string;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
