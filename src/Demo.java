import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Demo {
    public void count() {
        Random r = new Random();

        // Simulate real progress
        try {
            // Sleep up to 1s
            Thread.sleep(r.nextInt(1, 1000));
        } catch(InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        System.out.println("Single counter demo:");
        List<Demo> demos = IntStream.range(0, 100)
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
                .mapToObj(l -> IntStream.range(0, 100)
                        .mapToObj(i -> new Demo())
                        .toList())
                .parallel()
                // Register multiple lists
                .forEach(list -> {
                    for(Demo d : Progress.of(list)) d.count();
                });
    }
}
