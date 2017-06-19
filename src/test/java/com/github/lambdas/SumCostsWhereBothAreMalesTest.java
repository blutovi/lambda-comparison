package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class SumCostsWhereBothAreMalesTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesIterable functionToMeasure = new SumCostsWhereBothAreMalesIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesFor functionToMeasure = new SumCostsWhereBothAreMalesFor(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesJDKLambda functionToMeasure = new SumCostsWhereBothAreMalesJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final SumCostsWhereBothAreMalesGuava functionToMeasure = new SumCostsWhereBothAreMalesGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class SumCostsWhereBothAreMalesFor implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesFor(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            double sum = 0.0d;
            List<Sale> sales = db.getSales();

            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final Sale sale = sales.get(i);

                if (sale.getBuyer().isMale() && sale.getSeller().isMale()) {
                    sum += sale.getCost();
                }
            }
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesIterable implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            double sum = 0.0d;
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().isMale() && sale.getSeller().isMale()) {
                    sum += sale.getCost();
                }
            }
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesJDKLambda implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final Double sum = db.getSales()
                    .stream()
                    .filter((Sale s) -> s.getBuyer().isMale() && s.getSeller().isMale())
                    .mapToDouble((Sale s) -> s.getCost())
                    .sum();
            return sum;
        }
    }

    private class SumCostsWhereBothAreMalesGuava implements Supplier<Double> {
        private final Db db;

        public SumCostsWhereBothAreMalesGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {

            final Collection<Double> transform = Collections2.transform(Collections2.filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getBuyer().isMale() && input.getSeller().isMale();
                }
            }), new Function<Sale, Double>() {
                @Override
                public Double apply(final Sale input) {
                    return input.getCost();
                }
            });

            double sum = 0.0d;
            for (double aDouble : transform) {
                sum += aDouble;
            }
            return sum;
        }
    }

}
