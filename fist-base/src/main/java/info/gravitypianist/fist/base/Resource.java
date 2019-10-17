package info.gravitypianist.fist.base;

public class Resource<T, E extends Throwable> {

    public static <T, E extends Throwable> Resource<T, E> success(T data) {
        return new Success<>(data);
    }

    public static <T, E extends Throwable> Resource<T, E> failure(String message, E error) {
        return new Failure<>(message, error);
    }

    public static <T, E extends Throwable> Resource<T, E> inProgress() {
        return new InProgress<>();
    }

    public interface ErrorMapping<T, E extends Throwable> {
        Resource<T, E> invoke(Throwable error);
    }

    public static final class Success<T, E extends Throwable> extends Resource<T, E> {

        private T data;

        private Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class Failure<T, E extends Throwable> extends Resource<T, E> {

        private String message;

        private E error;

        private Failure(String message, E error) {
            this.message = message;
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public E getError() {
            return error;
        }

    }

    public static final class InProgress<T, E extends Throwable> extends Resource<T, E> {

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return this.getClass() == other.getClass();
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

    }
}
