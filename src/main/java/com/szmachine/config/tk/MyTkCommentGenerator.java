package com.szmachine.config.tk;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;
import tk.mybatis.mapper.generator.MapperCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @Description: 注释自定义
 * @Author: zhaoyu
 * @Date 2019/7/29
 */
public class MyTkCommentGenerator extends MapperCommentGenerator {
    private Properties properties = new Properties();
    private String author="MBG";
    private boolean suppressDate = false;
    private boolean suppressAllComments = false;
    private boolean addRemarkComments = false;
    private SimpleDateFormat dateFormat;
    private String dateStr;

    public MyTkCommentGenerator() {

    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.suppressDate = StringUtility.isTrue(properties.getProperty("suppressDate"));
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
        this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
        String dateFormatString = properties.getProperty("dateFormat");
        if (StringUtility.stringHasValue(dateFormatString)) {
            this.dateFormat = new SimpleDateFormat(dateFormatString);
            dateStr=dateFormat.format(new Date());
        }else {
            this.dateFormat = new SimpleDateFormat("yyyy-MM-DD");
            dateStr=dateFormat.format(new Date());
        }
        String author = properties.getProperty("author");
        if (StringUtility.stringHasValue(author)) {
            this.author = author;
        }

    }

    /**
     *  @Description: java文件注释，会放在java文件最顶部，package之上。
     *  @Author: zhaoyu
     *  @Date: 2018/12/30
     */
    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    /**

     *  @Description: xml 根注释
     *  @Author: zhaoyu
     *  @Date: 2018/12/30
     */
    public void addRootComment(XmlElement rootElement) {
        if (!this.suppressAllComments) {
            rootElement.addElement(new TextElement("<!-- "+" @Author: "+author+ "-->"));
        }
    }
    /**
     *  @Description: xml element 注释
     *  @Author: zhaoyu
     *  @Date: 2018/12/30
     */
    public void addComment(XmlElement xmlElement) {
        if (!this.suppressAllComments) {
            xmlElement.addElement(new TextElement("<!--  -->"));
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        addClassComment(innerClass,introspectedTable,false);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (!this.suppressAllComments) {
            innerClass.addJavaDocLine("/**");
            String remarks = introspectedTable.getRemarks();
            if (this.addRemarkComments && StringUtility.stringHasValue(remarks)) {
                String[] remarkLines = remarks.split(System.getProperty("line.separator"));
                for(int i = 0; i < remarkLines.length; ++i) {
                    String remarkLine = remarkLines[i];
                    if (i==0){
                        innerClass.addJavaDocLine(" * @Description: "+remarkLine);
                    }else {
                        innerClass.addJavaDocLine(" *       " + remarkLine);
                    }
                }
            }else {
                innerClass.addJavaDocLine(" * @Description: ");
            }
            innerClass.addJavaDocLine(" * @Author: "+author);
            innerClass.addJavaDocLine(" * @Date: "+dateStr);
            innerClass.addJavaDocLine(" */");
        }
    }

    /**
     *  @Description: model实体类注释
     *  @Author: zhaoyu
     *  @Date: 2018/12/30
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addClassComment(topLevelClass,introspectedTable,false);
    }


    /**
     *  @Description: 字段注释生产 -- 注解方式
     *  @Author: zhaoyu
     *  @Date: 2018/12/30
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!this.suppressAllComments) {
            String require;
            String annotation;
            if (introspectedColumn.isNullable()){
                annotation="@ApiModelProperty(value = \""+introspectedColumn.getRemarks()+"\")";
            }else {
                annotation="@ApiModelProperty(value = \""+introspectedColumn.getRemarks()+"\" ,required = true)";
            }
            field.addAnnotation(annotation);
            //添加tk的注释。
            //super.addFieldComment(field,introspectedTable,introspectedColumn);
        }
    }

    /**
     * 非数据库生产字段。
     * @param field
     * @param introspectedTable
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * @Description: ");
            method.addJavaDocLine(" * @Author: "+author);
            method.addJavaDocLine(" * @Date: "+dateStr);
            method.addJavaDocLine(" */");
        }
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }


}
