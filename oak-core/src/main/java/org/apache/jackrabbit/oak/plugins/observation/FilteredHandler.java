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
name|plugins
operator|.
name|observation
operator|.
name|filter
operator|.
name|EventFilter
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Filtered event handler. This decorator class applies an {@link EventFilter}  * on all detected changes, and forwards the filtered change events to a given  * delegate handler.  */
end_comment

begin_class
specifier|public
class|class
name|FilteredHandler
extends|extends
name|DefaultEventHandler
block|{
specifier|private
specifier|final
name|EventFilter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|EventHandler
name|handler
decl_stmt|;
specifier|public
name|FilteredHandler
parameter_list|(
name|EventFilter
name|filter
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|handler
operator|.
name|enter
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|handler
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
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
block|{
name|EventFilter
name|f
init|=
name|filter
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|EventHandler
name|h
init|=
name|handler
operator|.
name|getChildHandler
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|h
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|FilteredHandler
argument_list|(
name|f
argument_list|,
name|h
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeAdd
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|handler
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeChange
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|handler
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeDelete
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|handler
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeAdd
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|handler
operator|.
name|nodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeDelete
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
condition|)
block|{
name|handler
operator|.
name|nodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
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
block|{
if|if
condition|(
name|filter
operator|.
name|includeMove
argument_list|(
name|sourcePath
argument_list|,
name|name
argument_list|,
name|moved
argument_list|)
condition|)
block|{
name|handler
operator|.
name|nodeMoved
argument_list|(
name|sourcePath
argument_list|,
name|name
argument_list|,
name|moved
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
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
block|{
if|if
condition|(
name|filter
operator|.
name|includeReorder
argument_list|(
name|destName
argument_list|,
name|name
argument_list|,
name|reordered
argument_list|)
condition|)
block|{
name|handler
operator|.
name|nodeReordered
argument_list|(
name|destName
argument_list|,
name|name
argument_list|,
name|reordered
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

