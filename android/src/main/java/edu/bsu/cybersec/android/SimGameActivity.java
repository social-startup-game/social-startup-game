package edu.bsu.cybersec.android;

import playn.android.GameActivity;

import edu.bsu.cybersec.core.SimGame;

public class SimGameActivity extends GameActivity {

    @Override
    public void main() {
        new SimGame(platform());
    }
}
