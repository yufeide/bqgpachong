package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesReader {

    /**
     * 从文件路径读取 Properties 配置
     *
     * @param filePath 配置文件路径
     * @return Properties 对象
     * @throws IOException 如果文件读取失败
     */
    public static Properties loadFromFile(String filePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
        return properties;
    }

    /**
     * 从类路径资源读取 Properties 配置
     *
     * @param resourcePath 类路径资源路径
     * @return Properties 对象
     * @throws IOException 如果资源读取失败
     */
    public static Properties loadFromResource(String resourcePath) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = PropertiesReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("资源未找到: " + resourcePath);
            }
            properties.load(is);
        }
        return properties;
    }

    /**
     * 使用 UTF-8 编码读取 Properties 配置
     *
     * @param filePath 配置文件路径
     * @return Properties 对象
     * @throws IOException 如果文件读取失败
     */
    public static Properties loadWithUtf8(String filePath) throws IOException {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    /**
     * 获取指定配置项的值
     *
     * @param properties Properties 对象
     * @param key 配置键
     * @param defaultValue 默认值（当键不存在时返回）
     * @return 配置值或默认值
     */
    public static String getProperty(Properties properties, String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取整数类型的配置值
     *
     * @param properties Properties 对象
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 整数值
     */
    public static int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // 格式错误时返回默认值
            }
        }
        return defaultValue;
    }

    /**
     * 获取布尔类型的配置值
     *
     * @param properties Properties 对象
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * 打印所有配置项
     *
     * @param properties Properties 对象
     */
    public static void printAllProperties(Properties properties) {
        System.out.println("==== Properties 配置内容 ====");
        properties.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
    }

    /**
     * 保存配置到文件
     *
     * @param properties Properties 对象
     * @param filePath 文件路径
     * @param comments 注释信息
     * @throws IOException 如果保存失败
     */
    public static void saveToFile(Properties properties, String filePath, String comments) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        try (OutputStream os = Files.newOutputStream(path)) {
            properties.store(os, comments);
        }
    }

    /**
     * 示例用法
     */
//    public static void main(String[] args) {
//        try {
//            // 1. 从文件系统读取
//            Properties fileProps = loadFromFile("D:\\Multi-threading-java\\network\\src\\config.properties");
//            printAllProperties(fileProps);
//
//            // 2. 从类路径资源读取
//            Properties resourceProps = loadFromResource("app.properties");
//            printAllProperties(resourceProps);
//
//            // 3. 使用UTF-8编码读取（处理中文）
//            Properties utf8Props = loadWithUtf8("config_utf8.properties");
//            printAllProperties(utf8Props);
//
//            // 4. 获取特定配置项
//            String dbUrl = getProperty(fileProps, "db.url", "jdbc:default");
//            int port = getIntProperty(fileProps, "server.port", 8080);
//            boolean debug = getBooleanProperty(fileProps, "app.debug", false);
//
//            System.out.println("\n特定配置项:");
//            System.out.println("db.url = " + dbUrl);
//            System.out.println("server.port = " + port);
//            System.out.println("app.debug = " + debug);
//
//            // 5. 修改并保存配置
//            fileProps.setProperty("app.version", "2.0.0");
//            saveToFile(fileProps, "config_updated.properties", "Updated configuration");
//
//        } catch (IOException e) {
//            System.err.println("配置文件操作失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
