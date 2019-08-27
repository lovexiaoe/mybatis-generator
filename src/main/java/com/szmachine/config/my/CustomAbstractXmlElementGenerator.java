package com.szmachine.config.my;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomAbstractXmlElementGenerator extends AbstractXmlElementGenerator {

    public static final String ORDERBY_AND_LIMIT="OrderBy_And_Limit";
    public static final String WHERE_BY_CONDITION="Where_By_Condition";
    public static final String COUNT_BY_CONDITION="countByCondition";
    public static final String PAGE_BY_CONDITION ="pageByCondition";

    @Override
    public void addElements(XmlElement parentElement) {
        selectById(parentElement);
        updateById(parentElement);
        queryOrderLimit(parentElement);
        whereByCondition(parentElement);
        pageByCondition(parentElement);
        countByCondition(parentElement);
    }

    // 根据id查询。
    public void selectById(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", "selectById"));
        if (this.introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getBaseResultMapId()));
        }

        String parameterType;
        if (this.introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterType = this.introspectedTable.getPrimaryKeyType();
        } else if (this.introspectedTable.getPrimaryKeyColumns().size() > 1) {
            parameterType = "map";
        } else {
            parameterType = ((IntrospectedColumn)this.introspectedTable.getPrimaryKeyColumns().get(0)).getFullyQualifiedJavaType().toString();
        }

        answer.addAttribute(new Attribute("parameterType", parameterType));
        this.context.getCommentGenerator().addComment(answer);
        StringBuilder sb = new StringBuilder();
        sb.append("select ");

        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(this.getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from ");
        sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        boolean and = false;
        Iterator var6 = this.introspectedTable.getPrimaryKeyColumns().iterator();

        while(var6.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)var6.next();
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(this.getParameterClause(introspectedColumn,(String)null));
            answer.addElement(new TextElement(sb.toString()));
        }

        parentElement.addElement(answer);
    }

    //根据id更新
    public void updateById(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", "updateById"));
        answer.addAttribute(new Attribute("parameterType", this.introspectedTable.getBaseRecordType()));
        this.context.getCommentGenerator().addComment(answer);
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("set ");
        Iterator iter;
        iter = ListUtilities.removeGeneratedAlwaysColumns(this.introspectedTable.getNonPrimaryKeyColumns()).iterator();
//        iter = ListUtilities.removeGeneratedAlwaysColumns(this.introspectedTable.getBaseColumns()).iterator();

        while(iter.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)iter.next();
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(this.getParameterClause(introspectedColumn,null));
            if (iter.hasNext()) {
                sb.append(',');
            }

            answer.addElement(new TextElement(sb.toString()));
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }

        boolean and = false;
        Iterator var6 = this.introspectedTable.getPrimaryKeyColumns().iterator();

        while(var6.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)var6.next();
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
//            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sb.append(this.getParameterClause(introspectedColumn,null));
            answer.addElement(new TextElement(sb.toString()));
        }
        parentElement.addElement(answer);

    }

    //排序分页sql
    private void queryOrderLimit(XmlElement parentElement) {
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", ORDERBY_AND_LIMIT));

        StringBuilder sb = new StringBuilder();

        XmlElement orderByNotNullElement = new XmlElement("if");
        sb.setLength(0);
        sb.append("orderBy != null and orderBy != '' and orderType != null and orderType != ''");
        orderByNotNullElement.addAttribute(new Attribute("test", sb.toString()));
        sb.setLength(0);
        sb.append("ORDER BY ${orderBy} ${orderType}");
        orderByNotNullElement.addElement(new TextElement(sb.toString()));

        sql.addElement(orderByNotNullElement);

        XmlElement limitNotNullElement = new XmlElement("if");
        sb.setLength(0);
        sb.append("page != null and page &gt; 0 and limit != null and limit &gt; 0");
        limitNotNullElement.addAttribute(new Attribute("test", sb.toString()));
        sb.setLength(0);
        sb.append("LIMIT ${(page-1)*limit},${limit}");
        limitNotNullElement.addElement(new TextElement(sb.toString()));

        sql.addElement(limitNotNullElement);
        parentElement.addElement(sql);
    }

    //动态where条件
    private void whereByCondition(XmlElement parentElement) {
        // 动态where条件。
        StringBuilder sb=new StringBuilder();
        XmlElement whereByCondition = new XmlElement("sql");
        whereByCondition.addAttribute(new Attribute("id", WHERE_BY_CONDITION));
        whereByCondition.addElement(new TextElement("<!-- prefixOverrides 自动覆盖第一个and或者or.-->"));
        XmlElement selectTrimElement = new XmlElement("trim"); //设置trim标签
        selectTrimElement.addAttribute(new Attribute("prefix", "WHERE"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "AND | OR")); //添加where和and
        for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append("null != ");
            sb.append(introspectedColumn.getJavaProperty("record."));
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            // 添加and
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            // 添加等号
            sb.append(" = ");
            sb.append(this.getParameterClause(introspectedColumn,"record."));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }
        whereByCondition.addElement(selectTrimElement);
        parentElement.addElement(whereByCondition);
    }

    private static String getParameterClause(IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append("#{");
        sb.append(introspectedColumn.getJavaProperty(prefix));
        sb.append('}');
        return sb.toString();
    }

    //根据条件分页查询
    private void pageByCondition(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", PAGE_BY_CONDITION));
        if (this.introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getBaseResultMapId()));
        }
        answer.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));

        answer.addElement(new TextElement("<!-- 根据条件分页查询 -->"));
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(this.getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from ");
        sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        //include
        answer.addElement(getIncludeByRefid(WHERE_BY_CONDITION));
        answer.addElement(getIncludeByRefid(ORDERBY_AND_LIMIT));
        parentElement.addElement(answer);
    }

    //根据条件统计总数
    private void countByCondition(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", COUNT_BY_CONDITION));
        answer.addAttribute(new Attribute("resultType", "java.lang.Long"));
        answer.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));

        answer.addElement(new TextElement("<!-- 根据条件统计总数 -->"));
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ");
        sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        //include
        answer.addElement(getIncludeByRefid(WHERE_BY_CONDITION));
        parentElement.addElement(answer);
    }

    // 构造include
    private XmlElement getIncludeByRefid(String refid){
        //include
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", refid));
        return include;
    }
}