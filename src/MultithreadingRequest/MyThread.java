package MultithreadingRequest;

import utils.DynamicUserAgentGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class MyThread implements Runnable {
    // 使用原子计数器保证线程安全
    private static final AtomicInteger count = new AtomicInteger(1);

    // 文件路径
    private final String FILE_PATH;
    // HttpClient实例
    private static final HttpClient client = HttpClient.newHttpClient();
    // 基础URL
    private final String BASEURL;
    // 文件写入锁
    private final Lock lock;
    // 最大页数
    private final int maxPage;
    // 同步计数器
    private final CountDownLatch latch;

    public MyThread(Lock lock, String FILE_PATH, String BASEURL, int maxPage, CountDownLatch latch) {
        this.lock = lock;
        this.FILE_PATH = FILE_PATH;
        this.BASEURL = BASEURL;
        this.maxPage = maxPage;
        this.latch = latch;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " 开始工作");

        while (true) {
            int currentCount = count.getAndIncrement(); // 原子操作获取页码

            // 检查是否超过最大页数
            if (currentCount > maxPage) {
                System.out.println(threadName + " 无更多任务，退出");
                break;
            }

            System.out.println(threadName + " 处理第 " + currentCount + " 页");

            try {
                // 构建请求
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASEURL + currentCount))
                        .header("User-Agent", DynamicUserAgentGenerator.getString())
                        .header("Accept", "text/html")
                        .timeout(java.time.Duration.ofSeconds(30))
                        .build();

                // 发送请求
                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                // 处理响应
                if (response.statusCode() != 200) {
                    System.out.println(threadName + " | 第 " + currentCount +
                            " 页请求失败，状态码: " + response.statusCode());
                    continue; // 继续下一页
                }

                String text = response.body();
                System.out.println(threadName + " | 成功获取第 " + currentCount + " 页内容");

                // 写入文件
                writeToFile(currentCount, text);

            } catch (IOException | InterruptedException e) {
                System.err.println(threadName + " | 处理第 " + currentCount +
                        " 页时出错: " + e.getMessage());
            } catch (Exception e) {
                System.err.println(threadName + " | 未处理异常: " + e.getMessage());
            }
        }

        // 任务完成，计数器减一
        latch.countDown();
        System.out.println(threadName + " 工作结束");
    }

    private void writeToFile(int page, String content) {
        lock.lock();
        try {
            Files.writeString(
                    Path.of(FILE_PATH),
                    "--- 第 " + page + " 页 ---\n" + content + "\n\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            System.out.println(Thread.currentThread().getName() +
                    " | 第 " + page + " 页已写入文件");
        } catch (IOException e) {
            System.err.println("写入第 " + page + " 页失败: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}