/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;

/**
 * 系统判断
 * @author Terran
 * @since  1.0
 */
public class SystemDecideUtil {

    /** WIN 正式文件存放地址*/
    public static final  String WIN_FORMAL_PATH = "C:\\lb-tmp\\formal\\";

    /** LINUX 正式文件存放地址*/
    public static final  String LINUX_FORMAL_PATH = "opt/lb-tmp/formal/";

    /** win 临时文件存放地址*/
    private static final String WIN_TEMPORARY_PATH = "C:\\lb-tmp\\temporary\\";

    /** linux 临时文件存放地址*/
    private static final String LINUX_TEMPORARY_PATH = "opt/lb-tmp/temporary/";

    private static final String WIN_PATH = "\\";

    private static final String LINUX_PATH = "/";

    private static final String OS_NAME = "os.name";

    private static final String WINDOWS = "WINDOWS";

    /**
     * 判断当前运行的操作系统
     * @return true -> win系统
     *         false -> linux/mac
     */
    public static boolean sysJudge(){
        return System.getProperty(OS_NAME).toUpperCase().indexOf(WINDOWS) >= 0 ? true : false;
    }

    /**
     * @param productZipPath 需要解析的ZIP文件
     * @return 返回临时存放地址
     */
    public static String sysTemporaryPath(String productZipPath) throws IOException {

        ZipFile zip = new ZipFile(productZipPath, Charset.forName("UTF-8"));
        String name;

        /** 结果: true -> win系统; false -> linux系统; */
        if (sysJudge()){

            name = zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));

            /** win 系统存放指定系统*/
            return WIN_TEMPORARY_PATH + name + WIN_PATH;
        }
            name = zip.getName().substring(zip.getName().lastIndexOf('/') + 1, zip.getName().lastIndexOf('.'));

            /** Linux 系统存放指定系统*/
        return LINUX_TEMPORARY_PATH + name + LINUX_PATH;
    }

}
