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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|CheckForNull
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_comment
comment|/**  * Helper class to track when a node was last modified.  */
end_comment

begin_class
specifier|final
class|class
name|LastRevs
implements|implements
name|Iterable
argument_list|<
name|Revision
argument_list|>
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Revision
argument_list|>
name|revs
decl_stmt|;
specifier|private
specifier|final
name|RevisionVector
name|readRevision
decl_stmt|;
specifier|private
specifier|final
name|Branch
name|branch
decl_stmt|;
specifier|private
name|Revision
name|branchRev
decl_stmt|;
name|LastRevs
parameter_list|(
name|RevisionVector
name|readRevision
parameter_list|)
block|{
name|this
argument_list|(
name|Collections
operator|.
expr|<
name|Integer
argument_list|,
name|Revision
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|readRevision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|LastRevs
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Revision
argument_list|>
name|revs
parameter_list|,
name|RevisionVector
name|readRevision
parameter_list|,
name|Branch
name|branch
parameter_list|)
block|{
name|this
operator|.
name|revs
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Revision
argument_list|>
argument_list|(
name|revs
argument_list|)
expr_stmt|;
name|this
operator|.
name|readRevision
operator|=
name|readRevision
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|branch
expr_stmt|;
block|}
name|void
name|update
parameter_list|(
annotation|@
name|Nullable
name|Revision
name|rev
parameter_list|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Revision
name|r
init|=
name|revs
operator|.
name|get
argument_list|(
name|rev
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
name|rev
operator|.
name|compareRevisionTime
argument_list|(
name|r
argument_list|)
operator|>
literal|0
condition|)
block|{
name|revs
operator|.
name|put
argument_list|(
name|rev
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|rev
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|updateBranch
parameter_list|(
annotation|@
name|Nullable
name|Revision
name|rev
parameter_list|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|rev
operator|=
name|rev
operator|.
name|asBranchRevision
argument_list|()
expr_stmt|;
if|if
condition|(
name|branch
operator|!=
literal|null
operator|&&
name|branch
operator|.
name|containsCommit
argument_list|(
name|rev
argument_list|)
operator|&&
name|readRevision
operator|.
name|getBranchRevision
argument_list|()
operator|.
name|compareRevisionTime
argument_list|(
name|rev
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|branchRev
operator|=
name|Utils
operator|.
name|max
argument_list|(
name|branchRev
argument_list|,
name|rev
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
name|Revision
name|getBranchRevision
parameter_list|()
block|{
return|return
name|branchRev
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Revision
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|revs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit
