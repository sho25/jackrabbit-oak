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
name|spi
operator|.
name|commit
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
name|spi
operator|.
name|commit
operator|.
name|ThreeWayConflictHandler
operator|.
name|Resolution
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

begin_class
specifier|public
class|class
name|ConflictHandlers
block|{
specifier|private
name|ConflictHandlers
parameter_list|()
block|{     }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|static
name|ThreeWayConflictHandler
name|wrap
parameter_list|(
name|PartialConflictHandler
name|handler
parameter_list|)
block|{
return|return
operator|new
name|ThreeWayConflictHandlerWrapper
argument_list|(
name|handler
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
specifier|static
name|Resolution
name|wrap
parameter_list|(
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
name|PartialConflictHandler
operator|.
name|Resolution
name|r
parameter_list|)
block|{
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
return|return
name|Resolution
operator|.
name|IGNORED
return|;
block|}
switch|switch
condition|(
name|r
condition|)
block|{
case|case
name|OURS
case|:
return|return
name|Resolution
operator|.
name|OURS
return|;
case|case
name|THEIRS
case|:
return|return
name|Resolution
operator|.
name|THEIRS
return|;
case|case
name|MERGED
case|:
return|return
name|Resolution
operator|.
name|MERGED
return|;
block|}
return|return
name|Resolution
operator|.
name|IGNORED
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
specifier|static
class|class
name|ThreeWayConflictHandlerWrapper
implements|implements
name|ThreeWayConflictHandler
block|{
specifier|private
specifier|final
name|PartialConflictHandler
name|handler
decl_stmt|;
specifier|public
name|ThreeWayConflictHandlerWrapper
parameter_list|(
name|PartialConflictHandler
name|handler
parameter_list|)
block|{
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
name|Resolution
name|addExistingProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|addExistingProperty
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|changeDeletedProperty
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeChangedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|changeChangedProperty
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|deleteDeletedProperty
argument_list|(
name|parent
argument_list|,
name|base
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|theirs
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|deleteChangedProperty
argument_list|(
name|parent
argument_list|,
name|theirs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|addExistingNode
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|changeDeletedNode
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|ours
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|theirs
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|deleteChangedNode
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|theirs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|handler
operator|.
name|deleteDeletedNode
argument_list|(
name|parent
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

