package info.gravitypianist.fist.base;

import org.jetbrains.annotations.NotNull;

public interface Model<Action, Value, Error extends Throwable> {

    void setResourceObserver(@NotNull Observer<Resource<Value, Error>> observer);

    void dispatch(@NotNull Action action);

    interface ActionMapping<Action, Value> {
        Value invoke(@NotNull Action action);

        Value invoke(@NotNull Action action, @NotNull Value value);
    }

}
