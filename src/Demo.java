import java.util.Random;

public class Demo implements Trackable {    // Demo needs to be Trackable!
    private int i = 0;

    public void count() {
        Random r = new Random();

        // Simulate real progress by increasing i
        for(i = 0; i < 100; i++) {
            try {
                // Sleep up to 1s
                Thread.sleep(r.nextInt(1, 1000));
            } catch(InterruptedException ignored) {}
        }
    }

    @Override
    public int currentProgress() {
        // In a real example 'i' would be calculated in some way e.g. (int) ((filesCopied / allFiles) * 100)
        return i;
    }

    public static void main(String[] args) {
        System.out.println("Single counter demo:");
        Progress.of(new Demo())     // Returns the Demo object passed in
                .count();

        try {
            // Without this, my console doesn't see the need to update to 100%
            Thread.sleep(100);
        } catch(InterruptedException ignored) {}

        System.out.println("\n\nMulticounter demo:");
        Progress.of(new Demo(),
                    new Demo(),
                    new Demo(),
                    new Demo(),
                    new Demo())
                // Returns List<Demo> objects passed in
                .stream()
                .parallel()
                .forEach(Demo::count);
    }
}
