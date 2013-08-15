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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|mongomk
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
name|commons
operator|.
name|PathUtils
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
comment|/**  * A<code>Collision</code> happens when a commit modifies a node, which was  * also modified in another branch not visible to the current session. This  * includes the following situations:  *<ul>  *<li>Our commit goes to trunk and another session committed to a branch  * not yet merged back.</li>  *<li>Our commit goes to a branch and another session committed to trunk  * or some other branch.</li>  *</ul>  * Other collisions like concurrent commits to trunk are handled earlier and  * do not require collision marking.  * See {@link Commit#createOrUpdateNode(DocumentStore, UpdateOp)}.  */
end_comment

begin_class
class|class
name|Collision
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Collision
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeDocument
name|document
decl_stmt|;
specifier|private
specifier|final
name|String
name|theirRev
decl_stmt|;
specifier|private
specifier|final
name|UpdateOp
name|ourOp
decl_stmt|;
specifier|private
specifier|final
name|String
name|ourRev
decl_stmt|;
name|Collision
parameter_list|(
annotation|@
name|Nonnull
name|NodeDocument
name|document
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|theirRev
parameter_list|,
annotation|@
name|Nonnull
name|UpdateOp
name|ourOp
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|ourRev
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|checkNotNull
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|this
operator|.
name|theirRev
operator|=
name|checkNotNull
argument_list|(
name|theirRev
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|ourOp
operator|=
name|checkNotNull
argument_list|(
name|ourOp
argument_list|)
expr_stmt|;
name|this
operator|.
name|ourRev
operator|=
name|checkNotNull
argument_list|(
name|ourRev
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**      * Marks the collision in the document store. Either our or their      * revision is annotated with a collision marker. Their revision is      * marked if it is not yet committed, otherwise our revision is marked.      *       * @param store the document store.      * @throws MicroKernelException if the mark operation fails.      */
name|void
name|mark
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
throws|throws
name|MicroKernelException
block|{
comment|// first try to mark their revision
if|if
condition|(
name|markCommitRoot
argument_list|(
name|document
argument_list|,
name|theirRev
argument_list|,
name|store
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// their commit wins, we have to mark ourRev
name|NodeDocument
name|newDoc
init|=
name|Collection
operator|.
name|NODES
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Utils
operator|.
name|deepCopyMap
argument_list|(
name|document
argument_list|,
name|newDoc
argument_list|)
expr_stmt|;
name|MemoryDocumentStore
operator|.
name|applyChanges
argument_list|(
name|newDoc
argument_list|,
name|ourOp
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|markCommitRoot
argument_list|(
name|newDoc
argument_list|,
name|ourRev
argument_list|,
name|store
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
literal|"Unable to annotate our revision "
operator|+
literal|"with collision marker. Our revision: "
operator|+
name|ourRev
operator|+
literal|", document:\n"
operator|+
name|Utils
operator|.
name|formatDocument
argument_list|(
name|newDoc
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * Marks the commit root of the change to the given<code>document</code> in      *<code>revision</code>.      *       * @param document the MongoDB document.      * @param revision the revision of the commit to annotated with a collision      *            marker.      * @param store the document store.      * @return<code>true</code> if the commit for the given revision was marked      *         successfully;<code>false</code> otherwise.      */
specifier|private
specifier|static
name|boolean
name|markCommitRoot
parameter_list|(
annotation|@
name|Nonnull
name|NodeDocument
name|document
parameter_list|,
annotation|@
name|Nonnull
name|String
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|DocumentStore
name|store
parameter_list|)
block|{
name|String
name|p
init|=
name|Utils
operator|.
name|getPathFromId
argument_list|(
name|document
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|commitRootPath
init|=
literal|null
decl_stmt|;
comment|// first check if we can mark the commit with the given revision
if|if
condition|(
name|document
operator|.
name|containsRevision
argument_list|(
name|revision
argument_list|)
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|isCommitted
argument_list|(
name|revision
argument_list|)
condition|)
block|{
comment|// already committed
return|return
literal|false
return|;
block|}
comment|// node is also commit root, but not yet committed
comment|// i.e. a branch commit, which is not yet merged
name|commitRootPath
operator|=
name|p
expr_stmt|;
block|}
else|else
block|{
comment|// next look at commit root
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|commitRoots
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
operator|)
name|document
operator|.
name|get
argument_list|(
name|NodeDocument
operator|.
name|COMMIT_ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitRoots
operator|!=
literal|null
condition|)
block|{
name|Integer
name|depth
init|=
name|commitRoots
operator|.
name|get
argument_list|(
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|depth
operator|!=
literal|null
condition|)
block|{
name|commitRootPath
operator|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|p
argument_list|,
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|p
argument_list|)
operator|-
name|depth
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|throwNoCommitRootException
argument_list|(
name|revision
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|throwNoCommitRootException
argument_list|(
name|revision
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
block|}
comment|// at this point we have a commitRootPath
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|commitRootPath
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|commitRootPath
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|document
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// check commit status of revision
if|if
condition|(
name|document
operator|.
name|isCommitted
argument_list|(
name|revision
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|COLLISIONS
argument_list|,
name|revision
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|document
operator|=
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
comment|// check again on old document right before our update was applied
if|if
condition|(
name|document
operator|.
name|isCommitted
argument_list|(
name|revision
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// otherwise collision marker was set successfully
name|LOG
operator|.
name|debug
argument_list|(
literal|"Marked collision on: {} for {} ({})"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|commitRootPath
block|,
name|p
block|,
name|revision
block|}
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|void
name|throwNoCommitRootException
parameter_list|(
annotation|@
name|Nonnull
name|String
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
parameter_list|)
throws|throws
name|MicroKernelException
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
literal|"No commit root for revision: "
operator|+
name|revision
operator|+
literal|", document: "
operator|+
name|Utils
operator|.
name|formatDocument
argument_list|(
name|document
argument_list|)
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

