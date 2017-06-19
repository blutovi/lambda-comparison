package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectAllSalesOfFerrariTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariIterable functionToMeasure = new SelectAllSalesOfFerrariIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariFor functionToMeasure = new SelectAllSalesOfFerrariFor(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariJDKLambda functionToMeasure = new SelectAllSalesOfFerrariJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambdaForEach() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariJDKLambdaForEach functionToMeasure = new SelectAllSalesOfFerrariJDKLambdaForEach(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final SelectAllSalesOfFerrariGuava functionToMeasure = new SelectAllSalesOfFerrariGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class SelectAllSalesOfFerrariFor implements Supplier<List<Sale>> {
        private final Db db;

        public SelectAllSalesOfFerrariFor(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> salesOfAFerrari = new ArrayList<>();
            List<Sale> sales = db.getSales();

            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final Sale sale = sales.get(i);
                if ("Ferrari".equals(sale.getCar().getBrand())) {
                    salesOfAFerrari.add(sale);
                }
            }
            return salesOfAFerrari;
        }
    }

    private class SelectAllSalesOfFerrariIterable implements Supplier<List<Sale>> {
        private final Db db;

        public SelectAllSalesOfFerrariIterable(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> salesOfAFerrari = new ArrayList<>();
            for (final Sale sale : db.getSales()) {
                if ("Ferrari".equals(sale.getCar().getBrand())) {
                    salesOfAFerrari.add(sale);
                }
            }
            return salesOfAFerrari;
        }
    }

    private class SelectAllSalesOfFerrariJDKLambdaForEach implements Supplier<List<Sale>> {
        private final Db db;

        public SelectAllSalesOfFerrariJDKLambdaForEach(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> salesOfAFerrari = new ArrayList<>();
            db.getSales().forEach(
                    (s) -> {
                        if ("Ferrari".equals(s.getCar().getBrand())) {
                            salesOfAFerrari.add(s);
                        }
                    }
            );
            return salesOfAFerrari;
        }
    }

    private class SelectAllSalesOfFerrariJDKLambda implements Supplier<List<Sale>> {
        private final Db db;

        public SelectAllSalesOfFerrariJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> salesOfAFerrari = db.getSales()
                    .stream()
                    .filter((Sale s) -> "Ferrari".equals(s.getCar().getBrand()))
                    .collect(Collectors.toList());

            return salesOfAFerrari;
        }
    }

    private class SelectAllSalesOfFerrariGuava implements Supplier<List<Sale>> {
        private final Db db;

        public SelectAllSalesOfFerrariGuava(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final List<Sale> salesOfAFerrari = Lists.newArrayList(Collections2.filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getCar().getBrand().equals("Ferrari");
                }
            }));
            return salesOfAFerrari;
        }
    }

}
