package httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//优化方向使用线程池进行多线程请求爬取

public class HttpClientExample {
    public static void main(String[] args) throws Exception {
        // 1. 创建HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 2. 构建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apibi.cc/api/chapter?id=12818&chapterid=1"))
                .GET() // 默认GET，可省略
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0")
                .header("Accept", "text/html")
                .build();

        // 3. 发送请求（同步）
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString() // 响应体处理为字符串
        );

        // 4. 输出结果
        if (response.statusCode() == 200) {
            System.out.println("获取到的HTML内容：\n" + response.body());
        } else {
            System.out.println("请求失败，状态码: " + response.statusCode());
        }


        String text = response.body();
        String filePath = "D:\\Multi-threading-java\\network\\data\\test3.txt";

        try {
            // 核心方法：写入字符串，指定编码（避免中文乱码）
            Files.write(Paths.get(filePath), text.getBytes(StandardCharsets.UTF_8));
            System.out.println("Files写入成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}