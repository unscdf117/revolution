Netty MD:

Channel EventLoop ChannelFuture三大接口 这三个接口可以看作Netty网络的抽象

**Channel: Socket
EventLoop: 控制和处理并发以及流
ChannelFuture: 异步通知**

Channel接口的Api降低了Socket使用的复杂性
 EmbeddedChannel
 LocalServerChannel
 NioDatagramChannel
 NioSctpChannel
 NioSocketChannel

EventLoop是Netty的核心抽象 用来处理连接生命周期中的事件
众多EventLoop被包含在EventLoopGroup当中 一个EventLoop在其生命周期当中只绑定一个Thread 并且所有EventLoop处理的I/O事件只再其专有的Thread上面执行
一个Channel在其生命周期中只能注册与一个EventLoop 而一个EventLoop可以被分配给多个Channel(EventLoop对于Channel而言就是一个一对多的关系)

ChannelFuture接口其中的addListener()注册了一个ChannelFutureListener用来获取某个操作的异步通知*(无论成功与否) 功效类似Future<T> 因为是异步的所以具体什么时候返回结果不定 同属于一个Channel的操作可以保证其调用顺序

ChannelPipeline接口提供了ChannelHandlerChain的容器 定义该Chain在入站/出站事件流的Api 当Channel被创建时会自动分配到其专属的ChannelPipeline
具体过程1. ChannelInitializer的实例注册到ServerBootStrap上 2.ChannelInit中的initChannel()方法调用后会往ChannelPipeline上安装入一组自定义的ChannelHandler 3.ChannelInit将自身移出ChannelPipeline
Netty初始化或引导阶段会安装ChannelPipeline和ChannelHandler 一个ChannelPipeline里出站(OutBound)和进站(InBound)的Handler相互独立 每个方向上的Handler形成一个HandlerExecutorChain(类似SpringMVC) Handler之间也有执行链路

_@117: 在阅读Netty源码时发现for(;;)用的很多 相比while循环编译后的指令只有一条 而while则有4条 相比之下for(;;)的性能更好_

**ByteBuf:** Netty用来替代java.nio.ByteBuffer 作为Netty的数据容器存在
 它可以被用户自定义的缓冲区类型扩展
 通过内置的复合缓冲区类型实现了透明的零拷贝
 容量可以按需增长（类似于 JDK 的 StringBuilder）
 在读和写这两种模式之间切换不需要调用 ByteBuffer 的 flip()方法
 读和写使用了不同的索引
 支持方法的链式调用
 支持引用计数
 支持池化
ByteBuf(可指定容量)维护了两套索引 readerIndex和writerIndex 起始位置都是0 

_@117: Netty的资源管理很有意思 每当HandlerExecutorChain中的Handler(in/out BoundHandler)处理数据时 需要保证没有任何的内存泄露 否则大流量的情况下会OOM的 在使用池化ByteBuf时(netty默认PooledByteBufAllocator)需要进行采样 在SIMPLE和ADVANCED这两个采样等级时 将113(int)进行取模 一旦命中就创建一个PhantomReference 创建一个Wrapper来包装ByteBuf和Reference 而Wrapper在执行其release()时会调用到Reference.clear() 而JVM在GC时会检查没有执行clear()的Reference并且放入ResourceLeakDetector中定义的ReferenceQueue<T>当中 而PhantomReference被创建时会去check一下有没有没有执行release()的Reference并将之清除掉 如果在JVM进行GC时有PhantomReference被塞入了ReferenceQueue则ResourceLeakDetector中的reportLeak()中的死循环 -> {从ReferenceQueue.poll()并且强转成DefaultResourceLeak对象 如果对象存在 则调用其close() 如果对象此时还没被关闭 则报告MemoryLeak}_

**ChannelPipeline**接口: ChannelPipeline的本质就是一个ChannelHandler的执行链 维护了ChannelIn/OutBoundHandler 一个事件被某个ChannelHandler处理随后调用ChannelHandlerContext进行实现 然后再被转发给继承同一个父类的其他ChannelHandler处理..
 
**NETTY SERVER的启动源码分析**:
根据《netty in action》的做法 
具体的代码逻辑在本项目的com.unsc.nettys.chat包下.
创建了两个NioEventLoopGroup 用户给BootStrap做绑定

     /**
     * Set the {@link EventLoopGroup} for the parent (acceptor) and the child (client). These
     * {@link EventLoopGroup}'s are used to handle all the events and IO for {@link ServerChannel} and
     * {@link Channel}'s.
     */
    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        if (childGroup == null) {
            throw new NullPointerException("childGroup");
        }
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        this.childGroup = childGroup;
        return this;
    }
而NioEventLoopGroup自身也维护了一个EventLoopGroup 小心机婊..: )

Netty 的C/S端初始化必须要有一个BootStrap 例: ServerBootStrap 代码如下:
    
    ServerBootstrap bs = new ServerBootstrap();
    
                bs.group(inGroup, outGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new StupidChatServerInit())
                        .option(ChannelOption.SO_BACKLOG, 256)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                        
而在BootStrap被实例化之后 需要实现其功能 需要绑定其EventLoopGroup Channel childHandler Option ChildOption 其.channel(..)中的实现如下

    /**
     * The {@link Class} which is used to create {@link Channel} instances from.
     * You either use this or {@link #channelFactory(io.netty.channel.ChannelFactory)} if your
     * {@link Channel} implementation has no no-args constructor.
     */
    public B channel(Class<? extends C> channelClass) {
        if (channelClass == null) {
            throw new NullPointerException("channelClass");
        }
        return channelFactory(new ReflectiveChannelFactory<C>(channelClass));
    }
入参是一个C(Channel)的子类 如果入参不是null 则调用channelFactory(new ReflectiveChannelFactory<C>(channelClass)) 通过反射Channel工厂以Channel的class对象创建Channel实例(例如上面BootStrap初始化时候传入的NioServerSocketChannel.class)
以下是netty的ServerBootStrap的init() 相比BootStrap的init()内容更加丰富

    @Override
    void init(Channel channel) throws Exception {
        final Map<ChannelOption<?>, Object> options = options0();
        synchronized (options) {channel.config().setOptions(options);}
        final Map<AttributeKey<?>, Object> attrs = attrs0();
        synchronized (attrs) {
            for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
                AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }
        ChannelPipeline p = channel.pipeline();
        final EventLoopGroup currentChildGroup = childGroup;
        final ChannelHandler currentChildHandler = childHandler;
        final Entry<ChannelOption<?>, Object>[] currentChildOptions;
        final Entry<AttributeKey<?>, Object>[] currentChildAttrs;
        synchronized (childOptions) {currentChildOptions = childOptions.entrySet().toArray(newOptionArray(childOptions.size()));}
        synchronized (childAttrs) {currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(childAttrs.size()));}
        p.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = config.handler();
                if (handler != null) {pipeline.addLast(handler); }
                // We add this handler via the EventLoop as the user may have used a ChannelInitializer as handler.
                // In this case the initChannel(...) method will only be called after this method returns. Because
                // of this we need to ensure we add our handler in a delayed fashion so all the users handler are
                // placed in front of the ServerBootstrapAcceptor.
                ch.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {pipeline.addLast(new ServerBootstrapAcceptor(
                                currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                    }
                });
            }
        });
    }

之前的所有对于ServerBootStrap的配置的init都在这个方法当中得以实现了 比较有意思的是 这里ChannelPipeline的adLast中需要传入的是某个ChannelHandler的实现类 而ChannelInitializer<>的父类也是impl了ChannelHandler的而这个ChannelInit类中做的就是ChannelPipeline的添加ChannelHandler操作..至于具体的ChannelInitial实现类 在本项目的netty module的 chat包中能看到



