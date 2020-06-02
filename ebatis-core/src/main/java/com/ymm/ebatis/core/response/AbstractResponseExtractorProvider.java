package com.ymm.ebatis.core.response;

import com.ymm.ebatis.core.meta.MethodMeta;
import com.ymm.ebatis.core.meta.RequestType;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author 章多亮
 * @since 2020/1/17 14:12
 */
public abstract class AbstractResponseExtractorProvider implements ResponseExtractorProvider {
    private final RequestType requestType;

    protected AbstractResponseExtractorProvider(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public boolean support(MethodMeta method) {
        return this.requestType == method.getRequestType();
    }

    @Override
    public ResponseExtractor<?> getResponseExtractor(MethodMeta meta) {
        Method method = meta.getElement();

        boolean wrapped = isWrapped(meta);

        ResolvableType resolvedResultType;
        if (wrapped) {
            resolvedResultType = ResolvableType.forMethodReturnType(method).getGeneric(0);
        } else {
            resolvedResultType = ResolvableType.forMethodReturnType(method);
        }

        return getResponseExtractor(resolvedResultType);
    }

    /**
     * 判断返回值是否被 {@link java.util.concurrent.CompletableFuture} 和 {@link java.util.Optional} 类型包装
     *
     * @return 如果返回值类型的是 {@link java.util.concurrent.CompletableFuture} 和 {@link java.util.Optional}，返回<code>true</code>
     */
    private boolean isWrapped(MethodMeta meta) {
        Class<?> returnType = meta.getReturnType();
        return CompletableFuture.class == returnType || Optional.class == returnType;
    }

    /**
     * 获取指定类型返回值提取器
     *
     * @param resolvedResultType 解析后的返回值类型，已经却掉包装类型
     * @return 响应提取器
     */
    protected abstract ResponseExtractor<?> getResponseExtractor(ResolvableType resolvedResultType); // NOSONAR
}
