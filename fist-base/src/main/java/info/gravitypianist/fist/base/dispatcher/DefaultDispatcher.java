package info.gravitypianist.fist.base.dispatcher;

import info.gravitypianist.fist.base.Dispatcher;
import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.internal.subject.DefaultSubject;
import info.gravitypianist.fist.base.internal.subject.Subject;
import org.jetbrains.annotations.NotNull;

public class DefaultDispatcher<Action> implements Dispatcher<Action> {

    private Subject<Action> subject;

    private DefaultDispatcher(Subject<Action> subject) {
        this.subject = subject;
    }

    @NotNull
    public static <Action> Dispatcher<Action> create() {
        return new DefaultDispatcher<>(DefaultSubject.create(null));
    }

    @Override
    public void dispatch(@NotNull Action action) {
        subject.setValue(action);
    }

    @Override
    public void process(@NotNull Observer<Action> observer) {
        subject.addObserver(observer);
    }
}
