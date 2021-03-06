//package com.luxf.plus;
//
//import cn.hutool.core.util.StrUtil;
//import com.luxf.plus.entity.base.BaseEntity;
//import com.luxf.plus.model.page.base.ReqPageVO;
//import com.baomidou.mybatisplus.core.toolkit.StringPool;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.po.TableInfo;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * TODO: 高版本的mybatis-plus, 需要单独引入mybatis-plus-generate相关的jar包、
// * @author luxf
// * @date 2020-11-16 16:19
// **/
//public class GenerateCode {
//
//    /**
//     * 当前项目地址
//     */
//    private static final String PROJECT_PATH = System.getProperty("user.dir");
//
//    /**
//     * 生成的代码默认输出文件目录
//     */
//    private static final String DEFAULT_OUT_PUT_DIR = PROJECT_PATH + "/src/main/java";
//
//    /**
//     * 默认公共包名称
//     */
//    private static final String DEFAULT_PARENT_PACKAGE = "com.luxf.plus";
//
//    public static void main(String[] args) {
//        String[] tableNames = {"business", "business_request", "business_request_material", "business_request_material_detail",
//                "code", "code_group", "material", "material_template", "org"};
////        String[] tableNames = {"business"};
////         generateByTables(tableNames);
//        generateVO(tableNames);
//        //generatePageVO(BaseEntity.class, ReqPageVO.class, DEFAULT_PARENT_PACKAGE + ".entity", false, tableNames);
//    }
//
//    private static void generateVO(String... tableNames) {
//        /**
//         * 通过实体类、生成VO对象
//         */
//        generatePageVO(BaseEntity.class, ReqPageVO.class, DEFAULT_PARENT_PACKAGE + ".entity", false, tableNames);
//        generateReqVO(BaseEntity.class, DEFAULT_PARENT_PACKAGE + ".entity", false, tableNames);
//    }
//
//    private static void generateByTables(String... tableNames) {
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        gc.setOutputDir(DEFAULT_OUT_PUT_DIR);
//        gc.setAuthor("luxf");
//        gc.setOpen(false);
//        // 实体属性 Swagger2 注解
//        gc.setSwagger2(true);
//        gc.setServiceName("%sService");
//        // XML中的ResultMap标签
//        gc.setBaseResultMap(true);
//        // 文件覆盖设置
//        gc.setFileOverride(true);
//        mpg.setGlobalConfig(gc);
//
//        // 数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:30018/plus?serverTimezone=UTC&useUnicode=true&useSSL=false&characterEncoding=utf8");
//        dsc.setDriverName("com.mysql.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("root123");
//        mpg.setDataSource(dsc);
//
//        // 包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("");
//        pc.setParent(DEFAULT_PARENT_PACKAGE);
//        mpg.setPackageInfo(pc);
//
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//            }
//        };
//
//        // 模板引擎: velocity
//        String templatePath = "/templates/mapper.xml.vm";
//
//        TemplateConfig templateConfig = new TemplateConfig();
//
//        // main.java.mapper路径中不生成Mapper.xml文件
//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);
//
//        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
//        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名, 如果你Entity设置了前后缀、此处注意xml的名称会跟着发生变化.
//                return PROJECT_PATH + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        /**
//         * TODO：此处重写后,可以添加/修改Velocity模板参数、
//         */
//        // 重写VelocityTemplateEngine
//        mpg.setTemplateEngine(new IVelocityTemplateEngine());
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass(BaseEntity.class);
//        strategy.setEntityLombokModel(true);
//        strategy.setRestControllerStyle(true);
//        strategy.setInclude(tableNames);
//        strategy.setControllerMappingHyphenStyle(true);
//        mpg.setStrategy(strategy);
//        mpg.execute();
//    }
//
//    private static void generatePageVO(Class<?> entityParentClass, Class<?> modelParentClass, String entityPackage, boolean isTablePrefix, String... tableNames) {
//        for (String name : tableNames) {
//            String tableName = name;
//            tableName = isTablePrefix ? tableName.substring(tableName.indexOf("_") + 1) : tableName;
//            String packPath = ("com.luxf.plus.model.page.".replaceAll("\\.", "/")) + getSubStr(tableName) + "/req";
//            String outPutDir = getOutPutDir(packPath);
//
//            try {
//                String entityName = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
//                String fileName = entityName + modelParentClass.getSimpleName() + ".java";
//                FileWriter fw = getFileWriter(outPutDir, fileName);
//                BufferedReader reader = getEntityReader(entityName);
//                String line;
//                AtomicBoolean atomic = new AtomicBoolean(true);
//                while ((line = reader.readLine()) != null) {
//                    line = line.replace(entityParentClass.getName(), modelParentClass.getName());
//                    line = replaceApiModel(line,"分页查询VO");
//                    line = replaceNameOrPackage(entityPackage, packPath, entityName, fileName, line);
//                    line = line.replace(entityParentClass.getSimpleName(), modelParentClass.getSimpleName());
//                    if (isNeedSkip(line, atomic, entityName + ";")) {
//                        continue;
//                    }
//                    if (atomic.get()) {
//                        line += "\r\n";
//                    }
//                    if (!atomic.get() && StrUtil.isEmpty(line)) {
//                        atomic.set(true);
//                    }
//                    fw.write(line);
//                }
//                fw.close();
//                reader.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void generateReqVO(Class<?> entityParentClass, String entityPackage, boolean isTablePrefix, String... tableNames) {
//        for (String name : tableNames) {
//            String tableName = name;
//            tableName = isTablePrefix ? tableName.substring(tableName.indexOf("_") + 1) : tableName;
//            String packPath = ("com.luxf.plus.model.vo.".replaceAll("\\.", "/")) + getSubStr(tableName) + "/req";
//            String outPutDir = getOutPutDir(packPath);
//            try {
//                String entityName = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
//                String fileName = entityName + "ReqVO.java";
//                FileWriter fw = getFileWriter(outPutDir, fileName);
//                BufferedReader reader = getEntityReader(entityName);
//                String line;
//                AtomicBoolean atomic = new AtomicBoolean(true);
//                while ((line = reader.readLine()) != null) {
//                    line = replaceApiModel(line,"请求VO");
//                    line = replaceNameOrPackage(entityPackage, packPath, entityName, fileName, line);
//                    line = line.replace("extends BaseEntity", "implements Serializable");
//
//                    if (isNeedSkip(line, atomic, "EqualsAndHashCode", entityParentClass.getSimpleName() + ";")) {
//                        continue;
//                    }
//                    if (line.endsWith("lombok.Data;")) {
//                        line = line + "\r\n\r\n" + "import java.io.Serializable;";
//                    }
//                    if (atomic.get()) {
//                        line += "\r\n";
//                    }
//                    if (!atomic.get() && StrUtil.isEmpty(line)) {
//                        atomic.set(true);
//                    }
//
//                    fw.write(line);
//                }
//                fw.close();
//                reader.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static String replaceApiModel(String line,String replacement) {
//        if (line.contains("@ApiModel(value=")) {
//            line = line.replace("对象", replacement);
//        }
//        return line;
//    }
//
//    private static String getOutPutDir(String packPath) {
//        return DEFAULT_OUT_PUT_DIR + "/" + packPath + "/";
//    }
//
//    /**
//     * 用于生成包路径、business,business_request,business_request_material 都是business路径下、
//     *
//     * @param tableName 表名、
//     */
//    private static String getSubStr(String tableName) {
//        int endIndex = tableName.indexOf("_");
//        String substring;
//        if (endIndex > 0) {
//            substring = tableName.toLowerCase().substring(0, endIndex);
//        } else {
//            substring = tableName.toLowerCase();
//        }
//        return substring;
//    }
//
//    private static FileWriter getFileWriter(String outPutDir, String fileName) throws IOException {
//        File outFile = new File(outPutDir);
//        if (!outFile.exists()) {
//            outFile.mkdirs();
//        }
//        File voFile = new File(outFile, fileName);
//        if (!voFile.exists()) {
//            voFile.createNewFile();
//        }
//        return new FileWriter(voFile);
//    }
//
//    private static String replaceNameOrPackage(String entityPackage, String packPath, String entityName, String fileName, String line) {
//        line = line.replace(entityPackage, packPath.replaceAll("/", "."));
//        String publicClass = "public class";
//        if (line.contains(publicClass)) {
//            line = line.replace(entityName, getJavaName(fileName));
//        }
//        return line;
//    }
//
//    private static String getJavaName(String fileName) {
//        return fileName.substring(0, fileName.length() - 5);
//    }
//
//    private static boolean isNeedSkip(String line, AtomicBoolean atomic, String... moreContainsCond) {
//        for (String cond : moreContainsCond) {
//            if (line.contains(cond)) {
//                return true;
//            }
//        }
//        String serial = "serialVersionUID";
//        if (line.contains(serial)) {
//            atomic.set(false);
//            return true;
//        }
//        return line.contains("TableName") || line.contains("TableField") || line.contains("Accessors") ||
//                line.contains("@ApiModelProperty(value = \"序号\")") || line.contains("private Integer seq;") ||
//                line.contains("@ApiModelProperty(value = \"标品外键") || line.contains("private Integer linkId;");
//    }
//
//    private static BufferedReader getEntityReader(String entityName) throws FileNotFoundException {
//        return new BufferedReader(
//                new FileReader(DEFAULT_OUT_PUT_DIR + "/" + (DEFAULT_PARENT_PACKAGE.replaceAll("\\.", "/")) + "/entity/" + entityName + ".java"));
//    }
//
//    private static class IVelocityTemplateEngine extends VelocityTemplateEngine {
//        /**
//         * 传递自定义的参数、
//         *
//         * @throws Exception
//         */
//        @Override
//        public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
//            TableInfo table = (TableInfo) objectMap.get("table");
////            Field[] fields = ReflectUtil.getFields(table.getClass());
////            for (Field field : fields) {
////                if (field.getType().equals(String.class)) {
////                    field.setAccessible(true);
////                    Object val = field.get(table);
////                    if (Objects.nonNull(val)) {
////                        val = val.toString().trim();
////                    }
////                    ReflectUtil.setFieldValue(table,field,val);
////                }
////            }
//            String[] tablePrefix = this.getConfigBuilder().getStrategyConfig().getTablePrefix();
//            String name = table.getName();
//            if (tablePrefix != null) {
//                for (String prefix : tablePrefix) {
//                    if (name.startsWith(prefix)) {
//                        name = name.substring(name.indexOf(prefix) + prefix.length() + 1);
//                        break;
//                    }
//                }
//            }
//            String customVal = name.split("_")[0];
//            objectMap.put("custom", customVal);
//            super.writer(objectMap, templatePath, outputFile);
//        }
//    }
//
//}
