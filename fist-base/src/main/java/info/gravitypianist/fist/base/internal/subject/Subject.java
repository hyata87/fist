package info.gravitypianist.fist.base.internal.subject;

import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.internal.maybe.Maybe;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Subject<T> {

    private Set<Observer<T>> observers = new CopyOnWriteArraySet<>();

    public void addObserver(@NotNull Observer<T> observer) {
        observers.add(observer);
    }

    public void removeObservers() {
        observers.clear();
    }

    void notifyObservers() {
        Maybe<T> value = getValue();
        if (!value.hasValue()) {
            return;
        }
        T data = value.getValue();

        for (Observer<T> observer : observers) {
            observer.update(data);
        }
    }

    @NotNull
    public abstract Maybe<T> getValue();

    public abstract void setValue(@NotNull T newValue);

}
