package com.github.lambdas;

import ch.lambdaj.demo.Car;
import ch.lambdaj.demo.Db;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class PrintAllBrandsTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsIterable functionToMeasure = new PrintAllBrandsIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsFor functionToMeasure = new PrintAllBrandsFor(db);

        performMeasurements(functionToMeasure);
    }


    @Test
    public void testJDKLambdaForEach() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsJDKLambdaForEach functionToMeasure = new PrintAllBrandsJDKLambdaForEach(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsJDKLambda functionToMeasure = new PrintAllBrandsJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final PrintAllBrandsGuava functionToMeasure = new PrintAllBrandsGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class PrintAllBrandsFor implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsFor(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final StringBuilder sb = new StringBuilder();
            List<Car> cars = db.getCars();

            int cSize = cars.size();
            for (int i = 0; i < cSize; i++) {
                final Car car = cars.get(i);
                sb.append(car.getBrand()).append(", ");
            }
            final String brands = sb.toString().substring(0, sb.length() - 2);
            return brands;
        }
    }

    private class PrintAllBrandsIterable implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsIterable(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final StringBuilder sb = new StringBuilder();
            for (final Car car : db.getCars()) {
                sb.append(car.getBrand()).append(", ");
            }
            final String brands = sb.toString().substring(0, sb.length() - 2);
            return brands;
        }
    }

    private class PrintAllBrandsJDKLambdaForEach implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsJDKLambdaForEach(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final StringBuilder sb = new StringBuilder();
            db.getCars().forEach(
                    (c) -> sb.append(c.getBrand()).append(", ")
            );
            final String brands = sb.toString().substring(0, sb.length() - 2);

            return brands;
        }
    }

    private class PrintAllBrandsJDKLambda implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final String value =
                    db.getCars()
                            .stream()
                            .map(c -> c.getBrand())
                            .collect(Collectors.joining(","));

            return value;
        }
    }

    private class PrintAllBrandsGuava implements Supplier<String> {
        private final Db db;

        public PrintAllBrandsGuava(final Db db) {
            this.db = db;
        }

        @Override
        public String get() {
            final String brands = Joiner.on(",").join(Collections2.transform(db.getCars(), new Function<Car, String>() {
                @Override
                public String apply(final Car input) {
                    return input.getBrand();
                }
            }));
            return brands;
        }
    }

}
