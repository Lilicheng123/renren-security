package io.renren.modules.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import freemarker.ext.beans.HashAdapter;
import io.renren.common.config.AlipayConfig;
import io.renren.common.utils.JsonUtils;
import io.renren.common.utils.R;
import io.renren.modules.pay.e.PayStatus;
import io.renren.modules.pay.service.AlipayService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author llc
 * @Description
 * @Date 2019/4/30 13:34
 */
@Service("alipayService")
public class AplipayServiceImpl  implements AlipayService {
    private static final Logger _log = LoggerFactory.getLogger(AplipayServiceImpl.class);

    @Autowired
    AlipayConfig alipayConfig;
    /**
     * 查询交易结果
     * @param tradeNo
     * @return
     */
    @Override
    public R tradeQuery(String tradeNo) {

        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        Map<String, String> map = new HashMap<>(1);
        map.put("trade_no", tradeNo);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(JsonUtils.object2String(map));
        String result=""; //支付宝查询结果
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                _log.error("支付宝查单调用失败,参数:" + tradeNo);
            }

            String tradeStatus = response.getTradeStatus();
            if(StringUtils.isBlank(tradeStatus)){
                return R.error("交易单号不存在!");
            }
            switch (tradeStatus) {
                case "TRADE_SUCCESS":
                case "TRADE_FINISHED":
                    result = PayStatus.SUCC.getType();
                    break;
                case "WAIT_BUYER_PAY":
                    result = PayStatus.READY.getType();
                    break;
                case "TRADE_CLOSED":
                    result =PayStatus.TIMEOUT.getType();
                    break;
                default:
                    result ="未知错误!";
                    break;
            }
        } catch (AlipayApiException e) {
            _log.error("支付宝APP查单失败：" + e.getErrMsg());
            return R.error().put("result","支付宝APP查单失败：" + e.getErrMsg());
        }
        return R.ok().put("result",result);
    }


    /**
     * 生成二维码预支付
     * @param pay
     * @return
     */
    @Override
    public R qrCodePay(BigDecimal pay) {
        R re=R.ok();
        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        Map<String,Object> map=new HashMap<>();
        map.put("out_trade_no",System.currentTimeMillis()+""); //自己生成交易单号
        map.put("seller_id",alipayConfig.getPartner());
        map.put("total_amount",pay);
        map.put("subject","iphone18");//商户名称或商品名称
        request.setBizContent(JsonUtils.object2String(map));
        request.setNotifyUrl("http://27c72ba1.ittun.com/api/alipay/alipayNotify");  //自己写好回调地址，必须为公网地址，且不能有任何需要认证的
        //通过alipayClient调用API，获得对应的response类
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
            System.out.println(response.getBody());
            String resultStr=response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            String qr_code = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
            re.put("qrcode",qr_code);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return re;
    }


    //回调业务代码处理
    @Override
    public Boolean dealBusiness(HttpServletRequest request) {
        //先将支付宝回调的信息处理成map
        Map<String, String> map = toMap(request);
        try {
            boolean success = isSuccess(map);
            if(success){
                System.out.println(JsonUtils.object2String(map));
                System.out.println("处理业务完毕");
            }else{
                return false;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 将异步通知的参数转化为Map
     * @param request
     * @return
     */
    public static Map<String, String> toMap(HttpServletRequest request) {
        System.out.println(">>>>" + request.getQueryString());
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 校验支付宝支付是否成功
     *
     * @param params
     *            http请求
     * @return 成功即为真
     * @throws AlipayApiException
     */
    private boolean isSuccess(final Map<String, String> params) throws AlipayApiException {
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(),
                alipayConfig.getInputCharset(), alipayConfig.getSignType()); // 调用SDK验证签名
        if (!signVerified) {
            return false;
        }
        // 交易状态
        String trade_status = params.get("trade_status");
        if (!trade_status.equals("TRADE_FINISHED") && !trade_status.equals("TRADE_SUCCESS")) {
            return false;
        }
        return true;
    }

}
