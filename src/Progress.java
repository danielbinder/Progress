import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Progress {
    public static <T extends Trackable> T of(T trackable) {
        return of("", trackable);
    }

    public static <T extends Trackable> T of(String description, T trackable) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                int currentProgress = trackable.currentProgress();
                String s = "";

                long startTime = System.currentTimeMillis();

                while(currentProgress < 100) {
                    int currentProgressTemp = trackable.currentProgress();

                    if(currentProgress != currentProgressTemp) {
                        System.out.print("\b".repeat(s.length()));

                        currentProgress = currentProgressTemp;
                        s = "\u001B[32mProgress" + (description.isBlank() ? "" : "[" + description + "]") + ": " +
                                currentProgress + "%  " + getTimeString(startTime, currentProgress) +
                                "\u001B[0m";

                        System.out.print(s);
                    }

                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException ignored) {}
                }
            }
        }.start();

        return trackable;
    }

    /** Multiprogress */

    @SafeVarargs
    public static <T extends Trackable> List<T> of(T...trackables) {
        return of(Arrays.stream(trackables).toList());
    }

    public static <T extends Trackable> List<T> of(List<T> trackables) {
        return of(trackables.stream()
                          .map(t -> String.valueOf(trackables.indexOf(t)))
                          .toList(),
                  trackables);
    }

    public static <T extends Trackable> List<T> of(Map<T, String> trackablesWithDescriptions) {
        List<String> descriptions = new ArrayList<>();
        List<T> trackables = new ArrayList<>();

        trackablesWithDescriptions.forEach((k, v) -> {
            trackables.add(k);
            descriptions.add(v);
        });

        return of(descriptions, trackables);
    }

    public static <T extends Trackable> List<T> of(List<String> descriptions, List<T> trackables) {
        List<String> transformedDescriptions = descriptions.stream()
                .map(d -> "Progress[" + d + "]: ")
                .toList();

        new Thread() {
            @Override
            public void run() {
                super.run();

                String oldString = "";
                int minProgress = trackables.stream()
                        .map(Trackable::currentProgress)
                        .min(Integer::compareTo)
                        // stop if no minimum found
                        .orElse(100);

                long startTime = System.currentTimeMillis();

                while(minProgress < 100) {
                    minProgress = trackables.stream()
                            .map(Trackable::currentProgress)
                            .min(Integer::compareTo)
                            // stop if no minimum found
                            .orElse(100);



                    String currString = "\u001B[32m" + transformedDescriptions.stream()
                            .map(d -> d + trackables.get(transformedDescriptions.indexOf(d)).currentProgress() + "%  ")
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

        return trackables;
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
