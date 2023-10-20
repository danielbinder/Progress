import java.util.*;
import java.util.stream.Collectors;

public class Progress<T> implements Iterable<T> {
    private static final Map<String, Integer> progressMap = new HashMap<>();
    private static int i = 0;
    private static long startTime;
    private final String description;
    private final List<T> list;

    static {
        reset();
    }

    private Progress(String description, List<T> list) {
        progressMap.put(description, 0);

        this.description = description;
        this.list = list;
    }

    public static <T> Progress<T> of(String description, List<T> list) {
        return new Progress<>(description, list);
    }

    public static <T> Progress<T> of(List<T> list) {
        return of(String.valueOf(i++), list);
    }

    public static <T> Progress<T> of(String description, Collection<T> collection) {
        return of(description, collection.stream().toList());
    }

    public static <T> Progress<T> of(Collection<T> collection) {
        return of(collection.stream().toList());
    }

    public static <T> Progress<T> of(String description, T...array) {
        return of(description, Arrays.asList(array));
    }

    public static <T> Progress<T> of(T...array) {
        return of(Arrays.asList(array));
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < list.size();
            }

            @Override
            public T next() {
                updateProgress(description, (100 * i) / Math.max(1, list.size() - 1));

                return list.get(i++);
            }
        };
    }

    private static void updateProgress(String description, int percentage) {
        progressMap.put(description, percentage);
    }

    public static void reset() {
        progressMap.clear();
        i = 0;
        startTime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                super.run();

                while(progressMap.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException ignored) {}
                }

                String oldString = "";
                int minProgress = progressMap.values().stream()
                        .min(Integer::compareTo)
                        // stop if no minimum found
                        .orElse(100);

                while(minProgress < 100) {
                    minProgress = progressMap.values().stream()
                            .min(Integer::compareTo)
                            // stop if no minimum found
                            .orElse(100);

                    String currString = "\u001B[32m" + progressMap.keySet().stream()
                            .map(d -> "[" + d + "] " + progressMap.get(d) + "%  ")
                            .collect(Collectors.joining("", "", getTimeString(startTime, minProgress)));

                    if(!currString.equals(oldString)) {
                        System.out.print("\b".repeat(oldString.length()));

                        oldString = currString;

                        System.out.print(currString);
                    }

                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException ignored) {}
                }
            }
        }.start();
    }

    private static String getTimeString(long startTime, int minProgress) {
        long currTime = System.currentTimeMillis();
        long estimatedTimeLeft = ((currTime - startTime) / Math.max(minProgress, 1)) * (100 - minProgress);

        return "   \u001B[34mTime: " +
                formatTimeMillis(currTime - startTime) +
                " | " +
                formatTimeMillis(estimatedTimeLeft) +
                "\u001B[0m";
    }

    private static String formatTimeMillis(long timeMillis) {
        long y = ((((timeMillis / 1000) / 60) / 60) / 24) / 365;
        long d = ((((timeMillis / 1000) / 60) / 60) / 24) % 365;
        long h = (((timeMillis / 1000) / 60) / 60) % 24;
        long min = ((timeMillis / 1000) / 60) % 60;
        long s = (timeMillis / 1000) % 60;

        return (y == 0 ? "" : y + "y") +
                (d == 0 ? "" : (y > 0 ? "0".repeat(3 - String.valueOf(d).length()) + d : d) + "d") +
                (h == 0 ? "" : (d > 0 ? "0".repeat(2 - String.valueOf(h).length()) + h : h) + ":") +
                (min == 0 ? "" : (h > 0 ? "0".repeat(2 - String.valueOf(min).length()) + min : min) + ":") +
                (h == 0 && min == 0 ? s + "s" : s);
    }
}
