package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindMostCostlySaleTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleIterable functionToMeasure = new FindMostCostlySaleIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleFor functionToMeasure = new FindMostCostlySaleFor(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleJDKLambda functionToMeasure = new FindMostCostlySaleJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaReduce() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleJDKLambdaReduce functionToMeasure = new FindMostCostlySaleJDKLambdaReduce(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindMostCostlySaleGuava functionToMeasure = new FindMostCostlySaleGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindMostCostlySaleFor implements Supplier<Double> {
        private final Db db;

        public FindMostCostlySaleFor(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            double maxCost = 0.0d;
            List<Sale> sales = db.getSales();

            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final double cost = sales.get(i).getCost();
                if (cost > maxCost) maxCost = cost;
            }
            return maxCost;
        }
    }

    private class FindMostCostlySaleIterable implements Supplier<Double> {
        private final Db db;

        public FindMostCostlySaleIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            double maxCost = 0.0d;
            for (final Sale sale : db.getSales()) {
                final double cost = sale.getCost();

                if (cost > maxCost) maxCost = cost;
            }
            return maxCost;
        }
    }

    private class FindMostCostlySaleJDKLambda implements Supplier<Double> {
        private final Db db;

        public FindMostCostlySaleJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final Double maxCost = db.getSales()
                    .stream()
                    .max(Comparator.comparing(e -> e.getCost()))
                    .get()
                    .getCost();
            return maxCost;
        }
    }

    private class FindMostCostlySaleJDKLambdaReduce implements Supplier<Double> {
        private final Db db;

        public FindMostCostlySaleJDKLambdaReduce(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final Double maxCost = db.getSales()
                    .stream()
                    .reduce((a, b) -> a.getCost() < b.getCost() ? b : a)
                    .get()
                    .getCost();
            return maxCost;
        }
    }

    private class FindMostCostlySaleGuava implements Supplier<Double> {
        private final Db db;

        public FindMostCostlySaleGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Double get() {
            final Double maxCost = Collections.max(Collections2.transform(db.getSales(), new Function<Sale, Double>() {
                @Override
                public Double apply(final Sale input) {
                    return input.getCost();
                }
            }));
            return maxCost;
        }
    }

}
