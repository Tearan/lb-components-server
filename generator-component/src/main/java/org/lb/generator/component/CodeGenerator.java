/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Terran
 * @since  1.0
 */
@Slf4j
public class CodeGenerator {

    /**
     * 提取资源资源文件到当前目录
     * @throws IOException
     */
    private static void extractResources() throws IOException {
        new File("tmp\\").mkdir();
        /** 提取資源文件到当前目录*/
        try (InputStream inputStream = CodeGenerator.class.getClassLoader()
                .getResourceAsStream("generated-demo.zip");
             OutputStream outputStream = new FileOutputStream("tmp\\generated-demo.zip")){
            IOUtils.copy(inputStream,outputStream);
        }
        try (InputStream inputStream = CodeGenerator.class.getClassLoader()
                .getResourceAsStream("device.ftl");
             OutputStream outputStream = new FileOutputStream("tmp\\device.ftl")){
            IOUtils.copy(inputStream,outputStream);
        }
        try (InputStream inputStream = CodeGenerator.class.getClassLoader()
                .getResourceAsStream("service.ftl");
             OutputStream outputStream = new FileOutputStream("tmp\\service.ftl")){
            IOUtils.copy(inputStream,outputStream);
        }
    }

    public static void generateService(ProductInfo productInfo){
         Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
         try {
             configuration.setDirectoryForTemplateLoading(new File("tmp\\"));
             configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_25));
             Template template = configuration.getTemplate("service.ftl");
             for (String sid : productInfo.getServiceCapabilityMap().keySet()){
                 DeviceService deviceService = productInfo.getServiceCapabilityMap().get(sid);
                 File file = new File("generated-demo/src/main/java/com/lbcloud/sdk/iot/device/demo/" + deviceService.getServiceType() + "Service.java");
                 Map<String, Object> root = new HashMap<String, Object>(2);
                 root.put("service", deviceService);
                 Writer javaWriter = new FileWriter(file);
                 template.process(root, javaWriter);
                 javaWriter.flush();
                 log.info("文件生成路径：" + file.getCanonicalPath());
                 javaWriter.close();
             }
         }catch (Exception e){

         }
    }

    public static void deleteFile(File file){
        if (file == null){
            return;
        }
        /** 如果dir对应的文件不存在，则退出*/
        if (!file.exists()){
            return;
        }
        if (file.isFile()){
            file.delete();
        }else {
            for (File file1 : file.listFiles()){
                CodeGenerator.deleteFile(file1);
            }
        }
        file.delete();
    }

    /**
     * 生成maven项目
     * @param productZipPath 文件路径
     * @throws IOException IO异常
     * @return boolean 返回true成功,其它都是失败
     */
    public static boolean generateMavenProject(String productZipPath) throws IOException{
        /** 提取资源资源文件到当前目录*/
        extractResources();
        DeviceProfileParser.unZipFiles("tmp\\generated-demo.zip", "");
        ProductInfo productInfo = DeviceProfileParser.pareProductFile(productZipPath);
        generateService(productInfo);
        log.info("demo code generated to:[{}\\generated-demo]",new File("").getAbsolutePath());
        /**删除临时文件*/
        deleteFile(new File("tmp\\"));
        return true;
    }

}
