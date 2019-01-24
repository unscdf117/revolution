IOC / DI

IOC(Inversion of Control) 所谓控制反转就是将编码当中需要码农自己手动去build 去new 去getInstance的这一部分创建类的实例的操作交给IOC容器实现而非硬编码的方式.所以需要一个可用的IOC容器 Spring就是一个最好实现.通过配置文件等可以让IOC容器得知自己要创建的对象与对象之间的关系做到有的放矢.
DI(Dependency injection) 所谓依赖注入 是指对象创建的时候并非主动去进行寻找依赖而是被动得接收依赖的类 也就是说并非是说在实例化对象的时候主动将其依赖的类通过某些方式进行注入

IOC容器的根本在于BeanFactory
![Image text](https://github.com/unscdf117/revolution/blob/master/images/DefaultListableBeanFactory.png)

BeanFactory作为各路骚BeanFactory的顶层接口里面定义了IOC容器的基本功能和规范 从类图上看其有三个子接口 分别是ListableBeanFactory(管理所有可生产的实例) HierarchicalBeanFactory(处理继承关系) AutowireCapableBeanFactory(自动装配规则) 默认的实现类为DefaultListableBeanFactory因为这厮实现了上面的接口
![Image text](https://github.com/unscdf117/revolution/blob/master/images/ListableBeanFactoryMethods.png)
BeanDefinition相关: 获取BeanDefinition的数量 名称的数组 指定类型名称的数组

@117: BeanDefinition是Spring中非常得劲的一个类 每个BeanDefinition都包含某个类在BeanFactory中的所有属性描述..

BeanNames4Type相关: 根据指定的类型(包含子类) 获取其对应的所有Bean的名称
BeansOfType: 根据类型(包括子类) 返回指定Bean的名称和Bean的Map
getBeanWithAnnotation和getBeanNames4Annottaion则分别根据注解类型查找Bean的Map和Bean的Map
findAnnotationOnBean: 根据指定的Bean名称找到其类上对应的注解

IOC容器获取Bean: 

  Class DefaultListableBeanFactory...
  
	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return getBean(requiredType, (Object[]) null);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
		NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args);
		if (namedBean != null) {
			return namedBean.getBeanInstance();
		}
		BeanFactory parent = getParentBeanFactory();
		if (parent != null) {
			return (args != null ? parent.getBean(requiredType, args) : parent.getBean(requiredType));
		}
		throw new NoSuchBeanDefinitionException(requiredType);
	}
  
resolveNamedBean和getParentBeanFactory两个方法最后都会指向AbstractBeanFactory中的getBean方法(此过程中BeanFactoryUtils会调用transFormBeanName(String name) 取出FactoryBean修饰符比如& $..) 由doGetBean方法去真正执行.

**@117: Spring中 实际操作的方法都有个特点 就是方法名前面都有个do前缀**

Bean的加载过程: FileSystemXmlApplicationContext 继承 AbstractApplicationContext调用其refresh() 启动IOC容器加载Bean

    public void refresh() throws BeansException, IllegalStateException {
        Object var1 = this.startupShutdownMonitor;
        synchronized(this.startupShutdownMonitor) {
            //准备刷新 获取当前的时间戳 给IOC容器设置sync标识
            this.prepareRefresh();
            //告知子类调用refreshBeanFactory() 载入Bean的配置信息
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            //为BeanFactory配置 比如: 类加载器 事件处理器等..
            this.prepareBeanFactory(beanFactory);

            try {
                // 为IOC容器的一些子类设定BeanPostProcessor
                this.postProcessBeanFactory(beanFactory);
                //调用所有注册的BeanFactoryPostProcessor的Bean
                this.invokeBeanFactoryPostProcessors(beanFactory);
                //给BeanFactory中注册 BeanPostProcessor(为Bean后置处理器 监听IOC容器所触发的事件)
                this.registerBeanPostProcessors(beanFactory);
                //初始化信息源 解决 i18n
                this.initMessageSource();
                //初始化IOC容器事件传播器
                this.initApplicationEventMulticaster();
                //调用其子类中的某些(奇葩)Bean的初始化方法
                this.onRefresh();
                //注册事件传播器的监听器
                this.registerListeners();
                //初始化所有剩下的单例Bean
                this.finishBeanFactoryInitialization(beanFactory);
                //初始化IOC容器LifeCycleProcessor 发布IOC容器的生命周期事件
                this.finishRefresh();
            } catch (BeansException var9) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                }
                //销毁已经创建的单例Bean
                this.destroyBeans();
                //取消refresh() 并且重置IOC容器的sync标识
                this.cancelRefresh(var9);
                throw var9;
            } finally {
                this.resetCommonCaches();
            }

        }
    }

refresh()是IOC容器中Bean的生命周期的管理 在其中会检查是否有IOC容器存在 如果存在则会被销毁并且走完refresh()之后使用新建立的IOC容器 类似某种重启机制确保能在一个你没有玩过的全新IOC容器当中对容器进行初始化并且根据Bean的定义信息(XML Yaml等)进行载入

@117: 这里BeanFactory -> ApplicationContext 会发现其中具体的IOC容器的操作是sync住的 而且是单例存在的 这很重要 毕竟一山不容二虎不允许出现多个IOC容器(这不是扯淡嘛 bean生成找谁去) 而且也能保障多线程情况下的线程安全

BeanDefinition在IOC容器的注册:
BeanDefinition是Spring中的存储Bean的信息的一种数据类型 Spring在解析配置文件的时候并没有实例化Bean而是创建了定义Bean的BeanDefinition 把Bean相关的配置信息设置进BeanDefinition 当发生依赖注入的时候才将这些配置信息创建并实例化Bean对象
此时BeanDefinition中只是存有Bean的一部分静态信息 需要向IOC容器注册Bean的信息才能完成IOC容器的初始化过程.

    public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {
		// 获取需要解析的Bean名称
		String beanName = definitionHolder.getBeanName();
		// 向IOC容器注册BeanDefintion (入参Bean的名称 , BeanDefintion)
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
		// 如果BeanDefintion有别名则向容器注册别名
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}

@117: 这段代码来自BeanDefinitionReaderUtils 也是Spring中将解析好的BeanDefinition的包装类BeanDefinitionHolder传入进行注册 调用本类中的registerBeanDefinition方法想IOC容器注册经过解析后的Bean 实际完成注册功能的则是DefaultListableBeanFactory

DefaultListableBeanFactory向IOC容器注册BeanDefinition的过程: 

    /** Spring这里有一个ConcurrentHashMap<String, BeanDefinition> key是BeanName Value是对应的BeanDefinition 做了个映射 :) */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    	
    '''此处省略一万字'''
    
    @Override
    	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
    			throws BeanDefinitionStoreException {
    
    		Assert.hasText(beanName, "Bean name must not be empty");
    		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
            //校验解析后的BeanDefinition
    		if (beanDefinition instanceof AbstractBeanDefinition) {
    			try {
    				((AbstractBeanDefinition) beanDefinition).validate();
    			}
    			catch (BeanDefinitionValidationException ex) {
    				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
    						"Validation of bean definition failed", ex);
    			}
    		}
            //获取BeanDefinition
    		BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
            //此处会检测IOC容器中是否有同名的BeanDefinition
    		if (existingDefinition != null) {
    		    //发现已注册的同名的BeanDefinition
    			if (!isAllowBeanDefinitionOverriding()) {
    			    //存在同名 而且不允许被覆写 嗯 抛异常
    				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
    						"Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName +
    						"': There is already [" + existingDefinition + "] bound.");
    			}
    			//**可以被覆写**
    			else if (existingDefinition.getRole() < beanDefinition.getRole()) {
    				// e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
    				if (logger.isWarnEnabled()) {
    					logger.warn("Overriding user-defined bean definition for bean '" + beanName +
    							"' with a framework-generated bean definition: replacing [" +
    							existingDefinition + "] with [" + beanDefinition + "]");
    				}
    			}
    			else if (!beanDefinition.equals(existingDefinition)) {
    			    //如果允许被覆盖
    				if (logger.isInfoEnabled()) {
    				    //打日志记录覆盖信息 后注册的覆盖先注册的BeanDefinition
    					logger.info("Overriding bean definition for bean '" + beanName +
    							"' with a different definition: replacing [" + existingDefinition +
    							"] with [" + beanDefinition + "]");
    				}
    			}
    			//**IOC容器当中没有已经注册过的同名的Bean 走正常注册流程**
    			else {
    				if (logger.isDebugEnabled()) {
    					logger.debug("Overriding bean definition for bean '" + beanName +
    							"' with an equivalent definition: replacing [" + existingDefinition +
    							"] with [" + beanDefinition + "]");
    				}
    			}
    			this.beanDefinitionMap.put(beanName, beanDefinition);
    		}
    		else {
    			if (hasBeanCreationStarted()) {
    				// Cannot modify startup-time collection elements anymore (for stable iteration)
    				synchronized (this.beanDefinitionMap) {
    					this.beanDefinitionMap.put(beanName, beanDefinition);
    					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
    					updatedDefinitions.addAll(this.beanDefinitionNames);
    					updatedDefinitions.add(beanName);
    					this.beanDefinitionNames = updatedDefinitions;
    					if (this.manualSingletonNames.contains(beanName)) {
    						Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
    						updatedSingletons.remove(beanName);
    						this.manualSingletonNames = updatedSingletons;
    					}
    				}
    			}
    			else {
    				// Still in startup registration phase
    				this.beanDefinitionMap.put(beanName, beanDefinition);
    				this.beanDefinitionNames.add(beanName);
    				this.manualSingletonNames.remove(beanName);
    			}
    			this.frozenBeanDefinitionNames = null;
    		}
    		if (existingDefinition != null || containsSingleton(beanName)) {
    			//重置所有已经注册的BeanDefinition的缓存
    			resetBeanDefinition(beanName);
    		}
    	}
    	
@117:  运行完毕之后BeanDefinition 被注册到IOC容器中 完成了IOC容器初始化的所有操作 这些BeanDefinition已经是真正可用的了 IOC容器管理这些BeanDefinition也正因为如此才能真正做到控制反转并且因为这些BeanDefinition才能IOC容器才有依赖注入的根据

IOC流程总结: IOC容器初始化是在IOC容器的实现类中 调用refresh()完成的 之后需要将Bean载入IOC容器 通过ResourceLoader的实现类(默认DefaultResourceLoader 而ApplicationContext接口也是隐式继承者) 或者上下文环境中 从类的路径 文件系统 URL等方式来定位资源 Bean的定义被抽象成Resource交给IOC容器管理 IOC容器通过BeanDefinitionReader完成BeanDefinition的解析和注册 调用loadBeanDefinition()获得具体的BeanDefinition 再调用registerBeanDefinition() 将其注册进IOC容器当中 由IOC容器实现BeanDefinitionRegistry接口实现. 整个所谓的注册过程其实就是IOC容器当中有个ConcurrentHashMap<String, BeanDefinition> key为BeanName Value是BeanDefinition 这么一个映射关系 之后所有对Bean的操作都是围绕这个容器展开 接下去就可以通过BeanFactory的实现类或者ApplicationContext的实现类来使用IOC容器了.

@117: BeanFactory和FactoryBean -> 提到了BeanFactory 有时候会有写狗比面试官问你FactoryBean和他有什么关系的傻逼问题..这种人不是看不起人就是智障 我来解释一下区别 首先名字都不一样.BeanFactory是IOC容器的抽象(是个接口) 具体的实现是各种IOC容器 比如xxxApplicationContext xxxBeanFactory等.而FactoryBean(同样是个接口)则是一个被IOC管理的Bean需要impl的接口,是对各种处理过程和资源使用的一种抽象.FactoryBean是一个接口所以使用的时候创建的不是这个接口而是一个具体的实现类 是一种**抽象工厂模式**的具体体现. Spring包括了大量通用资源和服务访问抽象的FactoryBean的实现 比如JNDI Proxy

@117: 实例化Bean和依赖注入 -> IOC容器懒加载(LazyInit) 实现预实例化 -> IOC容器在初始化的时候只是对BeanDefinition进行载入 对Bean所依赖的资源进行定位 并且向IOC容器进行注册 此时IOC容器对Bean的依赖注入并没有发生 依赖注入是在Java应用第一次向IOC容器请求Bean的时候通过IOC容器.getBean()时才完成依赖注入 如果某个Bean在定义时配置了LazyInit属性 IOC容器会在初始化的时候对该Bean也一并进行预实例化 此时就不用再次初始化Bean和对Bean进行依赖注入了 Java应用从IOC容器.getBean()

@117: 依赖检查 -> Spring IOC容器只处理单例模式下的循环依赖(Scope默认是Singleton哟) 如果是原型模式的话直接抛BeanCurrentlyInCreationException Spring在创建单例Bean的时候(可以看上面部分)不是等Bean完全实例化之后才加入缓存 而是在创建Bean之前会把其ObjectFactory先加入缓存 这样**一旦需要创建某个Bean的时候如果需要依赖其他Bean会直接使用其ObjectFactory** 但是原型模式(ProtoType) 没办法使用到缓存所以Spring容器对其循环依赖只能不处理.

AOP相关:
切面(Aspect) -> 