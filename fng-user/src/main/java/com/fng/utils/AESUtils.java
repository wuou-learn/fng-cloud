package com.fng.utils;

import cn.hutool.core.util.StrUtil;

import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESUtils
 */
public final class AESUtils {
	private static final Logger LOGGER  =Logger.getLogger(AESUtils.class.toString());
	
	private static final String KEY_AES = "AES";
	/**
	 * 128位密匙长度
	 */
	public static final Integer AES128=128;
	/**
	 * 192位密匙长度
	 */
	public static final Integer AES192=192;
	/**
	 * 256位密匙长度
	 */
	public static final Integer AES256=256;
	/**
	 * 加密key
	 */
	private String key="";
	/**
	 * new对象使用模式
	 */
	private Integer pattern;
	
	public AESUtils(String key,Integer pattern){
		this.key = key;
		this.pattern = pattern;
	}
	
	/**
	 * 
	* @Title: encrypt  
	* @Description: AES加密方法   
	* @param @param data  加密的数据
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	public String encrypt(String data) throws EncryptComponentException{
		return doAES(data, this.key, Cipher.ENCRYPT_MODE,this.pattern);
	}
	/**
	 * 
	* @Title: decrypt  
	* @Description: 解密方法
	* @param @param data 解密的数据
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	public String decrypt(String data) throws EncryptComponentException{
		return doAES(data, this.key, Cipher.DECRYPT_MODE,this.pattern);
	}
	
	/**
	 * 
	* @Title: encrypt  
	* @Description: 加密
	* @param @param data 内容
	* @param @param key  加密key
	* @param @param pattern 模式
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	public static String encrypt(String data, String key,int pattern) throws EncryptComponentException {
		return doAES(data, key, Cipher.ENCRYPT_MODE,pattern);
	}
	
	public static byte[] encrypt(byte[] data, String key,int pattern) throws EncryptComponentException {
		return doByteAES(data, key, Cipher.ENCRYPT_MODE,pattern);
	}
	public static byte[] decrypt(byte[]data, String key,int pattern) throws EncryptComponentException {
		return doByteAES(data, key, Cipher.DECRYPT_MODE,pattern);
	}
	/**
	 * 
	* @Title: getJsGeneratorKey  
	* @Description: 获取AES算法js加解密需要的key
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	public String getJsGeneratorKey() throws EncryptComponentException{
		return AESUtils.getJsGeneratorKey(this.key,this.pattern);
	}
	/**
	 * 
	* @Title: decrypt  
	* @Description: 解密
	* @param @param data  内容
	* @param @param key  解密key
	* @param @param pattern 模式
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String 返回类型
	* @throws
	 */
	public static String decrypt(String data, String key,int pattern) throws EncryptComponentException {
		return doAES(data, key, Cipher.DECRYPT_MODE,pattern);
	}

	/**
	 * 
	* @Title: doAES  
	* @Description: 加密解密方法
	* @param @param data 内容
	* @param @param key  key
	* @param @param mode 加密还是解密
	* @param @param pattern 模式
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	private static String doAES(String data, String key, int mode,int pattern) throws EncryptComponentException{
		try {
			if (StrUtil.isBlank(data) || StrUtil.isBlank(key)) {
				return null;
			}
			// 判断是加密还是解密
			boolean encrypt = mode == Cipher.ENCRYPT_MODE;
			byte[] content;
			// true 加密内容 false 解密内容
			if (encrypt) {
				content = data.getBytes(CommonConstant.CHARSET);
			} else {
				content = HexConvertUtils.parseByteByHex(data);
			}
			/**
			 * 1.构造密钥生成器，指定为AES算法,
			 * 2.不区分大小写 生成一个128位的随机源,根据传入的字节数组
			 */
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        random.setSeed(key.getBytes());
			KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
			kgen.init(pattern,random);
			// 3.产生原始对称密钥
			SecretKey secretKey = kgen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] enCodeFormat = secretKey.getEncoded();
			// 5.根据字节数组生成AES密钥
			SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
			// 6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			// 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(mode, keySpec);
			byte[] result = cipher.doFinal(content);
			if (encrypt) {
				return HexConvertUtils.parseHexByByte(result);
			} else {
				return new String(result, CommonConstant.CHARSET);
			}
		} catch (Exception e) {
			LOGGER.severe("AES加密、解密处理异常"+e.getMessage());;
			throw new EncryptComponentException(e.getMessage());
		}
	}
	
	
	
	private static  byte[] doByteAES(byte[] data, String key, int mode,int pattern) throws EncryptComponentException{
		try {
			if (data==null || StrUtil.isBlank(key)) {
				return null;
			}
			byte[] content = data;
			/**
			 * 1.构造密钥生成器，指定为AES算法,
			 * 2.不区分大小写 生成一个128位的随机源,根据传入的字节数组
			 */
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        random.setSeed(key.getBytes());
			KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
			kgen.init(pattern,random);
			// 3.产生原始对称密钥
			SecretKey secretKey = kgen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] enCodeFormat = secretKey.getEncoded();
			// 5.根据字节数组生成AES密钥
			SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
			// 6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			// 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(mode, keySpec);
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			LOGGER.severe("AES加密、解密处理异常"+e.getMessage());;
			throw new EncryptComponentException(e.getMessage());
		}
	}
	/**
	 * 
	* @Title: getJsGeneratorKey  
	* @Description: 获取js加密需要的key
	* @param @param key 加密key字符串
	* @param @param pattern 加密位数
	* @param @return
	* @param @throws EncryptComponentException    参数  
	* @return String    返回类型  
	* @throws
	 */
	public static String getJsGeneratorKey(String key,int pattern) throws EncryptComponentException{
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance(KEY_AES);
			kgen.init(pattern, new SecureRandom(key.getBytes(CommonConstant.CHARSET)));
			// 3.产生原始对称密钥
			SecretKey secretKey = kgen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] enCodeFormat = secretKey.getEncoded();
			return HexConvertUtils.parseHexByByte(enCodeFormat);
		} catch (Exception e) {
			LOGGER.severe("AES获取js使用密钥异常"+e.getMessage());
			throw new EncryptComponentException(e.getMessage());
		}
	}
}