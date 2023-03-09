package com.anjiplus.template.gaea.business.modules.encryption.cotroller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anji.plus.gaea.annotation.Permission;
import com.anji.plus.gaea.bean.ResponseBean;
import com.anjiplus.template.gaea.business.util.Base64Util;
import org.springframework.util.StringUtils;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@Api(tags = "加密-解密处理")
@RequestMapping("/encryption")
@Permission(code = "encryptionManage", name = "授权管理")
public class EncryPtionnColler {

    @PostMapping("/decrypt")
    public ResponseBean getRsa(String cipherStr, String macAddress,String customCode) {
        if (StringUtils.isEmpty(cipherStr)) {
            return ResponseBean.builder().code("502").message("cipherStr 参数不能为空").build();
        } else {
            if(cipherStr.indexOf("/-") == -1 || cipherStr.split("/-").length != 3){
                return ResponseBean.builder().code("502").message("密文信息错误，请检查密文信息是否正确").build();
            }
            String nowCipherStr = cipherStr.replaceAll("&","+");
            String[] array = nowCipherStr.split("/-");
            String KEY = array[0];
            String IV = array[1];
            String getCiphertext = array[2];
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                // AES 解密
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(256);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                String string = new String(cipher.doFinal(decoder.decodeBuffer(getCiphertext)), StandardCharsets.UTF_8);
                // Basee64 解密
                String string2 = Base64Util.decode(string);
                JSONObject jsonObject = JSON.parseObject(string2);
                String DataString = jsonObject.get("effectiveTime").toString();
                // 字符串转时间戳
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                try {
                    date = sf.parse(DataString);
                } catch (ParseException e) {
                    e.printStackTrace();

                }
                long oldDate = date.getTime();
                long nowDate = new Date().getTime();
                if (oldDate > nowDate ) {
                    if(jsonObject.getString("macAddress").equals(macAddress)){
                        if(jsonObject.getString("customCode").equals(customCode)){
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("authorizedFlag",true);
                            jsonObject1.put("authorizedTime",oldDate);
                            jsonObject1.put("authorizedNumber", jsonObject.getString("authorizedNumber"));
                            return ResponseBean.builder().code("200").message("授权通过").data(jsonObject1).build();
                        }else{
                            return ResponseBean.builder().code("502").message("客户编码输入有误，请检查客户编码是否正确").data(false).build();
                        }

                    }else{
                        return ResponseBean.builder().code("502").message("Mac 地址输入有误，请检查 Mac 地址是否正确").data(false).build();
                    }
                } else {
                    return ResponseBean.builder().code("502").message("当前授权有效期已经失效，请联系相关人员进行申请").data(false).build();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseBean.builder().code("502").message("密文信息错误，请检查密文信息是否正确").build();
            }
        }
    }

}
