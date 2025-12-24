package MultithreadingRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import utils.PropertiesReader;


public class SendRequest {
    public static void main(String[] args) throws IOException, InterruptedException {


        // 指定目录名（相对路径）
        Properties configProperties = PropertiesReader.loadFromFile("D:\\Multi-threading-java\\network\\src\\config.properties");
        String dirName = configProperties.getProperty("dirName");
        String fileName = configProperties.getProperty("fileName");

        Path dirPath = Paths.get(dirName);
        Path filePath = dirPath.resolve(fileName);

        try {
            // 创建目录（如果父目录不存在会自动创建）
            if (!Files.exists(dirPath)) {
                Path createdDirPath = Files.createDirectory(dirPath); // 创建单级目录
                System.out.println("目录创建成功: " + createdDirPath.toAbsolutePath());
            } else {
                System.out.println("目录已存在");
            }

            try {
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                    System.out.println("文件创建成功: " + filePath.toAbsolutePath());
                } else {
                    System.out.println("文件已存在: " + filePath.toAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("创建文件失败: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
        }

        Lock lock = new ReentrantLock();
        String FILE_PATH = String.valueOf(filePath.toAbsolutePath());
        String BASEURL = configProperties.getProperty("baseUrl");
        Integer maxPage = Integer.valueOf(configProperties.getProperty("maxPage"));
        int threadCount = Integer.parseInt(configProperties.getProperty("threadCount", "6")); // 从配置读取线程数


        //创建线程池 用于后续提交任务
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadCount,
                threadCount + threadCount,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //创建任务实例
//        MyThread myThread = new MyThread(lock,FILE_PATH,BASEURL,maxPage);

        //根据自己的配置调整提交任务的数量
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);
//        threadPoolExecutor.submit(myThread);

        // 创建CountDownLatch同步任务完成
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("开始爬取任务，共" + maxPage + "页，使用" + threadCount + "个线程...");

        // 提交任务
        for (int i = 0; i < threadCount; i++) {
            threadPoolExecutor.submit(new MyThread(lock, FILE_PATH, BASEURL, maxPage, latch));
        }

        // 关闭线程池（不再接受新任务）
        threadPoolExecutor.shutdown();

        // 等待所有任务完成
        boolean finished = threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        if (!finished) {
            System.err.println("部分任务未在指定时间内完成");
        }

        // 或者使用CountDownLatch等待
        latch.await();
        System.out.println("所有爬取任务已完成！");

        //保存配置信息到文件
        configProperties.setProperty("totalFileName", String.valueOf(filePath.toAbsolutePath()));
        PropertiesReader.saveToFile(configProperties, "D:\\Multi-threading-java\\network\\src\\config.properties", "存放文件完整路径");
    }
}
