package com.zjw.plugin

import javassist.*
import javassist.bytecode.CodeAttribute
import javassist.bytecode.LocalVariableAttribute
import org.gradle.api.Project

public class MyInject {

    private static ClassPool pool = ClassPool.getDefault()
    static String CostTime = "CostTime";
    static String AppMethodTime = "AppMethodTime";
    static String AppMethodOrder = "AppMethodOrder";
    static String LogLevel = "e";

    // 存储文件列表
    private static ArrayList<String> fileList = new ArrayList<>();


    public
    static void injectDir(String androidJarPath, String path, String jarsPath, boolean useCostTime, boolean showLog) {
        //path is D:\GitBlit\AppMethodTime\app\build\intermediates\classes\debug
        pool.appendClassPath(path)
        pool.insertClassPath(androidJarPath)
        //编译顺序：先编译lib库再编译主项目
        //所以需要加载依赖的lib jar
        File libJarDir = new File(jarsPath)
        try {
            if (libJarDir.exists() && libJarDir.isDirectory()) {
                ArrayList<String> arr = getFile(libJarDir);
                for (String a : arr) {
                    pool.appendClassPath(a);
                }
            }
        } catch (Exception e) {

        }

        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件以及注解文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")
                        && !filePath.contains("CostTime")
                ) {
                    //  println("filePath is " + filePath);
                    String classPath = filePath.split("\\\\debug\\\\")[1]
                    String className = classPath.substring(0, classPath.length() - 6).replace('\\', '.').replace('/', '.')
                    //   println("className is " + className);
                    //开始修改class文件
                    CtClass c = pool.getCtClass(className)
                    if (c.isFrozen()) {
                        c.defrost()
                    }
                    // pool.importPackage(myPackageName)
                    //c.getMethod("setDname", "(Ljava/lang/String;)V") 指定函数名和参数获取函数对象
                    //遍历类的所有方法
                    CtMethod[] methods = c.getDeclaredMethods();
                    for (CtMethod method : methods) {
                        //println("method ====" + method.longName)
                        if (method.isEmpty() || Modifier.isNative(method.getModifiers())) {
                            //空函数体有可能是抽象函数以及接口函数或者native方法
                            return
                        }
                        if (useCostTime
                                && method.getAvailableAnnotations() != null
                                && method.getAvailableAnnotations().length >= 1
                                && "${method.getAvailableAnnotations()[0]}".contains(CostTime)) {
                            insertCostTimeCode(method, c, showLog)
                        } else if (!useCostTime) {
                            insertCostTimeCode(method, c, showLog)
                        }
                    }//END   for (CtMethod method : methods)
                    c.writeFile(path)
                    c.detach()
                }
            }
        }
    }

    private static void insertCostTimeCode(CtMethod method, CtClass c, boolean showLog) {
        if (showLog) {
            println("\n==================  InsertCostTimeCode Start =======================")
            println(method.longName + "{")
        }

        ArrayList<String> paramNameList = new ArrayList<>();
        try {
            //获取方法参数名称
            CodeAttribute codeAttribute = method.methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            String[] paramNames = new String[method.getParameterTypes().length];
            int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = attr.variableName(i + pos);
                paramNameList.add(attr.variableName(i + pos));
            }
        }catch (Exception e){
            if (showLog) {
                e.printStackTrace()
            }
        }

        def StringType = pool.getCtClass("java.lang.String");
        method.addLocalVariable("startTime", CtClass.longType);
        method.addLocalVariable("endTime", CtClass.longType);
        method.addLocalVariable("fullClassName", StringType);
        method.addLocalVariable("className", StringType);
        method.addLocalVariable("methodName", StringType);
        method.addLocalVariable("lineNumber", CtClass.intType);
        method.addLocalVariable("info", StringType);
        if (showLog) {
            println("   long startTime;")
            println("   long endTime;")
            println("   String fullClassName;")
            println("   String className;")
            println("   String methodName;")
            println("   String lineNumber;")
            println("   String info;")
        }

        def lineNumber = method.methodInfo.getLineNumber(0);
        //插入到函数第一句
        StringBuilder startInjectStr = new StringBuilder();
        startInjectStr.append("     startTime = System.nanoTime();\n");
        startInjectStr.append("     fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();\n");
        startInjectStr.append("     className = fullClassName.substring(fullClassName.lastIndexOf(\".\") + 1)+\".java\";\n");
        startInjectStr.append("     methodName = Thread.currentThread().getStackTrace()[2].getMethodName();\n");
        startInjectStr.append("     lineNumber = " + lineNumber + ";\n");
        startInjectStr.append("     info =\"===\"+startTime+\"===  \"+ fullClassName+\": \"+methodName + \" (\" + className + \":\"+ lineNumber + \")\";\n");
        startInjectStr.append("     android.util.Log.${LogLevel}(\"${AppMethodOrder}\",");
        startInjectStr.append("     info +\": ");
        for (int i = 0; i < paramNameList.size(); i++) {
            startInjectStr.append(" <${paramNameList.get(i)}: \"+\$" + (i + 1) + "+\"> ");
        }
        startInjectStr.append(" \"); ")
        startInjectStr.append("\n     Thread.dumpStack();");
        try {
            method.insertBefore(startInjectStr.toString())
        } catch (Exception e) {
            if (showLog) {
                e.printStackTrace()
            }
        }
        //  println("方法第一句插入了：" + startInjectStr.toString() + "语句")
        if (showLog) {
            println(startInjectStr.toString())
            println("   <<<==== original code ====>>>   ")
        }

        //插入到函数最后一句
        StringBuilder endInjectStr = new StringBuilder();
        endInjectStr.append("   endTime = System.nanoTime();\n");
        endInjectStr.append("   android.util.Log.${LogLevel}(\"${AppMethodTime}\",");
        endInjectStr.append("info + \": \" ");
        endInjectStr.append("+(endTime - startTime)*1.0f/1000000+\" (毫秒) return is \"+\$_ +\" ");
        for (int i = 0; i < paramNameList.size(); i++) {
            endInjectStr.append(" <${paramNameList.get(i)}: \"+\$" + (i + 1) + "+\"> ");
        }
        endInjectStr.append(" \"); ");
        // endInjectStr.append("\n     Thread.dumpStack();");
        try {
            method.insertAfter(endInjectStr.toString())
        } catch (Exception e) {
            if (showLog) {
                e.printStackTrace()
            }
        }
        if (showLog) {
            println(endInjectStr.toString())
            println("}");
            println("==================  InsertCostTimeCode End =======================\n")
        }
    }


    private static ArrayList<String> getFile(File path) throws IOException {
        File[] listFile = path.listFiles();
        for (File a : listFile) {
            if (a.isDirectory()) {
                // 递归调用getFile()方法
                getFile(new File(a.getAbsolutePath()));
            } else if (a.isFile() && a.absolutePath.endsWith(".jar")) {
                this.fileList.add(a.getAbsolutePath());
            }
        }
        return fileList;
    }


}