package handle;


import utils.PropertiesReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterExtractor {
    private static final Properties configProperties;
    private static final String SEPARATOR = "\n\n==============================\n\n";

    static{
        try {
            configProperties = PropertiesReader.loadFromFile("D:\\Multi-threading-java\\network\\src\\config.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws IOException {
        // 输入文件路径
        String inPutFileName = configProperties.getProperty("totalFileSortName");

        String dirName = configProperties.getProperty("dirName");
        String fileName = configProperties.getProperty("resultFileName");
        Path dirPath = Paths.get(dirName);
        Path filePath = dirPath.resolve(fileName);
        //保存配置信息到文件
        configProperties.setProperty("totalResultFileName", String.valueOf(filePath.toAbsolutePath()));
        PropertiesReader.saveToFile(configProperties, "D:\\Multi-threading-java\\network\\src\\config.properties", "存放文件完整路径");
        String OUTPUT_FILE = configProperties.getProperty("totalResultFileName");

        // 读取JSON文件内容
        String json = new String(Files.readAllBytes(Paths.get(inPutFileName)), StandardCharsets.UTF_8);

        // 初始化输出文件（如果存在则清空）
        Files.write(Paths.get(OUTPUT_FILE), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // 提取章节信息并保存到单个文件
        extractAndSaveToSingleFile(json, OUTPUT_FILE);

        System.out.println("所有章节已保存至: " + OUTPUT_FILE);
    }

    public static void extractAndSaveToSingleFile(String json, String outputFile) {
        // 1. 提取章节名
        Pattern chapterPattern = Pattern.compile("\"chaptername\":\"(.*?)\"");
        Matcher chapterMatcher = chapterPattern.matcher(json);

        // 2. 提取章节内容
        Pattern contentPattern = Pattern.compile("\"txt\":\"(.*?)\"(?:,\\s*\"time\"|})", Pattern.DOTALL);
        Matcher contentMatcher = contentPattern.matcher(json);

        int chapterCount = 0;

        while (chapterMatcher.find() && contentMatcher.find()) {
            try {
                String chapterName = chapterMatcher.group(1);
                String rawContent = contentMatcher.group(1);

                // 处理转义字符
                String processedContent = rawContent
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");

                // 格式化章节内容
                String chapterEntry = "章节名: " + chapterName + "\n\n"
                        + "内容:\n" + processedContent
                        + SEPARATOR;

                // 追加写入文件
                Files.write(Paths.get(outputFile),
                        chapterEntry.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);

                chapterCount++;

            } catch (IOException e) {
                System.err.println("写入章节失败: " + e.getMessage());
            }
        }

        System.out.println("成功保存 " + chapterCount + " 个章节");

        if (chapterCount == 0) {
            System.out.println("警告: 未找到任何章节数据");
        }
    }
}