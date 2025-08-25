```markdown
# Nostalgia Mody Videos Generator - Starter

This is a small JavaFX app that demonstrates how to build a simple GUI frontend that applies remix-style video transforms using ffmpeg. It is designed to be packaged with an Azul Zulu OpenJDK runtime (see "Packaging with Azul Zulu" below).

Features (starter):
- Load a video file.
- Speed up / slow down (basic).
- Generate a simple randomized remix (extract random short clips and concat).
- Apply a "round" circular mask demo (example ffmpeg filter).
- "Annoying / faster" preset (speed + stutter demo).
- All processing is performed by calling ffmpeg externally.

Requirements:
- Java 17+ (Azul Zulu OpenJDK recommended).
- ffmpeg available on PATH.

Build:
- Uses Gradle and JavaFX.
- To build: `./gradlew build` (on Windows use `gradlew.bat`).

Packaging with Azul Zulu:
1. Download Azul Zulu JDK for your platform (e.g. Zulu JDK 17+).
   - https://www.azul.com/downloads/
2. Set JAVA_HOME to the Azul Zulu JDK root.
3. Use `jlink`/`jpackage` to create a runtime or installer. Example (mac/linux):
   - Create runtime image:
     `jlink --module-path $JAVA_HOME/jmods:build/libs --add-modules java.base,java.desktop,java.logging,javafx.controls --output runtime-image`
   - Or build an installer with jpackage (Java 17+ jpackage available in the JDK):
     `jpackage --name NostalgiaMody --input build/libs --main-jar nostalgia-mody.jar --runtime-image runtime-image --type dmg` (for mac)
4. You can point jpackage to Azul Zulu's jdk so the packaged app uses Azul Zulu runtime.

Notes:
- This project intentionally delegates heavy lifting to ffmpeg. That keeps the Java GUI simple while letting ffmpeg perform complex transforms.
- To create YTPMV / remix effects you'll edit ffmpeg filter graphs in FFmpegProcessor.java.

License: adapt to your needs.
```