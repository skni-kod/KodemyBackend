package pl.sknikod.kodemybackend.infrastructure.module.material.producer;

public interface Producer<T> {
    void publish(T object);
}
