package org.lb.drools.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.event.DefaultProcessEventListener;
import org.drools.core.event.DefaultRuleRuntimeEventListener;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.lb.drools.component.common.KieAccessor;
import org.lb.drools.component.util.FileUtil;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static org.lb.drools.component.common.Constants.*;

/**
 * @ClassName KieTemplate
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 11:07
 * @Version 1.0
 */
@Slf4j
public class KieTemplate extends KieAccessor implements BeanClassLoaderAware {

    /** 使用分布式：Redis等；没有使用则使用本地缓存*/
    public Map<String,String> CACHE_RULE = new ConcurrentHashMap<String, String>();

    public static Map<String, BlockingQueue<KieSession>> SESSION = new ConcurrentHashMap<String, BlockingQueue<KieSession>>();

    public static Map<String,BlockingQueue<KieBase>> BASE = new ConcurrentHashMap<String, BlockingQueue<KieBase>>();

    private ClassLoader classLoader;

    @Override
    public void afterPropertiesSet()throws Exception{
        super.afterPropertiesSet();
        if (classLoader == null){
            classLoader = getClass().getClassLoader();
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 根据文件名获取KieSession
     * @param fileName 文件名，可以输入多个(需要带后缀)
     * @return KieSession
     */
    public KieSession getKieSession(String... fileName){
        List<String> ds = new ArrayList<String>();
        for (String name : fileName){
            String content = CACHE_RULE.get(name);
            if (StringUtils.isBlank(content)){
                ds = doReadTemp(fileName);
                return decodeToSession(ds.toArray(new String[]{}));
            }
            ds.add(CACHE_RULE.get(name));
        }
        return decodeToSession(ds.toArray(new String[]{}));
    }

    /**
     * 规则文件，决策表解析成字符串
     * @param realPath  决策表路径
     * @return  字符串
     */
    public String encodeToString(String realPath) {
        File file = new File(realPath);
        if (!file.exists()) {
            return null;
        }
        // drl文件
        if (realPath.endsWith(SUFFIX_DRL)) {
            return read(file);
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("file not fount.");
        }
        // excel文件 xls和xlsx都支持
        if (realPath.endsWith(SUFFIX_EXCEL)||realPath.endsWith(SUFFIX_EXCEL_2007)) {
            return new SpreadsheetCompiler().compile(is, InputType.XLS);
        }
        // csv文件
        if (realPath.endsWith(SUFFIX_CSV)) {
            return new SpreadsheetCompiler().compile(is, InputType.CSV);
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.error("close stream error=>", e);
            }
        }
        return "package src.main.resources;";
    }

    /**
     * 读取drl文件
     */
    private String read(File file) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 把字符串解析成KieSession
     * @param drl 规则文件字符串
     * @return KieSession
     */
    public KieSession decodeToSession(String... drl) {
        KieHelper kieHelper = new KieHelper();
        for (String drools : drl){
            kieHelper.addContent(drools, ResourceType.DRL);
        }
        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.WARNING,Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING,Message.Level.ERROR);
            for (Message message : messages){
               log.error("ERROR:{}",message.getText());
            }
            throw new IllegalStateException("Compilation error.");
        }
        KieBaseConfiguration kieBaseConfiguration = kieHelper.ks.newKieBaseConfiguration();
        if (EventProcessingOption.STREAM.getMode().equals(getMode())){
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
        }else {
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
        }
        KieBase kieBase = kieHelper.build(kieBaseConfiguration);
        KieSession kieSession = kieBase.newKieSession();
        if (getListener() == null || !LISTENER_CLOSE.equalsIgnoreCase(getMode())){
            kieSession.addEventListener(new DefaultRuleRuntimeEventListener());
            kieSession.addEventListener(new DefaultAgendaEventListener());
            kieSession.addEventListener(new DefaultProcessEventListener());
        }
        return kieSession;
    }

    /**
     * 获取绝对路径下的规则文件对应的KieBase
     * @param classPath 绝对路径/文件目录
     * @return KieBase
     * @throws Exception
     */
    public KieBase getKieBase(String classPath) throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        Resource resource = ResourceFactory.newFileResource(classPath);
        kieFileSystem.write(resource);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() > 0){
            throw new Exception();
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        return kieContainer.getKieBase();
    }

    /**
     * 私有,do开头,0结尾的方法全部为私有
     */
    public void doRead0(){
        /** 存入1级缓存*/
        String pathTotal = getPath();
        if (pathTotal == null || pathTotal.length() == 0){
            return;
        }
        String[] pathArray = pathTotal.split(KieAccessor.PATH_SPLIT);
        List<File> fileList = new ArrayList<>();
        for (String path : pathArray){
            FileUtil.fileList(path,fileList);
        }
        for (File file : fileList){
            String fileName = file.getName();
            String content = encodeToString(file.getPath());
            CACHE_RULE.put(fileName,content);
        }

        /** redis【2级缓存】*/
    }

    private List<String> doReadTemp(String... fileName){
        /** 转换成集合*/
        List<String> stringList = Arrays.asList(fileName);
        /** 存放临时规则文件*/
        List<String> strings = new ArrayList<>();
        /** 存入1级缓存*/
        String pathTotal = getPath();
        Assert.notNull(pathTotal,"path must be not null");
        String[] pathArray = pathTotal.split(KieAccessor.PATH_SPLIT);
        List<File> fileList = new ArrayList<>();
        for (String path : pathArray){
            FileUtil.fileList(path,fileList);
        }
        for (File file : fileList){
            if (stringList.contains(file.getName())){
                String content = encodeToString(file.getPath());
                strings.add(content);
                CACHE_RULE.put(file.getName(),content);
            }
        }
        return strings;
    }
}
