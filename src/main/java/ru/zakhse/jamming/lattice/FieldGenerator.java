package ru.zakhse.jamming.lattice;

import java.util.concurrent.Callable;

/**
 * Allows to generate a square lattice filled randomly with k-mers with the condition tha
 * then not any more k-mers can be placed in this lattice.
 *
 * @author Zakhse
 */
class FieldGenerator implements Callable<Double> {
    private int size;
    private int kmerSize;

    /**
     * Constructs a generator that is able to generate a field filled with k-mers
     *
     * @param size     height (and width) of the field
     * @param kmerSize length of k-mers
     */
    FieldGenerator(int size, int kmerSize) {
        this.size = size;
        this.kmerSize = kmerSize;
    }

    /**
     * Computes the part of the lattice filled by k-mers with respect to the whole space of the lattice
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Double call() throws Exception {
        KmerField field = new KmerField(false);
        field.generateField(size, kmerSize);
        return field.getFilledSpace();
    }
}
