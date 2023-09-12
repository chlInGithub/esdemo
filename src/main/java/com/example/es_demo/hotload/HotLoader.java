package com.example.es_demo.hotload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import cn.hutool.core.util.ZipUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
//import xyz.erupt.core.service.EruptCoreService;

@Component
public class HotLoader {

    private final static String packageStr = "package xyz.erupt.flow.model.autoload;";
    public final static String packagePath = "/xyz/erupt/flow/model/autoload";
    public final static String packageDotPaht = "xyz.erupt.flow.model.autoload.";

    private static String hotCodeDir = "/opt/erupt-flow-hotCode";
    private static String hotCodePackageRealPath;
    private static String hotCodeRealPath;

    public static synchronized String codeRootDir() throws IOException {
        if (StringUtils.isNotBlank(hotCodeRealPath)) {
            return hotCodeRealPath;
        }
        File file = new File(hotCodeDir);
        FileUtils.forceMkdir(file);
        hotCodeRealPath = file.getAbsolutePath();
        System.out.println("HotLoader hotCodeRealPath " + hotCodeRealPath);
        return hotCodeRealPath;
    }

    public static synchronized String codeDir() throws IOException {
        if (StringUtils.isNotBlank(hotCodePackageRealPath)) {
            return hotCodePackageRealPath;
        }
        // 配置 动态编译的class的存放路径
        String path = codeRootDir() + packagePath;
        File file = new File(path);
        FileUtils.forceMkdir(file);
        hotCodePackageRealPath = file.getAbsolutePath();
        System.out.println("HotLoader hotCodePackageRealPath" + hotCodePackageRealPath);
        return hotCodePackageRealPath;
    }

    /**
     * copy
     * @param fileName
     * @param sourceJava
     * @return
     * @throws IOException
     */
    public static File saveToCodeDir(String fileName, File sourceJava) throws IOException {
        File desFile = new File(codeDir() + "/" + (StringUtils.isBlank(fileName) ? sourceJava.getName() : fileName));
        FileUtils.copyFile(sourceJava, desFile);
        return desFile;
    }

    /**
     * 写入
     * @param fileName
     * @param content
     * @return
     * @throws IOException
     */
    public static File saveToCodeDir(String fileName, String content) throws IOException {
        File desFile = new File(codeDir() + "/" + fileName);
        FileUtils.write(desFile, content, "utf-8");
        return desFile;
    }

    public static void addPackage(File javaFile) throws IOException {
        String chartset = "utf-8";
        List<String> oldLines = FileUtils.readLines(javaFile, chartset);
        oldLines = oldLines.stream().filter(line -> !line.trim().startsWith("package")).collect(Collectors.toList());
        List<String> lines = new ArrayList<>();
        lines.add(packageStr);
        lines.addAll(oldLines);
        FileUtils.writeLines(javaFile, lines,  false);
    }

    final static String insert = "dataProxy = xyz.erupt.flow.model.dataProxy.EruptFlowApplyInfoDataProxy.class";

    public static void addPackageAndDataProxy(File javaFile) throws IOException {
        String chartset = "utf-8";
        List<String> oldLines = FileUtils.readLines(javaFile, chartset);
        oldLines = oldLines.stream().filter(line -> !line.trim().startsWith("package")).map(line -> {
            if (!line.trim().startsWith("@EruptField") && line.trim().startsWith("@Erupt")) {
                int i = line.indexOf("(");
                if (i != -1) {
                    String before = line.substring(0, i + 1);
                    String after = line.substring(i + 1);
                    line = before.concat(insert);
                    if (!after.startsWith(")")) {
                        line = line.concat(",");
                    }
                    line = line.concat(after);
                }else {
                    line = line.concat("(").concat(insert).concat(")");
                }
            }
            return line;
        }).collect(Collectors.toList());
        List<String> lines = new ArrayList<>();
        lines.add(packageStr);
        lines.addAll(oldLines);
        FileUtils.writeLines(javaFile, lines,  false);
    }

    public static boolean runtimeCompile(File javaFile) throws IOException {
        String clazzPath = codeDir()+"/"+ javaFile.getName().split("\\.")[0]+".class";
        FileWriter fileWriter = new FileWriter(clazzPath, false);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
        Iterable units = fileMgr.getJavaFileObjects(javaFile);
        JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
        Boolean call = t.call();
        fileMgr.close();
        fileWriter.flush();
        fileWriter.close();
        return call;
    }

    static ClassLoader getAppClassLoader(ClassLoader classLoader) {
        if (Objects.isNull(classLoader)) {
            return null;
        }
        if ("sun.misc.Launcher$AppClassLoader".equals(classLoader.getClass().getName())) {
            return classLoader;
        }
        return getAppClassLoader(classLoader.getParent());
    }

    /**
     * 向当前classLoader添加扫描范围
     * @throws Exception
     */
    static ClassLoader addUrlAndGetClassLoader() throws Exception {
        ClassLoader contextClassLoader = getAppClassLoader(Thread.currentThread().getContextClassLoader());
        if (Objects.isNull(contextClassLoader)) {
            throw new RuntimeException("not fund AppClassLoader");
        }

        URLClassLoader urlClassLoader = (URLClassLoader) contextClassLoader;
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        URL url = (new File(codeRootDir())).toURI().toURL();
        addURL.invoke(urlClassLoader, url);
        return contextClassLoader;
    }

    private static ClassLoader classLoader;
    public static synchronized ClassLoader classLoader() throws Exception {
        if (null == classLoader) {
            classLoader = addUrlAndGetClassLoader();
            //ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            //classLoader = URLClassLoader.newInstance(new URL[]{new File(codeRootDir()).toURI().toURL()}, contextClassLoader);
            //classLoader = contextClassLoader;
        }
        return classLoader;
    }

    /**
     * @param javaName java simple name 不包括package
     * @return java全路径，如x.x.x.x.javaName
     */
    public static String packageClassPath(String javaName) {
        String result = packageDotPaht + javaName;
        return result;
    }

    /**
     * @param className x.x.x.name
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class loadClass(String className) throws Exception {
        System.out.println("loadClass " + className);
        Class<?> loadClass = classLoader().loadClass(className);
        return loadClass;
    }

    public static void genCLassFile(String name, byte[] bytes) throws IOException {
        String classFileName = codeDir() + "/" + name + ".class";
        File file = new File(classFileName);
        FileUtils.forceDeleteOnExit(file);
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    public static byte[] getClassContent(String name) throws IOException {
        String classFileName = codeDir() + "/" + name + ".class";
        File file = new File(classFileName);
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return bytes;
    }

    @AllArgsConstructor
    @Data
    public static class JavaAndClassContent{
        String javaContent;
        byte[] classContent;
    }

    public static JavaAndClassContent compileAndGetClassBytes(String fileName, File sourceJava) throws IOException {
        File file = saveToCodeDir(fileName, sourceJava);
        addPackage(file);
        runtimeCompile(file);
        String className = codeDir() + "/" + file.getName().split("\\.")[ 0 ] + ".class";
        File classFile = FileUtils.getFile(className);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileUtils.copyFile(classFile, outputStream);
        byte[] bytes = outputStream.toByteArray();
        String javaContent = FileUtils.readFileToString(file, "utf-8");
        return new JavaAndClassContent(javaContent, bytes);
    }

    public static byte[] compileAndGetClassBytes(String fileName, String content) throws IOException {
        File file = saveToCodeDir(fileName, content);
        //addPackage(file);
        runtimeCompile(file);
        String className = codeDir() + "/" + file.getName().split("\\.")[ 0 ] + ".class";
        File classFile = FileUtils.getFile(className);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileUtils.copyFile(classFile, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return bytes;
    }

    public static void registerErupt(String javaName, byte[] classContent) throws Exception {
        if (StringUtils.isBlank(javaName) || ArrayUtils.isEmpty(classContent)) {
            return;
        }
        HotLoader.genCLassFile(javaName, classContent);
        Class clazz = HotLoader.loadClass(HotLoader.packageClassPath(javaName));
        // 注册到erupt,重复注册会报错，所以先删除。
        //EruptCoreService.unregisterErupt(clazz);
        //EruptCoreService.registerErupt(clazz);
    }

    /**
     * @return package的所在jar的绝对路径
     * @param packagePath 如xyz/erupt/flow
     * @throws IOException
     */
    public static String getPackagePath(String packagePath) throws IOException {
        ClassLoader cl = HotLoader.class.getClassLoader();
        Enumeration resourceUrls = cl != null ? cl.getResources(packagePath) : ClassLoader.getSystemResources(packagePath);
        String urlPath = null;
        //if (resourceUrls.hasMoreElements()) {
            URL url = (URL)resourceUrls.nextElement();
        if (null != url) {
            urlPath = url.getPath();
            System.out.println(urlPath);
            int i = urlPath.indexOf("file:/");
            if (i >= 0){
                i += 6;
                urlPath = urlPath.substring(i);
            }
            i = urlPath.indexOf("jar!");
            if (i >= 0) {
                i += 3;
                urlPath = urlPath.substring(0, i);
            }
        }
        //}
        return urlPath;
    }

    /**
     * @param zipPath  压缩包绝对路径  如D:/work/m2/xyz/erupt/erupt-generator/1.10.14/erupt-generator-1.10.14.jar
     * @return
     */
    public static String unzip(String zipPath){
        File unzip = ZipUtil.unzip(zipPath);
        System.out.println(unzip.getAbsolutePath());
        String absolutePath = unzip.getAbsolutePath();
        return absolutePath;
    }

    /**
     * @param unZipPath 压缩包解压后的绝对路径 如D:/work/m2/xyz/erupt/erupt-generator/1.10.14/erupt-generator-1.10.14
     * @param packageStr 如/xyz/erupt/generator
     * @param javaName 如 test
     * @param bytes 文件内容
     */
    public static void addFile2ZipPath(String unZipPath, String packageStr, String javaName, byte[] bytes)
            throws IOException {
        FileUtils.writeByteArrayToFile(new File(unZipPath + packageStr + "/" + javaName + ".class"), bytes);
    }

    /**
     * @param unZipPath 压缩包解压后的绝对路径 如D:/work/m2/xyz/erupt/erupt-generator/1.10.14/erupt-generator-1.10.14
     * @param zipPath 压缩包绝对路径  如D:/work/m2/xyz/erupt/erupt-generator/1.10.14/erupt-generator-1.10.14.jar
     */
    public static void zip(String unZipPath, String zipPath) {
        File zip = ZipUtil.zip(unZipPath, zipPath, true);
    }

    public static void jar(String unZipPath, String zipPath) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        String commond = "jar -cf " + zipPath + " -C " + unZipPath + " .";
        System.out.println(commond);
        Process exec = runtime.exec(commond);
        exec.waitFor();
        System.out.println("end waitfor");
    }
}
