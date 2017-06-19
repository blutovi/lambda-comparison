package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FindMostBoughtCarTest extends AbstractMeasurementTest {

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarFor functionToMeasure = new FindMostBoughtCarFor(db);

        performMeasurements(functionToMeasure);

    }

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarIterable functionToMeasure = new FindMostBoughtCarIterable(db);

        performMeasurements(functionToMeasure);

    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarJDKLambda functionToMeasure = new FindMostBoughtCarJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindMostBoughtCarGuava functionToMeasure = new FindMostBoughtCarGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindMostBoughtCarFor implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarFor(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Map<Car, Integer> carsBought = new HashMap<>();
            final List<Sale> sales = db.getSales();

            final BiFunction<Car, Integer, Integer> biFunc = new BiFunction<Car, Integer, Integer>() {
                @Override
                public Integer apply(Car car, Integer integer) {
                    return integer == null ? 1 : integer + 1;
                }
            };

            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final Car car = sales.get(i).getCar();
                carsBought.compute(car, biFunc);
            }

            return sortEntryByValue(carsBought).get(0).getKey();
        }
    }

    private class FindMostBoughtCarIterable implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Map<Car, Integer> carsBought = new HashMap<>();

            final BiFunction<Car, Integer, Integer> biFunc = new BiFunction<Car, Integer, Integer>() {
                @Override
                public Integer apply(Car car, Integer integer) {
                    return integer == null ? 1 : integer + 1;
                }
            };

            for (final Sale sale : db.getSales()) {
                final Car car = sale.getCar();
                carsBought.compute(car, biFunc);
            }

            return sortEntryByValue(carsBought).get(0).getKey();
        }
    }

    private class FindMostBoughtCarJDKLambda implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Car mostBoughtCar = db.getSales()
                    .stream()
                    .collect(
                            Collectors.groupingBy((Sale s) -> s.getCar(), Collectors.counting())
                    )
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue((final Long o1, final Long o2) -> Long.compare(o2, o1)))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
                    .stream()
                    .findFirst().get();

            return mostBoughtCar;
        }
    }

    private class FindMostBoughtCarGuava implements Supplier<Car> {
        private final Db db;

        public FindMostBoughtCarGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Car get() {
            final Collection<Sale> max = Collections.max(Multimaps.index(db.getSales(), new Function<Sale, Car>() {
                @Override
                public Car apply(final Sale input) {
                    return input.getCar();
                }
            }).asMap().values(), new Comparator<Collection<Sale>>() {
                @Override
                public int compare(final Collection<Sale> o1, final Collection<Sale> o2) {
                    return Long.compare(o1.size(), o2.size());
                }
            });
            final Car mostBoughtCar = Iterables.getFirst(max, null).getCar();
            return mostBoughtCar;
        }
    }

}
