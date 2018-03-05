package com.github.rutvijkumarshah.debounce;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Test case for Debounce.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DebounceTests {

    private Debouncer debouncer;
    private Counter counter;
    private static final int TEST_DELAY_TIME = 1000;//1 sec

    @Before
    public void setup() {
        Debouncer.setDefaultDelayTime(TEST_DELAY_TIME);
        debouncer = Debouncer.getInstance();
        counter = new Counter(0);
    }


    @After
    public void tearDown() {
        Debouncer.reset();
        counter = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultDelayTimeMustBeSetBeforeGetInstance() {
        Debouncer.setDefaultDelayTime(50);
    }

    @Test
    public void checkBasicSetup() {
        assertEquals(debouncer.getDefaultDelayTimeMillis(), TEST_DELAY_TIME);
        assertNotNull(debouncer.getMainHandler());
    }

    @Test
    public void taskShouldBeRemovedAfterFinished() {
        debouncer.debounce("Test", () -> counter.incrementBy(10));
        assertThat(debouncer.getRunnables().size(), is(1));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(debouncer.getRunnables().size(), is(0));
    }

    @Test
    public void listOfPendingTasksShouldMatchNotExecutedTask() {
        debouncer.debounce("Test1", () -> counter.incrementBy(10));
        debouncer.debounce("Test2", () -> counter.incrementBy(10));
        debouncer.debounce("Test3", () -> counter.incrementBy(10));
        assertThat(debouncer.getRunnables().size(), is(3));
        debouncer.flush("Test3");
        assertThat(debouncer.getRunnables().size(), is(2));
        debouncer.flush("Test2");
        assertThat(debouncer.getRunnables().size(), is(1));
        Debouncer.Worker worker = debouncer.getRunnables().get("Test1");
        assertNotNull(worker);
        assertNotNull(worker.getHandler());
        assertNotNull(worker.getKey());
        assertNotNull(worker.getRunnable());
        debouncer.flushAll();
        assertThat(debouncer.getRunnables().size(), is(0));
    }

    @Test
    public void flushShouldImmediatelyExecuteAndRemoveTask() {
        debouncer.debounce("Test1", () -> counter.incrementBy(10));
        assertEquals(debouncer.getRunnables().size(), 1);
        assertEquals(counter.getVal(), 0);
        debouncer.flush("Test1");
        assertEquals(debouncer.getRunnables().size(), 0);
        assertEquals(counter.getVal(), 10);
    }

    @Test
    public void clearShouldImmediatelyRemoveTaskWithoutExecuting() {
        debouncer.debounce("Test1", () -> counter.incrementBy(10));
        assertEquals(debouncer.getRunnables().size(), 1);
        assertEquals(counter.getVal(), 0);
        debouncer.clear("Test1");
        assertEquals(debouncer.getRunnables().size(), 0);
        assertEquals(counter.getVal(), 0);
    }

    @Test
    public void lastTaskShouldBeExecuteIfSameTaskIsSubmittedMultipleTimes() {
        counter.setVal(0);
        debouncer.debounce("Test", () -> counter.incrementBy(10));
        debouncer.debounce("Test", () -> counter.incrementBy(20));
        debouncer.debounce("Test", () -> counter.incrementBy(30));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(counter.getVal(), is(30));
    }


    /**
     * Counter class to help in testing.
     */
    class Counter {

        private int val;

        Counter(int initVal) {
            val = initVal;
        }

        void incrementBy(int by) {
            val = val + by;
        }

        void increment() {
            val++;
        }

        int getVal() {
            return val;
        }

        void setVal(int val) {
            this.val = val;
        }
    }
}
