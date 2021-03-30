/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.lb.generator.component.*;
import org.lb.generator.component.attribute.DeviceCapability;
import org.lb.generator.component.attribute.DeviceService;
import org.lb.generator.component.attribute.ProductInfo;
import org.lb.generator.component.entity.GenerateEntity;
import org.lb.generator.component.profile.DeviceProfileParser;
import org.lb.generator.component.service.GenerateService;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件操作
 * @author Terran
 * @since  1.0
 */

@Slf4j
public class FileUtil {

    private static final String DEVICE_TYPE_CAPABILITY = "devicetype-capability";

    private static final String SERVICE_TYPE_CAPABILITY = "servicetype-capability";

    private static final int BUFFER = 1024;

    /**
     * 获取ZIP文件
     * @param productZipPath
     * @return 返回文件
     * @throws IOException
     */
    public static String getZipFile(String productZipPath) throws IOException {
        ZipFile zip = new ZipFile(productZipPath, Charset.forName("UTF-8"));
        if (SystemDecideUtil.sysJudge()){
            return zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().length());
        }else {
            return zip.getName().substring(zip.getName().lastIndexOf('/') + 1, zip.getName().length());
        }
    }

    /**
     * 获取文件夹
     * @param productZipPath
     * @return 返回文件
     * @throws IOException
     */
    public static String getZipFolder(String productZipPath) throws IOException {
        ZipFile zip = new ZipFile(productZipPath, Charset.forName("UTF-8"));
        if (SystemDecideUtil.sysJudge()){
            return zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));
        }else {
            return zip.getName().substring(zip.getName().lastIndexOf('/') + 1, zip.getName().lastIndexOf('.'));
        }
    }

    /**
     * 提取资源文件到当前目录
     * @param fileName 临时路径
     * @param file 文件夹
     * @throws IOException
     */
    public static void extractResources(String fileName,String file) throws IOException {

        /** 创建临时存放的地址 */
        boolean mkdirs = new File(fileName).mkdirs();
        if (!mkdirs){
            return;
        }

        /**
         * 提取資源文件到当前目录
         * CodeGenerator.class.getClassLoader().getResourceAsStream("lb-generator-demo.zip"); 获取classpath目录下 lb-generator-demo.zip文件。
         * OutputStream outputStream = new FileOutputStream("C:\lb-tmp\temporary\\lb-generator-demo.zip"); 创建 lb-generator-demo.zip 文件。
         * IOUtils.copy(inputStream,outputStream);  将inputStream流,复制到outputStream。
         * 同理
         * .....
         * */
        try (InputStream inputStream = CodeGenerator.class.getClassLoader().getResourceAsStream("lb-generator-demo.zip");
             OutputStream outputStream = new FileOutputStream(fileName + file)){
            IOUtils.copy(inputStream,outputStream);
        }

        try (InputStream inputStream = CodeGenerator.class.getClassLoader().getResourceAsStream("device.ftl");
             OutputStream outputStream = new FileOutputStream(fileName + "device.ftl")){
            IOUtils.copy(inputStream,outputStream);
        }

        try (InputStream inputStream = CodeGenerator.class.getClassLoader().getResourceAsStream("entity.ftl");
             OutputStream outputStream = new FileOutputStream(fileName + "entity.ftl")){
            IOUtils.copy(inputStream,outputStream);
        }
    }


    /**
     * 解析ZIP文件
     * @param zipFile 文件
     * @param descDir 解压文件
     * @throws IOException IO异常
     * @return 返回路径
     */
    public static List<String> unZipFiles(String zipFile, String descDir) throws IOException {

        /** 打开一个ZIP进行读取*/
        try (ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"))){

            /** 获取文件名 */
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
                try (InputStream in = zip.getInputStream(entry);FileOutputStream out = new FileOutputStream(outPath)) {
                    byte[] buf1 = new byte[BUFFER];
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
     * zip文件解析
     * @param zipFile 文件路径
     * @return 产品信息
     */
    public static ProductInfo pareProductFile(String zipFile, String fileName, String folder){
        ProductInfo productInfo = new ProductInfo();
        try {
            /** 读取设备能力及服务能力*/
            List<DeviceCapability> deviceCapabilities = null;
            Map<String, DeviceService> deviceServiceMap = new HashMap<String,DeviceService>(66);
            List<String> list = unZipFiles(zipFile,fileName);
            if (list != null){
                for (String outPath : list){
                    if (outPath == null){
                        continue;
                    }

                    /** 读取设备能力*/
                    if (outPath.contains(DEVICE_TYPE_CAPABILITY)){
                        deviceCapabilities = DeviceProfileParser.getDeviceCapability(outPath);
                    }

                    /** 读取服务能力*/
                    if (outPath.contains(SERVICE_TYPE_CAPABILITY)){
                        Map<String,DeviceService> deviceServiceMap1 = GenerateEntity.getEntityCapability(outPath,false);
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
     * 删除文件
     * @param file 文件
     */
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
                FileUtil.deleteFile(file1);
            }
        }

        file.delete();
    }

}
