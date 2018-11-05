package cn.com.egova.hello_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

//1.注册注解处理器(Processor导包一定不要导错，应该是 javax里的)
@AutoService(Processor.class)
//2.当前注解处理器能够处理的注解 代替 getSupportedAnnotationTypes函数
@SupportedAnnotationTypes({"cn.com.egova.hello_annotation.Route"})
//3.java版本 代替 getSupportedAnnotationTypes 函数
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouteProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "RouteProcessor=============init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "RouteProcessor=============process");
        for (TypeElement typeElement : set) {
            //创建类方法(method)
            MethodSpec route = MethodSpec.methodBuilder("route")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(String.class, "path")
                    .addStatement("$T.out.println($S)", System.class, "Hello, I am Route!")
                    .build();

            //创建类(class)
            TypeSpec router = TypeSpec.classBuilder("Router")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(route)
                    .build();

            //创建文件
            JavaFile javaFile = JavaFile.builder("cn.com.egova.apt", router)
                    .build();

            try {
                //输出文件
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
