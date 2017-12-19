package com.code.library.http.callback;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by yue on 2016/4/7.
 */
public abstract class JsonCallBack<T> extends Callback<T> {
    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T t = new Gson().fromJson(response.body().string(), type);
        //getSuperclassTypeParameter(getClass())
        return t;
    }

    //也可以通过Gson方式来获取 <T>的Class<T> clazz
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
}
