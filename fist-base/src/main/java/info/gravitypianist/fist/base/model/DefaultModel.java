package info.gravitypianist.fist.base.model;

import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DefaultModel<Action, Value, Error extends Throwable>
        implements Model<Action, Value, Error> {

    private ModelLogic<Action, Value, Error> logic;

    private DefaultModel(
            ExecutorService executorService,
            ModelLogic<Action, Value, Error> logic
    ) {
        this.logic = logic;
        this.logic.setDispatcherObserver(action -> executorService.submit(() -> logic.update(action)));
    }

    public static <Action, Value, Error extends Throwable> DefaultModel<Action, Value, Error> create(
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping
    ) {
        return new DefaultModel<>(
                Executors.newSingleThreadExecutor(),
                ModelLogic.create(
                        actionMapping,
                        errorMapping
                )
        );
    }

    public static <Action, Value, Error extends Throwable> DefaultModel<Action, Value, Error> create(
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping,
            @NotNull Value value
    ) {
        return new DefaultModel<>(
                Executors.newSingleThreadExecutor(),
                ModelLogic.create(
                        actionMapping,
                        errorMapping,
                        value
                )
        );
    }

    @Override
    public void setResourceObserver(@NotNull Observer<Resource<Value, Error>> observer) {
        logic.setResourceObserver(observer);
    }

    @Override
    public void dispatch(@NotNull Action action) {
        logic.dispatch(action);
    }
}
