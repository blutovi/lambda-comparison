package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndexCarsByBrandTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandIterable functionToMeasure = new IndexCarsByBrandIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandFor functionToMeasure = new IndexCarsByBrandFor(db);
        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaForEach() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandJDKLambdaForEach functionToMeasure = new IndexCarsByBrandJDKLambdaForEach(db);
        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandJDKLambda functionToMeasure = new IndexCarsByBrandJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final IndexCarsByBrandGuava functionToMeasure = new IndexCarsByBrandGuava(db);

        performMeasurements(functionToMeasure);
    }


    private class IndexCarsByBrandFor implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandFor(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = new HashMap<>();
            List<Car> cars = db.getCars();

            int cSize = cars.size();
            for (int i = 0; i < cSize; i++) {
                final Car car = cars.get(i);
                carsByBrand.put(car.getBrand(), car);
            }
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandJDKLambdaForEach implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandJDKLambdaForEach(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = new HashMap<>();

            db.getCars().forEach(
                    (c) -> carsByBrand.put(c.getBrand(), c)
            );

            return carsByBrand;
        }
    }

    private class IndexCarsByBrandIterable implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = new HashMap<>();
            for (final Car car : db.getCars()) {
                carsByBrand.put(car.getBrand(), car);
            }
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandJDKLambda implements Supplier<Map<String, Car>> {
        private final Db db;

        public IndexCarsByBrandJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Map<String, Car> get() {
            final Map<String, Car> carsByBrand = db.getCars()
                    .stream()
                    .distinct()
                    .collect(Collectors.toMap(c -> c.getBrand(), c -> c));
            return carsByBrand;
        }
    }

    private class IndexCarsByBrandGuava implements Supplier<ImmutableListMultimap<String, Car>> {
        private final Db db;

        public IndexCarsByBrandGuava(final Db db) {
            this.db = db;
        }

        @Override
        public ImmutableListMultimap<String, Car> get() {
            final ImmutableListMultimap<String, Car> carsByBrand = Multimaps.index(db.getCars(), new Function<Car, String>() {
                @Override
                public String apply(final Car input) {
                    return input.getBrand();
                }
            });
            return carsByBrand;
        }
    }

}
