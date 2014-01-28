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
name|plugins
operator|.
name|observation
operator|.
name|filter
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * Filter for determining what changes to report the the event listener.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EventFilter
block|{
comment|/**      * Include an added property      * @param after  added property      * @return  {@code true} if the property should be included      */
name|boolean
name|includeAdd
parameter_list|(
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Include a changed property      * @param before  property before the change      * @param after  property after the change      * @return  {@code true} if the property should be included      */
name|boolean
name|includeChange
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Include a deleted property      * @param before  deleted property      * @return  {@code true} if the property should be included      */
name|boolean
name|includeDelete
parameter_list|(
name|PropertyState
name|before
parameter_list|)
function_decl|;
comment|/**      * Include an added node      * @param name name of the node      * @param after  added node      * @return  {@code true} if the node should be included      */
name|boolean
name|includeAdd
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
function_decl|;
comment|/**      * Include a deleted node      * @param name name of the node      * @param before deleted node      * @return  {@code true} if the node should be included      */
name|boolean
name|includeDelete
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
function_decl|;
comment|/**      * Include a moved node      * @param sourcePath  source path of the move operation      * @param name        name of the moved node      * @param moved       the moved node      * @return  {@code true} if the node should be included      */
name|boolean
name|includeMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|moved
parameter_list|)
function_decl|;
comment|/**      * Include a reordered node      * @param destName    name of the {@code orderBefore()} destination node      * @param name        name of the reordered node      * @param reordered   the reordered node      * @return  {@code true} if the node should be included      */
name|boolean
name|includeReorder
parameter_list|(
name|String
name|destName
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|reordered
parameter_list|)
function_decl|;
comment|/**      * Factory for creating a filter instance for the given child node      * @param name  name of the child node      * @param before  before state of the child node      * @param after  after state of the child node      * @return  filter instance for filtering the child node or {@code null} to      *          exclude the sub tree rooted at this child node.      */
annotation|@
name|CheckForNull
name|EventFilter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

