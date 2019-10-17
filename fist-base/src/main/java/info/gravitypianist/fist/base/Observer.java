package info.gravitypianist.fist.base;

import org.jetbrains.annotations.NotNull;

public interface Observer<T> {
    void update(@NotNull T value);
}
