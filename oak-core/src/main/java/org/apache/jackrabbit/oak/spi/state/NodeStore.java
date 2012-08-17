begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|state
package|;
end_package

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
name|CoreValueFactory
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
name|commit
operator|.
name|CommitEditor
import|;
end_import

begin_comment
comment|/**  * Storage abstraction for trees. At any given point in time the stored  * tree is rooted at a single immutable node state.  *<p>  * This is a low-level interface that doesn't cover functionality like  * merging concurrent changes or rejecting new tree states based on some  * higher-level consistency constraints.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStore
block|{
comment|/**      * Returns the latest state of the tree.      *      * @return root node state      */
annotation|@
name|Nonnull
name|NodeState
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Creates a new branch of the tree to which transient changes can be applied.      *      * @return branch      */
annotation|@
name|Nonnull
name|NodeStoreBranch
name|branch
parameter_list|(
name|CommitEditor
name|commitEditor
parameter_list|)
function_decl|;
comment|/**      * Returns a builder for constructing a new or modified node state.      * The builder is initialized with all the properties and child nodes      * from the given base node state.      *      * @param base  base node state, or {@code null} for building new nodes      * @return  builder instance      */
annotation|@
name|Nonnull
name|NodeBuilder
name|getBuilder
parameter_list|(
name|NodeState
name|base
parameter_list|)
function_decl|;
comment|/**      * Returns the factory for creating values used for building node states.      *      * @return value factory      */
annotation|@
name|Nonnull
name|CoreValueFactory
name|getValueFactory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

