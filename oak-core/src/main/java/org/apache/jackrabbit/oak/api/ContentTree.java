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

begin_comment
comment|/**  * A content tree represents a snapshot of the content in a  * {@code ContentRepository} at the time the instance was acquired.  * {@code ContentTree} instances may become invalid over time due to  * garbage collection of old content, at which point an outdated  * snapshot will start throwing {@code IllegalStateException}s to  * indicate that the snapshot is no longer available.  *<p>  * {@code ContentTree} instance belongs to the client and its state  * is only modified in response to method calls made by the client.  * The various accessors on this class mirror these of {@code NodeState}.  * However, since instances of this class are mutable return values may  * change between invocations.  *<p>  * {@code ContentTree} instances are not thread-safe for write access, so  * writing clients need to ensure that they are not accessed concurrently  * from multiple threads. {@code ContentTree} instances are however  * thread-safe for read access, so implementations need to ensure that all  * reading clients see a coherent state.  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentTree
block|{
enum|enum
name|Status
block|{
name|EXISTING
block|,
name|NEW
block|,
name|MODIFIED
block|,
name|REMOVED
block|}
comment|/**      * @return  the name of this {@code ContentTree} instance.      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return  path of this {@code ContentTree} instance.      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * @return  the parent of this {@code ContentTree} instance.      */
name|ContentTree
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Get a property state      * @param name name of the property state      * @return  the property state with the given {@code name} or {@code null}      *          if no such property state exists.      */
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine if a property state exists      * @param name  name of the property state      * @return  {@code true} if and only if a property with the given {@code name}      *          exists.      */
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine the number of properties.      * @return  number of properties      */
name|long
name|getPropertyCount
parameter_list|()
function_decl|;
comment|/**      * All property states. The returned {@code Iterable} has snapshot semantics. That      * is, it reflect the state of this {@code ContentTree} instance at the time of the      * call. Later changes to this instance are no visible to iterators obtained from      * the returned iterable.      * @return  An {@code Iterable} for all property states      */
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Get a child of this {@code ContentTree} instance      * @param name  name of the child      * @return  the child with the given {@code name} or {@code null} if no such child      * exists.      */
name|ContentTree
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Status
name|getChildStatus
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine if a child of this {@code ContentTree} instance exists.      * @param name  name of the child      * @return  {@code true} if and only if a child with the given {@code name}      *          exists.      */
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine the number of children of this {@code ContentTree} instance.      * @return  number of children      */
name|long
name|getChildrenCount
parameter_list|()
function_decl|;
comment|/**      * All children of this {@code ContentTree} instance. The returned {@code Iterable}      * has snapshot semantics. That is, it reflect the state of this {@code ContentTree}      * instance. instance at the time of the call. Later changes to this instance are no      * visible to iterators obtained from the returned iterable.      * @return  An {@code Iterable} for all children      */
name|Iterable
argument_list|<
name|ContentTree
argument_list|>
name|getChildren
parameter_list|()
function_decl|;
comment|/**      * Add a child with the given {@code name}. Does nothing if such a child      * already exists.      *      * @param name name of the child      * @return the {@code ContentTree} instance of the child with the given {@code name}.      */
name|ContentTree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Remove a child with the given {@code name}. Does nothing if no such child exists.      * @param name  name of the child to remove      * @return  {@code false} iff no such child exists.      */
name|boolean
name|removeChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a single valued property state      *      * @param name The name of this property      * @param value The value of this property      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Scalar
name|value
parameter_list|)
function_decl|;
comment|/**      * Set a multivalued valued property state      *      * @param name The name of this property      * @param values The value of this property      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Scalar
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Remove a property      * @param name name of the property      */
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

