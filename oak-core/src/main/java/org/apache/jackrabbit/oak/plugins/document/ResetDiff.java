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
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|commons
operator|.
name|PathUtils
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|NodeStateDiff
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
import|;
end_import

begin_comment
comment|/**  * Implementation of a node state diff, which translates a diff into reset  * operations on a branch.  */
end_comment

begin_class
class|class
name|ResetDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|Revision
name|revision
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|operations
decl_stmt|;
specifier|private
name|UpdateOp
name|update
decl_stmt|;
name|ResetDiff
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|operations
parameter_list|)
block|{
name|this
argument_list|(
name|revision
argument_list|,
literal|"/"
argument_list|,
name|operations
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ResetDiff
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|operations
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|operations
operator|=
name|checkNotNull
argument_list|(
name|operations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|getUpdateOp
argument_list|()
operator|.
name|removeMapEntry
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|getUpdateOp
argument_list|()
operator|.
name|removeMapEntry
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|getUpdateOp
argument_list|()
operator|.
name|removeMapEntry
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|NodeDocument
operator|.
name|removeCommitRoot
argument_list|(
name|getUpdateOp
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|ResetDiff
name|diff
init|=
operator|new
name|ResetDiff
argument_list|(
name|revision
argument_list|,
name|p
argument_list|,
name|operations
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
name|diff
operator|.
name|getUpdateOp
argument_list|()
decl_stmt|;
name|NodeDocument
operator|.
name|removeDeleted
argument_list|(
name|op
argument_list|,
name|revision
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
name|diff
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
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
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|ResetDiff
argument_list|(
name|revision
argument_list|,
name|p
argument_list|,
name|operations
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|ResetDiff
name|diff
init|=
operator|new
name|ResetDiff
argument_list|(
name|revision
argument_list|,
name|p
argument_list|,
name|operations
argument_list|)
decl_stmt|;
name|NodeDocument
operator|.
name|removeDeleted
argument_list|(
name|diff
operator|.
name|getUpdateOp
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
return|return
name|MISSING_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|getOperations
parameter_list|()
block|{
return|return
name|operations
return|;
block|}
specifier|private
name|UpdateOp
name|getUpdateOp
parameter_list|()
block|{
if|if
condition|(
name|update
operator|==
literal|null
condition|)
block|{
name|update
operator|=
name|operations
operator|.
name|get
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|update
operator|==
literal|null
condition|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|update
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operations
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
name|NodeDocument
operator|.
name|removeRevision
argument_list|(
name|update
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|removeCommitRoot
argument_list|(
name|update
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
return|return
name|update
return|;
block|}
block|}
end_class

end_unit

