package com.zjw.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project> {

    void apply(Project project) {

        //注册build.gradle中
        project.extensions.create('AppMethodTime', MyCustomPluginExtension)

      /*  project.task('appPlugin') << {
            project.pluginsrc.cost
        }*/

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new MyTransform(project))
    }
}