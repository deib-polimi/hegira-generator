package it.polimi.hegira.generator;

public interface Randomizable<T> {

    public T randomize(Object dependency);
}
