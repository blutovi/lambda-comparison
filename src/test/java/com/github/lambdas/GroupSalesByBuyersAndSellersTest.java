package com.github.lambdas;

import ch.lambdaj.demo.Db;
import ch.lambdaj.demo.Person;
import ch.lambdaj.demo.Sale;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class GroupSalesByBuyersAndSellersTest extends AbstractMeasurementTest {

    @Test
    public void testIterable() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersIterable functionToMeasure = new GroupSalesByBuyersAndSellersIterable(db);
        performMeasurements(functionToMeasure);
    }

    @Test
    public void testFor() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersFor functionToMeasure = new GroupSalesByBuyersAndSellersFor(db);
        performMeasurements(functionToMeasure);
    }

    @Test
    public void testJDKLambda() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersJDKLambda functionToMeasure = new GroupSalesByBuyersAndSellersJDKLambda(db);
        performMeasurements(functionToMeasure);
    }

    @Test
    public void testGuava() throws Exception {
        final Db db = Db.getInstance();
        final GroupSalesByBuyersAndSellersGuava functionToMeasure = new GroupSalesByBuyersAndSellersGuava(db);

        performMeasurements(functionToMeasure);
    }

    private class GroupSalesByBuyersAndSellersFor implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersFor(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
            final Map<Person, Map<Person, Sale>> map = new HashMap<>();

            List<Sale> sales = db.getSales();
            int sSize = sales.size();
            for (int i = 0; i < sSize; i++) {
                final Sale sale = sales.get(i);
                final Person buyer = sale.getBuyer();

                Map<Person, Sale> buyerMap = map.get(buyer);
                if (buyerMap == null) {
                    buyerMap = new HashMap<>();
                    map.put(buyer, buyerMap);
                }
                buyerMap.put(sale.getSeller(), sale);
            }

            Person youngest = null;
            Person oldest = null;
            List<Person> persons = db.getPersons();
            int pSize = persons.size();

            for (int i = 0; i < pSize; i++) {
                final Person person = persons.get(i);
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
                if (oldest == null || person.getAge() > oldest.getAge()) {
                    oldest = person;
                }
            }

            return map.get(youngest).get(oldest);
        }
    }

    private class GroupSalesByBuyersAndSellersIterable implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersIterable(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
            final Map<Person, Map<Person, Sale>> map = new HashMap<>();
            for (final Sale sale : db.getSales()) {
                final Person buyer = sale.getBuyer();
                Map<Person, Sale> buyerMap = map.get(buyer);
                if (buyerMap == null) {
                    buyerMap = new HashMap<>();
                    map.put(buyer, buyerMap);
                }
                buyerMap.put(sale.getSeller(), sale);
            }
            Person youngest = null;
            Person oldest = null;
            for (final Person person : db.getPersons()) {
                if (youngest == null || person.getAge() < youngest.getAge()) {
                    youngest = person;
                }
                if (oldest == null || person.getAge() > oldest.getAge()) {
                    oldest = person;
                }
            }

            return map.get(youngest).get(oldest);
        }
    }

    private class GroupSalesByBuyersAndSellersJDKLambda implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersJDKLambda(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {

            final Map<Person, Map<Person, List<Sale>>> buyerToSale =
                    db.getSales()
                            .stream()
                            .collect(
                                    Collectors.groupingBy(
                                            (Sale s) -> s.getBuyer(),
                                            Collectors.collectingAndThen(
                                                    Collectors.groupingBy((Sale s) -> s.getSeller()),
                                                    HashMap::new
                                    ))
                            );

            List<Person> persons = db.getPersons()
                    .stream()
                    .sorted((Person p1, Person p2) -> Integer.compare(p1.getAge(), p2.getAge()))
                    .collect(Collectors.toList());

            return buyerToSale.get(persons.get(0)).get(persons.get(persons.size() - 1)).get(0);
        }
    }

    private class GroupSalesByBuyersAndSellersGuava implements Supplier<Sale> {
        private final Db db;

        public GroupSalesByBuyersAndSellersGuava(final Db db) {
            this.db = db;
        }

        @Override
        public Sale get() {
            final ImmutableMap<Person, Collection<Sale>> buyerToSale = Multimaps.index(db.getSales(), new Function<Sale, Person>() {
                @Override
                public Person apply(final Sale input) {
                    return input.getBuyer();
                }
            }).asMap();
            final Map<Person, Map<Person, Collection<Sale>>> buyerToSellerToSale = Maps.transformValues(buyerToSale, new Function<Collection<Sale>, Map<Person, Collection<Sale>>>() {
                @Override
                public Map<Person, Collection<Sale>> apply(final Collection<Sale> input) {
                    return Multimaps.index(input, new Function<Sale, Person>() {
                        @Override
                        public Person apply(final Sale input) {
                            return input.getSeller();
                        }
                    }).asMap();
                }
            });

            final ImmutableMap<Integer, Person> mapped = Maps.uniqueIndex(db.getPersons(), new Function<Person, Integer>() {
                @Override
                public Integer apply(final Person input) {
                    return input.getAge();
                }
            });
            final Person youngest = Collections.min(mapped.entrySet(), new Comparator<Map.Entry<Integer, Person>>() {
                @Override
                public int compare(final Map.Entry<Integer, Person> o1, final Map.Entry<Integer, Person> o2) {
                    return Integer.compare(o1.getKey(), o2.getKey());
                }
            }).getValue();
            final Person oldest = Collections.max(mapped.entrySet(), new Comparator<Map.Entry<Integer, Person>>() {
                @Override
                public int compare(final Map.Entry<Integer, Person> o1, final Map.Entry<Integer, Person> o2) {
                    return Integer.compare(o1.getKey(), o2.getKey());
                }
            }).getValue();

            return Iterables.getFirst(buyerToSellerToSale.get(youngest).get(oldest), null);
        }
    }

}
