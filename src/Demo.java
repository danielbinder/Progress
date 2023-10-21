import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Demo {
    private static final int MAX_SLEEP_MS = 1000;
    private static final int MAX_ELEMENTS_IN_LIST = 1234;
    private static final Random r = new Random();


    public void count() {
        try {
            // Simulate real work by sleeping
            Thread.sleep(r.nextInt(1, MAX_SLEEP_MS));
        } catch(InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        System.out.println("Single counter demo:");
        List<Demo> demos = IntStream.range(0, 123)
                .mapToObj(i -> new Demo())
                .toList();

        for(Demo d : Progress.of(demos)) d.count();

        // My console won't update to 100% unless I add this
        try {
            Thread.sleep(100);
        } catch(InterruptedException ignored) {}

        System.out.println("\n\nMulticounter demo:");
        Progress.reset();
        IntStream.range(0, 5)
                // Random amount of list elements
                .mapToObj(l -> IntStream.range(0, r.nextInt(MAX_ELEMENTS_IN_LIST))
                        .mapToObj(i -> new Demo())
                        .toList())
                // Register multiple counters in different threads
                .forEach(list ->
                    Thread.ofPlatform().start(() -> {
                        for(Demo d : Progress.of(list)) d.count();
                    })
                );
    }
}
