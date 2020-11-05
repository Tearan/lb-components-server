package org.lb.generator.component.ProductParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @ClassName DeviceProfileParser
 * @Description 设备配置文件分析器
 * @Author Terran
 * @Date 2020/11/3 23:21
 * @Version 1.0
 */
@Slf4j
public class DeviceProfileParser {

    private static final String DEVICETYPE_CAPABILITY = "devicetype-capability";

    private static final String SERVICETYPE_CAPABILITY = "servicetype-capability";

    private static final String DEVICES_TITLE = "devices";

    private static final String SERVICES_TITLE = "services";

    private static final int BUFFER = 1024;

    private static final long MAX_FILE_NUM = 1000;

    private static final long MAX_PACKAGE_SIZE = 4 * 1024 * 1024;

    /**
     * zip文件解析
     * @param zipFile
     * @return
     */
    public static ProductInfo pareProductFile(String zipFile){
        ProductInfo productInfo = new ProductInfo();
        try {
            /** 读取设备能力及服务能力*/
            List<DeviceCapability> deviceCapabilities = null;
            Map<String,DeviceService> deviceServiceMap = new HashMap<String,DeviceService>();
            List<String> list = unZipFiles(zipFile,"tmp\\");
            if (list != null){
                for (String outPath : list){
                    if (outPath == null){
                        continue;
                    }

                    /** 读取设备能力*/
                    if (outPath.contains(DEVICETYPE_CAPABILITY)){
                        deviceCapabilities = getDeviceCapability(outPath);
                    }
                    /** 读取服务能力*/
                    if (outPath.contains(SERVICETYPE_CAPABILITY)){
                        Map<String,DeviceService> deviceServiceMap1 = getServiceCapability(outPath,false);
                        if (deviceServiceMap1 != null){
                            deviceServiceMap.putAll(deviceServiceMap1);
                        }
                    }
                }
            }
            productInfo.setDeviceCapability(deviceCapabilities.get(0));
            productInfo.setServiceCapabilityMap(deviceServiceMap);

        }catch (Exception e){
            log.error("ZIP文件读取异常【{}】",e.getMessage());
        }
        return productInfo;
    }

    /**
     * 解析ZIP文件
     * @param zipFile
     * @param descDir
     * @throws IOException
     */
    public static List<String> unZipFiles(String zipFile, String descDir) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"))){
            String name = zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));
            List<String> files = new ArrayList<>();
            File pathFile = new File(descDir + name);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                String zipEntryName = entry.getName();
                String outPath = (descDir + name + "/" + zipEntryName).replaceAll("\\*", "/");
                /** 判断路径是否存在,不存在则创建文件路径*/
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                /** 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压*/
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                try (InputStream in = zip.getInputStream(entry);
                     FileOutputStream out = new FileOutputStream(outPath)) {
                    byte[] buf1 = new byte[1024];
                    int len;
                    while ((len = in.read(buf1)) > 0) {
                        out.write(buf1, 0, len);
                    }
                    files.add(outPath);
                }
            }
            return files;
        }
    }

    /**
     * 设备能力
     * @param filePath
     * @return
     */
    private static List<DeviceCapability> getDeviceCapability(String filePath){
        if (filePath == null){
            log.debug("设备功能路径为空");
            return null;
        }

        JsonFactory factory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(factory);
        File file = new File(filePath);
        TypeReference<HashMap<String,List<DeviceCapability>>> typeReference = new TypeReference<HashMap<String, List<DeviceCapability>>>() {
        };
        HashMap<String,List<DeviceCapability>> hm = null;
        try {
            hm = objectMapper.readValue(file,typeReference);
            if (hm == null){
                log.debug("hm为空，读取设备功能失败。");
                return null;
            }
            return hm.get(DEVICES_TITLE);
        }catch (Exception e){
            log.error("设备读取失败【{}】",e.getMessage());
        }
        return null;
    }

    private static Map<String,DeviceService> getServiceCapability(String filePath, boolean checkForDataType){
        if (filePath == null){
            log.debug("服务路径为空");
            return null;
        }
        JsonFactory factory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        TypeReference<HashMap<String,List<DeviceService>>> typeReference = new TypeReference<HashMap<String, List<DeviceService>>>() {
        };
        HashMap<String,List<DeviceService>> hm = null;
        Map<String, DeviceService> serviceCapabilityMap = new HashMap<String, DeviceService>();
        try {
            hm = objectMapper.readValue(file,typeReference);
            if (hm == null){
                log.debug("hm为空，读取设备功能失败。");
                return serviceCapabilityMap;
            }
            List<DeviceService> list = hm.get(SERVICES_TITLE);
            for (DeviceService deviceService : list){
                for (ServiceProperty serviceProperty : deviceService.getProperties()){
                    String dataType = serviceProperty.getDataType();
                    /***
                     * 创建实体属性
                     * int|long|decimal|string|DateTime|jsonObject|enum|boolean|string
                     */
                    if (dataType.equalsIgnoreCase("tinyint")){
                        serviceProperty.setJavaType("Integer");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("smallint")){
                        serviceProperty.setJavaType("Integer");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("mediumint")){
                        serviceProperty.setJavaType("Integer");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("int")){
                        serviceProperty.setJavaType("Integer");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("Integer")){
                        serviceProperty.setJavaType("Integer");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("bigint")){
                        serviceProperty.setJavaType("Long");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("Long")){
                        serviceProperty.setJavaType("Long");
                        serviceProperty.setVal("random.nextInt(100)");
                    }else if (dataType.equalsIgnoreCase("decimal")){
                        serviceProperty.setJavaType("double");
                        serviceProperty.setVal("random.nextFloat()");
                    }else if (dataType.equalsIgnoreCase("boolean")){
                        serviceProperty.setJavaType("boolean");
                        serviceProperty.setVal("true");
                    }else {
                        serviceProperty.setJavaType("String");
                        serviceProperty.setVal("\"hello\"");
                    }
                    if (serviceProperty.getMethod() != null && serviceProperty.getMethod().contains("W")) {
                        serviceProperty.setWriteable("true");
                    } else {
                        serviceProperty.setWriteable("false");
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

    private static String getPathName(String path){
        String name = "";
        path = Normalizer.normalize(path, Form.NFKC);
        int index = path.lastIndexOf("/");
        if (-1 == index){
            return name;
        }
        name = path.substring(0,index + 1);
        return name;
    }

}
