package io.renren.common.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.Charset;


/**
 * Base64Util工具类 --- 加密和解密
 * @author java1234_小锋
 * @site www.java1234.com
 * @company Java知识分享网
 * @create 2019-02-10 下午 2:49
 */
public class Base64Util {
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	/**
     * 解密
     * @param str
     * @return
     */
    public static String decodeStr(String str){
        if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return "";
		}
		return new String(Base64.decodeBase64(new String(str).getBytes(DEFAULT_CHARSET)),DEFAULT_CHARSET).trim();
    }

    /**
     * 加密
     * 
     * @param str
     * @return
     */
    public static String encodeStr(String str){
        if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return "";
		}
        return new String(Base64.encodeBase64Chunked(str.getBytes(DEFAULT_CHARSET)),DEFAULT_CHARSET).trim();
    }

}
