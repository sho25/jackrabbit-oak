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
name|CheckForNull
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
name|PropertyState
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

begin_comment
comment|/**  * Builder interface for constructing new {@link NodeState node states}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeBuilder
block|{
comment|/**      * Returns an immutable node state that matches the current state of      * the builder.      *      * @return immutable node state      */
annotation|@
name|Nonnull
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
comment|/**      * Returns the original base state that this builder is modifying.      * Returns {@code null} if this builder represents a new node that      * didn't exist in the base content tree.      *      * @return base node state, or {@code null}      */
annotation|@
name|CheckForNull
name|NodeState
name|getBaseState
parameter_list|()
function_decl|;
name|boolean
name|isNew
parameter_list|()
function_decl|;
name|boolean
name|isRemoved
parameter_list|()
function_decl|;
name|boolean
name|isModified
parameter_list|()
function_decl|;
comment|/**      * Replaces the base state of this builder and throws away all changes.      * The effect of this method is equivalent to replacing this builder      * (and the connected subtree) with a new builder returned by      * {@code state.builder()}.      *<p>      * This method only works on builders acquired directly from a call      * to {@link NodeState#builder()}. Calling it on a builder returned      * by the {@link #child(String)} method will throw an      * {@link IllegalStateException}.      *      * @param state new base state      * @throws IllegalStateException if this is not a root builder      */
name|void
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
throws|throws
name|IllegalStateException
function_decl|;
comment|/**      * Returns the current number of child nodes.      *      * @return number of child nodes      */
name|long
name|getChildNodeCount
parameter_list|()
function_decl|;
comment|/**      * Checks whether the named child node currently exists.      *      * @param name child node name      * @return {@code true} if the named child node exists,      *         {@code false} otherwise      */
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the names of current child nodes.      *      * @return child node names      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
function_decl|;
comment|/**      * Adds or replaces a subtree.      *      * @param name name of the child node containing the new subtree      * @param nodeState subtree      * @return this builder      */
annotation|@
name|Nonnull
name|NodeBuilder
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**      * Remove a child node. This method has no effect if a      * name of the given {@code name} does not exist.      *      * @param name  name of the child node      * @return this builder      */
annotation|@
name|Nonnull
name|NodeBuilder
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the current number of properties.      *      * @return number of properties      */
name|long
name|getPropertyCount
parameter_list|()
function_decl|;
comment|/**      * Returns the current properties.      *      * @return current properties      */
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Returns the current state of the named property, or {@code null}      * if the property is not set.      *      * @param name property name      * @return property state      */
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param property  The property state to set      * @return this builder      */
name|NodeBuilder
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param name  The name of this property      * @param value  The value of this property      * @param<T>  The type of this property. Must be one of {@code String, Blob, byte[], Long, Integer, Double, Boolean, BigDecimal}      * @throws IllegalArgumentException if {@code T} is not one of the above types.      *      * @param name  name of the property      * @return this builder      */
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param name  The name of this property      * @param value  The value of this property      * @param<T>  The type of this property.      * @return this builder      */
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**     * Remove the named property. This method has no effect if a     * property of the given {@code name} does not exist.     * @param name  name of the property     */
annotation|@
name|Nonnull
name|NodeBuilder
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns a builder for constructing changes to the named child node.      * If the named child node does not already exist, a new empty child      * node is automatically created as the base state of the returned      * child builder. Otherwise the existing child node state is used      * as the base state of the returned builder.      *<p>      * All updates to the returned child builder will implicitly affect      * also this builder, as if a      * {@code setNode(name, childBuilder.getNodeState())} method call      * had been made after each update. Repeated calls to this method with      * the same name will return the same child builder instance until an      * explicit {@link #setNode(String, NodeState)} or      * {@link #removeNode(String)} call is made, at which point the link      * between this builder and a previously returned child builder for      * that child node name will get broken.      *      * @since Oak 0.6      * @param name name of the child node      * @return child builder      */
annotation|@
name|Nonnull
name|NodeBuilder
name|child
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

