package info.gravitypianist.fist.base.model;

import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class DefaultModelTest {

    @Test
    @SuppressWarnings("unchecked")
    public void Dispatch_SendActions_ReceivedResources() {
        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        when(actionMapping.invoke(any())).thenReturn(mock(Sample.class));

        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);

        Model<SampleAction, Sample, SampleError> model = DefaultModel.create(actionMapping, errorMapping);

        SampleResourceObserver observer = mock(SampleResourceObserver.class);
        model.setResourceObserver(observer);

        model.dispatch(SampleAction.ACTION1);
        model.dispatch(SampleAction.ACTION2);
        model.dispatch(SampleAction.ACTION3);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InOrder order = inOrder(observer);
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
    }

    private enum SampleAction {
        ACTION1,
        ACTION2,
        ACTION3
    }

    private static class Sample {
    }

    private static class SampleError extends Throwable {
    }

    private abstract static class SampleActionMapping implements Model.ActionMapping<SampleAction, Sample> {
    }

    private abstract static class SampleErrorMapping implements Resource.ErrorMapping<Sample, SampleError> {
    }

    private abstract static class SampleResourceObserver implements Observer<Resource<Sample, SampleError>> {
    }
}
