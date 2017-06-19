package com.github.lambdas;

import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static java.text.MessageFormat.format;

public abstract class AbstractMeasurementTest {

    private static final transient Logger logger = LoggerFactory.getLogger(AbstractMeasurementTest.class);

    public static final int WARMUP_ITERATIONS_COUNT = 10;
    public static final int ITERATIONS_COUNT = 10;
    public static final int ITERATION_DURATION_MSEC = 1000;

    private volatile boolean shouldStop;
    private Object result;

    protected void performMeasurements(final Supplier supplier) {
        logger.info(format("================<{0}>================", supplier.getClass().getSimpleName()));

        warmup(supplier);
        List<Long> statistics = benchmark(supplier);
        printStatistics(supplier, statistics);
    }

    private void printStatistics(final Supplier supplier, final List<Long> statistics) {
        final StatisticalSummary descriptiveStatistics = new DescriptiveStatistics(Doubles.toArray(statistics));
        logger.info(format("{0}: Min elapsed time: {1}", supplier.getClass().getSimpleName(), descriptiveStatistics.getMin()));
        logger.info(format("{0}: Max elapsed time: {1}", supplier.getClass().getSimpleName(), descriptiveStatistics.getMax()));
        logger.info(format("{0}: Avg elapsed time: {1}", supplier.getClass().getSimpleName(), descriptiveStatistics.getMean()));
        logger.info(format("{0}: Standard deviation: {1}", supplier.getClass().getSimpleName(), descriptiveStatistics.getStandardDeviation()));
        logger.info(format("{0}: Confidence interval width: {1}", supplier.getClass().getSimpleName(), getConfidenceIntervalWidth(descriptiveStatistics, 0.95)));
    }

    private double getConfidenceIntervalWidth(final StatisticalSummary statisticalSummary, final double significance) {
        final TDistribution tDist = new TDistribution(statisticalSummary.getN() - 1);
        final double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statisticalSummary.getStandardDeviation() / Math.sqrt(statisticalSummary.getN());
    }


    private void warmup(final Supplier functionToMeasure) {
        for (int i = 0; i < WARMUP_ITERATIONS_COUNT; i++) {
            performMeasurement(functionToMeasure);
        }
    }

    private ArrayList<Long> benchmark(final Supplier functionToMeasure) {
        final ArrayList<Long> measurements = Lists.newArrayList();

        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            long time = performMeasurement(functionToMeasure);
            logger.info("Iteration " + i + ": " + time);
            measurements.add(time);
        }
        return measurements;
    }

    private long performMeasurement(final Supplier toMeasure) {

        Thread finishThread = new Thread(new FinishNotifyTask());
        finishThread.start();

        final Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();

        long count = 0;
        shouldStop = false;
        while (!shouldStop) {
            result = toMeasure.get();
            count++;
        }
        stopWatch.stop();

        try {
            finishThread.join();
        } catch (InterruptedException ignored) {
        }

        return stopWatch.elapsedTime(TimeUnit.NANOSECONDS) / count;
    }

    private class FinishNotifyTask implements Runnable {

        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(ITERATION_DURATION_MSEC);
            } catch (InterruptedException ignored) {
            }
            shouldStop = true;
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = sortEntryByValue(map);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortEntryByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return (e2.getValue()).compareTo(e1.getValue());
            }
        });

        return list;
    }
}
