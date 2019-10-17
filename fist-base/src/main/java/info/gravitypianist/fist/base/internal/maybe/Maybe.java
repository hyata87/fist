package info.gravitypianist.fist.base.internal.maybe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Maybe<T> {

    @NotNull
    static <T> Maybe<T> ofNullable(@Nullable T value) {
        if (value == null) {
            return None.of();
        }
        return Some.of(value);
    }

    @NotNull Boolean hasValue();

    @NotNull T getValue();
}
