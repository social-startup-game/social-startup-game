package edu.bsu.cybersec.core;


import static com.google.common.base.Preconditions.checkNotNull;

public class NarrativeEvent implements Runnable {

    public static class Option {
        public final String text;
        public final Runnable action;

        public Option(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }

    private final GameWorld gameWorld;
    public final String text;
    public final Option[] options;

    public NarrativeEvent(GameWorld world, String text, Option... options) {
        this.gameWorld = checkNotNull(world);
        this.text = text;
        this.options = options;
    }

    @Override
    public void run() {
        gameWorld.onNarrativeEvent.emit(this);
    }
}
