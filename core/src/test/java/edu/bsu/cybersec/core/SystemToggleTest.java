package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;
import tripleplay.entity.System;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SystemToggleTest {

    private tripleplay.entity.System system;
    private SystemToggle toggle;

    @Before
    public void setUp() {
        system = mock(System.class);
        toggle = new SystemToggle(system);
    }

    @Test
    public void testDisable() {
        toggle.disable();
        verify(system).setEnabled(false);
    }

    @Test
    public void testEnable() {
        toggle.enable();
        verify(system).setEnabled(true);
    }
}

