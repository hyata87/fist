package info.gravitypianist.fist.base.model;

import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class DefaultModelTest {

    @Test
    public void test() {

        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);

        when(actionMapping.invoke(any())).thenReturn(mock(Sample.class));
        when(actionMapping.invoke(any(), any())).thenReturn(mock(Sample.class));

        Model<SampleAction, Sample, SampleError> sampleModel = DefaultModel.create(
                actionMapping,
                errorMapping
        );

        SampleResourceObserver resourceObserver = mock(SampleResourceObserver.class);

        sampleModel.setResourceObserver(resourceObserver);

        sampleModel.dispatch(SampleAction.ACTION1);
        sampleModel.dispatch(SampleAction.ACTION2);
        sampleModel.dispatch(SampleAction.ACTION3);

        InOrder actionOrder = inOrder(actionMapping);

        actionOrder.verify(actionMapping).invoke(SampleAction.ACTION1);
        actionOrder.verify(actionMapping).invoke(eq(SampleAction.ACTION2), any());
        actionOrder.verify(actionMapping).invoke(eq(SampleAction.ACTION3), any());

        InOrder resourceOrder = inOrder(resourceObserver);

        resourceOrder.verify(resourceObserver).update(isA(Resource.InProgress.class));
        resourceOrder.verify(resourceObserver).update(isA(Resource.Success.class));
        resourceOrder.verify(resourceObserver).update(isA(Resource.InProgress.class));
        resourceOrder.verify(resourceObserver).update(isA(Resource.Success.class));
        resourceOrder.verify(resourceObserver).update(isA(Resource.InProgress.class));
        resourceOrder.verify(resourceObserver).update(isA(Resource.Success.class));

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

    static class SampleResourceObserver implements Observer<Resource<Sample, SampleError>> {
        @Override
        public void update(@NotNull Resource<Sample, SampleError> value) {
            System.out.println(value);
        }
    }
}
