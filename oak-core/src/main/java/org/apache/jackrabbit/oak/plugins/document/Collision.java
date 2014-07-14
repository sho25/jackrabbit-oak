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
name|Revision
name|theirRev
decl_stmt|;
specifier|private
specifier|final
name|UpdateOp
name|ourOp
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|ourRev
decl_stmt|;
specifier|private
specifier|final
name|RevisionContext
name|context
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
parameter_list|,
annotation|@
name|Nonnull
name|RevisionContext
name|context
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
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Marks the collision in the document store. Either our or their      * revision is annotated with a collision marker. Their revision is      * marked if it is not yet committed, otherwise our revision is marked.      *      * @param store the document store.      * @return the revision that was marked. Either our or their.      * @throws DocumentStoreException if the mark operation fails.      */
annotation|@
name|Nonnull
name|Revision
name|mark
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
throws|throws
name|DocumentStoreException
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
return|return
name|theirRev
return|;
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
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|document
operator|.
name|deepCopy
argument_list|(
name|newDoc
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|newDoc
argument_list|,
name|ourOp
argument_list|,
name|context
operator|.
name|getRevisionComparator
argument_list|()
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
name|IllegalStateException
argument_list|(
literal|"Unable to annotate our revision "
operator|+
literal|"with collision marker. Our revision: "
operator|+
name|ourRev
operator|+
literal|", document:\n"
operator|+
name|newDoc
operator|.
name|format
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|ourRev
return|;
block|}
comment|/**      * Marks the commit root of the change to the given<code>document</code> in      *<code>revision</code>.      *      * @param document the document.      * @param revision the revision of the commit to annotated with a collision      *            marker.      * @param store the document store.      * @return<code>true</code> if the commit for the given revision was marked      *         successfully;<code>false</code> otherwise.      */
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
name|Revision
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
name|document
operator|.
name|getPath
argument_list|()
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
name|commitRootPath
operator|=
name|document
operator|.
name|getCommitRootPath
argument_list|(
name|revision
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitRootPath
operator|==
literal|null
condition|)
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
name|NodeDocument
name|commitRoot
init|=
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
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|// check commit status of revision
if|if
condition|(
name|commitRoot
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
comment|// check if there is already a collision marker
if|if
condition|(
name|commitRoot
operator|.
name|getLocalMap
argument_list|(
name|NodeDocument
operator|.
name|COLLISIONS
argument_list|)
operator|.
name|containsKey
argument_list|(
name|revision
argument_list|)
condition|)
block|{
comment|// already marked
return|return
literal|true
return|;
block|}
name|NodeDocument
operator|.
name|addCollision
argument_list|(
name|op
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|commitRoot
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
name|commitRoot
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
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|Document
name|document
parameter_list|)
throws|throws
name|DocumentStoreException
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"No commit root for revision: "
operator|+
name|revision
operator|+
literal|", document: "
operator|+
name|document
operator|.
name|format
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

