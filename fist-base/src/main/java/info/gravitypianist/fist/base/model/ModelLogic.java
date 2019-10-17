package info.gravitypianist.fist.base.model;

import info.gravitypianist.fist.base.*;
import info.gravitypianist.fist.base.dispatcher.DefaultDispatcher;
import info.gravitypianist.fist.base.internal.maybe.Maybe;
import info.gravitypianist.fist.base.internal.subject.DefaultSubject;
import info.gravitypianist.fist.base.internal.subject.Subject;
import info.gravitypianist.fist.base.subject.DefaultResourceSubject;
import org.jetbrains.annotations.NotNull;

public final class ModelLogic<Action, Value, Error extends Throwable> {

    private final Dispatcher<Action> dispatcher;

    private final ResourceSubject<Value, Error> resourceSubject;

    private final Subject<Value> valueSubject;

    private final Model.ActionMapping<Action, Value> actionMapping;

    private ModelLogic(
            Subject<Value> valueSubject,
            Dispatcher<Action> dispatcher,
            ResourceSubject<Value, Error> resourceSubject,
            Model.ActionMapping<Action, Value> actionMapping
    ) {
        this.valueSubject = valueSubject;
        this.dispatcher = dispatcher;
        this.resourceSubject = resourceSubject;
        this.actionMapping = actionMapping;
    }

    public static <Action, Value, Error extends Throwable> ModelLogic<Action, Value, Error> create(
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping
    ) {
        return new ModelLogic<>(
                DefaultSubject.create(),
                DefaultDispatcher.create(),
                DefaultResourceSubject.create(errorMapping),
                actionMapping
        );
    }

    public static <Action, Value, Error extends Throwable> ModelLogic<Action, Value, Error> create(
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping,
            @NotNull Value value
    ) {
        return new ModelLogic<>(
                DefaultSubject.create(value),
                DefaultDispatcher.create(),
                DefaultResourceSubject.create(errorMapping),
                actionMapping
        );
    }

    public void update(@NotNull Action action) {
        synchronized (dispatcher) {
            resourceSubject.inProgress();
            try {
                Maybe<Value> value = valueSubject.getValue();

                if (value.hasValue()) {
                    Value newValue = actionMapping.invoke(action, value.getValue());
                    valueSubject.setValue(newValue);
                    resourceSubject.onNext(newValue);
                } else {
                    Value newValue = actionMapping.invoke(action);
                    valueSubject.setValue(newValue);
                    resourceSubject.onNext(newValue);
                }
            } catch (Throwable error) {
                resourceSubject.onError(error);
            }
        }
    }

    public void setDispatcherObserver(Observer<Action> observer) {
        dispatcher.process(observer);
    }

    public void setResourceObserver(@NotNull Observer<Resource<Value, Error>> observer) {
        resourceSubject.setObserver(observer);
    }

    public void dispatch(@NotNull Action action) {
        dispatcher.dispatch(action);
    }
}
