package handle;

import utils.PropertiesReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterSorter {

    public static void main(String[] args) throws IOException {
        Properties configProperties = PropertiesReader.loadFromFile("D:\\Multi-threading-java\\network\\src\\config.properties");

        String inputPath = configProperties.getProperty("totalFileName");

        String dirName = configProperties.getProperty("dirName");
        String fileName = configProperties.getProperty("fileSortName");
        Path dirPath = Paths.get(dirName);
        Path filePath = dirPath.resolve(fileName);
        //保存配置信息到文件
        configProperties.setProperty("totalFileSortName", String.valueOf(filePath.toAbsolutePath()));
        PropertiesReader.saveToFile(configProperties, "D:\\Multi-threading-java\\network\\src\\config.properties", "存放文件完整路径");
        String outputPath = configProperties.getProperty("totalFileSortName");

        try {
            sortFileByChapterId(inputPath, outputPath);
            System.out.println("文件排序完成！");
        } catch (IOException e) {
            System.err.println("处理文件时出错: " + e.getMessage());
        }
    }

    public static void sortFileByChapterId(String inputPath, String outputPath) throws IOException {
        // 1. 读取所有行
        List<String> lines = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);

        // 2. 将每行转换为可排序的对象
        List<ChapterLine> chapterLines = new ArrayList<>();
        for (String line : lines) {
            chapterLines.add(new ChapterLine(line));
        }

        // 3. 根据 chapterid 排序
        Collections.sort(chapterLines);

        // 4. 提取排序后的原始行
        List<String> sortedLines = new ArrayList<>();
        for (ChapterLine cl : chapterLines) {
            sortedLines.add(cl.originalLine);
        }

        // 5. 写入新文件
        Files.write(Paths.get(outputPath), sortedLines, StandardCharsets.UTF_8);
    }

    // 辅助类，用于提取 chapterid 并实现排序
    static class ChapterLine implements Comparable<ChapterLine> {
        String originalLine;
        int chapterId;

        public ChapterLine(String line) {
            this.originalLine = line;
            this.chapterId = extractChapterId(line);
        }

        private int extractChapterId(String line) {
            // 使用正则表达式匹配 chapterid
            Pattern pattern = Pattern.compile("\"chapterid\":\"(\\d+)\"");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    return Integer.MAX_VALUE; // 无效ID放在最后
                }
            }
            return Integer.MAX_VALUE; // 未找到放在最后
        }

        @Override
        public int compareTo(ChapterLine other) {
            return Integer.compare(this.chapterId, other.chapterId);
        }
    }
}
