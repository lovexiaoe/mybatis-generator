package com.szmachine;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MybatisGeneratorApplication {

	public static void main(String[] args) {
		// 执行中的异常信息会保存在warnings中
		List<String> warnings = new ArrayList<String>();
		//覆盖之前的文件
		boolean overwrite = true;
		//System.out.println(System.getProperty("user.dir")); //项目根路径
//		Resource resource = new ClassPathResource("mybatis-generator/tkgeneratorConfig.xml");
		Resource resource = new ClassPathResource("mybatis-generator/generatorConfig.xml");
		try {
			File configFile =resource.getFile();
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(configFile);
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLParserException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//打印异常信息。
		for (int i = 0; i < warnings.size(); i++) {
			System.out.println(warnings.get(i));
		}
	}

}
