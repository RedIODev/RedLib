package dev.redio.concurrent.test;

import java.lang.reflect.Method;

import dev.redio.concurrent.task.Context;
import dev.redio.concurrent.task.Poll;
import dev.redio.concurrent.task.Task;
import dev.redio.internal.concurrent.StateMachine;

public class AsyncTest$WaitAsync extends StateMachine implements Task<Void> {

    static {
        addMachine(AsyncTest.class, "waitAsync", null, AsyncTest$WaitAsync.class);
    }

    enum State {
        PART0,
        PART1
    }

    private State state = State.PART0;

    @Override
    public Poll<Void> poll(Context context) {
        return switch (state) {
            case PART0 -> {
                part1();
                state = State.PART1;
                yield new Poll.Pending<>();
            }
            case PART1 -> {
                yield new Poll.Ready<>(null);
            }
        };
    }

    private void part1() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
