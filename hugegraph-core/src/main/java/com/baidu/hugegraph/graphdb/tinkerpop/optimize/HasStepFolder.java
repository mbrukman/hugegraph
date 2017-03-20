// Copyright 2017 HugeGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.graphdb.tinkerpop.optimize;

import com.baidu.hugegraph.core.Cardinality;
import com.baidu.hugegraph.core.PropertyKey;
import com.baidu.hugegraph.core.HugeGraphTransaction;
import com.baidu.hugegraph.graphdb.query.QueryUtil;
import com.baidu.hugegraph.graphdb.query.HugeGraphPredicate;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.Ranging;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.RangeGlobalStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.OrderGlobalStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.IdentityStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ElementValueComparator;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;

import org.javatuples.Pair;

import java.util.Comparator;
import java.util.List;

/**
 */
public interface HasStepFolder<S, E> extends Step<S, E> {

    public void addAll(Iterable<HasContainer> hasContainers);

    public void orderBy(String key, Order order);

    public void setLimit(int limit);

    public int getLimit();

    public static boolean validHugeGraphHas(HasContainer has) {
        return HugeGraphPredicate.Converter.supports(has.getBiPredicate());
    }

    public static boolean validHugeGraphHas(Iterable<HasContainer> has) {
        for (HasContainer h : has) {
            if (!validHugeGraphHas(h)) return false;
        }
        return true;
    }

    public static boolean validHugeGraphOrder(OrderGlobalStep ostep, Traversal rootTraversal,
                                          boolean isVertexOrder) {
        for (Pair<Traversal.Admin<Object, Comparable>, Comparator<Comparable>> comp : (List<Pair<Traversal.Admin<Object, Comparable>, Comparator<Comparable>>>) ostep.getComparators()) {
            if (!(comp.getValue1() instanceof ElementValueComparator)) return false;
            ElementValueComparator evc = (ElementValueComparator) comp.getValue1();
            if (!(evc.getValueComparator() instanceof Order)) return false;

            HugeGraphTransaction tx = HugeGraphTraversalUtil.getTx(rootTraversal.asAdmin());
            String key = evc.getPropertyKey();
            PropertyKey pkey = tx.getPropertyKey(key);
            if (pkey == null || !(Comparable.class.isAssignableFrom(pkey.dataType()))) return false;
            if (isVertexOrder && pkey.cardinality() != Cardinality.SINGLE) return false;
        }
        return true;
    }

    public static void foldInHasContainer(final HasStepFolder hugegraphStep, final Traversal.Admin<?, ?> traversal) {

        Step<?, ?> currentStep = hugegraphStep.getNextStep();
        while (true) {
            if (currentStep instanceof HasContainerHolder) {
                Iterable<HasContainer> containers = ((HasContainerHolder) currentStep).getHasContainers();
                if (validHugeGraphHas(containers)) {
                    hugegraphStep.addAll(containers);
                    currentStep.getLabels().forEach(hugegraphStep::addLabel);
                    traversal.removeStep(currentStep);
                }
            } else if (currentStep instanceof IdentityStep) {
                // do nothing, has no impact
            } else {
                break;
            }
            currentStep = currentStep.getNextStep();
        }
    }

//    public static boolean addLabeledStepAsIdentity(Step<?,?> currentStep, final Traversal.Admin<?, ?> traversal) {
//        if (currentStep.getLabel().isPresent()) {
//            final IdentityStep identityStep = new IdentityStep<>(traversal);
//            identityStep.setLabel(currentStep.getLabel().get());
//            TraversalHelper.insertAfterStep(identityStep, currentStep, traversal);
//            return true;
//        } else return false;
//    }

    public static void foldInOrder(final HasStepFolder hugegraphStep, final Traversal.Admin<?, ?> traversal,
                                   final Traversal<?, ?> rootTraversal, boolean isVertexOrder) {
        Step<?, ?> currentStep = hugegraphStep.getNextStep();
        OrderGlobalStep<?, ?> lastOrder = null;
        while (true) {
            if (currentStep instanceof OrderGlobalStep) {
                if (lastOrder != null) { //Previous orders are rendered irrelevant by next order (since re-ordered)
                    lastOrder.getLabels().forEach(hugegraphStep::addLabel);
                    traversal.removeStep(lastOrder);
                }
                lastOrder = (OrderGlobalStep) currentStep;
            } else if (currentStep instanceof IdentityStep) {
                // do nothing, can be skipped
            } else if (currentStep instanceof HasStep) {
                // do nothing, can be skipped
            } else {
                break;
            }
            currentStep = currentStep.getNextStep();
        }

        if (lastOrder != null && lastOrder instanceof OrderGlobalStep) {
            if (validHugeGraphOrder(lastOrder, rootTraversal, isVertexOrder)) {
                //Add orders to HasStepFolder
                for (Pair<Traversal.Admin<Object, Comparable>, Comparator<Comparable>> comp : (List<Pair<Traversal.Admin<Object, Comparable>, Comparator<Comparable>>>) ((OrderGlobalStep) lastOrder).getComparators()) {
                    ElementValueComparator evc = (ElementValueComparator) comp.getValue1();
                    hugegraphStep.orderBy(evc.getPropertyKey(), (Order) evc.getValueComparator());
                }
                lastOrder.getLabels().forEach(hugegraphStep::addLabel);
                traversal.removeStep(lastOrder);
            }
        }
    }

    public static class OrderEntry {

        public final String key;
        public final Order order;

        public OrderEntry(String key, Order order) {
            this.key = key;
            this.order = order;
        }
    }

    public static <E extends Ranging> void foldInRange(final HasStepFolder hugegraphStep, final Traversal.Admin<?, ?> traversal) {
        Step<?, ?> nextStep = HugeGraphTraversalUtil.getNextNonIdentityStep(hugegraphStep);

        if (nextStep instanceof RangeGlobalStep) {
            RangeGlobalStep range = (RangeGlobalStep) nextStep;
            int limit = QueryUtil.convertLimit(range.getHighRange());
            hugegraphStep.setLimit(QueryUtil.mergeLimits(limit, hugegraphStep.getLimit()));
            if (range.getLowRange() == 0) { //Range can be removed since there is no offset
                nextStep.getLabels().forEach(hugegraphStep::addLabel);
                traversal.removeStep(nextStep);
            }
        }
    }


}