package org.lb.drools.component.ProductParser;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CodeGenerator
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 15:35
 * @Version 1.0
 */
@Slf4j
public class CodeGenerator {

    private static void extractResources() throws IOException {
        new File("tmp\\").mkdir();
        //提取资源文件到当前目录
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
         Configuration configuration = new Configuration();
         try {
             configuration.setDirectoryForTemplateLoading(new File("tmp\\"));
             configuration.setObjectWrapper(new DefaultObjectWrapper());
             Template template = configuration.getTemplate("service.ftl");
             for (String sid : productInfo.getServiceCapabilityMap().keySet()){
                 DeviceService deviceService = productInfo.getServiceCapabilityMap().get(sid);
                 File file = new File("generated-demo/src/main/java/com/huaweicloud/sdk/iot/device/demo/" + deviceService.getServiceType() + "Service.java");

                 Map<String, Object> root = new HashMap<String, Object>();
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

    public static void main(String[] args) throws Exception{
        String productZipPath = "E:\\QQ微信记录\\test_3297a84779f647a890beb65c3e4ab711_test.zip";
        //提取资源文件到当前目录
        extractResources();
        List<String> strings = DeviceProfileParser.unZipFiles("tmp\\generated-demo.zip", "");
        log.info("11111" + strings);
        ProductInfo productInfo = DeviceProfileParser.pareProductFile(productZipPath);
        productInfo.getDeviceCapability().getModel();
        generateService(productInfo);
        log.info("demo code generated to: " + new File("").getAbsolutePath() + "\\generated-demo");
        //删除临时文件
        deleteFile(new File("tmp\\"));
    }
}
