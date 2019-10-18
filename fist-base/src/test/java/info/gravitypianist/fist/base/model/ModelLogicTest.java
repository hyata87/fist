package info.gravitypianist.fist.base.model;


import info.gravitypianist.fist.base.Model;
import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class ModelLogicTest {

    @Test
    public void Dispatch_SendActions_ReceivedActions() {

        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        when(actionMapping.invoke(any())).thenReturn(mock(Sample.class));

        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);

        SampleDispatchObserver observer = mock(SampleDispatchObserver.class);

        ModelLogic<SampleAction, Sample, SampleError> logic = ModelLogic.create(actionMapping, errorMapping);
        logic.setDispatcherObserver(observer);

        logic.dispatch(SampleAction.ACTION1);
        logic.dispatch(SampleAction.ACTION2);
        logic.dispatch(SampleAction.ACTION3);

        InOrder order = inOrder(observer);
        order.verify(observer).update(SampleAction.ACTION1);
        order.verify(observer).update(SampleAction.ACTION2);
        order.verify(observer).update(SampleAction.ACTION3);
    }

    @Test
    public void Update_SendActions_ReceivedResources() {

        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        when(actionMapping.invoke(any())).thenReturn(mock(Sample.class));

        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);

        SampleResourceObserver observer = mock(SampleResourceObserver.class);

        ModelLogic<SampleAction, Sample, SampleError> logic = ModelLogic.create(actionMapping, errorMapping);
        logic.setResourceObserver(observer);

        logic.update(SampleAction.ACTION1);
        logic.update(SampleAction.ACTION2);
        logic.update(SampleAction.ACTION3);

        InOrder order = inOrder(observer);
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
    }

    @Test
    public void Update_SendActions_ReceivedResourceAndErrorResource() {
        SampleActionMapping actionMapping = mock(SampleActionMapping.class);
        when(actionMapping.invoke(eq(SampleAction.ACTION1), any()))
                .thenReturn(mock(Sample.class));
        when(actionMapping.invoke(eq(SampleAction.ACTION2), any()))
                .thenThrow(new RuntimeException());
        when(actionMapping.invoke(eq(SampleAction.ACTION3), any()))
                .thenReturn(mock(Sample.class));

        SampleErrorMapping errorMapping = mock(SampleErrorMapping.class);
        when(errorMapping.invoke(isA(RuntimeException.class)))
                .thenReturn(Resource.failure("test", new SampleError()));

        SampleResourceObserver observer = mock(SampleResourceObserver.class);

        ModelLogic<SampleAction, Sample, SampleError> logic = ModelLogic.create(
                actionMapping,
                errorMapping,
                mock(Sample.class)
        );

        logic.setResourceObserver(observer);

        logic.update(SampleAction.ACTION1);
        logic.update(SampleAction.ACTION2);
        logic.update(SampleAction.ACTION3);

        InOrder order = inOrder(observer);
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Success.class));
        order.verify(observer).update(isA(Resource.InProgress.class));
        order.verify(observer).update(isA(Resource.Failure.class));
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

    private abstract static class SampleDispatchObserver implements Observer<SampleAction> {
    }

    private abstract static class SampleResourceObserver implements Observer<Resource<Sample, SampleError>> {
    }
}
