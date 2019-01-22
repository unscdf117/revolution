IOC / DI
概念: 
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
  
resolveNamedBean和getParentBeanFactory两个方法最后都会指向AbstractBeanFactory中的getBean方法 由doGetBean方法去真正执行.
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

