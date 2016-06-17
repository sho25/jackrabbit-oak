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
operator|.
name|mongo
operator|.
name|replica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Revision
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
name|RevisionVector
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
operator|.
name|isGreaterOrEquals
import|;
end_import

begin_comment
comment|/**  * This class maintains a list of local changes (paths+revisions), which  * shouldn't be read from the secondary Mongo, as we are not sure if they have  * been already replicated from primary. Once we get this confidence, the entry  * will be removed from the map.  */
end_comment

begin_class
specifier|public
class|class
name|LocalChanges
implements|implements
name|ReplicaSetInfoListener
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
name|LocalChanges
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * How many paths should be stored in the {@link #localChanges} map. If      * there's more paths added (and not removed in the      * {@link #gotRootRevisions(RevisionVector)}), only the latest changed      * revision will be remembered.      */
specifier|private
specifier|static
specifier|final
name|int
name|SIZE_LIMIT
init|=
literal|100
decl_stmt|;
comment|/**      * This map contains document paths and revisions in which they have been      * changed. Paths in this collection hasn't been replicated to secondary      * instances yet.      */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RevisionVector
argument_list|>
name|localChanges
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevisionVector
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * If there's more than {@link #SIZE_LIMIT} paths in the      * {@link #localChanges}, the class will clear the above map and update this      * variable with the latest changed revision. {@code true} will be returned      * for all {@link #mayContainChildrenOf(String)} and {@link #mayContain(String)}      * invocations until this revision is replicated to all secondary instances.      *<p>      * This is a safety mechanism, so the {@link #localChanges} won't grow too much.      */
specifier|private
specifier|volatile
name|RevisionVector
name|latestChange
decl_stmt|;
comment|/**      * True if the current Mongo installation is an working replica. Otherwise      * there's no need to store the local changes.      */
specifier|private
specifier|volatile
name|boolean
name|replicaActive
decl_stmt|;
specifier|private
specifier|volatile
name|RevisionVector
name|rootRevision
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
name|String
name|id
parameter_list|,
name|Collection
argument_list|<
name|Revision
argument_list|>
name|revs
parameter_list|)
block|{
name|RevisionVector
name|revsV
init|=
operator|new
name|RevisionVector
argument_list|(
name|revs
argument_list|)
decl_stmt|;
name|RevisionVector
name|localRootRev
init|=
name|rootRevision
decl_stmt|;
if|if
condition|(
name|localRootRev
operator|!=
literal|null
operator|&&
name|isGreaterOrEquals
argument_list|(
name|localRootRev
argument_list|,
name|revsV
argument_list|)
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|latestChange
operator|!=
literal|null
operator|&&
name|isGreaterOrEquals
argument_list|(
name|latestChange
argument_list|,
name|revsV
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|replicaActive
condition|)
block|{
name|localChanges
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|revsV
argument_list|)
expr_stmt|;
if|if
condition|(
name|localChanges
operator|.
name|size
argument_list|()
operator|>=
name|SIZE_LIMIT
condition|)
block|{
name|localChanges
operator|.
name|clear
argument_list|()
expr_stmt|;
name|latestChange
operator|=
name|revsV
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"The local changes count == {}. Clearing the list and switching to the 'latest change' mode: {}"
argument_list|,
name|SIZE_LIMIT
argument_list|,
name|latestChange
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|latestChange
operator|=
name|revsV
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Check if it's possible that the given document hasn't been replicated to      * the secondary yet.      *      * @param documentId      * @return {@code true} if it's possible that the document is still in the      *         Mongo replication queue      */
specifier|public
name|boolean
name|mayContain
parameter_list|(
name|String
name|documentId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|replicaActive
operator|||
name|latestChange
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|localChanges
operator|.
name|containsKey
argument_list|(
name|documentId
argument_list|)
return|;
block|}
block|}
comment|/**      * Check if it's possible that the children of the given document hasn't      * been replicated to the secondary yet.      *      * @param documentId      * @return {@code true} if it's possible that the children of given document      *         are still in the Mongo replication queue      */
specifier|public
name|boolean
name|mayContainChildrenOf
parameter_list|(
name|String
name|parentId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|replicaActive
operator|||
name|latestChange
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|String
name|key
range|:
name|localChanges
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|parentId
operator|.
name|equals
argument_list|(
name|Utils
operator|.
name|getParentId
argument_list|(
name|key
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|gotRootRevisions
parameter_list|(
name|RevisionVector
name|rootRevision
parameter_list|)
block|{
if|if
condition|(
name|rootRevision
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|rootRevision
operator|=
name|rootRevision
expr_stmt|;
if|if
condition|(
operator|!
name|replicaActive
condition|)
block|{
name|replicaActive
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Replica set became active"
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|latestChange
operator|!=
literal|null
operator|&&
name|latestChange
operator|.
name|compareTo
argument_list|(
name|rootRevision
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|latestChange
operator|=
literal|null
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|RevisionVector
argument_list|>
name|it
init|=
name|localChanges
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|compareTo
argument_list|(
name|rootRevision
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

