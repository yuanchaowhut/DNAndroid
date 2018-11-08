package cn.com.egova.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.com.egova.annotation.Route;
import cn.com.egova.annotation.model.RouteMeta;
import cn.com.egova.compiler.utils.Consts;
import cn.com.egova.compiler.utils.Log;
import cn.com.egova.compiler.utils.Utils;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by yuanchao on 2018/11/7.
 */
/**
 * 处理器自动注册
 */
@AutoService(Processor.class)
/**
 * 处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()} 函数.
 */
@SupportedOptions(Consts.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 */
@SupportedAnnotationTypes(Consts.ANN_TYPE_ROUTE)
public class RouteProcessor extends AbstractProcessor {

    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;

    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;
    /**
     * 参数
     */
    private String moduleName;

    private Log log;

    /**
     * 初始化 从 {@link ProcessingEnvironment} 中获得一系列处理器工具
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        log = Log.newLog(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnvironment.getOptions();
        if(!Utils.isEmpty(options)){
            moduleName = options.get(Consts.ARGUMENTS_NAME);
        }
        log.i("RouteProcessor Parmaters:" + moduleName);
        if (Utils.isEmpty(moduleName)) {
            throw new RuntimeException("Not set Processor Parmaters.");
        }
    }

    /**
     * 相当于main函数，正式处理注解
     *
     * @param set              使用了支持处理注解  的节点集合
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(!Utils.isEmpty(set)){
            //被Route注解 注解的节点集合
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            if(!Utils.isEmpty(routeElements)){
                processRoute(routeElements);
            }
            return true;
        }
        return false;
    }



    /**
     * 处理 被注解了的 节点
     *
     * @param routeElements
     */
    private void processRoute(Set<? extends Element> routeElements) {
        //这里只处理Activity节点和IService节点
        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Consts.ACTIVITY);
        //暴露的服务
        TypeElement service = elementUtils.getTypeElement(Consts.ISERVICE);

        //通过循环遍历，最终完成 groupMap 集合的填充,辅助生成DNRouter$$Group$$XXX类.
        for (Element element : routeElements) {
            RouteMeta routeMeta;
            //类信息
            TypeMirror typeMirror = element.asType();
            log.i("Route class:" + typeMirror.toString());
            Route route = element.getAnnotation(Route.class);
            //只能在指定的类上面使用
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            } else if(typeUtils.isSubtype(typeMirror, service.asType())){
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, route, element);
            } else {
                throw new RuntimeException("[Just support] Activity Route：" + element);
            }
            // 检查是否配置 group 如果没有配置 则从path截取出组名
            categories(routeMeta);
        }
        //groupMap
        TypeElement iRouteGroup = elementUtils.getTypeElement(Consts.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Consts.IROUTE_ROOT);

        //生成 $$Group$$ 记录分组表
        generatedGroup(iRouteGroup);

        //生成 $$Root$$  记录路由表
        /**
         * 生成Root类 作用:记录 <分组，对应的Group类>
         */
        try {
            generatedRoot(iRouteRoot, iRouteGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否配置 group 如果没有配置 则从path截取出组名
     *
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            log.i("Group :" + routeMeta.getGroup() + " path=" + routeMeta.getPath());
            //分组与组中的路由信息
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            log.i("Group Info Error:" + routeMeta.getPath());
        }

    }

    /**
     * 验证地址合法性
     *
     * @param routeMeta
     * @return
     */
    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        // 必须以 / 开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置 我们从path中获得group
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出的group还是空
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }


    private void generatedGroup(TypeElement iRouteGroup) {
        //创建参数类型
//        Map<String, RouteMeta>
        ParameterizedTypeName parameterizedType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        log.i("generatedGroup");
        //创建参数
        //Map<String, RouteMeta> atlas
        ParameterSpec atlas = ParameterSpec.builder(parameterizedType, "atlas").build();

        //遍历分组 每一个分组 创建一个 $$Group$$ 类
        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            /**
             * @Override public void loadInto(Map<String, RouteMeta> atlas)
             */
            MethodSpec.Builder method = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(PUBLIC)
                    .returns(void.class)  //这一行可以不写，默认就是void
                    .addAnnotation(Override.class)
                    .addParameter(atlas);

            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                //添加函数体
                //atlas.put("/main/test", RouteMeta.build(RouteMeta.Type.ACTIVITY,SecondActivity.class, "/main/test", "main"));
                // $S = String
                // $T = Class 类
                // $L = 字面量
//                String.format()
                method.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),  //ACTIVITY 还是 ISERVICE 枚举
                        ClassName.get((TypeElement) routeMeta.getElement()),
                        routeMeta.getPath(),
                        routeMeta.getGroup());
            }
            //类名
            String groupClassName = Consts.NAME_OF_GROUP + groupName;
            //创建类
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(PUBLIC)
                    .addMethod(method.build())
                    .build();
            //生成java文件
            JavaFile javaFile = JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //完成rootMap的填充，用于辅助生成DNRouter$$Root$$tXXX类。
            rootMap.put(groupName, groupClassName);
        }

    }

    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) throws IOException {
        //类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                )
        );

        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec rootParamSpec = ParameterSpec.builder(routes, "routes")
                .build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder
                (Consts.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);

        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry
                    .getKey(), ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.getValue
                    ()));
        }
        //生成 $Root$类
        String rootClassName = Consts.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(iRouteRoot))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);

        log.i("Generated RouteRoot: " + Consts.PACKAGE_OF_GENERATE_FILE + "." + rootClassName);
    }
}
