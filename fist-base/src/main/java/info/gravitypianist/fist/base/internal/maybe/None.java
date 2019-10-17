package info.gravitypianist.fist.base.internal.maybe;

import org.jetbrains.annotations.NotNull;

public final class None<T> implements Maybe<T> {

    @NotNull
    public static <T> Maybe<T> of() {
        return new None<>();
    }

    @Override
    @NotNull
    public Boolean hasValue() {
        return false;
    }

    @Override
    @NotNull
    public T getValue() {
        throw new NullPointerException("value is null");
    }

}
