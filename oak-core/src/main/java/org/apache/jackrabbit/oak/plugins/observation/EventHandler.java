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
comment|/**  * Handler of content change events. Used to decouple processing of changes  * from the content diff logic that detects them.  *<p>  * As the content diff recurses down the content tree, it will call the  * {@link #getChildHandler(String, NodeState, NodeState)} method to  * specialize the handler instance for each node under which changes are  * detected. The other handler methods always apply to the properties  * and direct children of the node for which that handler instance is  * specialized. The handler is expected to keep track of contextual  * information like the path or identifier of the current node based on  * the sequence of those specialization calls.  *<p>  * All names and paths passed to handler methods use unmapped Oak names.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EventHandler
block|{
comment|/**      * Returns a handler of events within the given child node, or      * {@code null} if changes within that child are not to be processed.      *      * @param name  name of the child node      * @param before before state of the child node, possibly non-existent      * @param after  after state of the child node, possibly non-existent      * @return handler of events within the child node, or {@code null}      */
annotation|@
name|CheckForNull
name|EventHandler
name|getChildHandler
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
comment|/**      * Notification for an added property      * @param after  added property      */
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Notification for a changed property      * @param before  property before the change      * @param after  property after the change      */
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Notification for a deleted property      * @param before  deleted property      */
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
function_decl|;
comment|/**      * Notification for an added node      * @param name  name of the node      * @param after  added node      */
name|void
name|nodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
function_decl|;
comment|/**      * Notification for a deleted node      * @param name  name of the deleted node      * @param before  deleted node      */
name|void
name|nodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
function_decl|;
comment|/**      * Notification for a moved node      * @param sourcePath  source of the moved node      * @param name        name of the moved node      * @param moved       moved node      */
name|void
name|nodeMoved
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
comment|/**      * Notification for a reordered node      * @param destName    name of the {@code orderBefore()} destination node      * @param name        name of the moved node      * @param reordered       moved node      */
name|void
name|nodeReordered
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
block|}
end_interface

end_unit

