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
name|core
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
name|ConflictHandler
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
name|Tree
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
comment|/**  * This implementation of a {@link ConflictHandler} always returns the same resolution.  * It can be used to implement default behaviour or as a base class for more specialised  * implementations.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultConflictHandler
implements|implements
name|ConflictHandler
block|{
comment|/**      * A {@code ConflictHandler} which always return {@link Resolution#OURS}.      */
specifier|public
specifier|static
specifier|final
name|ConflictHandler
name|OURS
init|=
operator|new
name|DefaultConflictHandler
argument_list|(
name|Resolution
operator|.
name|OURS
argument_list|)
decl_stmt|;
comment|/**      * A {@code ConflictHandler} which always return {@link Resolution#THEIRS}.      */
specifier|public
specifier|static
specifier|final
name|ConflictHandler
name|THEIRS
init|=
operator|new
name|DefaultConflictHandler
argument_list|(
name|Resolution
operator|.
name|THEIRS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Resolution
name|resolution
decl_stmt|;
comment|/**      * Create a new {@code ConflictHandler} which always returns {@code resolution}.      *      * @param resolution  the resolution to return from all methods of this      * {@code ConflictHandler} instance.      */
specifier|public
name|DefaultConflictHandler
parameter_list|(
name|Resolution
name|resolution
parameter_list|)
block|{
name|this
operator|.
name|resolution
operator|=
name|resolution
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingProperty
parameter_list|(
name|Tree
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
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeChangedProperty
parameter_list|(
name|Tree
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
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingNode
parameter_list|(
name|Tree
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
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|resolution
return|;
block|}
block|}
end_class

end_unit

