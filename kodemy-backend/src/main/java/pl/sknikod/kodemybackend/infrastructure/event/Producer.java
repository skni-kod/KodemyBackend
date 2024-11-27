package pl.sknikod.kodemybackend.infrastructure.event;

public interface Producer<T> {
    void publish(T object);
}
