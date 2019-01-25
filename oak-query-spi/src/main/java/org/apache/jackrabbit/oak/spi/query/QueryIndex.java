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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|api
operator|.
name|Type
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

begin_import
import|import static
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
operator|.
name|Filter
operator|.
name|PropertyRestriction
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
comment|/**      * Returns the minimum cost which {@link #getCost(Filter, NodeState)} would return in the best possible case.      *<p>      * The implementation should return a static/cached value because it is called very often.      *      * @return the minimum cost for the index      */
name|double
name|getMinimumCost
parameter_list|()
function_decl|;
comment|/**      * Estimate the worst-case cost to query with the given filter. The returned      * cost is a value between 1 (very fast; lookup of a unique node) and the      * estimated number of entries to traverse, if the cursor would be fully      * read, and if there could in theory be one network roundtrip or disk read      * operation per node (this method may return a lower number if the data is      * known to be fully in memory).      *<p>      * The returned value is supposed to be an estimate and doesn't have to be      * very accurate. Please note this method is called on each index whenever a      * query is run, so the method should be reasonably fast (not read any data      * itself, or at least not read too much data).      *<p>      * If an index implementation can not query the data, it has to return      * {@code Double.MAX_VALUE}.      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return the estimated cost in number of read nodes      */
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
comment|/**      * Query the index. The returned cursor is supposed to return as few nodes      * as possible, but may return more nodes than necessary.      *<p>      * An implementation should only filter the result if it can do so easily      * and efficiently; the query engine will verify the data again (in memory)      * and check for access rights.      *<p>      * The method is only called if this index is used for the given query and      * selector, which is only the case if the given index implementation      * returned the lowest cost for the given filter. If the implementation      * returned {@code Double.MAX_VALUE} in the getCost method for the given      * filter, then this method is not called. If it is still called, then it is      * supposed to throw an exception (as it would be an internal error of the      * query engine).      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return a cursor to iterate over the result      */
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
comment|/**      * Get the query plan for the given filter. This method is called when      * running an {@code EXPLAIN SELECT} query, or for logging purposes. The      * result should be human readable.      *      * @param filter the filter      * @param rootState root state of the current repository snapshot      * @return the query plan      */
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
comment|/**      * Get the generic index name (normally the index type).      *      * @return the index name      */
name|String
name|getIndexName
parameter_list|()
function_decl|;
comment|/**      * Get the specific index name (the path of the index definition, or the      * index type if that one is unique).      *      * @return the index name      */
specifier|default
name|String
name|getIndexName
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|getIndexName
argument_list|()
return|;
block|}
comment|/**      *  A marker interface which means this index supports executing native queries      */
interface|interface
name|NativeQueryIndex
block|{
comment|// a marker interface
block|}
comment|/**      * A marker interface which means this index supports may support more than      * just the minimal fulltext query syntax. If this index is used, then the      * query engine does not verify the fulltext constraint(s) for the given      * selector.      */
interface|interface
name|FulltextQueryIndex
extends|extends
name|QueryIndex
extends|,
name|NativeQueryIndex
block|{
comment|/**          * Returns the NodeAggregator responsible for providing the aggregation          * settings or null if aggregation is not available/desired.          *          * @return the node aggregator or null          */
annotation|@
name|Nullable
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
function_decl|;
block|}
interface|interface
name|AdvanceFulltextQueryIndex
extends|extends
name|FulltextQueryIndex
extends|,
name|AdvancedQueryIndex
block|{
comment|// a marker interface
block|}
comment|/**      * An query index that may support using multiple access orders      * (returning the rows in a specific order), and that can provide detailed      * information about the cost.      */
interface|interface
name|AdvancedQueryIndex
block|{
comment|/**          * Return the possible index plans for the given filter and sort order.          * Please note this method is supposed to run quickly. That means it          * should usually not read any data from the storage.          *          * @param filter the filter          * @param sortOrder the sort order or null if no sorting is required          * @param rootState root state of the current repository snapshot          * @return the list of index plans (null if none)          */
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|getPlans
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**          * Get the query plan description (for logging purposes).          *<p>          * The index plan is one of the plans that the index returned in the          * getPlans call.          *          * @param plan the index plan          * @param root root state of the current repository snapshot          * @return the query plan description          */
name|String
name|getPlanDescription
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|root
parameter_list|)
function_decl|;
comment|/**          * Start a query. The filter and sort order of the index plan is to be          * used.          *<p>          * The index plan is one of the plans that the index returned in the          * getPlans call.          *          * @param plan the index plan to use          * @param rootState root state of the current repository snapshot          * @return a cursor to iterate over the result          */
name|Cursor
name|query
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
function_decl|;
block|}
comment|/**      * An index plan.      */
annotation|@
name|ProviderType
interface|interface
name|IndexPlan
extends|extends
name|Cloneable
block|{
comment|/**          * The cost to execute the query once. The returned value should          * approximately match the number of disk read operations plus the          * number of network roundtrips (worst case).          *          * @return the cost per execution, in estimated number of I/O operations          */
name|double
name|getCostPerExecution
parameter_list|()
function_decl|;
comment|/**          * The cost to read one entry from the cursor. The returned value should          * approximately match the number of disk read operations plus the          * number of network roundtrips (worst case).          *          * @return the lookup cost per entry, in estimated number of I/O operations          */
name|double
name|getCostPerEntry
parameter_list|()
function_decl|;
comment|/**          * The estimated number of entries in the cursor that is returned by the query method,          * when using this plan. This value does not have to be accurate.          *          * @return the estimated number of entries          */
name|long
name|getEstimatedEntryCount
parameter_list|()
function_decl|;
comment|/**          * The filter to use.          *          * @return the filter          */
name|Filter
name|getFilter
parameter_list|()
function_decl|;
comment|/**          * Use the given filter.          */
name|void
name|setFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
function_decl|;
comment|/**          * Whether the index is not always up-to-date.          *          * @return whether the index might be updated asynchronously          */
name|boolean
name|isDelayed
parameter_list|()
function_decl|;
comment|/**          * Whether the fulltext part of the filter is evaluated (possibly with          * an extended syntax). If set, the fulltext part of the filter is not          * evaluated any more within the query engine.          *          * @return whether the index supports full-text extraction          */
name|boolean
name|isFulltextIndex
parameter_list|()
function_decl|;
comment|/**          * Whether the cursor is able to read all properties from a node.          * If yes, then the query engine will not have to read the data itself.          *          * @return wheter node data is returned          */
name|boolean
name|includesNodeData
parameter_list|()
function_decl|;
comment|/**          * The sort order of the returned entries, or null if unsorted.          *          * @return the sort order          */
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|getSortOrder
parameter_list|()
function_decl|;
comment|/**          * The node state with the index definition.          *          * @return the node state with the index definition.          */
name|NodeState
name|getDefinition
parameter_list|()
function_decl|;
comment|/**          * The path prefix for this index plan.          */
name|String
name|getPathPrefix
parameter_list|()
function_decl|;
comment|/**          * Whether index for this plan supports handling path restrictions natively.          */
name|boolean
name|getSupportsPathRestriction
parameter_list|()
function_decl|;
comment|/**          * The property restriction for this index plan or<code>null</code> if          * this index plan isn't base on a property restriction. E.g. a plan          * based on an order by clause in the query.          *          * @return the restriction this plan is based on or<code>null</code>.          */
annotation|@
name|Nullable
name|PropertyRestriction
name|getPropertyRestriction
parameter_list|()
function_decl|;
comment|/**          * Creates a cloned copy of current plan. Mostly used when the filter needs to be          * modified for a given call          *          * @return clone of current plan          */
name|IndexPlan
name|copy
parameter_list|()
function_decl|;
comment|/**          * Returns the value of the named attribute as an<code>Object</code>,          * or<code>null</code> if no attribute of the given name exists.          *          * @param name<code>String</code> specifying the name of          * the attribute          *          * @return an<code>Object</code> containing the value          * of the attribute, or<code>null</code> if the attribute does not exist          */
annotation|@
name|Nullable
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**          * Get the unique plan name.          *          * @return the plan name          */
annotation|@
name|Nullable
name|String
name|getPlanName
parameter_list|()
function_decl|;
comment|/**          * Whether the index is deprecated.          *          * @return if it is deprecated          */
name|boolean
name|isDeprecated
parameter_list|()
function_decl|;
comment|/**          * A builder for index plans.          */
class|class
name|Builder
block|{
specifier|protected
name|double
name|costPerExecution
init|=
literal|1.0
decl_stmt|;
specifier|protected
name|double
name|costPerEntry
init|=
literal|1.0
decl_stmt|;
specifier|protected
name|long
name|estimatedEntryCount
init|=
literal|1000000
decl_stmt|;
specifier|protected
name|Filter
name|filter
decl_stmt|;
specifier|protected
name|boolean
name|isDelayed
decl_stmt|;
specifier|protected
name|boolean
name|isFulltextIndex
decl_stmt|;
specifier|protected
name|boolean
name|includesNodeData
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
decl_stmt|;
specifier|protected
name|NodeState
name|definition
decl_stmt|;
specifier|protected
name|PropertyRestriction
name|propRestriction
decl_stmt|;
specifier|protected
name|String
name|pathPrefix
init|=
literal|"/"
decl_stmt|;
specifier|protected
name|boolean
name|supportsPathRestriction
init|=
literal|false
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|planName
decl_stmt|;
specifier|protected
name|boolean
name|deprecated
decl_stmt|;
specifier|public
name|Builder
name|setCostPerExecution
parameter_list|(
name|double
name|costPerExecution
parameter_list|)
block|{
name|this
operator|.
name|costPerExecution
operator|=
name|costPerExecution
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setCostPerEntry
parameter_list|(
name|double
name|costPerEntry
parameter_list|)
block|{
name|this
operator|.
name|costPerEntry
operator|=
name|costPerEntry
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setEstimatedEntryCount
parameter_list|(
name|long
name|estimatedEntryCount
parameter_list|)
block|{
name|this
operator|.
name|estimatedEntryCount
operator|=
name|estimatedEntryCount
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setDelayed
parameter_list|(
name|boolean
name|isDelayed
parameter_list|)
block|{
name|this
operator|.
name|isDelayed
operator|=
name|isDelayed
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setFulltextIndex
parameter_list|(
name|boolean
name|isFulltextIndex
parameter_list|)
block|{
name|this
operator|.
name|isFulltextIndex
operator|=
name|isFulltextIndex
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setIncludesNodeData
parameter_list|(
name|boolean
name|includesNodeData
parameter_list|)
block|{
name|this
operator|.
name|includesNodeData
operator|=
name|includesNodeData
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setSortOrder
parameter_list|(
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|)
block|{
name|this
operator|.
name|sortOrder
operator|=
name|sortOrder
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setDefinition
parameter_list|(
name|NodeState
name|definition
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setPropertyRestriction
parameter_list|(
name|PropertyRestriction
name|restriction
parameter_list|)
block|{
name|this
operator|.
name|propRestriction
operator|=
name|restriction
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setPathPrefix
parameter_list|(
name|String
name|pathPrefix
parameter_list|)
block|{
name|this
operator|.
name|pathPrefix
operator|=
name|pathPrefix
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setSupportsPathRestriction
parameter_list|(
name|boolean
name|supportsPathRestriction
parameter_list|)
block|{
name|this
operator|.
name|supportsPathRestriction
operator|=
name|supportsPathRestriction
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setPlanName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|planName
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setDeprecated
parameter_list|(
name|boolean
name|deprecated
parameter_list|)
block|{
name|this
operator|.
name|deprecated
operator|=
name|deprecated
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|IndexPlan
name|build
parameter_list|()
block|{
return|return
operator|new
name|IndexPlan
argument_list|()
block|{
specifier|private
specifier|final
name|double
name|costPerExecution
init|=
name|Builder
operator|.
name|this
operator|.
name|costPerExecution
decl_stmt|;
specifier|private
specifier|final
name|double
name|costPerEntry
init|=
name|Builder
operator|.
name|this
operator|.
name|costPerEntry
decl_stmt|;
specifier|private
specifier|final
name|long
name|estimatedEntryCount
init|=
name|Builder
operator|.
name|this
operator|.
name|estimatedEntryCount
decl_stmt|;
specifier|private
name|Filter
name|filter
init|=
name|Builder
operator|.
name|this
operator|.
name|filter
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isDelayed
init|=
name|Builder
operator|.
name|this
operator|.
name|isDelayed
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isFulltextIndex
init|=
name|Builder
operator|.
name|this
operator|.
name|isFulltextIndex
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includesNodeData
init|=
name|Builder
operator|.
name|this
operator|.
name|includesNodeData
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
init|=
name|Builder
operator|.
name|this
operator|.
name|sortOrder
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ArrayList
argument_list|<
name|OrderEntry
argument_list|>
argument_list|(
name|Builder
operator|.
name|this
operator|.
name|sortOrder
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|definition
init|=
name|Builder
operator|.
name|this
operator|.
name|definition
decl_stmt|;
specifier|private
specifier|final
name|PropertyRestriction
name|propRestriction
init|=
name|Builder
operator|.
name|this
operator|.
name|propRestriction
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathPrefix
init|=
name|Builder
operator|.
name|this
operator|.
name|pathPrefix
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|supportsPathRestriction
init|=
name|Builder
operator|.
name|this
operator|.
name|supportsPathRestriction
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|Builder
operator|.
name|this
operator|.
name|attributes
decl_stmt|;
specifier|private
specifier|final
name|String
name|planName
init|=
name|Builder
operator|.
name|this
operator|.
name|planName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|deprecated
init|=
name|Builder
operator|.
name|this
operator|.
name|deprecated
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"{ costPerExecution : %s,"
operator|+
literal|" costPerEntry : %s,"
operator|+
literal|" estimatedEntryCount : %s,"
operator|+
literal|" filter : %s,"
operator|+
literal|" isDelayed : %s,"
operator|+
literal|" isFulltextIndex : %s,"
operator|+
literal|" includesNodeData : %s,"
operator|+
literal|" sortOrder : %s,"
operator|+
literal|" definition : %s,"
operator|+
literal|" propertyRestriction : %s,"
operator|+
literal|" pathPrefix : %s,"
operator|+
literal|" deprecated : %s,"
operator|+
literal|" supportsPathRestriction : %s }"
argument_list|,
name|costPerExecution
argument_list|,
name|costPerEntry
argument_list|,
name|estimatedEntryCount
argument_list|,
name|filter
argument_list|,
name|isDelayed
argument_list|,
name|isFulltextIndex
argument_list|,
name|includesNodeData
argument_list|,
name|sortOrder
argument_list|,
name|definition
argument_list|,
name|propRestriction
argument_list|,
name|pathPrefix
argument_list|,
name|deprecated
argument_list|,
name|supportsPathRestriction
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCostPerExecution
parameter_list|()
block|{
return|return
name|costPerExecution
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCostPerEntry
parameter_list|()
block|{
return|return
name|costPerEntry
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEstimatedEntryCount
parameter_list|()
block|{
return|return
name|estimatedEntryCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDelayed
parameter_list|()
block|{
return|return
name|isDelayed
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFulltextIndex
parameter_list|()
block|{
return|return
name|isFulltextIndex
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includesNodeData
parameter_list|()
block|{
return|return
name|includesNodeData
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|getSortOrder
parameter_list|()
block|{
return|return
name|sortOrder
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyRestriction
name|getPropertyRestriction
parameter_list|()
block|{
return|return
name|propRestriction
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathPrefix
parameter_list|()
block|{
return|return
name|pathPrefix
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getSupportsPathRestriction
parameter_list|()
block|{
return|return
name|supportsPathRestriction
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexPlan
name|copy
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|IndexPlan
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlanName
parameter_list|()
block|{
return|return
name|planName
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDeprecated
parameter_list|()
block|{
return|return
name|deprecated
return|;
block|}
block|}
return|;
block|}
block|}
block|}
comment|/**      * A sort order entry.      */
class|class
name|OrderEntry
block|{
comment|/**          * The property name on where to sort.          */
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
comment|/**          * The property type. Null if not known.          */
specifier|private
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
decl_stmt|;
comment|/**          * The sort order (ascending or descending).          */
specifier|public
enum|enum
name|Order
block|{
name|ASCENDING
block|,
name|DESCENDING
block|}
specifier|private
specifier|final
name|Order
name|order
decl_stmt|;
specifier|public
name|OrderEntry
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
parameter_list|,
name|Order
name|order
parameter_list|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|propertyType
operator|=
name|propertyType
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
block|}
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|propertyName
return|;
block|}
specifier|public
name|Order
name|getOrder
parameter_list|()
block|{
return|return
name|order
return|;
block|}
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getPropertyType
parameter_list|()
block|{
return|return
name|propertyType
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"{ propertyName : %s, propertyType : %s, order : %s }"
argument_list|,
name|propertyName
argument_list|,
name|propertyType
argument_list|,
name|order
argument_list|)
return|;
block|}
block|}
interface|interface
name|NodeAggregator
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParents
parameter_list|(
name|NodeState
name|rootState
parameter_list|,
name|String
name|path
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

