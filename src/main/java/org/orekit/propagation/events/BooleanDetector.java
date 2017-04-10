/* Contributed in the public domain.
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orekit.propagation.events;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.DoubleBinaryOperator;

import org.hipparchus.util.FastMath;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.handlers.ContinueOnEvent;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

/**
 * This class provides AND and OR operations for event detectors. This class treats
 * positive values of the g function as true and negative values as false.
 *
 * <p> One example for an imaging satellite might be to only detect events when a
 * satellite is overhead (elevation &gt; 0) AND when the ground point is sunlit (Sun
 * elevation &gt; 0). Another slightly contrived example using the OR operator would be to
 * detect access to a set of ground stations and only report events when the satellite
 * enters or leaves the field of view of the set, but not hand-offs between the ground
 * stations.
 *
 * <p> For the BooleanDetector is important that the sign of the g function of the
 * underlying event detector is not arbitrary, but has a semantic meaning, e.g. in or out,
 * true or false. This class works well with event detectors that detect entry to or exit
 * from a region, e.g. {@link EclipseDetector}, {@link ElevationDetector}, {@link
 * LatitudeCrossingDetector}. Using this detector with detectors that are not based on
 * entry to or exit from a region, e.g. {@link DateDetector}, {@link
 * LongitudeCrossingDetector}, will likely lead to unexpected results. To apply conditions
 * to this latter type of event detectors a {@link EventEnablingPredicateFilter} is
 * usually more appropriate.
 *
 * @author Evan Ward
 * @see #and(Collection)
 * @see #or(Collection)
 * @see #not(EventDetector)
 * @see EventEnablingPredicateFilter
 * @see EventSlopeFilter
 */
public class BooleanDetector extends AbstractDetector<BooleanDetector> {

    /** Serializable UID. */
    private static final long serialVersionUID = 20170410L;

    /** Original detectors: the operands. */
    private final List<EventDetector> detectors;

    /** The composition function. Should be associative for predictable behavior. */
    private final DoubleBinaryOperator operator;

    /**
     * Private constructor with all the parameters.
     *
     * @param detectors    the operands.
     * @param operator     reduction operator to apply to value of the g function of the
     *                     operands.
     * @param newMaxCheck  max check interval in seconds.
     * @param newThreshold convergence threshold in seconds.
     * @param newMaxIter   max iterations.
     * @param newHandler   event handler.
     */
    private BooleanDetector(final List<EventDetector> detectors,
                            final DoubleBinaryOperator operator,
                            final double newMaxCheck,
                            final double newThreshold,
                            final int newMaxIter,
                            final EventHandler<? super BooleanDetector> newHandler) {
        super(newMaxCheck, newThreshold, newMaxIter, newHandler);
        this.detectors = detectors;
        this.operator = operator;
    }

    /**
     * Create a new event detector that is the logical AND of the given event detectors.
     *
     * <p> The created event detector's g function is positive if and only if the g
     * functions of all detectors in {@code detectors} are positive.
     *
     * <p> The starting interval, threshold, and iteration count are set to the most
     * stringent (minimum) of all the {@code detectors}. The event handlers of the
     * underlying {@code detectors} are not used, instead the default handler is {@link
     * ContinueOnEvent}.
     *
     * @param detectors the operands. Must contain at least one detector.
     * @return a new event detector that is the logical AND of the operands.
     * @throws NoSuchElementException if {@code detectors} is empty.
     * @see BooleanDetector
     * @see #and(Collection)
     * @see #or(EventDetector...)
     * @see #not(EventDetector)
     */
    public static BooleanDetector and(final EventDetector... detectors) {
        return and(Arrays.asList(detectors));
    }

    /**
     * Create a new event detector that is the logical AND of the given event detectors.
     *
     * <p> The created event detector's g function is positive if and only if the g
     * functions of all detectors in {@code detectors} are positive.
     *
     * <p> The starting interval, threshold, and iteration count are set to the most
     * stringent (minimum) of the {@code detectors}. The event handlers of the
     * underlying {@code detectors} are not used, instead the default handler is {@link
     * ContinueOnEvent}.
     *
     * @param detectors the operands. Must contain at least one detector.
     * @return a new event detector that is the logical AND of the operands.
     * @throws NoSuchElementException if {@code detectors} is empty.
     * @see BooleanDetector
     * @see #and(EventDetector...)
     * @see #or(Collection)
     * @see #not(EventDetector)
     */
    public static BooleanDetector and(
            final Collection<? extends EventDetector> detectors) {

        return new BooleanDetector(new ArrayList<>(detectors), // copy for immutability
                FastMath::min,
                detectors.stream().map(EventDetector::getMaxCheckInterval).min(Double::compareTo).get(),
                detectors.stream().map(EventDetector::getThreshold).min(Double::compareTo).get(),
                detectors.stream().map(EventDetector::getMaxIterationCount).min(Integer::compareTo).get(),
                new ContinueOnEvent<>());
    }

    /**
     * Create a new event detector that is the logical OR or the given event detectors.
     *
     * <p> The created event detector's g function is positive if and only if at least
     * one of g functions of the event detectors in {@code detectors} is positive.
     *
     * <p> The starting interval, threshold, and iteration count are set to the most
     * stringent (minimum) of the {@code detectors}. The event handlers of the
     * underlying EventDetectors are not used, instead the default handler is {@link
     * ContinueOnEvent}.
     *
     * @param detectors the operands. Must contain at least one detector.
     * @return a new event detector that is the logical OR of the operands.
     * @throws NoSuchElementException if {@code detectors} is empty.
     * @see BooleanDetector
     * @see #or(Collection)
     * @see #and(EventDetector...)
     * @see #not(EventDetector)
     */
    public static BooleanDetector or(final EventDetector... detectors) {
        return or(Arrays.asList(detectors));
    }

    /**
     * Create a new event detector that is the logical OR or the given event detectors.
     *
     * <p> The created event detector's g function is positive if and only if at least
     * one of g functions of the event detectors in {@code detectors} is positive.
     *
     * <p> The starting interval, threshold, and iteration count are set to the most
     * stringent (minimum) of the {@code detectors}. The event handlers of the
     * underlying EventDetectors are not used, instead the default handler is {@link
     * ContinueOnEvent}.
     *
     * @param detectors the operands. Must contain at least one detector.
     * @return a new event detector that is the logical OR of the operands.
     * @throws NoSuchElementException if {@code detectors} is empty.
     * @see BooleanDetector
     * @see #or(EventDetector...)
     * @see #and(Collection)
     * @see #not(EventDetector)
     */
    public static BooleanDetector or(
            final Collection<? extends EventDetector> detectors) {

        return new BooleanDetector(new ArrayList<>(detectors), // copy for immutability
                FastMath::max,
                detectors.stream().map(EventDetector::getMaxCheckInterval).min(Double::compareTo).get(),
                detectors.stream().map(EventDetector::getThreshold).min(Double::compareTo).get(),
                detectors.stream().map(EventDetector::getMaxIterationCount).min(Integer::compareTo).get(),
                new ContinueOnEvent<>());
    }

    /**
     * Create a new event detector that negates the g function of another detector.
     *
     * <p> This detector will be initialized with the same {@link
     * EventDetector#getMaxCheckInterval()}, {@link EventDetector#getThreshold()}, and
     * {@link EventDetector#getMaxIterationCount()} as {@code detector}. The event handler
     * of the underlying detector is not used, instead the default handler is {@link
     * ContinueOnEvent}.
     *
     * @param detector to negate.
     * @return an new event detector whose g function is the same magnitude but opposite
     * sign of {@code detector}.
     * @see #and(Collection)
     * @see #or(Collection)
     * @see BooleanDetector
     */
    public static NegateDetector not(final EventDetector detector) {
        return new NegateDetector(detector);
    }

    @Override
    public double g(final SpacecraftState s) throws OrekitException {
        // can't use stream/lambda here because g(s) throws a checked exception
        // so write out and combine the map and reduce loops
        double ret = Double.NaN; // return value
        boolean first = true;
        for (final EventDetector detector : detectors) {
            if (first) {
                ret = detector.g(s);
                first = false;
            } else {
                ret = this.operator.applyAsDouble(ret, detector.g(s));
            }
        }
        // return the result of applying the operator to all operands
        return ret;
    }

    @Override
    protected BooleanDetector create(final double newMaxCheck,
                                     final double newThreshold,
                                     final int newMaxIter,
                                     final EventHandler<? super BooleanDetector> newHandler) {
        return new BooleanDetector(detectors, operator, newMaxCheck, newThreshold,
                newMaxIter, newHandler);
    }

    @Override
    public void init(final SpacecraftState s0, final AbsoluteDate t) {
        super.init(s0, t);
        for (final EventDetector detector : detectors) {
            detector.init(s0, t);
        }
    }

}
