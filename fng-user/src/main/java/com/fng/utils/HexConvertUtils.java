package com.fng.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 16进制相关工具类
 */
public final class HexConvertUtils {
	
	/**
	 * 
	* @Title: byteToHex  
	* @Description: 将加密以后的byte转换成16进制，以便于好看  
	* @param @param params 加密后的byte数组
	* @param @return    参数  
	* @return String    返回类型  
	* @throws
	 */
	public static String parseHexByByte(byte[] params) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < params.length; i++) {
			temp = Integer.toHexString(params[i] & 0xFF);
			if (temp.length() == CommonConstant.ONE) {
				stringBuffer.append(CommonConstant.ZERO.toString());
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 
	* @Title: parseBinaryByHex  
	* @Description: 讲16进制转换为byte数组
	* @param @param data 16进制数据
	* @param @return    参数   byte数组
	* @return byte[]    返回类型  
	* @throws
	 */
	public static byte[] parseByteByHex(String data) {
		if(StrUtil.isBlank(data))
		{
			return null;
		}
		byte[] result = new byte[data.length() / 2];
		for (int i = 0; i < data.length() / 2; i++) {
			int high = Integer.parseInt(data.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(data.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}
