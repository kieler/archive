package de.cau.cs.kieler.kiml.layouter.metrics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.cau.cs.kieler.core.alg.DefaultFactory;
import de.cau.cs.kieler.core.alg.IFactory;
import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.core.properties.MapPropertyHolder;
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;
import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.klay.layered.LayeredLayoutProvider;
import de.cau.cs.kieler.klay.layered.p4nodes.NodePlacementStrategy;
import de.cau.cs.kieler.klay.layered.properties.GreedyType;
import de.cau.cs.kieler.klay.layered.properties.Properties;

public class GreedySwitchMeasurer {

    private final String[] PHASE = new String[] { "Greedy switch crossing reduction", };
    private final int COUNTER_WARMUP_NODE_COUNT = 600;
    /**
     * Mask for getting a file name from a system time value.
     */
    private final long TIME_MASK = 0xfff;
    /**
     * Number of bytes in a megabyte.
     */
    private final long MEGA = 1048576;
    private final IPropertyHolder propertyHolder;
    private final Parameters parameters;
    private final IFactory<AbstractLayoutProvider> layoutProvider;

    public static void main(final String[] args) throws IOException {
        Parameters parameters = null;
        try {
            parameters = new Parameters(args);
        } catch (IllegalArgumentException e) {
            System.exit(1);
        }
        new GreedySwitchMeasurer(parameters);

    }

    private GreedySwitchMeasurer(final Parameters parameters) throws IOException {
        this.parameters = parameters;

        layoutProvider = new DefaultFactory<AbstractLayoutProvider>(LayeredLayoutProvider.class);

        propertyHolder = setProperties();

        startNodeIncreaseMeasurement();
        startDensityIncreaseMeasurement();
        startThouroughnessIncreaseMeasurement();
    }

    private IPropertyHolder setProperties() {
        // Define a property setter for layout configuration
        IPropertyHolder pH = new MapPropertyHolder();
        pH.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.ORTHOGONAL);
        pH.setProperty(LayoutOptions.RANDOM_SEED, 1);
        pH.setProperty(LayoutOptions.SEPARATE_CC, false);
        pH.setProperty(Properties.NODE_PLACER, NodePlacementStrategy.SIMPLE);
        // propertyHolder.setProperty(Properties.THOROUGHNESS, 1); // TODO-alan
        return pH;
    }

    private void startNodeIncreaseMeasurement() {
        OutputStream fileStream = null;
        try {
            setNodeIncreaseParameters();
            fileStream = new FileOutputStream(filename());
            OutputStreamWriter osw = new OutputStreamWriter(fileStream);
            for (GreedyType greedyType : GreedyType.values()) {
    
                System.out.println(greedyType);
                osw.write(greedyType + "\n");
    
                measure(osw, greedyType);
            }
    
        } catch (Exception exception) {
            printError(exception);
        } finally {
            closeStream(fileStream);
        }
    }

    private void setNodeIncreaseParameters() {
        parameters.linearScale = true;
        parameters.endDecade = 3;
        parameters.graphSizesPerDecade = 10;
    }

    private void startDensityIncreaseMeasurement() {
        setDensityIncreaseParameters();
        OutputStream fileStream = null;
        try {
            setNodeIncreaseParameters();
            fileStream = new FileOutputStream(filename());
            OutputStreamWriter osw = new OutputStreamWriter(fileStream);
            GreedyType[] greedyTypes =
                    { GreedyType.ONE_SIDED_ON_DEMAND_CROSSING_MATRIX,
                            GreedyType.TWO_SIDED_ON_DEMAND_CROSSING_MATRIX };
    
            for (GreedyType greedyType : greedyTypes) {
                parameters.doWarmup = true;
                for (float relativeEdgeCount = 1f; relativeEdgeCount < 3.4; relativeEdgeCount +=
                        0.2f) {
    
                    String descr = greedyType + " Relative Edge Count: " + relativeEdgeCount + "\n";
                    System.out.println(descr);
                    osw.write(descr);
    
                    parameters.relativeEdgeCount = relativeEdgeCount;
    
                    measure(osw, greedyType);
                    parameters.doWarmup = false;
                }
    
            }
    
        } catch (Exception exception) {
            printError(exception);
        } finally {
            closeStream(fileStream);
        }
    }

    private void setDensityIncreaseParameters() {
        parameters.onlyTestOneNodeCount = true;
        parameters.exactNodeAmount = 200;
    }

    private void startThouroughnessIncreaseMeasurement() {
        setThourughnessIncreaseParameters();
        OutputStream fileStream = null;
        try {
            setNodeIncreaseParameters();
            fileStream = new FileOutputStream(filename());
            OutputStreamWriter osw = new OutputStreamWriter(fileStream);
            GreedyType[] greedyTypes =
                    { GreedyType.ONE_SIDED_ON_DEMAND_CROSSING_MATRIX,
                            GreedyType.TWO_SIDED_ON_DEMAND_CROSSING_MATRIX };

            for (GreedyType greedyType : greedyTypes) {
                parameters.doWarmup = true;
                for (int thouroughness = 1; thouroughness < 30; thouroughness++) {

                    String descr = greedyType + " Thouroughness: " + thouroughness + "\n";
                    System.out.println(descr);
                    osw.write(descr);

                    propertyHolder.setProperty(Properties.THOROUGHNESS, thouroughness);

                    measure(osw, greedyType);

                    parameters.doWarmup = false;
                }

            }

        } catch (Exception exception) {
            printError(exception);
        } finally {
            closeStream(fileStream);
        }
    }

    private void setThourughnessIncreaseParameters() {
        parameters.onlyTestOneNodeCount = true;
        parameters.exactNodeAmount = 200;
        parameters.relativeEdgeCount = 1.4f;
    }

    private void measure(final OutputStreamWriter osw, final GreedyType greedyType)
            throws IOException {
        propertyHolder.setProperty(Properties.GREEDY_TYPE, greedyType);
        ExecutionTimeMetric metric =
                new ExecutionTimeMetric(layoutProvider, parameters, propertyHolder, PHASE, osw);

        metric.measure();

        osw.write('\n');
    }

    private String filename() {
        return "measurement" + (System.currentTimeMillis() & TIME_MASK) + ".csv";
    }

    private void closeStream(final OutputStream fileStream) {
        try {
            if (fileStream != null) {
                fileStream.close();
            }
        } catch (IOException exception) {
            // ignore exception
        }
    }

    private void printError(final Exception exception) {
        exception.printStackTrace();
        System.out.println("Memory: " + Runtime.getRuntime().freeMemory() / MEGA + "mb free / "
                + Runtime.getRuntime().totalMemory() / MEGA + " mb total");
    }
}
