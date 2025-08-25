package com.example.nostalgiamody;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

/**
 * Simple helper that runs ffmpeg to perform basic operations.
 * This is example code: improve error handling and temporary file cleanup for production.
 */
public class FFmpegProcessor {

    private static void runCommand(List<String> cmd, Consumer<Double> progressCallback) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            // Very simple progress parsing: read ffmpeg stderr lines and ignore.
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    // parse time= or frame= lines if you want progress
                    // For now call a tiny progress pulse
                    if (progressCallback != null) {
                        progressCallback.accept(0.5);
                    }
                    System.out.println(line);
                }
            }
            int rc = p.waitFor();
            if (progressCallback != null) progressCallback.accept(rc == 0 ? 1.0 : 0.0);
        } catch (Exception e) {
            e.printStackTrace();
            if (progressCallback != null) progressCallback.accept(0.0);
        }
    }

    public static void changeSpeed(File in, File out, double multiplier, Consumer<Double> progressCallback) {
        // Note: atempo supports 0.5-2.0 per filter. For >2.0 you'd chain atempo filters.
        String atempo = String.format(Locale.ROOT, "%.3f", multiplier);
        List<String> cmd = Arrays.asList(
                "ffmpeg", "-y", "-i", in.getAbsolutePath(),
                "-filter_complex", String.format("[0:v]setpts=PTS/%s[v];[0:a]atempo=%s[a]", atempo, atempo),
                "-map", "[v]", "-map", "[a]",
                out.getAbsolutePath()
        );
        runCommand(cmd, progressCallback);
    }

    public static void randomRemix(File in, File out, int clips, double clipLengthSeconds, Consumer<Double> progressCallback) {
        try {
            double duration = probeDuration(in);
            if (duration <= 0) duration = clips * clipLengthSeconds;
            File tmpDir = Files.createTempDirectory("remix").toFile();
            List<File> parts = new ArrayList<>();
            Random rnd = new Random();
            List<String> concatLines = new ArrayList<>();
            for (int i = 0; i < clips; i++) {
                double start = Math.max(0, rnd.nextDouble() * (duration - clipLengthSeconds));
                File part = new File(tmpDir, "part" + i + ".mp4");
                List<String> cmd = Arrays.asList("ffmpeg", "-y", "-ss", String.valueOf(start), "-i", in.getAbsolutePath(), "-t",
                        String.valueOf(clipLengthSeconds), "-c", "copy", part.getAbsolutePath());
                runCommand(cmd, (p)->{
                    if (progressCallback != null) progressCallback.accept( (i + p) / (double)clips * 0.9 );
                });
                parts.add(part);
                concatLines.add("file '" + part.getAbsolutePath().replace("'", "'\\''") + "'");
            }

            File concatFile = new File(tmpDir, "concat.txt");
            try (PrintWriter pw = new PrintWriter(concatFile)) {
                for (String l : concatLines) pw.println(l);
            }
            List<String> finalCmd = Arrays.asList("ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", concatFile.getAbsolutePath(), "-c", "copy", out.getAbsolutePath());
            runCommand(finalCmd, (p)->{
                if (progressCallback != null) progressCallback.accept(0.95 + p*0.05);
            });
        } catch (IOException e) {
            e.printStackTrace();
            if (progressCallback != null) progressCallback.accept(0.0);
        }
    }

    public static void roundMask(File in, File out, Consumer<Double> progressCallback) {
        // Example round mask: crop to square, then apply circular alpha mask using ffmpeg's geq (advanced).
        // This example uses a simple vignette as a placeholder; replace with a proper circular mask if desired.
        List<String> cmd = Arrays.asList("ffmpeg", "-y", "-i", in.getAbsolutePath(),
                "-vf", "crop='min(iw,ih)':'min(iw,ih)',scale=720:720,format=rgba,geq=r='r(X,Y)':g='g(X,Y)':b='b(X,Y)':a='if(lt(pow(X-W/2,2)+pow(Y-H/2,2),pow(min(W,H)/2,2)),255,0)'",
                "-c:v", "libx264", "-pix_fmt", "yuv420p", out.getAbsolutePath());
        runCommand(cmd, progressCallback);
    }

    public static void annoyingFaster(File in, File out, Consumer<Double> progressCallback) {
        // Example "annoying" preset: speed up video + repeat very short clips (stutter).
        // We'll speed up by 1.75 and add a tremor effect by concatenating repeated tiny segments.
        try {
            File tmp = Files.createTempFile("annoy", ".mp4").toFile();
            changeSpeed(in, tmp, 1.75, (p)->{
                if (progressCallback != null) progressCallback.accept(p*0.6);
            });

            // Create a stutter by extracting many 0.08s clips and concatenating
            double clipLen = 0.08;
            double duration = probeDuration(tmp);
            int parts = Math.min(80, (int)(duration/clipLen));
            File tmpDir = Files.createTempDirectory("sttrs").toFile();
            List<String> concatLines = new ArrayList<>();
            for (int i=0;i<parts;i++) {
                double start = i * clipLen;
                File part = new File(tmpDir, "p" + i + ".mp4");
                List<String> cmd = Arrays.asList("ffmpeg", "-y", "-ss", String.valueOf(start), "-i", tmp.getAbsolutePath(), "-t",
                        String.valueOf(clipLen), "-c", "copy", part.getAbsolutePath());
                runCommand(cmd, (p2)->{
                    if (progressCallback != null) progressCallback.accept(0.6 + (i + p2)/parts*0.35);
                });
                concatLines.add("file '" + part.getAbsolutePath().replace("'", "'\\''") + "'");
            }
            File concatFile = new File(tmpDir, "concat.txt");
            try (PrintWriter pw = new PrintWriter(concatFile)) {
                for (String l : concatLines) pw.println(l);
            }
            List<String> finalCmd = Arrays.asList("ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", concatFile.getAbsolutePath(), "-c", "copy", out.getAbsolutePath());
            runCommand(finalCmd, (p)->{ if (progressCallback != null) progressCallback.accept(0.98 + p*0.02); });
        } catch (IOException e) {
            e.printStackTrace();
            if (progressCallback != null) progressCallback.accept(0.0);
        }
    }

    private static double probeDuration(File in) {
        // very naive duration probe using ffprobe if available
        try {
            ProcessBuilder pb = new ProcessBuilder("ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", in.getAbsolutePath());
            Process p = pb.start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = r.readLine();
                if (line != null) {
                    return Double.parseDouble(line.trim());
                }
            }
            p.waitFor();
        } catch (Exception e) {
            // ignore
        }
        return -1;
    }
}