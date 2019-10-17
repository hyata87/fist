package info.gravitypianist.fist.base.internal.subject;

import info.gravitypianist.fist.base.internal.maybe.Maybe;
import info.gravitypianist.fist.base.internal.maybe.None;
import info.gravitypianist.fist.base.internal.maybe.Some;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultSubject<T> extends Subject<T> {

    private Maybe<T> value;

    private DefaultSubject(@NotNull Maybe<T> value) {
        this.value = value;
    }

    public static <T> Subject<T> create() {
        return new DefaultSubject<>(None.of());
    }

    public static <T> Subject<T> create(@Nullable T defaultValue) {
        return new DefaultSubject<>(Maybe.ofNullable(defaultValue));
    }

    @Override
    @NotNull
    public Maybe<T> getValue() {
        return value;
    }

    @Override
    public void setValue(@NotNull T newValue) {
        value = Some.of(newValue);
        notifyObservers();
    }

}
