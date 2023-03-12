package dev.redio.concurrent.test;

import dev.redio.concurrent.task.Context;
import dev.redio.concurrent.task.Poll;
import dev.redio.concurrent.task.Task;
import dev.redio.internal.concurrent.StateMachine;

public class AsyncTest$TestAsync extends StateMachine implements Task<String> {

    static {
        addMachine(AsyncTest.class, "testAsync", null, AsyncTest$TestAsync.class);
    }

    enum State {
        PART0,
        PART1,
        ASYNC1POLL,
        PART2,
    }

    private State state = State.PART0;

    private Task<Void> asyncResult1;

    private String string;

    @Override
    public Poll<String> poll(Context context) {
        return switch (state) {
            case PART0 -> {
                part1();
                state = State.PART1;
                yield new Poll.Pending<>();
            }
            default -> throw new RuntimeException();
        };
    }

    private void part1() {
        string = System.console().readLine();

    }

    private void part2() {

    }

}
