/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component;

import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.attribute.ProductInfo;
import org.lb.generator.component.entity.GenerateEntity;
import org.lb.generator.component.util.FileUtil;
import org.lb.generator.component.util.SystemDecideUtil;

import java.io.*;

/**
 * @author Terran
 * @since  1.0
 */
@Slf4j
public class CodeGenerator {


    /**
     * 生成maven项目
     * @param productZipPath 文件路径
     * @throws IOException IO异常
     * @return boolean 返回true成功,其它都是失败
     */
    public static boolean generateMavenProject(String productZipPath) throws Exception{

        /** 判断当前运行操作系统 -> 返回指定的临时路径*/
        String fileName = SystemDecideUtil.sysTemporaryPath(productZipPath);

        /** 获取文件*/
        String file = FileUtil.getZipFile(productZipPath);

        String folder = FileUtil.getZipFolder(productZipPath);

        /** 提取资源文件*/
        FileUtil.extractResources(fileName,file);

        FileUtil.unZipFiles(fileName + file, SystemDecideUtil.WIN_FORMAL_PATH);

        ProductInfo productInfo = FileUtil.pareProductFile(productZipPath,fileName,folder);

        GenerateEntity.createEntity(productInfo,fileName,folder);

        log.debug("demo code generated to:[{}\\lb-generated-demo]",new File("").getAbsolutePath());

        /** 删除临时文件 */
        FileUtil.deleteFile(new File(fileName));

        return true;
    }

    public static void main(String[] args) throws Exception{
        generateMavenProject("E:\\QQ微信记录\\DoorLock_3297a84779f647a890beb65c3e4ab711_ddd (1).zip");

    }
}
