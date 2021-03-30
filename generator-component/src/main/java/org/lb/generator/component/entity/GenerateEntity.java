package org.lb.generator.component.entity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lb.generator.component.attribute.DeviceService;
import org.lb.generator.component.attribute.ProductInfo;
import org.lb.generator.component.attribute.ServiceProperty;
import org.lb.generator.component.util.SystemDecideUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GenerateEntity
 * @Description TODO
 * @Author Terran
 * @Date 2021/3/22 10:38
 * @Version 1.0
 */

@Slf4j
public class GenerateEntity {

    private static final String SERVICES_TITLE = "services";

    /***
     * 创建Entity层
     * @param productInfo 信息表
     * @param fileName 全路径
     * @param folder 项目名称
     * @throws IOException io错误
     * @throws TemplateException template获取失败
     */
    public static void createEntity(ProductInfo productInfo, String fileName, String folder) throws IOException, TemplateException{
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
        configuration.setDirectoryForTemplateLoading(new File(fileName));
        configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_25));
        Template template = configuration.getTemplate("entity.ftl");
        try {
            for (String sid : productInfo.getServiceCapabilityMap().keySet()){
                DeviceService deviceService = productInfo.getServiceCapabilityMap().get(sid);
                File file = FileUtils.getFile(
                        SystemDecideUtil.WIN_FORMAL_PATH + folder + "/lb-generator-demo/src/main/java/com/lb/generator/entity/"
                                + deviceService.getServiceType() + "Model.java");
                Map<String, Object> root = new HashMap<String, Object>(66);
                root.put("service", deviceService);

                /** 构造给定文件对象的FileWriter对象: 要写入的文件对象。*/
                try (Writer javaWriter = new FileWriter(file);){
                    template.process(root, javaWriter);
                    javaWriter.flush();
                    log.info("文件生成路径：" + file.getCanonicalPath());
                }

            }
        }catch (IOException | TemplateException e){
            log.error("generateService error" + e.getMessage());
            throw e;
        }
    }

    /**
     * 获取产品能力
     * @param filePath 文件路径
     * @param checkForDataType 数据类型
     * @return
     */
    public static Map<String,DeviceService> getEntityCapability(String filePath, boolean checkForDataType){
        if (filePath == null){
            log.debug("服务路径为空");
            return null;
        }
        JsonFactory factory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(factory);
        File file = new File(filePath);

        TypeReference<HashMap<String, List<DeviceService>>> typeReference = new TypeReference<HashMap<String, List<DeviceService>>>() {
        };
        HashMap<String,List<DeviceService>> hm = null;
        Map<String, DeviceService> serviceCapabilityMap = new HashMap<String, DeviceService>(66);
        try {
            hm = objectMapper.readValue(file,typeReference);
            if (hm == null){
                log.debug("hm为空，读取设备功能失败。");
                return serviceCapabilityMap;
            }
            List<DeviceService> list = hm.get(SERVICES_TITLE);
            for (DeviceService deviceService : list){
                for (ServiceProperty property : deviceService.getProperties()){

                    /***
                     * 创建实体属性
                     * int|long|decimal|string|DateTime|jsonObject|enum|boolean|string
                     */

                    String dataType = property.getDataType();
                    if (dataType.equalsIgnoreCase("int")) {
                        property.setJavaType("int");
                        property.setVal("random.nextInt(100)");
                    } else if (dataType.equalsIgnoreCase("long")) {
                        property.setJavaType("long");
                        property.setVal("random.nextInt(100)");
                    } else if (dataType.equalsIgnoreCase("boolean")) {
                        property.setJavaType("boolean");
                        property.setVal("true");
                    } else if (dataType.equalsIgnoreCase("decimal")) {
                        property.setJavaType("double");
                        property.setVal("random.nextFloat()");
                    } else {
                        property.setJavaType("String");
                        property.setVal("\"hello\"");
                    }
                    if (property.getMethod() != null && property.getMethod().contains("W")) {
                        property.setWriteable("true");
                    } else {
                        property.setWriteable("false");
                    }
                }

                serviceCapabilityMap.put(deviceService.getServiceType(), deviceService);
            }
            return serviceCapabilityMap;
        }catch (Exception e){
            log.error("设备读取失败【{}】",e.getMessage());
        }
        return null;
    }
}
