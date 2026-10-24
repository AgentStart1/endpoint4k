# Build and release verification

Use JDK 21 and the checked-in Gradle wrapper. Android targets also require an
Android SDK with platform 36. On Windows, use `gradlew.bat` instead of `./gradlew`.

## Query decoding and response reading

Run `./gradlew :http4k:test :okhttp:test --warning-mode all` to exercise the
http4k server query decoder and OkHttp response reader. The tests include repeated,
absent and valueless list query parameters, JSON and string responses, and empty
responses returned as `String` or `Unit`.

Run static analysis for these surfaces with:

```sh
./gradlew :http4k:detekt :http4k:http4k-server:detekt :okhttp:detekt :okhttp:okhttp-client:detekt
```

## Local Maven verification

The following command builds and publishes the affected JVM artifacts, sources,
documentation, POMs and Gradle metadata into an isolated local repository:

```sh
./gradlew :common:publishJvmPublicationToMavenLocal \
  :http4k:http4k-server:publishToMavenLocal \
  :okhttp:okhttp-client:publishToMavenLocal \
  -x :common:signJvmPublication \
  -x :http4k:http4k-server:signMavenPublication \
  -x :okhttp:okhttp-client:signMavenPublication \
  -Pversion=0.0.0-issue10-SNAPSHOT -Pgroup=io.github.storytellerf \
  -Dmaven.repo.local=/absolute/path/to/temporary-maven-repository \
  --warning-mode all
```

Replace the repository path with a temporary directory. This local check excludes
signing tasks and does not validate signing, Central upload, or non-JVM targets.
The tag-triggered publish workflow retains signing and Central publication and
prints all Gradle deprecation warnings.

## Tracing Gradle deprecations

Use `./gradlew help --warning-mode all -Dorg.gradle.deprecation.trace=true` for
configuration warnings; repeat with the relevant test or publish tasks to include
execution-time warnings. Inspect the first plugin or project frame in each trace.

During issue #10 verification with Gradle 9.8.2, these remaining configuration
warnings originate in third-party plugins:

| Deprecated API | Observed source | Removal |
| --- | --- | --- |
| `ReportingExtension.file(String)` | Detekt 1.23.8, `DetektPlugin.apply` | Gradle 10 |
| Enum attribute `KotlinNativeBundleArtifactsTypes` | Kotlin plugin, `KotlinNativeBundleArtifactFormat` | Gradle 10 |
| `Configuration.setVisible(boolean)` | Detekt and Android Gradle Plugin | Gradle 11 |

The project task registration uses `tasks.register` and `TaskProvider.configure`.
Do not suppress the remaining plugin warnings or interpret a successful Gradle 9
build as proof of Gradle 10 compatibility. Recheck the traces when upgrading the
owning plugins. Disabled iOS tests on Windows require macOS to run; they are
separate from deprecation warnings.

Action runtime warnings must be checked in a new GitHub Actions run. A local
Gradle build cannot verify the runtimes of checkout, Java setup or Android setup.
