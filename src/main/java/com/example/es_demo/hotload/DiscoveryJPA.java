/*
package com.example.es_demo.hotload;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import xyz.erupt.flow.core.service.EruptFlowCoreService;
import xyz.erupt.flow.model.EruptFlowManage;
import xyz.erupt.flow.service.EruptFlowManageDataService;
import xyz.erupt.flow.utils.JsonUtil;

@Component
@Slf4j
public class DiscoveryJPA implements ApplicationListener {
    @Resource
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Resource
    EruptFlowCoreService eruptFlowCoreService;

    static AtomicBoolean needDiscoveryJPA = new AtomicBoolean(true);
    public static void setNeedDiscoveryJPA() {
        needDiscoveryJPA.set(true);
    }

    public static boolean isNeedDiscoveryJPA() {
        return needDiscoveryJPA.get();
    }

    */
/**
     * 再次调用jpa发现入口
     * @throws Exception
     *//*

    public synchronized void reDiscovoryJPA() throws Exception {
        */
/*这里有个注意点：加载“注册到erupt的class”的classloader  与  加载“注册到jpa的class”的classloader 必须相同！！！
        否则 sun.reflect.UnsafeFieldAccessorImpl#ensureObj中this.field.getDeclaringClass().isAssignableFrom(var1.getClass())为false，从而抛出异常，导致jpa无法get或set filed  *//*

        registerErupt();

        localContainerEntityManagerFactoryBean.afterPropertiesSet();

        needDiscoveryJPA.set(false);
    }


    @Resource
    private EruptFlowManageDataService eruptFlowManageDataService;

    void registerErupt(){
        List<EruptFlowManage> allEruptFLowManger = eruptFlowManageDataService.getAllEruptFLowManger();
        if (!CollectionUtils.isEmpty(allEruptFLowManger)) {
            for (EruptFlowManage eruptFlowManage : allEruptFLowManger) {
                byte[] classContent = eruptFlowManage.getClassContent();
                String javaName = eruptFlowManage.getJavaName();
                if (StringUtils.isNotBlank(javaName) && ArrayUtils.isNotEmpty(classContent)) {
                    // test
                    EruptFlowManage byJavaName = eruptFlowManageDataService.getEruptFlowManageByJavaName(javaName);
                    log.info("getByJavaName {} {}", javaName, JsonUtil.toJson(byJavaName));

                    // 写入class文件
                    try {
                        log.debug("erupt_flow registerErupt {}", javaName);
                        HotLoader.registerErupt(javaName, classContent);
                    } catch (Exception e) {
                        log.error("erupt_flow registerErupt {} error EruptFlowManageID{}", javaName, eruptFlowManage.getId(), e);
                    }
                }
            }
        }

        try {
            eruptFlowCoreService.run(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test 读取所有上传的java文件
        */
/*try {
            String codeDir = HotLoader.codeDir();
            String javeName = "EruptFlowManage1";
            byte[] bytes = HotLoader.getClassContent(javeName);
            // 写入class文件
            HotLoader.registerErupt(javeName, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }*//*



    }



    public static void main(String[] args) throws Exception, ClassNotFoundException {
        byte[] eruptFlowManage1s = HotLoader.getClassContent("EruptFlowManage1");
        byte[] types = HotLoader.getClassContent("AClass");
        File file = new File("D:\\workspace\\ce-erupt-example\\target\\classes\\com\\example\\demo\\hotcode\\DIscovoryJPA.class");
        byte[] bytes = new byte[ 1000 ];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(bytes);


        String co = HotLoader.codeRootDir();
        System.out.println(co);
        Class aClass = HotLoader.loadClass(HotLoader.packageDotPaht + "AClass");
        Class bClass = HotLoader.loadClass(HotLoader.packageDotPaht + "BClass");
        Class cClass = HotLoader.loadClass(HotLoader.packageDotPaht + "CClass");
        System.out.println();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            try {
                reDiscovoryJPA();
            } catch (Exception e) {
                throw new RuntimeException("reDiscovoryJPA", e);
            }
        }
    }
}
*/
