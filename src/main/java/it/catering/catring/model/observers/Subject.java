package it.catering.catring.model.observers;

public interface Subject<T> {
    void addObserver(T observer);
    void removeObserver(T observer);
    void notifyObservers();
}