
package com.fng.exception;

/**
 * 自定义应用异常
 */
public class AppException extends RuntimeException {

	private Integer code;

	private static final long serialVersionUID = -6544650564419636179L;

	/**
	 * 根据异常信息实例化自定义异常信息
	 * @param val 异常key或者异常描述信息
	 */
	public AppException(String val){
		super(val);
		this.code=-1;
	}

	/**
	 * 通过异常对象实例化自定义异常信息
	 * @param t 异常对象
	 */
	public AppException(Throwable t){
		super(t);
	}

	/**
	 * 通过异常信息和异常对象实例化自定义异常信息
	 * @param val 异常key或者异常描述信息
	 * @param t 异常对象
	 */
	public AppException(String val, Throwable t){
		super(val, t);
	}

	public AppException(Integer code, String val){
		super(val);
		this.code=code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
