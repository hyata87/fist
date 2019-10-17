package info.gravitypianist.fist.rx;

import info.gravitypianist.fist.base.Resource;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

public interface RxModel<Action, Value, Error extends Throwable> {

    Flowable<Resource<Value, Error>> getResource();

    void dispatch(@NotNull Action action);

    void dispose();

}
