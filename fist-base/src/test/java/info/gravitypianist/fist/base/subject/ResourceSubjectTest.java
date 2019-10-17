package info.gravitypianist.fist.base.subject;

import info.gravitypianist.fist.base.Observer;
import info.gravitypianist.fist.base.Resource;
import info.gravitypianist.fist.base.ResourceSubject;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;


public class ResourceSubjectTest {

    @Test
    public void test() {
        ResourceSubject<Sample, SampleError> resourceSubject = DefaultResourceSubject.create(
                error -> {
                    if (error instanceof SampleError.NoError) {
                        return Resource.failure("No Error", new SampleError.NoError());
                    } else {
                        return Resource.failure("Unknown Error", new SampleError.UnknownError(error));
                    }
                }
        );

        MockObserver mockObserver = mock(MockObserver.class);

        resourceSubject.setObserver(mockObserver);

        resourceSubject.inProgress();

        Sample result1 = new Sample("title1", "decs1");
        Sample result2 = new Sample("title2", "decs2");

        resourceSubject.onNext(result1);
        resourceSubject.onNext(result2);

        InOrder order = inOrder(mockObserver);

        order.verify(mockObserver, times(3)).update(any());
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

    static class MockObserver implements Observer<Resource<Sample, SampleError>> {
        @Override
        public void update(Resource<Sample, SampleError> value) {
        }
    }
}
