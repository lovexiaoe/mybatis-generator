package com.szmachine.config.my;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

public class CustomJavaMapperMethodGenerator extends AbstractJavaMapperMethodGenerator {

    @Override
    public void addInterfaceElements(Interface interfaze) {
        countByCondition(interfaze);
        pageByCondition(interfaze);
        selectById(interfaze);
        updateById(interfaze);
    }

    // 通过id查找
    public void selectById(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType returnType = this.introspectedTable.getRules().calculateAllFieldsClass();
        method.setReturnType(returnType);
        importedTypes.add(returnType);
        method.setName("selectById");
        // 设置主键的类型为long，获取第一个主键名称，不能处理多主键
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(long.class.getName()),
                introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
        method.addParameter(parameter);

        this.addMapperAnnotations(interfaze, method);
        this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    //根据id更新
    public void updateById(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet();
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType());
        importedTypes.add(parameterType);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("updateById");
        method.addParameter(new Parameter(parameterType, "record"));
        this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    private void countByCondition(Interface interfaze) {
        // 先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        // 创建方法对象
        Method method = new Method();
        // 设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);
        // 设置返回类型
        method.setReturnType(new FullyQualifiedJavaType(long.class.getName()));
        // 设置方法名称为我们在IntrospectedTable类中初始化的 “countByCondition”
        method.setName("countByCondition");

        // 设置参数类型是对象
        FullyQualifiedJavaType parameterType;
        parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // import参数类型对象
        importedTypes.add(parameterType);
        String annotation="@Param(\"record\")";
        // 为方法添加参数，变量名称record
        method.addParameter(new Parameter(parameterType, "record",annotation));
        //
        addMapperAnnotations(interfaze, method);
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }



    // 根据条件分页
    private void pageByCondition(Interface interfaze) {
        // 先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        // 添加Lsit的包
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        // 添加Param注解导入
        importedTypes.add(new FullyQualifiedJavaType(org.apache.ibatis.annotations.Param.class.getName()));
        // 创建方法对象
        Method method = new Method();
        // 设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);
        // 设置返回类型是List
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType;
        // 设置List的类型是实体类的对象
        listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(listType);
        // 返回类型对象设置为List
        returnType.addTypeArgument(listType);
        // 方法对象设置返回类型对象
        method.setReturnType(returnType);
        // 设置方法名称为我们在IntrospectedTable类中初始化的 “pageByCondition”
        method.setName("pageByCondition");

        // 设置参数类型是对象
        FullyQualifiedJavaType parameterType;
        parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // import参数类型对象
        importedTypes.add(parameterType);
        String annotation="@Param(\"record\")";
        // 为方法添加参数，变量名称record
        method.addParameter(new Parameter(parameterType, "record",annotation));

        // page参数
        parameterType=FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(parameterType);
        annotation="@Param(\"page\")";
        method.addParameter(new Parameter(parameterType, "page",annotation));

        // limit参数
        parameterType=FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(parameterType);
        annotation="@Param(\"limit\")";
        method.addParameter(new Parameter(parameterType, "limit",annotation));

        // orderBy参数
        parameterType=FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(parameterType);
        annotation="@Param(\"orderBy\")";
        method.addParameter(new Parameter(parameterType, "orderBy",annotation));

        // orderType参数
        parameterType=FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(parameterType);
        annotation="@Param(\"orderType\")";
        method.addParameter(new Parameter(parameterType, "orderType",annotation));

        //
        addMapperAnnotations(interfaze, method);
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
    }
}