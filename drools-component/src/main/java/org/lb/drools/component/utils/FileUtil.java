/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author Terran
 * @since 1.0
 */
@Slf4j
public class FileUtil {

    public static final String TEMP_DIR;

    static {
        boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
        if (isLinux){
            TEMP_DIR = "/tmp";
        }else {
            Map<String,String> map = System.getenv();
            String win = map.get("TEMP");
            if (win != null && win.length()>0){
                TEMP_DIR = map.get("TEMP");
            }else{
                TEMP_DIR = "temp";
            }
        }
    }

    /**
     * 整合所有文件,到所传的LIST中
     * @param path 可能包含有classPath形式,因此需要进行判断
     * @param fileList {@code List<File>} 所传入的FileList
     */
    public static void fileList(String path, List<File> fileList){
        if (path.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)){
            try {
                createTempDir();
                ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resourcePatternResolver.getResources(path);
                for (Resource resource : resources){
                    fileList.add(copyResourceToTempFile(resource));
                }
            }catch (IOException e){
                log.error("Resource read exception,[{}]",e.getMessage());
            }
        }else {
            File file = new File(path);
            if (file.exists()){
                if (!file.isDirectory()){
                    fileList.add(file);
                }
                if (file.isDirectory()){
                    File[] files = file.listFiles();
                    if (files != null){
                        for (File nextFile : files){
                            fileList(nextFile.getPath(),fileList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将资源文件转为临时文件
     * @param resource 资源目录下的文件，需要copy出来，因为打包后可能导致资源路径不正确
     * @return {@code File} 资源目录对应的文件，但不是实际文件，是拷贝的。
     */
    public static File copyResourceToTempFile(Resource resource){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File targetFile = null;
        try {
            inputStream = resource.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            int readNum = inputStream.read(bytes);
            targetFile = new File(TEMP_DIR + File.separator + resource.getFilename());
            outputStream = new FileOutputStream(targetFile);
            outputStream.write(bytes);
        }catch (Exception e){
            log.error("Resource integration failure,[{}]",e.getMessage());
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                log.error("cannot close file,[{}]",e.getMessage());
            }
        }
        return targetFile;
    }

    /**
     * 创建临时目录文件夹
     */
    public static void createTempDir(){
        File file = new File(TEMP_DIR);
        if (!file.exists()){
            file.mkdir();
        }
    }

}
