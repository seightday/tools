package com.seightday;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * https://stackoverflow.com/questions/25160221/how-do-i-create-beans-programmatically-in-spring-boot/25175780#25175780
 * https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
@Configuration
public class FtpFilesImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar , EnvironmentAware {
    
    private Environment env;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String _days=env.getProperty("days");
        Integer days=Integer.parseInt(_days);
        String sys=env.getProperty("sys");
        String[] split = sys.split(",");
        //http://blog.csdn.net/hfmbook/article/details/70209178
        //http://412887952-qq-com.iteye.com/blog/2348445
        for (int i = 0; i < split.length; i++) {
            String s = split[i];



            String localDir=env.getProperty(s+".local.dir");
            GenericBeanDefinition filterBeanDefinition = new GenericBeanDefinition();
            filterBeanDefinition.setBeanClass(Filter.class);
            filterBeanDefinition.getPropertyValues().add("localDir",localDir).add("days",days);
            registry.registerBeanDefinition(s+"Filter", filterBeanDefinition);
            //未找到方法通过以下方式设置
//            Filter filter = new Filter(localDir, days);
//            filterBeanDefinition.set

            String sftpServer=env.getProperty(s+".ftp.server.info");
            String downloadLocation=env.getProperty(s+".ftp.local.dir");
            GenericBeanDefinition routerBeanDefinition = new GenericBeanDefinition();
            routerBeanDefinition.setBeanClass(Route.class);
            routerBeanDefinition.getPropertyValues().add("sftpServer",sftpServer).add("downloadLocation",downloadLocation);
            registry.registerBeanDefinition(s+"Router", routerBeanDefinition);

        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env=environment;
    }
}
