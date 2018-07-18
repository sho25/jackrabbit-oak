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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
comment|/**  * A revision context that represents a cluster node with an expired lease for  * which recovery is performed.  */
end_comment

begin_class
specifier|final
class|class
name|RecoveryContext
implements|implements
name|RevisionContext
block|{
specifier|private
specifier|final
name|NodeDocument
name|root
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|int
name|clusterId
decl_stmt|;
specifier|private
specifier|final
name|CommitValueResolver
name|resolver
decl_stmt|;
comment|/**      * A new recovery context.      *      * @param root the current root document.      * @param clock the clock.      * @param clusterId the clusterId for which to run recovery.      * @param resolver a commit resolver.      */
name|RecoveryContext
parameter_list|(
name|NodeDocument
name|root
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|int
name|clusterId
parameter_list|,
name|CommitValueResolver
name|resolver
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|UnmergedBranches
name|getBranches
parameter_list|()
block|{
comment|// an expired cluster node does not have active unmerged branches
return|return
operator|new
name|UnmergedBranches
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
comment|// an expired cluster node does not have
comment|// pending in-memory _lastRev updates
return|return
operator|new
name|UnsavedModifications
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
name|NotNull
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getHeadRevision
parameter_list|()
block|{
return|return
operator|new
name|RevisionVector
argument_list|(
name|root
operator|.
name|getLastRev
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Revision
name|newRevision
parameter_list|()
block|{
return|return
name|Revision
operator|.
name|newRevision
argument_list|(
name|clusterId
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
name|clock
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|getCommitValue
parameter_list|(
annotation|@
name|NotNull
name|Revision
name|changeRevision
parameter_list|,
annotation|@
name|NotNull
name|NodeDocument
name|doc
parameter_list|)
block|{
return|return
name|resolver
operator|.
name|resolve
argument_list|(
name|changeRevision
argument_list|,
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

