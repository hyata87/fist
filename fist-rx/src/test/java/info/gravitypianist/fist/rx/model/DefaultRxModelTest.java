package info.gravitypianist.fist.rx.model;


import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Resource;
import info.gravitypianist.fist.rx.RxModel;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class DefaultRxModelTest {

    @Test
    public void test() {
        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);

        when(actionMapping.invoke(any())).thenReturn(mock(Sample.class));
        when(actionMapping.invoke(any(), any())).thenReturn(mock(Sample.class));

        final TestScheduler testScheduler = new TestScheduler();

        RxModel<SampleAction, Sample, SampleError> sampleModel = DefaultRxModel.create(
                testScheduler,
                actionMapping,
                errorMapping
        );

        TestSubscriber<Resource<Sample, SampleError>> testSubscriber
                = sampleModel.getResource().subscribeOn(testScheduler).test();

        sampleModel.dispatch(SampleAction.ACTION1);

        sampleModel.dispatch(SampleAction.ACTION2);

        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        sampleModel.dispose();

        testSubscriber.assertValueAt(0, resource -> resource instanceof Resource.InProgress);

        testSubscriber.assertValueAt(1, resource -> resource instanceof Resource.Success);

        testSubscriber.assertValueAt(2, resource -> resource instanceof Resource.InProgress);

        testSubscriber.assertValueAt(3, resource -> resource instanceof Resource.Success);

        testSubscriber.assertComplete();
    }

    enum SampleAction {
        ACTION1,
        ACTION2,
        ACTION3
    }

    static class Sample {

        private String title;
        private String decs;

        Sample(String title, String decs) {
            this.title = title;
            this.decs = decs;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDecs() {
            return decs;
        }

        public void setDecs(String decs) {
            this.decs = decs;
        }

    }

    static class SampleError extends Throwable {

        static class NoError extends SampleError {

        }

        static class UnknownError extends SampleError {

            private Throwable error;

            UnknownError(Throwable error) {
                this.error = error;
            }

            public Throwable getError() {
                return error;
            }
        }


    }

    static class SampleActionMapping implements Model.ActionMapping<SampleAction, Sample> {

        @Override
        public Sample invoke(@NotNull SampleAction sampleAction) {
            return null;
        }

        @Override
        public Sample invoke(@NotNull SampleAction sampleAction, @NotNull Sample sample) {
            return null;
        }
    }

    static class SampleErrorMapping implements Resource.ErrorMapping<Sample, SampleError> {
        @Override
        public Resource<Sample, SampleError> invoke(Throwable error) {
            return null;
        }
    }
}
