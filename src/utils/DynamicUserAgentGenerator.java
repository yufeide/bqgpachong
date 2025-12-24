package utils;

import java.util.Random;

public class DynamicUserAgentGenerator {

    // 生成随机版本号 (e.g. 143.0.0.0 -> 143.0.0.X)
    private static String generateRandomVersion(String baseVersion) {
        Random rand = new Random();
        int patch = rand.nextInt(10);  // 0-9 的随机数
        return baseVersion + "." + patch;
    }

    public static String getString() {
        // 基础配置
        String os = "Windows NT 10.0; Win64; x64";
        String engine = "AppleWebKit/537.36 (KHTML, like Gecko)";
        String chromeBaseVersion = "143.0.0";
        String edgeBaseVersion = "143.0.0";

        // 生成带随机小版本的版本号
        String chromeVersion = generateRandomVersion(chromeBaseVersion);
        String edgeVersion = generateRandomVersion(edgeBaseVersion);

        // 构建 User-Agent
        String userAgent = String.format(
                "Mozilla/5.0 (%s) %s Chrome/%s Safari/537.36 Edg/%s",
                os, engine, chromeVersion, edgeVersion
        );

        System.out.println("动态生成的 User-Agent:");
        System.out.println(userAgent);
        return userAgent;
    }
}
