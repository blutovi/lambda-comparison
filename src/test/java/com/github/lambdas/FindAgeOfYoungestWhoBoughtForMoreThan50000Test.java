package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FindAgeOfYoungestWhoBoughtForMoreThan50000Test extends AbstractMeasurementTest {

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000For functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000For(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaReduce() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaReduce functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaReduce(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaParalelReduce() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaParalelReduce functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaParalelReduce(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaMapInt() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaMapInt functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaMapInt(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindAgeOfYoungestWhoBoughtForMoreThan50000Guava functionToMeasure = new FindAgeOfYoungestWhoBoughtForMoreThan50000Guava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000For implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000For(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            int age = Integer.MAX_VALUE;

            List<Sale> sales = db.getSales();

            int size = sales.size();
            for (int i = 0; i < size; i++) {
                final Sale sale = sales.get(i);

                if (sale.getCost() > 50000d) {
                    final int buyerAge = sale.getBuyer().getAge();
                    if (buyerAge < age) {
                        age = buyerAge;
                    }
                }
            }
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000Iterable(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            int age = Integer.MAX_VALUE;
            for (final Sale sale : db.getSales()) {
                if (sale.getCost() > 50000d) {
                    final int buyerAge = sale.getBuyer().getAge();
                    if (buyerAge < age) {
                        age = buyerAge;
                    }
                }
            }
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = Collections.min(db.getSales()
                    .stream()
                    .filter((Sale sale) -> sale.getCost() > 50000d)
                    .<Integer>map((Sale sale) -> sale.getBuyer().getAge())
                    .collect(Collectors.toList()));
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaReduce implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaReduce(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = db.getSales()
                    .stream()
                    .filter((Sale sale) -> sale.getCost() > 50000d)
                    .<Integer>map((Sale sale) -> sale.getBuyer().getAge())
                    .reduce(0, (x, y) -> x < y ? x : y);

            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaParalelReduce implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaParalelReduce(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = db.getSales()
                    .parallelStream()
                    .filter((Sale sale) -> sale.getCost() > 50000d)
                    .<Integer>map((Sale sale) -> sale.getBuyer().getAge())
                    .reduce(0, (x, y) -> x < y ? x : y);

            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaMapInt implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000JDKLambdaMapInt(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = db.getSales()
                    .stream()
                    .filter(sale -> sale.getCost() > 50000d)
                    .mapToInt(sale -> sale.getBuyer().getAge())
                    .min()
                    .orElse(0);
            return age;
        }
    }

    private class FindAgeOfYoungestWhoBoughtForMoreThan50000Guava implements Supplier<Integer> {
        private final Db db;

        public FindAgeOfYoungestWhoBoughtForMoreThan50000Guava(final Db db) {
            this.db = db;
        }

        @Override
        public Integer get() {
            final int age = Ordering.<Integer>natural().min(
                    Iterables.transform(Iterables.filter(db.getSales(), new Predicate<Sale>() {
                        @Override
                        public boolean apply(final Sale input) {
                            return input.getCost() > 50000.00d;
                        }
                    }), new Function<Sale, Integer>() {
                        @Override
                        public Integer apply(final Sale input) {
                            return input.getBuyer().getAge();
                        }
                    }));
            return age;
        }
    }


}
