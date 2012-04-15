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
comment|/**  * A transient node state represents a mutable node.  *<p>  * A transient node state contains the current state of a node and is  * in contrast to {@link org.apache.jackrabbit.mk.model.NodeState} instances  * mutable and not thread safe.  *<p>  * The various accessors on this class mirror these of {@code NodeState}. However,  * since instances of this class are mutable return values may change between  * invocations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransientNodeState
block|{
comment|/**      * @return  the name of this transient node state      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return  relative path of this transient node state      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * @return  the parent of this transient node state      */
name|TransientNodeState
name|getParent
parameter_list|()
function_decl|;
comment|/**      * @return  the branch this state belongs to      */
name|Branch
name|getBranch
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
comment|/**      * All property states. The returned {@code Iterable} has snapshot semantics. That      * is, it reflect the state of this transient node state instance at the time of the      * call. Later changes to this instance are no visible to iterators obtained from      * the returned iterable.      * @return  An {@code Iterable} for all property states      */
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Get a child node state      * @param name  name of the child node state      * @return  the child node state with the given {@code name} or {@code null}      *          if no such child node state exists.      */
name|TransientNodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine if a child node state exists      * @param name  name of the child node state      * @return  {@code true} if and only if a child node with the given {@code name}      *          exists.      */
name|boolean
name|hasNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Determine the number of child nodes.      * @return  number of child nodes.      */
name|long
name|getChildNodeCount
parameter_list|()
function_decl|;
comment|/**      * All child node states. The returned {@code Iterable} has snapshot semantics. That      * is, it reflect the state of this transient node state instance at the time of the      * call. Later changes to this instance are no visible to iterators obtained from      * the returned iterable.      * @return  An {@code Iterable} for all child node states      */
name|Iterable
argument_list|<
name|TransientNodeState
argument_list|>
name|getChildNodes
parameter_list|()
function_decl|;
comment|/**      * Add the child node state with the given {@code name}. Does nothing      * if such a child node already exists.      *      * @param name name of the new node state      * @return the transient state of the child node with that name or {@code null}      * if no such node exists.      */
name|TransientNodeState
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Remove the child node state with the given {@code name}. Does nothing      * if no such child node exists.      * @param name  name of the node state to remove      */
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a single valued property state on this node state.      *      * @param name The name of this property      * @param value The value of this property      */
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
comment|/**      * Set a multivalued valued property state on this node state.      *      * @param name The name of this property      * @param values The value of this property      */
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
comment|/**      * Remove a property from this node state      * @param name name of the property      */
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

