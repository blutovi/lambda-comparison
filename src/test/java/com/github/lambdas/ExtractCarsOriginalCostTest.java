package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractCarsOriginalCostTest extends AbstractMeasurementTest {

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostFor functionToMeasure = new ExtractCarsOriginalCostFor(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostIterable functionToMeasure = new ExtractCarsOriginalCostIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostJDKLambda functionToMeasure = new ExtractCarsOriginalCostJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaFunction() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostJDKLambdaFunction functionToMeasure = new ExtractCarsOriginalCostJDKLambdaFunction(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final ExtractCarsOriginalCostGuava functionToMeasure = new ExtractCarsOriginalCostGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class ExtractCarsOriginalCostFor implements Supplier<List<Double>> {
        private final Db db;

        public ExtractCarsOriginalCostFor(final Db db) {
            this.db = db;
        }

        @Override
        public List<Double> get() {
            final List<Double> costs = new ArrayList<>();

            final List<Car> cars = db.getCars();

            int size = cars.size();
            for (int i = 0; i < size; i++) {
                final Car car = cars.get(i);
                costs.add(car.getOriginalValue());
            }
            return costs;
        }
    }

    private class ExtractCarsOriginalCostIterable implements Supplier<List<Double>> {
        private final Db db;

        public ExtractCarsOriginalCostIterable(final Db db) {
            this.db = db;
        }

        @Override
        public List<Double> get() {
            final List<Double> costs = new ArrayList<Double>();
            for (final Car car : db.getCars()) {
                costs.add(car.getOriginalValue());
            }
            return costs;
        }
    }

    private class ExtractCarsOriginalCostJDKLambda implements Supplier<Iterable<Double>> {
        private final Db db;

        public ExtractCarsOriginalCostJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Iterable<Double> get() {
            final Iterable<Double> costs =
                    db.getCars()
                            .stream()
                            .map((c)->c.getOriginalValue())
                            .collect(Collectors.toList());
            return costs;
        }
    }

    private class ExtractCarsOriginalCostJDKLambdaFunction implements Supplier<Iterable<Double>> {
        private final Db db;

        public ExtractCarsOriginalCostJDKLambdaFunction(final Db db) {
            this.db = db;
        }

        @Override
        public Iterable<Double> get() {
            final Iterable<Double> costs =
                    db.getCars()
                            .stream()
                            .map(Car::getOriginalValue)
                            .collect(Collectors.toList());
            return costs;
        }
    }

    private class ExtractCarsOriginalCostGuava implements Supplier<List<Double>> {
        private final Db db;

        public ExtractCarsOriginalCostGuava(final Db db) {
            this.db = db;
        }

        @Override
        public List<Double> get() {
            final List<Double> costs = Lists.newArrayList(Lists.transform(db.getCars(), new Function<Car, Double>() {
                @Override
                public Double apply(final Car input) {
                    return input.getOriginalValue();
                }
            }));

            return costs;
        }
    }

}
