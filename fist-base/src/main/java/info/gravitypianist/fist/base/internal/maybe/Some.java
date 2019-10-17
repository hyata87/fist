package info.gravitypianist.fist.base.internal.maybe;


import org.jetbrains.annotations.NotNull;

public final class Some<T> implements Maybe<T> {

    private T value;

    private Some(@NotNull T value) {
        this.value = value;
    }

    @NotNull
    public static <T> Maybe<T> of(@NotNull T value) {
        return new Some<>(value);
    }

    @Override
    @NotNull
    public Boolean hasValue() {
        return true;
    }

    @Override
    @NotNull
    public T getValue() {
        return value;
    }

}
