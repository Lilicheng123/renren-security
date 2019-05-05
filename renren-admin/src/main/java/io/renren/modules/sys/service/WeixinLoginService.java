package io.renren.modules.sys.service;


import io.renren.modules.sys.entity.AccessToken;
import io.renren.modules.sys.entity.WechatUserUnionID;

/**
 * @author java1234_小锋
 * @site www.java1234.com
 * @company Java知识分享网
 * @create 2019-02-25 下午 10:47
 */
public interface WeixinLoginService {

    /**
     * 微信扫码登录请求地址生成
     * @return
     */
    public String genLoginUrl();

    /**
     * 用户授权后获取用户唯一标识
     * @param code
     * @return
     */
    public AccessToken getAccessToken(String code);

    /**
     * 获取用户统一标识。针对一个微信开放平台帐号下的应用，
     * 同一用户的unionid在多个应用中是唯一的。
     * 此方法不牵扯到多个应用时候可以不用。
     * 此处用到只是为了获取微信扫码用户的省份城市(此信息获取的只是微信用户所填的城市省份，
     * 并不是用户的实时位置信息，如果用户未填写是获取不到的。)
     * @return
     */
    public WechatUserUnionID getUserUnionID(String access_token, String openid);
}
