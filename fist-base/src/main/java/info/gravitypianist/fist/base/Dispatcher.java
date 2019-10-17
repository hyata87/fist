package info.gravitypianist.fist.base;

import org.jetbrains.annotations.NotNull;

public interface Dispatcher<Action> {
    void dispatch(@NotNull Action action);

    void process(@NotNull Observer<Action> observer);
}
