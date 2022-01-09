## 1. demo1 

  spring mvc框架测试，使用spring-boot默认Lettuce访问redis。

```
/demo/demoTest1 
纯框架测试，从内存直接返回结果。
```



```
/demo/demoTest2
使用Lettuce访问redis，get一个固定key，只能通过一个连接和redis交互。
```



## 2. demo2

spring webflux框架测试，使用spring-boot默认Lettuce访问redis。

```
/demo/demoTest1
纯框架测试，从内存直接返回结果。
```

```
/demo/demoTest2
使用Lettuce访问redis，get一个固定key，只能通过一个连接和redis交互。没有做publishOn优化。
```

```
/demo/demoTest3
使用Lettuce访问redis，get一个固定key，只能通过一个连接和redis交互。做了publishOn优化。
```





## 3.demo3

spring webflux框架测试，使用jedis访问redis。

```
/demo/demoTest1
纯框架测试，从内存直接返回结果。
```



```
/demo/demoTest2
使用jedis连接池，从redis get一个固定key。
```



## 4.demo4

spring mvc框架测试，使用jedis访问redis。

```
/demo/demoTest1
纯框架测试，从内存直接返回结果。
```



```
/demo/demoTest2
使用jedis连接池，从redis get一个固定key。
```







