import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 2;
    private static final int KEEP_ALIVE_TIME_IN_SECONDS = 60;
    private static final int QUEUE_CAPACITY = 1;

    private static final Queue<Integer> resultsQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME_IN_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY)
        );

        for (int i = 0; i < 3; ++i) {
            threadPoolExecutor.execute(getRunnable(i));
            Thread.sleep(1_000);
        }

        while (resultsQueue.size() != 3);
        for (int i = 0; i < 3; ++i) {
            System.out.println(resultsQueue.poll());
        }

        threadPoolExecutor.shutdown();
    }

    private static Runnable getRunnable(int id) {
        return () -> {
            try {
                Thread.sleep(10_000);
                resultsQueue.add(id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static class TestingRunnable implements Runnable {

        private static final long SLEEP_TIME_MS = 10_000;

        private final int id;

        public TestingRunnable(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP_TIME_MS);
                resultsQueue.add(id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
