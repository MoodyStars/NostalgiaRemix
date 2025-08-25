```markdown
# Nostalgia Mody Videos Generator (Java Swing)

A simple Java Swing GUI tool to generate brainstorming "plans" for nostalgia-style remix videos: YouTube Poop (YTP), YTPMV, remixes, loops, and short "annoying" edits. This generates textual plans (segment-by-segment ideas) that you can use to assemble videos in your editor (or extend to drive actual processing tools).

Important: This program does NOT edit media. It produces a script/plan only.

Requirements
- JDK 8+ (recommended Java 11+)
- No external libraries required

Build & Run (simple)
1. From repository root:
   - Compile:
     javac -d out src/main/java/com/moodystars/nostalgia/*.java
   - Run:
     java -cp out com.moodystars.nostalgia.Main

2. Or use your IDE (IntelliJ IDEA, Eclipse) — import as a plain Java project and run Main.

Usage
- Toggle options: include YTP / YTPMV / Remix, Round/Loop segments, commercial insertion, and an "annoying" mode.
- Adjust speed slider to suggest tempo changes.
- Set number of segments to generate.
- Click "Generate Plan" to create a textual plan.
- Click "Export Plan..." to save the text file.

Extending this project
- Integrate ffmpeg, TarsosDSP, or similar to implement actual audio/video processing.
- Add presets for known YTP/YTPMV styles.
- Save/load projects, export JSON for external tools.
- Add sample bank management and mapping from plan to processing pipeline.

License: Use as you like. This is a template / brainstorming tool; respect copyright for original media when creating actual remixes.
```