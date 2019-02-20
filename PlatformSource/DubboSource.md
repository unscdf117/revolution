**dubbo本地服务暴露:  <dubbo:service scope='local' />**

**dubbo远程服务暴露: <dubbo:service scope='remote' />**

**dubbo不暴露: <dubbo:service scope='none' />**

dubbo的scope如果没有设置 默认是两种方式都暴露 因为dubbo不清楚自身应用当中是否存在本地引用的问题...

本地暴露: 该方式为本地暴露 只有InJvm实现 具体代码在dubbo-rpc-injvm模块中

远程暴露: 该方式有多种方式和协议实现 例如默认的Dubbo协议 Hessian Rest协议..

**Dubbo核心流程:**

Dubbo领域模型中的核心是Invoker 其他模型都向其靠拢或者转换成Invoker的实现 Invoker是一个可执行体可以向其发起invoke调用 有可能是本地实现或者远程实现也有可能是集群实现

@117: cluster 集群实现 其实就是把多个invoker包起来在外界看来好像还是某个单独的invoker 算是一种伪装或者门面

    public interface Invoker<T> extends Node {
        /**获得Biz接口 比如xxxService xxxBiz*/
        Class<T> getInterface();
        /**调用的实际方法 invoke*/
        Result invoke(Invocation invocation) throws RpcException;
    
    }
    
Invoker分两种 服务提供者Invoker / 消费者Invoker 执行链路为: 消费者Biz代码 -> proxy -> 消费者Invoker(DubboInvoker HessianRpcInvoker等) ::TCP/IP网络::>> Exporter -> 提供者Invoker(AbstractProxyInvoker) -> 服务提供者Biz代码

在Invoker接口中 invoke方法是具体的执行方法 入参只有一个Invocation 这个接口是dubbo会话域接口其持有RPC调用过程中的各种变量 比如调用的方法名 参数等.

    public interface Invocation {
        /**获得方法名(被调用)*/
        String getMethodName();
        /**获得方法参数类型数组*/
        Class<?>[] getParameterTypes();
        /**获得方法参数数组*/
        Object[] getArguments();
        /**获得隐式参数 类似HTTP Request Header*/
        Map<String, String> getAttachments();
    
        String getAttachment(String key);
    
        String getAttachment(String key, String defaultValue);
        /**获得具体的Invoker对象*/
        Invoker<?> getInvoker();
    }
    
ProxyFactory: 用途就是创建Proxy 在引用服务的时候调用

消费者执行链路: (远端把服务转换成Invoker) ReferenceConfig -> Protocol(比如dubbo hessian) --Refer--> Invoker(比如dubboInvoker等) -> (消费者把Invoker转换成需要的接口) ProxyFactory(jdkProxyFactory或javassistProxyFactory) --getProxy()--> Ref

提供者执行链路: (提供者) ServiceConfig --Ref(对外提供服务的实现类 比如xxxFacadeImpl)--> ProxyFactory(jdkProxyFactory or javassistProxyFactory) --getInvoker()--> Invoker(AbstractProxyInvoker的子类) -> Protocol(配置的协议比如dubbo协议) --export()--> Exporter

    @SPI("javassist")
    public interface ProxyFactory {
    
        /**创建 Proxy  在引用服务调用*/
        @Adaptive({Constants.PROXY_KEY})
        <T> T getProxy(Invoker<T> invoker) throws RpcException;
    
        /**创建 Invoker  在暴露服务时调用 Param: proxy为Service对象 type为Service接口类型 url为Service对应的DubboURL*/
        @Adaptive({Constants.PROXY_KEY})
        <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
    }


