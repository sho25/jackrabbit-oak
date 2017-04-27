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
name|stats
operator|.
name|Clock
import|;
end_import

begin_comment
comment|/**  * Wraps an existing revision context and exposes a custom {@code clusterId}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RevisionContextWrapper
implements|implements
name|RevisionContext
block|{
specifier|private
specifier|final
name|RevisionContext
name|context
decl_stmt|;
specifier|private
specifier|final
name|int
name|clusterId
decl_stmt|;
specifier|public
name|RevisionContextWrapper
parameter_list|(
name|RevisionContext
name|context
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|UnmergedBranches
name|getBranches
parameter_list|()
block|{
return|return
name|context
operator|.
name|getBranches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|UnsavedModifications
name|getPendingModifications
parameter_list|()
block|{
return|return
name|context
operator|.
name|getPendingModifications
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getHeadRevision
parameter_list|()
block|{
return|return
name|context
operator|.
name|getHeadRevision
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Revision
name|newRevision
parameter_list|()
block|{
name|Revision
name|r
init|=
name|context
operator|.
name|newRevision
argument_list|()
decl_stmt|;
return|return
operator|new
name|Revision
argument_list|(
name|r
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|r
operator|.
name|getCounter
argument_list|()
argument_list|,
name|clusterId
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
name|context
operator|.
name|getClock
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCommitValue
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|NodeDocument
name|nodeDocument
parameter_list|)
block|{
return|return
name|context
operator|.
name|getCommitValue
argument_list|(
name|revision
argument_list|,
name|nodeDocument
argument_list|)
return|;
block|}
block|}
end_class

end_unit

