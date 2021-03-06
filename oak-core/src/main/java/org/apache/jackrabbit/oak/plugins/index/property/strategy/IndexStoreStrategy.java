begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|index
operator|.
name|property
operator|.
name|strategy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|base
operator|.
name|Supplier
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
name|CommitFailedException
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
name|query
operator|.
name|Filter
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
name|NodeBuilder
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
comment|/**  * Strategy that defines how the index content will be actually stored under the  * index node  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexStoreStrategy
block|{
comment|/**      * Updates the index for the given path.      *       * @param index the index node supplier      * @param path path stored in the index      * @param indexName the name of the index. May be null.      * @param indexMeta the definition of the index. May be null.      * @param beforeKeys keys that no longer apply to the path      * @param afterKeys keys that now do apply to the path      */
name|void
name|update
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeBuilder
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Check whether an entry for the given key exists.      *       * @param index the index node supplier      * @param key the key      * @return true if at least one entry exists      */
name|boolean
name|exists
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Search for a given set of values.      *       * @param filter the filter (can optionally be used for optimized query execution)      * @param indexName the name of the index (for logging)      * @param indexMeta the index metadata node (may not be null)      * @param values values to look for (null to check for property existence)      * @return an iterator of paths      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Count the occurrence of a given set of values. Used in calculating the      * cost of an index.      *      * @param root the root node (may not be null)      * @param indexMeta the index metadata node (may not be null)      * @param values values to look for (null to check for property existence)      * @param max the maximum value to return      * @return the aggregated count of occurrences for each provided value      */
name|long
name|count
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
function_decl|;
comment|/**      * Count the occurrence of a given set of values. Used in calculating the      * cost of an index.      *      * @param filter the filter which can be used to estimate better cost      * @param root the root node (may not be null)      * @param indexMeta the index metadata node (may not be null)      * @param values values to look for (null to check for property existence)      * @param max the maximum value to return      * @return the aggregated count of occurrences for each provided value      */
name|long
name|count
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
function_decl|;
name|String
name|getIndexNodeName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

