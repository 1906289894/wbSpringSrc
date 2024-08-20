package com.wb.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wb
 * @date 2024/8/20 10:29
 **/
public class ClassPathApplicationContext {
    private Class configClass;
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, Object> singletonObjects = new HashMap<>();
    public ClassPathApplicationContext(Class configClass) {
        this.configClass = configClass;
        //扫描路径
        scan();

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (Objects.isNull(beanDefinition.getScope()) || beanDefinition.getScope().equals("singleton")){
                //单例
                Object object = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, object);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object instance;
        try {
            instance = clazz.getConstructor().newInstance();
            //依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            return instance;

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName){
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("singleton")) {
            //有可能还没创建
            Object o = singletonObjects.get(beanName);
            if (Objects.isNull(o)){
                o = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, o);
            }
            return o;
        }else{
            return createBean(beanName, beanDefinition);
        }
    }

    private void scan(){
        //判断类是否有某个注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = annotation.value();

            //获取包下所有类
            path = path.replace(".", "/");
            ClassLoader classLoader = ClassPathApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()){
                for (File listFile : file.listFiles()) {
                    //判断文件否为有Component注解
                    String absolutePath = listFile.getAbsolutePath();
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class")).replace("\\", ".");

                    try {
                        Class<?> aClazz= classLoader.loadClass(absolutePath);
                        if (aClazz.isAnnotationPresent(Component.class)){
                            //bean 定义
                            String beanName = aClazz.getAnnotation(Component.class).value();
                            if ("".equals(beanName)){
                                beanName = Introspector.decapitalize(aClazz.getSimpleName());
                            }
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(aClazz);
                            if (aClazz.isAnnotationPresent(Scope.class)){
                                beanDefinition.setScope(aClazz.getAnnotation(Scope.class).value());
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }else{
            System.err.println("配置有误");
        }
    }
}
