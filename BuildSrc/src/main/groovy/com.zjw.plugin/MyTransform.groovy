package com.zjw.plugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class MyTransform extends Transform {
    Project project
    // 构造函数，我们将Project保存下来备用
    public MyTransform(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    // 将会在对应module的build\intermediates\transforms\目录下的生成MyTrans目录
    // 例如本案例(D:\GitBlit\AppMethodTime\app\build\intermediates\transforms\MyTrans)
    @Override
    public String getName() {
        return "MyTrans"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    // 指定Transform的作用范围
    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(@NonNull TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        if(!project.AppMethodTime.enabled){
            return;
        }
        def androidJarPath = getAndroidJarPath();

        String jarsDir
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        transformInvocation.inputs.each { TransformInput input ->
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件 输出表明 还包括了自建的依赖lib库的jar文件
                // （也就是主项目build.gradle中 dependencies下 compile的东西）
               // println("jarInput.file.getAbsolutePath() === " + jarInput.file.getAbsolutePath())
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                    println("jarName substring is "+jarName)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                //AppMethodTime\app\build\intermediates\transforms\MyTrans\debug\jars 拼凑这个目录
                jarsDir = dest.absolutePath.split("jars")[0]+"jars";
               // println("dest === " + dest.absolutePath)

                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }


            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等

                // directoryInput.file =============D:\GitBlit\AppMethodTime\app\build\intermediates\classes\debug
                MyInject.injectDir(androidJarPath,directoryInput.file.absolutePath,jarsDir,
                        project.AppMethodTime.useCostTime,project.AppMethodTime.showLog)
                // directoryInput.file =============D:\GitBlit\AppMethodTime\app\build\intermediates\classes\debug
                // dest.name =============bb2a44c10a4b1f1ea8a3f7b22453e3a96aa0d55d
                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                //println("directoryInput.file =============" + directoryInput.file);
               // println("dest.name =============" + dest.name);

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

        }
    }

    private String getAndroidJarPath() {
         def rootDir = project.rootDir
         def localProperties = new File(rootDir, "local.properties")
         def sdkDir = null;
         if (localProperties.exists()) {
             Properties properties = new Properties()
             localProperties.withInputStream { instr ->
                 properties.load(instr)
             }
             sdkDir = properties.getProperty('sdk.dir')
         }

          def platformsPath = sdkDir + File.separator + "platforms"

          def platformsFile = new File(platformsPath)

          if (platformsFile.exists() && platformsFile.isDirectory() && platformsFile.list().length >= 1) {
              return  platformsPath + File.separator +platformsFile.list().sort()[platformsFile.list().size()-1]+ File.separator +"android.jar"
          }

        return ""
    }

}