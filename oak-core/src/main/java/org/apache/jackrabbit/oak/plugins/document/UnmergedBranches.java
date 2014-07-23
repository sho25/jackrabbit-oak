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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<code>UnmergedBranches</code> contains all un-merged branches of a DocumentMK  * instance.  */
end_comment

begin_class
class|class
name|UnmergedBranches
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Map of branches with the head of the branch as key.      */
specifier|private
specifier|final
name|List
argument_list|<
name|Branch
argument_list|>
name|branches
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Branch
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Set to<code>true</code> once initialized.      */
specifier|private
specifier|final
name|AtomicBoolean
name|initialized
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**      * The revision comparator.      */
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comparator
decl_stmt|;
name|UnmergedBranches
parameter_list|(
annotation|@
name|Nonnull
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|comparator
operator|=
name|checkNotNull
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initialize with un-merged branches from<code>store</code> for this      *<code>clusterId</code>.      *      * @param store the document store.      * @param context the revision context.      */
name|void
name|init
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|RevisionContext
name|context
parameter_list|)
block|{
if|if
condition|(
operator|!
name|initialized
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already initialized"
argument_list|)
throw|;
block|}
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|purgeCount
init|=
name|doc
operator|.
name|purgeUncommittedRevisions
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|purgeCount
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Purged [{}] uncommitted branch revision entries"
argument_list|,
name|purgeCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Create a branch with an initial commit revision.      *      * @param base the base revision of the branch.      * @param initial the initial commit to the branch.      * @return the branch.      * @throws IllegalArgumentException if      */
annotation|@
name|Nonnull
name|Branch
name|create
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|base
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|initial
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|checkNotNull
argument_list|(
name|base
argument_list|)
operator|.
name|isBranch
argument_list|()
argument_list|,
literal|"base is not a trunk revision: %s"
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|checkNotNull
argument_list|(
name|initial
argument_list|)
operator|.
name|isBranch
argument_list|()
argument_list|,
literal|"initial is not a branch revision: %s"
argument_list|,
name|initial
argument_list|)
expr_stmt|;
name|SortedSet
argument_list|<
name|Revision
argument_list|>
name|commits
init|=
operator|new
name|TreeSet
argument_list|<
name|Revision
argument_list|>
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
name|initial
argument_list|)
expr_stmt|;
name|Branch
name|b
init|=
operator|new
name|Branch
argument_list|(
name|commits
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|branches
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/**      * Returns the branch, which contains the given revision or<code>null</code>      * if there is no such branch.      *      * @param r a revision.      * @return the branch containing the given revision or<code>null</code>.      */
annotation|@
name|CheckForNull
name|Branch
name|getBranch
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|r
parameter_list|)
block|{
for|for
control|(
name|Branch
name|b
range|:
name|branches
control|)
block|{
if|if
condition|(
name|b
operator|.
name|containsCommit
argument_list|(
name|r
argument_list|)
condition|)
block|{
return|return
name|b
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Removes the given branch.      * @param b the branch to remove.      */
name|void
name|remove
parameter_list|(
name|Branch
name|b
parameter_list|)
block|{
name|branches
operator|.
name|remove
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

