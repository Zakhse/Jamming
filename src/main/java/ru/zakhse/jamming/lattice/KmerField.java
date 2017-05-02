package ru.zakhse.jamming.lattice;

import java.util.*;

import ru.zakhse.Point;

/**
 * Arrangement of k-mers
 */
enum Arrangement {
    VERTICAL, HORIZONTAL
}

/**
 * Square lattice for placing k-mers in
 */
public class KmerField {

    private enum Filling {FILLED, EMPTY}

    // True if generated field can be visualized (in string)
    private boolean visualization;

    /**
     * Creates new KmerField
     *
     * @param visualization true if field should be visualized, false if it's not necessary
     */
    public KmerField(boolean visualization) {
        this.visualization = visualization;
    }

    private Point getRandom(Set<Point> s) {
        int l = rnd.nextInt(s.size());
        int i = 0;
        for (Point p : s)
            if (i++ == l) return p;
        return null;
    }

    private static Random rnd = new Random();

    private int size;
    private int kmerSize;
    private boolean generated = false;
    private double filledSpace = 0.0;

    public boolean isGenerated() {return generated;}

    public int getSize() {return size;}

    public int getKmerSize() {return kmerSize;}

    public boolean getVisualization() {return visualization;}

    /**
     * Gets part of square lattice which is covered by k-mers
     *
     * @return part of square lattice
     */
    public double getFilledSpace() {return filledSpace;}

    @Override
    public String toString() {
        if (!visualization) return null;
        if (!generated) return null;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == null || field[i][j].filling == Filling.EMPTY) str.append("  ");
                else if (field[i][j].arrangement == Arrangement.HORIZONTAL) str.append("- ");
                else str.append("| ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    private kmerCell[][] field;

    /**
     * Filling the whole square lattice by k-mers
     *
     * @param size     Size of square lattice's edge
     * @param kmerSize Size of k-mers to be placed
     */
    public void generateField(int size, int kmerSize) {
        if (size < kmerSize) throw new IllegalArgumentException("Size field cannot be less than size of k-mers.");

        this.size = size;
        this.kmerSize = kmerSize;

        if (visualization) {
            field = new kmerCell[size][];
            for (int i = 0; i < size; i++) {
                field[i] = new kmerCell[size];
            }
        }
        // Generating set of point where heads of kmers can be placed (two ones for horizontal and vertical kmers
        // separated)
        Set<Point> pointSetHorizontal = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - kmerSize + 1; j++) {
                pointSetHorizontal.add(new Point(i, j));
            }
        }
        Set<Point> pointSetVertical = new HashSet<>();
        for (int i = 0; i < size - kmerSize + 1; i++) {
            for (int j = 0; j < size; j++) {
                pointSetVertical.add(new Point(i, j));
            }
        }

        int numberOfPlacedKmers = 0;
        // Filling a lattice by kmers
        while (!pointSetHorizontal.isEmpty() || !pointSetVertical.isEmpty()) {
            numberOfPlacedKmers++;

            Arrangement chosenArrangement;
            Point chosenPoint;
            if (pointSetHorizontal.isEmpty() || (rnd.nextBoolean() && !pointSetVertical.isEmpty())) {
                chosenArrangement = Arrangement.VERTICAL;
                chosenPoint = getRandom(pointSetVertical);
            } else {
                chosenArrangement = Arrangement.HORIZONTAL;
                chosenPoint = getRandom(pointSetHorizontal);
            }

            int X = chosenPoint.getX();
            int Y = chosenPoint.getY();

            if (visualization) {
                kmerCell kmerToPlace = new kmerCell(X, Y, chosenArrangement);

                // Placing the kmer of this iteration
                if (chosenArrangement == Arrangement.HORIZONTAL)
                    for (int j = Y; j < Y + kmerSize; j++) {
                        field[X][j] = kmerToPlace;
                    }
                else
                    for (int i = X; i < X + kmerSize; i++) {
                        field[i][Y] = kmerToPlace;
                    }
            }

            // Cleaning points that can't have a head of any kmers now
            if (chosenArrangement == Arrangement.HORIZONTAL) {
                // Cleans set of point for horizontal kmers
                for (int i = Math.max(0, Y - kmerSize + 1); i <= Y + kmerSize - 1; i++)
                    pointSetHorizontal.remove(new Point(X, i));

                // Cleans set of point for vertical kmers
                for (int j = Y; j < Y + kmerSize; j++)
                    for (int i = Math.max(0, X - kmerSize + 1); i <= X; i++)
                        pointSetVertical.remove(new Point(i, j));

            } else {
                // Cleans set of point for vertical kmers
                for (int i = Math.max(0, X - kmerSize + 1); i <= X + kmerSize - 1; i++)
                    pointSetVertical.remove(new Point(i, Y));

                // Cleans set of point for horizontal kmers
                for (int i = X; i < X + kmerSize; i++)
                    for (int j = Math.max(0, Y - kmerSize + 1); j <= Y; j++)
                        pointSetHorizontal.remove(new Point(i, j));
            }
        } // while cycle

        generated = true;
        filledSpace = numberOfPlacedKmers * kmerSize / (double) (size * size);
    } // generation

    private class kmerCell {
        private Point point;
        private Arrangement arrangement;
        Filling filling;

        int getHeadX() {return point.getX();}

        int getHeadY() {return point.getY();}

        Arrangement getArrangement() {return arrangement;}

        kmerCell(int headX, int headY, Arrangement arrangement) {
            this.arrangement = arrangement;
            point = new Point(headX, headY);
            filling = Filling.FILLED;
        }
    }
}
