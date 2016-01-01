begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|query
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ProviderType
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
name|PropertyValue
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
name|Result
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
name|Tree
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
name|query
operator|.
name|ast
operator|.
name|ColumnImpl
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
name|query
operator|.
name|ast
operator|.
name|OrderingImpl
import|;
end_import

begin_comment
comment|/**  * A "select" or "union" query.  *<p>  * Lifecycle: use the constructor to create a new object. Call init() to  * initialize the bind variable map. If the query is re-executed, a new instance  * is created.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|Query
block|{
name|void
name|setExecutionContext
parameter_list|(
name|ExecutionContext
name|context
parameter_list|)
function_decl|;
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
function_decl|;
name|void
name|bindValue
parameter_list|(
name|String
name|key
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
function_decl|;
name|void
name|setTraversalEnabled
parameter_list|(
name|boolean
name|traversalEnabled
parameter_list|)
function_decl|;
name|Result
name|executeQuery
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|()
function_decl|;
name|ColumnImpl
index|[]
name|getColumns
parameter_list|()
function_decl|;
name|int
name|getColumnIndex
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
name|String
index|[]
name|getSelectorNames
parameter_list|()
function_decl|;
name|int
name|getSelectorIndex
parameter_list|(
name|String
name|selectorName
parameter_list|)
function_decl|;
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|getRows
parameter_list|()
function_decl|;
comment|/**      * Get the size if known.      *       * @return the size, or -1 if unknown      */
name|long
name|getSize
parameter_list|()
function_decl|;
comment|/**      * Get the size if known.      *       * @param precision the required precision      * @param max the maximum nodes read (for an exact size)      * @return the size, or -1 if unknown      */
name|long
name|getSize
parameter_list|(
name|Result
operator|.
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
function_decl|;
name|void
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
function_decl|;
name|void
name|setMeasure
parameter_list|(
name|boolean
name|measure
parameter_list|)
function_decl|;
name|void
name|setOrderings
parameter_list|(
name|OrderingImpl
index|[]
name|orderings
parameter_list|)
function_decl|;
comment|/**      * Initialize the query. This will 'wire' selectors into constraints, and      * collect bind variable names. It will also simplify expressions if      * possible, but will not prepare the query.      */
name|void
name|init
parameter_list|()
function_decl|;
comment|/**      * Prepare the query. The cost is estimated and the execution plan is      * decided here.      */
name|void
name|prepare
parameter_list|()
function_decl|;
comment|/**      * Get the query plan. The query must already be prepared.      *       * @return the query plan      */
name|String
name|getPlan
parameter_list|()
function_decl|;
comment|/**      * Get the index cost as a JSON string. The query must already be prepared.      *       * @return the index cost      */
name|String
name|getIndexCostInfo
parameter_list|()
function_decl|;
comment|/**      * Get the estimated cost.      *       * @return the estimated cost      */
name|double
name|getEstimatedCost
parameter_list|()
function_decl|;
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
name|boolean
name|isMeasureOrExplainEnabled
parameter_list|()
function_decl|;
name|void
name|setInternal
parameter_list|(
name|boolean
name|internal
parameter_list|)
function_decl|;
comment|/**      * Returns whether the results will be sorted by index. The query must already be prepared.      *      * @return if sorted by index      */
name|boolean
name|isSortedByIndex
parameter_list|()
function_decl|;
comment|/**      * Try to convert the query to an alternative form, specially a "union". To      * avoid any potential error due to state variables perform the conversion      * before the {@link #init()}.      *       * @return {@code this} if no conversions are possible or a new instance of      *         a {@link Query}. Cannot return null.      */
annotation|@
name|Nonnull
name|Query
name|buildAlternativeQuery
parameter_list|()
function_decl|;
comment|/**      *<p>      * returns a clone of the current object. Will throw an exception in case it's invoked in a non      * appropriate moment. For example the default {@link QueryImpl} cannot be cloned once the      * {@link #init()} has been executed.      *</p>      *       *<p>      *<strong>May return null if not implemented.</strong>      *</p>      * @return a clone of self      * @throws IllegalStateException      */
annotation|@
name|Nullable
name|Query
name|copyOf
parameter_list|()
throws|throws
name|IllegalStateException
function_decl|;
comment|/**      * @return {@code true} if the query has been already initialised. {@code false} otherwise.      */
name|boolean
name|isInit
parameter_list|()
function_decl|;
comment|/**      * @return the original statement as it was used to construct the object. If not provided the      *         toString() will be used instead.      */
name|String
name|getStatement
parameter_list|()
function_decl|;
comment|/**      *       * @return {@code true} if the current query is internal. {@code false} otherwise.      */
name|boolean
name|isInternal
parameter_list|()
function_decl|;
comment|/**      * Whether the condition contains a fulltext condition that can not be       * applied to the filter, for example because it is part of an "or" condition      * of the form "where a=1 or contains(., 'x')".      *       * @return true if yes      */
name|boolean
name|containsUnfilteredFullTextCondition
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

