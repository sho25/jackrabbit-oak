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
name|p2
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
comment|/**      * Removes a set of values from the index      *       * @param index the index node      * @param key the index key      * @param values the values to be removed from the given key      * @throws CommitFailedException      */
name|void
name|remove
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Inserts a set of values in the index      *       * @param index the index node      * @param key the index key      * @param unique if the index is defined as unique      *<b>Note:</b> If the uniqueness constraint is broken, the method will throw a<code>CommitFailedException</code>      * @param values the values to be added to the given key      * @throws CommitFailedException      */
name|void
name|insert
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Search for a given set of values.      *       * @param index index node (may not be null)      * @param values values to look for (null to check for property existence)      * @return the set of paths corresponding to the given values      */
annotation|@
name|Deprecated
name|Set
argument_list|<
name|String
argument_list|>
name|find
parameter_list|(
name|NodeState
name|index
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Search for a given set of values.      *       * @param indexName the name of the index (for logging)      * @param index index node (may not be null)      * @param values values to look for (null to check for property existence)      * @return an iterator of paths      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|String
name|indexName
parameter_list|,
name|NodeState
name|index
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Count the occurrence of a given set of values. Used in calculating the      * cost of an index.      *       * @param index the index node (may not be null)      * @param values values to look for (null to check for property existence)      * @return the aggregated count of occurrences for each provided value      */
name|int
name|count
parameter_list|(
name|NodeState
name|index
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

