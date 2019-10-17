package info.gravitypianist.fist.base.dispatcher;

import info.gravitypianist.fist.base.Dispatcher;
import info.gravitypianist.fist.base.Observer;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class DefaultDispatcherTest {

    @Test
    public void notAllowDuplication() {
        Dispatcher<SampleAction> dispatcher = DefaultDispatcher.create();

        Observer<SampleAction> observer = mock(SampleObserver.class);

        dispatcher.process(observer);

        dispatcher.dispatch(SampleAction.ACTION1);
        dispatcher.dispatch(SampleAction.ACTION1);
        dispatcher.dispatch(SampleAction.ACTION2);
        dispatcher.dispatch(SampleAction.ACTION3);

        InOrder order = inOrder(observer);

        order.verify(observer, times(2)).update(SampleAction.ACTION1);
        order.verify(observer).update(SampleAction.ACTION2);
        order.verify(observer).update(SampleAction.ACTION3);
    }

    enum SampleAction {
        ACTION1,
        ACTION2,
        ACTION3
    }

    static class SampleObserver implements Observer<SampleAction> {
        @Override
        public void update(@NotNull SampleAction value) {
        }
    }

}
