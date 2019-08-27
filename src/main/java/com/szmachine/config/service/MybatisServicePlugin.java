package com.szmachine.config.service;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class MybatisServicePlugin extends PluginAdapter {
    private FullyQualifiedJavaType baseServiceType;
    private FullyQualifiedJavaType baseServiceImplType;
    //id类型，默认为Long
    private FullyQualifiedJavaType idType= new FullyQualifiedJavaType("java.lang.Long");
    private FullyQualifiedJavaType serviceImplType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType modelType;
    private FullyQualifiedJavaType listType;
    private FullyQualifiedJavaType autowired;
    private FullyQualifiedJavaType service;
    private FullyQualifiedJavaType returnType;
    private String servicePack;
    private String serviceImplPack;
    private String project;
    private String modelPack;

    public MybatisServicePlugin() {
        super();
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {
        this.baseServiceType =new FullyQualifiedJavaType (properties.getProperty("baseService"));
        this.baseServiceImplType = new FullyQualifiedJavaType (properties.getProperty("baseServiceImpl"));
        if (StringUtility.stringHasValue(properties.getProperty("idType"))){
            this.idType = new FullyQualifiedJavaType(properties.getProperty("idType"));
        }
        this.servicePack = properties.getProperty("targetPackage");
        this.serviceImplPack = properties.getProperty("implementationPackage");
        this.project = properties.getProperty("targetProject");
        this.modelPack = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        this.autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
        this.service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");

        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<>();
        String table = introspectedTable.getBaseRecordType();
        //表名
        String tableName = table.replaceAll(this.modelPack + ".", "");
        //service接口
        serviceType = new FullyQualifiedJavaType(servicePack + "." + tableName + "Service");

        //mapper接口
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        //serviceImpl类
        serviceImplType = new FullyQualifiedJavaType(serviceImplPack + "." + tableName + "ServiceImpl");

        //实体类
        modelType = new FullyQualifiedJavaType(modelPack + "." + tableName);

        listType = new FullyQualifiedJavaType("java.util.List");
        Interface service = new Interface(serviceType);
        TopLevelClass serviceImpl = new TopLevelClass(serviceImplType);

        // 接口
        createService(service,introspectedTable, tableName, files);
        // 实现类
        createServiceImpl(serviceImpl,introspectedTable, tableName, files);
        return files;
    }


    protected void createService(Interface interface1, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        //添加导入
        interface1.addImportedType(modelType);

        interface1.setVisibility(JavaVisibility.PUBLIC);
        //添加父接口
        interface1.addImportedType(baseServiceType);
        baseServiceType.addTypeArgument(modelType);
        baseServiceType.addTypeArgument(idType);
        interface1.addSuperInterface(baseServiceType);

        GeneratedJavaFile file = new GeneratedJavaFile(interface1, project,context.getJavaFormatter());
        files.add(file);
    }


    protected void createServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        //添加导入
        topLevelClass.addImportedType(daoType);
        topLevelClass.addImportedType(serviceType);
        topLevelClass.addImportedType(modelType);
        topLevelClass.addImportedType(service);
        topLevelClass.addImportedType(autowired);

        baseServiceImplType.addTypeArgument(modelType);
        baseServiceImplType.addTypeArgument(daoType);
        baseServiceImplType.addTypeArgument(idType);

        topLevelClass.setSuperClass(baseServiceImplType);

        // 继承service接口
        topLevelClass.addSuperInterface(serviceType);
        // 添加注解
        topLevelClass.addAnnotation("@Service");

        topLevelClass.addField(createMapperField());
        topLevelClass.addMethod(createGetMapper());

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, project, context.getJavaFormatter());
        files.add(file);
    }

    protected Field createMapperField() {
        Field field = new Field();
        field.setName(toLowerCase(daoType.getShortName())); // set var name
        field.setType(daoType); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation("@Autowired");

        return field;
    }

    protected Method createGetMapper() {
        Method method = new Method();
        method.setName("getMapper");
        method.setReturnType(daoType);
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addAnnotation("@Override");
        method.addBodyLine("return "+toLowerCase(daoType.getShortName())+";");
        return method;
    }

    protected String toLowerCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}

