package info.gravitypianist.fist.base;

import org.jetbrains.annotations.NotNull;

public interface ResourceSubject<Value, Error extends Throwable> {

    void setObserver(@NotNull Observer<Resource<Value, Error>> observer);

    void inProgress();

    void onNext(@NotNull Value value);

    void onError(@NotNull Throwable error);

}
