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
name|api
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

begin_comment
comment|/**  * A tree instance represents a snapshot of the {@code ContentRepository}  * tree at the time the instance was acquired. Tree instances may  * become invalid over time due to garbage collection of old content, at  * which point an outdated snapshot will start throwing  * {@code IllegalStateException}s to indicate that the snapshot is no  * longer available.  *<p>  * A tree instance belongs to the client and its state is only modified  * in response to method calls made by the client. The various accessors  * on this interface mirror these of the underlying {@code NodeState}  * interface. However, since instances of this class are mutable return  * values may change between invocations.  *<p>  * Tree instances are not thread-safe for write access, so writing clients  * need to ensure that they are not accessed concurrently from multiple  * threads. Instances are however thread-safe for read access, so  * implementations need to ensure that all reading clients see a  * coherent state.  *<p>  * The data returned by this class and intermediary objects such as  * {@link PropertyState} is filtered for the access rights that are set in the  * {@link ContentSession} that created the {@link Root} of this object.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Tree
block|{
comment|/**      * Status of an item in a {@code Tree}      */
enum|enum
name|Status
block|{
comment|/**          * Item is persisted          */
name|EXISTING
block|,
comment|/**          * Item is new          */
name|NEW
block|,
comment|/**          * Item is modified: has added or removed children or added, removed or modified          * properties.          */
name|MODIFIED
block|,
comment|/**          * Item is removed          */
name|REMOVED
block|}
comment|/**      * @return  the name of this {@code Tree} instance.      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return {@code true} iff this is the root      */
name|boolean
name|isRoot
parameter_list|()
function_decl|;
comment|/**      * @return the absolute path of this {@code Tree} instance from its {@link Root}.      */
annotation|@
name|Nonnull
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Get the {@code Status} of this tree instance.      *      * @return The status of this tree instance.      */
annotation|@
name|Nonnull
name|Status
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * @return the current location      */
annotation|@
name|Nonnull
name|TreeLocation
name|getLocation
parameter_list|()
function_decl|;
comment|/**      * @return the parent of this {@code Tree} instance. This method returns      * {@code null} if the parent is not accessible or if no parent exists (root      * node).      */
annotation|@
name|CheckForNull
name|Tree
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Get a property state      *      * @param name The name of the property state.      * @return the property state with the given {@code name} or {@code null}      * if no such property state exists or the property is not accessible.      */
annotation|@
name|CheckForNull
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Get the {@code Status} of a property state or {@code null}.      *      * @param name The name of the property state.      * @return The status of the property state with the given {@code name}      * or {@code null} in no such property state exists or if the name refers      * to a property that is not accessible.      */
annotation|@
name|CheckForNull
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine if a property state exists and is accessible.      *      * @param name The name of the property state      * @return {@code true} if and only if a property with the given {@code name}      *          exists and is accessible.      */
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine the number of properties accessible to the current content session.      *      * @return The number of accessible properties.      */
name|long
name|getPropertyCount
parameter_list|()
function_decl|;
comment|/**      * All accessible property states. The returned {@code Iterable} has snapshot      * semantics. That is, it reflect the state of this {@code Tree} instance at      * the time of the call. Later changes to this instance are no visible to      * iterators obtained from the returned iterable.      *      * @return An {@code Iterable} for all accessible property states.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Get a child of this {@code Tree} instance.      *      * @param name The name of the child to retrieve.      * @return The child with the given {@code name} or {@code null} if no such      * child exists or the child is not accessible.      */
annotation|@
name|CheckForNull
name|Tree
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine if a child of this {@code Tree} instance exists. If no child      * exists or an existing child isn't accessible this method returns {@code false}.      *      * @param name The name of the child      * @return {@code true} if and only if a child with the given {@code name}      * exists and is accessible for the current content session.      */
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine the number of children of this {@code Tree} instance taking      * access restrictions into account.      *      * @return The number of accessible children.      */
name|long
name|getChildrenCount
parameter_list|()
function_decl|;
comment|/**      * All accessible children of this {@code Tree} instance. The returned      * {@code Iterable} has snapshot semantics. That is, it reflect the state of      * this {@code Tree} instance at the time of the call. Later changes to this      * instance are not visible to iterators obtained from the returned iterable.      *      * @return An {@code Iterable} for all accessible children      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
function_decl|;
comment|/**      * Remove this tree instance. This operation never succeeds for the root tree.      *      * @return {@code true} if the node was removed; {@code false} otherwise.      */
name|boolean
name|remove
parameter_list|()
function_decl|;
comment|/**      * Add a child with the given {@code name}. Does nothing if such a child      * already exists.      *      * @param name name of the child      * @return the {@code Tree} instance of the child with the given {@code name}.      */
annotation|@
name|Nonnull
name|Tree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a multivalued valued property state      *      * @param name The name of this property      * @param values The value of this property      * @return the affected property state      */
annotation|@
name|Nonnull
annotation|@
name|Deprecated
name|PropertyState
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param property  The property state to set      */
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param name  The name of this property      * @param value  The value of this property      * @param<T>  The type of this property. Must be one of {@code String, Blob, byte[], Long, Integer, Double, Boolean, BigDecimal}      * @throws IllegalArgumentException if {@code T} is not one of the above types.      */
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
function_decl|;
comment|/**      * Set a property state      * @param name  The name of this property      * @param value  The value of this property      * @param<T>  The type of this property.      */
parameter_list|<
name|T
parameter_list|>
name|void
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
comment|/**      * Remove the property with the given name. This method has no effect if a      * property of the given {@code name} does not exist.      *      * @param name The name of the property      */
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

