package org.example.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;

import java.util.Map;

public class JsonUtils {

    public static Boolean isJson(String s) {
        return JSONValidator.Type.Object.equals(JSONValidator.from(s).getType());
    }

    public static Boolean isJsonArray(String s) {
        return JSONValidator.Type.Array.equals(JSONValidator.from(s).getType());
    }

    public static Map<String, Object> toMap(String jsons) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(jsons)) {
            JSONObject jsonObject = JSONObject.parseObject(jsons);
            return jsonObject;
        }
        return null;
    }

}
