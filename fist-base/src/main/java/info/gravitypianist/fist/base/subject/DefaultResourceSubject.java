package info.gravitypianist.fist.base.subject;

import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import info.gravitypianist.fist.base.ResourceSubject;
import info.gravitypianist.fist.base.internal.subject.DefaultSubject;
import info.gravitypianist.fist.base.internal.subject.Subject;
import org.jetbrains.annotations.NotNull;

public class DefaultResourceSubject<Value, Error extends Throwable> implements ResourceSubject<Value, Error> {

    private Subject<Resource<Value, Error>> subject;

    private Resource.ErrorMapping<Value, Error> errorMapping;

    private DefaultResourceSubject(
            Subject<Resource<Value, Error>> subject,
            Resource.ErrorMapping<Value, Error> errorMapping
    ) {
        this.subject = subject;
        this.errorMapping = errorMapping;
    }

    @NotNull
    public static <Value, Error extends Throwable> DefaultResourceSubject<Value, Error> create(
            Resource.ErrorMapping<Value, Error> errorMapping
    ) {
        return new DefaultResourceSubject<>(
                DefaultSubject.create(),
                errorMapping
        );
    }

    @Override
    public void inProgress() {
        subject.setValue(Resource.inProgress());
    }

    @Override
    public void onNext(@NotNull Value value) {
        subject.setValue(Resource.success(value));
    }

    @Override
    public void onError(@NotNull Throwable error) {
        subject.setValue(errorMapping.invoke(error));
    }

    @Override
    public void setObserver(@NotNull Observer<Resource<Value, Error>> observer) {
        subject.addObserver(observer);
    }

}
