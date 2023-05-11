import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Copyright (c) 2022. All Rights Reserved
 *
 * @Author Zhang Youfa
 * @Date 2023/05/11 11:25 AM
 * @Description TODO
 */
public class FutureTest {
    private static final Logger logger = LoggerFactory.getLogger(FutureTest.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // jdk使用future的场景 ===> 异步化工作
        // 启用一个新线程 把处理结果返回给主线程
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                logger.debug("异步的工作处理");
                TimeUnit.SECONDS.sleep(5);
                return 1;
            }
        });

        logger.debug("result = {}", future.get());
        logger.debug("--------");
    }
}
