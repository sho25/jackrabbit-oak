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
name|CoreValue
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Builder interface for constructing new {@link NodeState node states}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStateBuilder
block|{
comment|/**      * Returns an immutable node state that matches the current state of      * the builder.      *      * @return immutable node state      */
annotation|@
name|Nonnull
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
comment|/**      * Add a sub-tree      *      * @param name  name child node containing the sub-tree      * @param nodeState  sub-tree      */
name|void
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**      * Remove a child node      * @param name  name of the child node      */
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a property.      *      * @param name property name      * @param value      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
function_decl|;
comment|/**      * Set a property.      *      * @param name property name      * @param values      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Remove the named property      * @param name  name of the property      */
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

