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
                System.out.print("Progress" +
                                         (description.isBlank() ? "" : "[" + description + "]") +
                                         ": ");

                int currentProgress = trackable.currentProgress();
                System.out.print(currentProgress + "%");

                while(currentProgress < 100) {
                    int currentProgressTemp = trackable.currentProgress();

                    if(currentProgress != currentProgressTemp) {
                        System.out.print("\b".repeat(String.valueOf(currentProgress).length() + 1));

                        currentProgress = currentProgressTemp;
                        System.out.print(currentProgress + "%");
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

                while(minProgress < 100) {
                    minProgress = trackables.stream()
                            .map(Trackable::currentProgress)
                            .min(Integer::compareTo)
                            // stop if no minimum found
                            .orElse(100);

                    String currString = transformedDescriptions.stream()
                            .map(d -> d + trackables.get(transformedDescriptions.indexOf(d)).currentProgress() + "%   ")
                            .collect(Collectors.joining());

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
}
