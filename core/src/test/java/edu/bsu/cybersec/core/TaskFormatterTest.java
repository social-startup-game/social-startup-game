package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.base.Preconditions.checkState;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public final class TaskFormatterTest {

    private TaskFormatter formatter;

    @Before
    public void setUp() {
        formatter = new TaskFormatter();
    }

    @Test
    public void testAsString_returnsANonEmptyString() {
        String actual = formatter.format(Task.IDLE);
        assertFalse(actual.isEmpty());
    }

    @Test
    public void testAsTask_returnsTheTask() {
        String string = formatter.format(Task.IDLE);
        int task = formatter.asTask(string);
        assertEquals(Task.IDLE, task);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormat_throwsExceptionOnUnrecognizedInput() {
        final int nonTaskInteger = Integer.MIN_VALUE;
        checkState(!Task.VALUES.contains(nonTaskInteger));
        formatter.format(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsTask_throwsExceptionOnUnrecognizedInput() {
        final String nonTaskString = "";
        formatter.asTask(nonTaskString);
    }
}
