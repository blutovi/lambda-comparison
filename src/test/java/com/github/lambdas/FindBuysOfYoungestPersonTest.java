package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FindBuysOfYoungestPersonTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonIterable functionToMeasure = new FindBuysOfYoungestPersonIterable(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonFor functionToMeasure = new FindBuysOfYoungestPersonFor(db);

        performMeasurements(functionToMeasure);
    }


    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonJDKLambda functionToMeasure = new FindBuysOfYoungestPersonJDKLambda(db);

        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final FindBuysOfYoungestPersonGuava functionToMeasure = new FindBuysOfYoungestPersonGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class FindBuysOfYoungestPersonFor implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonFor(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            Person youngest = null;

            final List<Person> persons = db.getPersons();
            final List<Sale> sales = db.getSales();

            int pSize = persons.size();
            for (int i = 0; i < pSize; i++) {
                final Person person = persons.get(i);

                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
            }

            final List<Sale> buys = new ArrayList<>();

            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final Sale sale = sales.get(i);

                if (sale.getBuyer().equals(youngest)) {
                    buys.add(sale);
                }
            }
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonIterable implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonIterable(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            Person youngest = null;
            for (final Person person : db.getPersons()) {
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
            }
            final List<Sale> buys = new ArrayList<>();
            for (final Sale sale : db.getSales()) {
                if (sale.getBuyer().equals(youngest)) {
                    buys.add(sale);
                }
            }
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonJDKLambda implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final Person min = Collections.min(db.getPersons(), (Person p1, Person p2) -> Integer.compare(p1.getAge(), p2.getAge()));
            final List<Sale> buys = db.getSales()
                    .stream()
                    .filter((Sale s) -> s.getBuyer().equals(min))
                    .collect(Collectors.toList());
            return buys;
        }
    }

    private class FindBuysOfYoungestPersonGuava implements Supplier<List<Sale>> {
        private final Db db;

        public FindBuysOfYoungestPersonGuava(final Db db) {
            this.db = db;
        }

        @Override
        public List<Sale> get() {
            final Person min = new Ordering<Person>() {
                @Override
                public int compare(Person left, Person right) {
                    return Ints.compare(left.getAge(), right.getAge());
                }
            }.min(db.getPersons());

            final List<Sale> buys = Lists.newArrayList(Collections2.filter(db.getSales(), new Predicate<Sale>() {
                @Override
                public boolean apply(final Sale input) {
                    return input.getBuyer().equals(min);
                }
            }));
            return buys;
        }
    }

}
