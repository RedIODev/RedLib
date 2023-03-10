package dev.redio.internal.concurrent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class StateMachine {    //TODO:Replace method with fully qualified method

    private static final Map<Method, Class<? extends StateMachine>> stateMachineMap = new HashMap<>();

    protected Object[] methodParameters;

    protected static void addMachine(Class<?> sourceType, String methodName, Class<?>[] parameterTypes,
            Class<? extends StateMachine> machineType) {
        try {
            var method = sourceType.getDeclaredMethod(methodName, parameterTypes);
            stateMachineMap.put(method, machineType);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AsyncRuntimeError(e);
        }
    }

}
