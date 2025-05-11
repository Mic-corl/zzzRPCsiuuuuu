package com.zyzz.proxy;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zyzz.model.RpcRequest;
import com.zyzz.model.RpcResponse;
import com.zyzz.serializer.JDKSerializer;
import com.zyzz.serializer.Serializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ServiceProxy 单元测试类
 */
public class ServiceProxyTest {

    private InvocationHandler serviceProxy;
    private Method sampleMethod;

    // Mock 对象
    private HttpRequest mockHttpRequest;
    private HttpResponse mockHttpResponse;
    private Serializer mockSerializer;

    // 静态方法mock
    private MockedStatic<HttpRequest> httpRequestMockedStatic;

    @Before
    public void setUp() throws Exception {
        // 初始化被测对象
        serviceProxy = new ServiceProxy();

        // 获取一个示例方法用于测试
        sampleMethod = SampleService.class.getMethod("sampleMethod", String.class, int.class);

        // 创建 mock 对象
        mockHttpRequest = mock(HttpRequest.class);
        mockHttpResponse = mock(HttpResponse.class);
        mockSerializer = mock(Serializer.class);

        // 替换原始的 HttpRequest.post 调用
        httpRequestMockedStatic = Mockito.mockStatic(HttpRequest.class);
        httpRequestMockedStatic.when(() -> HttpRequest.post("http://localhost:8080/rpc"))
                .thenReturn(mockHttpRequest);
    }

    @After
    public void tearDown() {
        if (httpRequestMockedStatic != null) {
            httpRequestMockedStatic.close();
        }
    }

    /**
     * 测试正常调用情况
     */
    @Test
    public void testInvoke_NormalCall() throws Throwable {
        // 准备测试数据
        Object[] args = {"testParam", 42};
        RpcRequest expectedRequest = RpcRequest.builder()
                .requestId(anyString())
                .interfaceName(sampleMethod.getDeclaringClass().getName())
                .parameterTypes(sampleMethod.getParameterTypes())
                .methodName(sampleMethod.getName())
                .parameters(args)
                .build();

        byte[] serializedRequest = "serializedData".getBytes();
        RpcResponse expectedResponse = RpcResponse.success("123", "resultData");
        byte[] serializedResponse = "responseData".getBytes();

        // 配置 mock 行为
        when(mockHttpRequest.body(serializedRequest)).thenReturn(mockHttpRequest);
        when(mockHttpRequest.contentType("application/json")).thenReturn(mockHttpRequest);
        when(mockHttpRequest.execute()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.body()).thenReturn("responseData");

        when(mockSerializer.serialize(any(RpcRequest.class))).thenReturn(serializedRequest);
        when(mockSerializer.deserialize(serializedResponse, RpcResponse.class)).thenReturn(expectedResponse);

        // 替换 ServiceProxy 中的 serializer
        ServiceProxy testProxy = new ServiceProxy();
        // 使用反射设置 serializer 字段

        // 执行测试
        Object result = testProxy.invoke(null, sampleMethod, args);

        // 验证结果
        assertNotNull(result);
        assertEquals("resultData", result);

        // 验证交互次数
        verify(mockSerializer, times(1)).serialize(any(RpcRequest.class));
        verify(mockSerializer, times(1)).deserialize(any(byte[].class), eq(RpcResponse.class));
        verify(mockHttpRequest, times(1)).execute();
    }

    /**
     * 测试网络请求失败的情况
     */
    @Test(expected = RuntimeException.class)
    public void testInvoke_HttpRequestFailure() throws Throwable {
        // 配置 mock 行为
        when(mockHttpRequest.body(any(byte[].class))).thenThrow(new RuntimeException("Network error"));

        // 替换 ServiceProxy 中的 serializer
        ServiceProxy testProxy = new ServiceProxy();


        // 执行测试
        testProxy.invoke(null, sampleMethod, new Object[]{"test", 1});
    }

    /**
     * 测试序列化失败的情况
     */
    @Test(expected = RuntimeException.class)
    public void testInvoke_SerializationFailure() throws Throwable {
        // 配置 mock 行为
        when(mockSerializer.serialize(any(RpcRequest.class))).thenThrow(new IOException("Serialization error"));

        // 替换 ServiceProxy 中的 serializer
        ServiceProxy testProxy = new ServiceProxy();
        java.lang.reflect.Field field = ServiceProxy.class.getDeclaredField("serializer");
        field.setAccessible(true);
        field.set(testProxy, mockSerializer);

        // 执行测试
        testProxy.invoke(null, sampleMethod, new Object[]{"test", 1});
    }

    /**
     * 测试反序列化失败的情况
     */
    @Test(expected = RuntimeException.class)
    public void testInvoke_DeserializationFailure() throws Throwable {
        // 准备测试数据
        byte[] serializedRequest = "serializedData".getBytes();
        byte[] serializedResponse = "responseData".getBytes();

        // 配置 mock 行为
        when(mockHttpRequest.body(serializedRequest)).thenReturn(mockHttpRequest);
        when(mockHttpRequest.contentType("application/json")).thenReturn(mockHttpRequest);
        when(mockHttpRequest.execute()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.body()).thenReturn("responseData");

        when(mockSerializer.serialize(any(RpcRequest.class))).thenReturn(serializedRequest);
        when(mockSerializer.deserialize(serializedResponse, RpcResponse.class)).thenThrow(new IOException("Deserialization error"));

        // 替换 ServiceProxy 中的 serializer
        ServiceProxy testProxy = new ServiceProxy();
        java.lang.reflect.Field field = ServiceProxy.class.getDeclaredField("serializer");
        field.setAccessible(true);
        field.set(testProxy, mockSerializer);

        // 执行测试
        testProxy.invoke(null, sampleMethod, new Object[]{"test", 1});
    }

    /**
     * 示例服务接口，用于测试 Method 参数
     */
    public interface SampleService {
        String sampleMethod(String param1, int param2);
    }
}

