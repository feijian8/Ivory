/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ivory.entity.parser;

import java.util.Date;

import org.apache.ivory.IvoryException;
import org.apache.ivory.entity.FeedHelper;
import org.apache.ivory.entity.ProcessHelper;
import org.apache.ivory.entity.v0.feed.Feed;
import org.apache.ivory.entity.v0.process.Cluster;
import org.apache.ivory.entity.v0.process.Input;
import org.apache.ivory.entity.v0.process.Output;
import org.apache.ivory.entity.v0.process.Process;
import org.apache.ivory.entity.v0.process.Validity;
import org.apache.ivory.expression.ExpressionHelper;

public final class CrossEntityValidations {

    public static void validateInstanceRange(Process process, Input input, Feed feed) throws IvoryException {

        try {
            for (Cluster cluster : process.getClusters().getClusters()) {
                String clusterName = cluster.getName();
                org.apache.ivory.entity.v0.feed.Validity feedValidity = FeedHelper.getCluster(feed, clusterName).getValidity();
                Date feedStart = feedValidity.getStart();
                Date feedEnd = feedValidity.getEnd();

                String instStartEL = input.getStart();
                String instEndEL = input.getEnd();
                ExpressionHelper evaluator = ExpressionHelper.get();

                Validity processValidity = ProcessHelper.getCluster(process, clusterName).getValidity();
                ExpressionHelper.setReferenceDate(processValidity.getStart());
                Date instStart = evaluator.evaluate(instStartEL, Date.class);
                if (instStart.before(feedStart))
                    throw new ValidationException("Start instance  " + instStartEL + " of feed " + feed.getName()
                            + " is before the start of feed " + feedValidity.getStart() + " for cluster " + clusterName);

                Date instEnd = evaluator.evaluate(instEndEL, Date.class);
                if (instEnd.after(feedEnd))
                    throw new ValidationException("End instance  " + instEndEL + " of feed " + feed.getName()
                            + " is before the start of feed " + feedValidity.getStart() + " for cluster " + clusterName);

                if (instEnd.before(instStart))
                    throw new ValidationException("End instance " + instEndEL + " for feed " + feed.getName()
                            + " is before the start instance " + instStartEL + " for cluster " + clusterName);

                if (instEnd.after(feedEnd))
                    throw new ValidationException("End instance " + instEndEL + " for feed " + feed.getName()
                            + " is after the end of feed " + feedValidity.getEnd() + " for cluster " + clusterName);
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new IvoryException(e);
        }
    }

    public static void validateFeedRetentionPeriod(String startInstance, Feed feed, String clusterName) throws IvoryException {
        String feedRetention = FeedHelper.getCluster(feed, clusterName).getRetention().getLimit().toString();
        ExpressionHelper evaluator = ExpressionHelper.get();

        Date now = new Date();
        ExpressionHelper.setReferenceDate(now);
        Date instStart = evaluator.evaluate(startInstance, Date.class);
        long feedDuration = evaluator.evaluate(feedRetention, Long.class);
        Date feedStart = new Date(now.getTime() - feedDuration);

        if (instStart.before(feedStart)) {
            throw new ValidationException("StartInstance :" + startInstance + " of process is out of range for Feed: "
                    + feed.getName() + "  in cluster: " + clusterName + "'s retention limit :" + feedRetention);
        }
    }

    // Mapping to oozie coord's dataset fields
    public static void validateInstance(Process process, Output output, Feed feed) throws IvoryException {

        try {
            for (Cluster cluster : process.getClusters().getClusters()) {
                String clusterName = cluster.getName();
                org.apache.ivory.entity.v0.feed.Validity feedValidity = FeedHelper.getCluster(feed, clusterName).getValidity();
                Date feedStart = feedValidity.getStart();
                Date feedEnd = feedValidity.getEnd();

                String instEL = output.getInstance();
                ExpressionHelper evaluator = ExpressionHelper.get();
                Validity processValidity = ProcessHelper.getCluster(process, clusterName).getValidity();
                ExpressionHelper.setReferenceDate(processValidity.getStart());
                Date inst = evaluator.evaluate(instEL, Date.class);
                if (inst.before(feedStart))
                    throw new ValidationException("Instance  " + instEL + " of feed " + feed.getName()
                            + " is before the start of feed " + feedValidity.getStart() + " for cluster" + clusterName);

                if (inst.after(feedEnd))
                    throw new ValidationException("End instance " + instEL + " for feed " + feed.getName()
                            + " is after the end of feed " + feedValidity.getEnd() + " for cluster" + clusterName);
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new IvoryException(e);
        }
    }

    public static void validateInputPartition(Input input, Feed feed) throws ValidationException {
        String[] parts = input.getPartition().split("/");
        if (feed.getPartitions() == null || feed.getPartitions().getPartitions().isEmpty()
                || feed.getPartitions().getPartitions().size() < parts.length)
            throw new ValidationException("Partition specification in input " + input.getName() + " is wrong");
    }

    public static void validateFeedDefinedForCluster(Feed feed, String clusterName) throws IvoryException {
        if (FeedHelper.getCluster(feed, clusterName) == null)
            throw new ValidationException("Feed " + feed.getName() + " is not defined for cluster " + clusterName);
    }
}
