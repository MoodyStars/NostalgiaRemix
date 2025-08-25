package com.moodystars.nostalgia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NostalgiaGenerator builds textual "plans" for nostalgia-style remix videos (YTP/YTPMV/remix).
 * This class is intentionally lightweight and does NOT manipulate media files. It produces
 * suggestions that you can follow when editing.
 */
public class NostalgiaGenerator {

    private final Random random;

    public NostalgiaGenerator(long seed) {
        this.random = new Random(seed);
    }

    public NostalgiaGenerator() {
        this.random = new Random();
    }

    public static class Options {
        public boolean includeYTP = true;
        public boolean includeYTPMV = true;
        public boolean includeRemix = true;
        public boolean addRoundLoop = false;
        public int speedPercent = 100; // 50 .. 200
        public boolean annoying = false;
        public boolean addCommercial = false;
        public int segmentCount = 6; // number of segments
    }

    public String generatePlan(Options opts) {
        StringBuilder out = new StringBuilder();
        out.append("Nostalgia Mody Video Plan\n");
        out.append("=========================\n\n");

        out.append("Options: \n");
        out.append(String.format("  YTP: %s, YTPMV: %s, Remix: %s\n", opts.includeYTP, opts.includeYTPMV, opts.includeRemix));
        out.append(String.format("  Round/Loop: %s, Speed: %d%%, Annoying effects: %s, Commercials: %s\n",
                opts.addRoundLoop, opts.speedPercent, opts.annoying, opts.addCommercial));
        out.append(String.format("  Segments: %d\n\n", opts.segmentCount));

        List<String> sources = pickSources(opts);
        List<String> transitions = defaultTransitions();
        List<String> effects = defaultEffects();

        out.append("Core sources (pool):\n");
        for (String s : sources) out.append(" - ").append(s).append("\n");
        out.append("\n");

        out.append("Plan (segment-by-segment):\n");

        for (int i = 1; i <= opts.segmentCount; i++) {
            out.append(String.format("Segment %d:\n", i));
            String src = sources.get(random.nextInt(sources.size()));
            out.append("  Source: ").append(src).append("\n");

            // Pick a transition into this segment (except for first)
            if (i > 1) {
                String t = transitions.get(random.nextInt(transitions.size()));
                out.append("  Transition from previous: ").append(t).append("\n");
            }

            // Effects stack
            List<String> chosenEffects = new ArrayList<>();
            // Always add a tempo modifier suggestion if speed != 100
            if (opts.speedPercent != 100) {
                chosenEffects.add(String.format("tempo x%d%%", opts.speedPercent));
            }

            if (opts.addRoundLoop && random.nextDouble() < 0.6) {
                chosenEffects.add("loop (round) two-beat repeat");
            }

            if (opts.includeYTP && random.nextDouble() < 0.5) {
                chosenEffects.add("glitch cut / stutter (YTP style)");
            }

            if (opts.includeYTPMV && random.nextDouble() < 0.45) {
                chosenEffects.add("sample pitch-shift / chop to melody (YTPMV style)");
            }

            if (opts.includeRemix && random.nextDouble() < 0.6) {
                chosenEffects.add("beat align & rhythm edit (remix)");
            }

            // Annoying effects more likely if opted-in
            if (opts.annoying) {
                if (random.nextDouble() < 0.8) chosenEffects.add("harsh clipping / heavy distortion");
                if (random.nextDouble() < 0.5) chosenEffects.add("random reverse snippets");
                if (random.nextDouble() < 0.5) chosenEffects.add("ear-piercing high freq sweep (short)");
            } else {
                if (random.nextDouble() < 0.25) chosenEffects.add("mild tape warble / wow & flutter");
            }

            // Add a random effect
            chosenEffects.add(effects.get(random.nextInt(effects.size())));

            for (String e : chosenEffects) {
                out.append("  Effect: ").append(e).append("\n");
            }

            // Suggest clip length
            int baseLen = 3 + random.nextInt(8); // 3-10s
            if (opts.annoying) baseLen = Math.max(1, baseLen / 2);
            out.append("  Duration (approx): ").append(baseLen).append("s\n");

            out.append("\n");
        }

        if (opts.addCommercial && random.nextDouble() < 0.9) {
            out.append("--- Inserted Nostalgia Commercial / Jingle ---\n");
            out.append("  Idea: quick 6s fake commercial for retro cereal (or toy), saturated colors, cheesy voice sample.\n\n");
        }

        out.append("Notes & polish suggestions:\n");
        out.append(" - Automate quick tempo changes between segments.\n");
        out.append(" - Use key-aware pitch shifts for YTPMV sections.\n");
        out.append(" - Add sudden cuts and a tiny 1-frame stutter to create classic YTP comedic timing.\n");
        out.append(" - Consider an 'annoying' master limiter on short sections, but avoid permanent damage to listener comfort.\n");

        // Add a random remix title
        out.append("\nGenerated Title Suggestions:\n");
        for (int i = 0; i < 5; i++) {
            out.append(" - ").append(makeTitle()).append("\n");
        }

        return out.toString();
    }

    private List<String> pickSources(Options opts) {
        List<String> pool = new ArrayList<>();
        // basic nostalgia sources
        String[] base = new String[]{
                "90s Cartoon Theme (chorus snippet)",
                "Educational TV Clip (announcer voice)",
                "Retro Game Soundtrack loop",
                "Old Commercial Jingle (toy)",
                "VHS sitcom laugh track",
                "Public domain movie line (dramatic)",
                "80s Pop chorus (sampled short)",
                "Late-night host clip (yelled line)",
                "Arcade cabinet sound effects",
                "Kids show theme (naive melody)"
        };
        for (String b : base) pool.add(b);

        // Add YTP-specific samples if asked
        if (opts.includeYTP) {
            pool.add("Cartoon character scream - chopped");
            pool.add("Weird off-tune singing - cut");
        }
        if (opts.includeYTPMV) {
            pool.add("Short melodic vocal sample to map");
            pool.add("Drum hit to be time-stretched");
        }
        if (opts.includeRemix) {
            pool.add("Acapella phrase for chops");
            pool.add("Synth stab for drop");
        }
        return pool;
    }

    private List<String> defaultTransitions() {
        List<String> t = new ArrayList<>();
        t.add("hard cut");
        t.add("white flash + reverse blur");
        t.add("stutter cut (3x)");
        t.add("beat-synced slice");
        t.add("spin/glitch wipe");
        return t;
    }

    private List<String> defaultEffects() {
        List<String> e = new ArrayList<>();
        e.add("bitcrush");
        e.add("tremolo");
        e.add("filter sweep");
        e.add("phaser");
        e.add("vocal chop");
        return e;
    }

    private String makeTitle() {
        String[] a = {"Nostalgia", "Mody", "Remix", "YTP", "Glitch", "Retro", "Annoying", "Loop", "Mega", "Odd"};
        String[] b = {"Mashup", "Compilation", "Hits", "Edit", "Remix", "Rewind", "Jank", "Sampler"};
        return a[random.nextInt(a.length)] + " " + b[random.nextInt(b.length)] + " " + (100 + random.nextInt(900));
    }
}