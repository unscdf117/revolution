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
@117: Spring中 实际操作的方法都有个特点 就是方法名前面都有个do前缀
