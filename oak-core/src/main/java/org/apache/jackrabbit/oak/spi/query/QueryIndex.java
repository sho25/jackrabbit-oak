begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * Represents an index. The index should use the data in the filter if possible  * to speed up reading.  *<p>  * The query engine will pick the index that returns the lowest cost for the  * given filter conditions.  *<p>  * The index should only use that part of the filter that speeds up data lookup.  * All other filter conditions should be ignored and not evaluated within this  * index, because the query engine will in any case evaluate the condition (and  * join condition), so that evaluating the conditions within the index would  * actually slow down processing. For example, an index on the property  * "lastName" should not try to evaluate any other restrictions than those on  * the property "lastName", even if the query contains other restrictions. For  * the query "where lastName = 'x' and firstName = 'y'", the query engine will  * set two filter conditions, one for "lastName" and another for "firstName".  * The index on "lastName" should not evaluate the condition on "firstName",  * even thought it will be set in the filter.  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryIndex
block|{
comment|/**      * Estimate the cost to query with the given filter. The returned      * cost is a value between 1 (very fast; lookup of a unique node) and the      * estimated number of nodes to traverse.      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return the estimated cost in number of read nodes      */
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Start a query.      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return a cursor to iterate over the result      */
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Get the query plan for the given filter.      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return the query plan      */
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Get the unique index name.      *      * @return the index name      */
name|String
name|getIndexName
parameter_list|()
function_decl|;
comment|//    /**
comment|//     * Return the possible index plans for the given filter and sort order.
comment|//     * Please note this method is supposed to run quickly. That means it should
comment|//     * usually not read any data from the storage.
comment|//     *
comment|//     * @param filter the filter
comment|//     * @param sortOrder the sort order or null if no sorting is required
comment|//     * @param rootState root state of the current repository snapshot
comment|//     * @return the list of index plans (null if none)
comment|//     */
comment|//    List<IndexPlan> getPlans(Filter filter, List<Order> sortOrder, NodeState rootState);
comment|//
comment|//    /**
comment|//     * Get the query plan description (for logging purposes).
comment|//     *
comment|//     * @param plan the index plan
comment|//     * @return the query plan description
comment|//     */
comment|//    String getPlanDescription(IndexPlan plan);
comment|//
comment|//    /**
comment|//     * Start a query. The filter and sort order of the index plan is to be used.
comment|//     *
comment|//     * @param plan the index plan to use
comment|//     * @param rootState root state of the current repository snapshot
comment|//     * @return a cursor to iterate over the result
comment|//     */
comment|//    Cursor query(IndexPlan plan, NodeState rootState);
comment|//
comment|//    /**
comment|//     * An index plan.
comment|//     */
comment|//    public static class IndexPlan {
comment|//
comment|//        /**
comment|//         * The cost to execute the query once. The returned value should
comment|//         * approximately match the number of disk read operations plus the
comment|//         * number of network roundtrips.
comment|//         */
comment|//        double costPerExecution;
comment|//
comment|//        /**
comment|//         * The cost to read one entry from the cursor. The returned value should
comment|//         * approximately match the number of disk read operations plus the
comment|//         * number of network roundtrips.
comment|//         */
comment|//        double costPerEntry;
comment|//
comment|//        /**
comment|//         * The estimated number of entries. This value does not have to be
comment|//         * accurate.
comment|//         */
comment|//        long estimatedEntryCount;
comment|//
comment|//        /**
comment|//         * The filter to use.
comment|//         */
comment|//        Filter filter;
comment|//
comment|//        /**
comment|//         * Whether transient (unsaved) changes are included.
comment|//         */
comment|//        boolean includeTransient;
comment|//
comment|//        /**
comment|//         * Whether the index is not always up-to-date.
comment|//         */
comment|//        boolean isDelayed;
comment|//
comment|//        /**
comment|//         * Whether the fulltext part of the filter is evaluated (possibly with
comment|//         * an extended syntax). If set, the fulltext part of the filter is not
comment|//         * evaluated any more within the query engine.
comment|//         */
comment|//        boolean isFulltextIndex;
comment|//
comment|//        /**
comment|//         * Whether the cursor is able to read all properties from a node.
comment|//         */
comment|//        boolean includesNodeData;
comment|//
comment|//        /**
comment|//         * The sort order of the returned entries, or null if unsorted.
comment|//         */
comment|//        List<Order> sortOrder;
comment|//
comment|//    }
comment|//
comment|//    /**
comment|//     * A sort order entry.
comment|//     */
comment|//    static class Order {
comment|//
comment|//        /**
comment|//         * The property name on where to sort.
comment|//         */
comment|//        String propertyName;
comment|//
comment|//        /**
comment|//         * True for descending, false for ascending.
comment|//         */
comment|//        boolean descending;
comment|//
comment|//    }
block|}
end_interface

end_unit

