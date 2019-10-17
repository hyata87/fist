package info.gravitypianist.fist.rx.model;

import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Resource;
import info.gravitypianist.fist.base.model.ModelLogic;
import info.gravitypianist.fist.rx.RxModel;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import org.jetbrains.annotations.NotNull;

public class DefaultRxModel<Action, Value, Error extends Throwable> implements RxModel<Action, Value, Error> {

    private ModelLogic<Action, Value, Error> logic;

    private PublishProcessor<Resource<Value, Error>> resourceProcessor = PublishProcessor.create();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Scheduler scheduler;

    private DefaultRxModel(
            Scheduler scheduler,
            ModelLogic<Action, Value, Error> logic
    ) {
        this.logic = logic;
        this.scheduler = scheduler;

        compositeDisposable.add(
                createDispatcherStreamFromLogic(this.logic)
                        .subscribeOn(scheduler)
                        .observeOn(scheduler)
                        .subscribe(action -> this.logic.update(action))
        );

        compositeDisposable.add(
                createResourceStreamFromLogic(this.logic)
                        .subscribeOn(scheduler)
                        .observeOn(scheduler)
                        .subscribe(value -> resourceProcessor.onNext(value))
        );
    }

    public static <Action, Value, Error extends Throwable> RxModel<Action, Value, Error> create(
            @NotNull Scheduler scheduler,
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping
    ) {
        return new DefaultRxModel<>(
                scheduler,
                ModelLogic.create(
                        actionMapping,
                        errorMapping
                )
        );
    }

    public static <Action, Value, Error extends Throwable> RxModel<Action, Value, Error> create(
            @NotNull Scheduler scheduler,
            @NotNull Model.ActionMapping<Action, Value> actionMapping,
            @NotNull Resource.ErrorMapping<Value, Error> errorMapping,
            @NotNull Value value
    ) {
        return new DefaultRxModel<>(
                scheduler,
                ModelLogic.create(
                        actionMapping,
                        errorMapping,
                        value
                )
        );
    }

    private Flowable<Action> createDispatcherStreamFromLogic(
            final ModelLogic<Action, Value, Error> logic
    ) {
        final PublishProcessor<Action> processor = PublishProcessor.create();
        logic.setDispatcherObserver(processor::onNext);
        return processor;
    }

    private Flowable<Resource<Value, Error>> createResourceStreamFromLogic(
            final ModelLogic<Action, Value, Error> logic
    ) {
        final PublishProcessor<Resource<Value, Error>> processor = PublishProcessor.create();
        logic.setResourceObserver(processor::onNext);
        return processor;
    }

    @Override
    public Flowable<Resource<Value, Error>> getResource() {
        return resourceProcessor;
    }

    @Override
    public void dispatch(@NotNull Action action) {
        this.scheduler.scheduleDirect(() -> logic.dispatch(action));
    }

    @Override
    public void dispose() {
        compositeDisposable.dispose();
        resourceProcessor.onComplete();
    }
}
