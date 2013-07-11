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
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|checkArgument
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

begin_comment
comment|/**  * Contains commit information about a branch and its base revision.  * TODO document  */
end_comment

begin_class
class|class
name|Branch
block|{
comment|/**      * The commits to the branch      */
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|Commit
argument_list|>
name|commits
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|base
decl_stmt|;
name|Branch
parameter_list|(
annotation|@
name|Nonnull
name|SortedSet
argument_list|<
name|Revision
argument_list|>
name|commits
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|base
parameter_list|,
annotation|@
name|Nonnull
name|Revision
operator|.
name|RevisionComparator
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|commits
operator|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|Commit
argument_list|>
argument_list|(
name|checkNotNull
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|commits
control|)
block|{
name|this
operator|.
name|commits
operator|.
name|put
argument_list|(
name|r
argument_list|,
operator|new
name|Commit
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the initial base of this branch.      */
name|Revision
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
comment|/**      * Returns the base revision for the given branch revision<code>r</code>.      *      * @param r revision of a commit in this branch.      * @return the base revision for<code>r</code>.      * @throws IllegalArgumentException if<code>r</code> is not a commit of      *                                  this branch.      */
specifier|synchronized
name|Revision
name|getBase
parameter_list|(
name|Revision
name|r
parameter_list|)
block|{
name|Commit
name|c
init|=
name|commits
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Revision "
operator|+
name|r
operator|+
literal|" is not a commit in this branch"
argument_list|)
throw|;
block|}
return|return
name|c
operator|.
name|getBase
argument_list|()
return|;
block|}
comment|/**      * Rebases the last commit of this branch to the given revision.      *      * @param head the new head of the branch.      * @param base rebase to this revision.      */
specifier|synchronized
name|void
name|rebase
parameter_list|(
name|Revision
name|head
parameter_list|,
name|Revision
name|base
parameter_list|)
block|{
name|Revision
name|last
init|=
name|commits
operator|.
name|lastKey
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|commits
operator|.
name|comparator
argument_list|()
operator|.
name|compare
argument_list|(
name|head
argument_list|,
name|last
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|commits
operator|.
name|put
argument_list|(
name|head
argument_list|,
operator|new
name|Commit
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
name|void
name|addCommit
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|r
parameter_list|)
block|{
name|Revision
name|last
init|=
name|commits
operator|.
name|lastKey
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|commits
operator|.
name|comparator
argument_list|()
operator|.
name|compare
argument_list|(
name|r
argument_list|,
name|last
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|commits
operator|.
name|put
argument_list|(
name|r
argument_list|,
operator|new
name|Commit
argument_list|(
name|commits
operator|.
name|get
argument_list|(
name|last
argument_list|)
operator|.
name|getBase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
name|SortedSet
argument_list|<
name|Revision
argument_list|>
name|getCommits
parameter_list|()
block|{
name|SortedSet
argument_list|<
name|Revision
argument_list|>
name|revisions
init|=
operator|new
name|TreeSet
argument_list|<
name|Revision
argument_list|>
argument_list|(
name|commits
operator|.
name|comparator
argument_list|()
argument_list|)
decl_stmt|;
name|revisions
operator|.
name|addAll
argument_list|(
name|commits
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|revisions
return|;
block|}
specifier|synchronized
name|boolean
name|hasCommits
parameter_list|()
block|{
return|return
operator|!
name|commits
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|synchronized
name|boolean
name|containsCommit
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|r
parameter_list|)
block|{
return|return
name|commits
operator|.
name|containsKey
argument_list|(
name|r
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|removeCommit
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|rev
parameter_list|)
block|{
name|commits
operator|.
name|remove
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the unsaved modifications for the given branch commit revision.      *      * @param r a branch commit revision.      * @return the unsaved modification for the given branch commit.      * @throws IllegalArgumentException if there is no commit with the given      *                                  revision.      */
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|UnsavedModifications
name|getModifications
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|r
parameter_list|)
block|{
name|Commit
name|c
init|=
name|commits
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Revision "
operator|+
name|r
operator|+
literal|" is not a commit in this branch"
argument_list|)
throw|;
block|}
return|return
name|c
operator|.
name|getModifications
argument_list|()
return|;
block|}
comment|/**      * Applies all unsaved modification of this branch to the given collection      * of unsaved trunk modifications with the given merge commit revision.      *      * @param trunk the unsaved trunk modifications.      * @param mergeCommit the revision of the merge commit.      */
specifier|public
specifier|synchronized
name|void
name|applyTo
parameter_list|(
annotation|@
name|Nonnull
name|UnsavedModifications
name|trunk
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|mergeCommit
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|trunk
argument_list|)
expr_stmt|;
for|for
control|(
name|Commit
name|c
range|:
name|commits
operator|.
name|values
argument_list|()
control|)
block|{
name|c
operator|.
name|getModifications
argument_list|()
operator|.
name|applyTo
argument_list|(
name|trunk
argument_list|,
name|mergeCommit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Gets the most recent unsaved last revision at<code>readRevision</code>      * or earlier in this branch for the given<code>path</code>.      *      * @param path         the path of a node.      * @param readRevision the read revision.      * @return the most recent unsaved last revision or<code>null</code> if      *         there is none in this branch.      */
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|Revision
name|getUnsavedLastRevision
parameter_list|(
name|String
name|path
parameter_list|,
name|Revision
name|readRevision
parameter_list|)
block|{
for|for
control|(
name|Revision
name|r
range|:
name|commits
operator|.
name|descendingKeySet
argument_list|()
control|)
block|{
if|if
condition|(
name|readRevision
operator|.
name|compareRevisionTime
argument_list|(
name|r
argument_list|)
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|Commit
name|c
init|=
name|commits
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Revision
name|modRevision
init|=
name|c
operator|.
name|getModifications
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|modRevision
operator|!=
literal|null
condition|)
block|{
return|return
name|modRevision
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Commit
block|{
specifier|private
specifier|final
name|UnsavedModifications
name|modifications
init|=
operator|new
name|UnsavedModifications
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|base
decl_stmt|;
name|Commit
parameter_list|(
name|Revision
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
name|Revision
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
name|UnsavedModifications
name|getModifications
parameter_list|()
block|{
return|return
name|modifications
return|;
block|}
block|}
block|}
end_class

end_unit

