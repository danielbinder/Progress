# Progress

This is a simple library for adding a console progress counter to your project.

### I want to use and/or adapt this project
Go for it - I tried to make everything as readable and modifiable as possible.
Check out the explanation below, and the license in the 'LICENSE' file.
Regardless of the license, it would be cool if you somehow mentioned, that you got this code from here :)

### How to use
1) Implement the [Trackable.java](https://github.com/danielbinder/Progress/blob/main/Progress/src/Trackable.java) in the Objects you want to track.
2) Wherever you want to track progress, wrap the Object to track with
   - `Progress.of(Object)`
   - `Progress.of(Description, Object)`
   - `Progress.of(Object1, Object2, ...)`
   - `Progress.of(List<Object>)`
   - `Progress.of(List<Description>, List<Object>)`
   - `Progress.of(Map<Object, Description>)`
3) All `Progress.of(...)` methods in [Progress.java](https://github.com/danielbinder/Progress/blob/main/Progress/src/Progress.java) return the incoming objects back to you.
If a single object was passed, it is returned directly.
Multiple objects are returned as a List<Object>.

### Implementation details
- Update rate of the counter is hard coded to 100ms.
- The goal of complicated progress update loop is to reduce IO access as much as possible.

### Demo
The [Demo.java](https://github.com/danielbinder/Progress/blob/main/Progress/src/Demo.java) demonstrates some usages described above, where the progress is determined by counters that increase randomly.