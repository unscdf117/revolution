**dubbo本地服务暴露:  <dubbo:service scope='local' />**

**dubbo远程服务暴露: <dubbo:service scope='remote' />**

**dubbo不暴露: <dubbo:service scope='none' />**

dubbo的scope如果没有设置 默认是两种方式都暴露 因为dubbo不清楚自身应用当中是否存在本地引用的问题...

本地暴露: 该方式为本地暴露 只有InJvm实现 具体代码在dubbo-rpc-injvm模块中

远程暴露: 该方式有多种方式和协议实现 例如默认的Dubbo协议 Hessian Rest协议..

