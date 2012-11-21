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
name|mongomk
operator|.
name|impl
operator|.
name|action
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
name|LinkedList
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoCommit
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoNode
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
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
import|;
end_import

begin_comment
comment|/**  * An action for fetching valid commits.  */
end_comment

begin_class
specifier|public
class|class
name|FetchCommitsAction
extends|extends
name|BaseAction
argument_list|<
name|List
argument_list|<
name|MongoCommit
argument_list|>
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|LIMITLESS
init|=
operator|-
literal|1
decl_stmt|;
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
name|FetchCommitsAction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|fromRevisionId
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|toRevisionId
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|maxEntries
init|=
name|LIMITLESS
decl_stmt|;
specifier|private
name|boolean
name|includeBranchCommits
init|=
literal|true
decl_stmt|;
comment|/**      * Constructs a new {@link FetchCommitsAction}      *      * @param nodeStore Node store.      */
specifier|public
name|FetchCommitsAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
argument_list|(
name|nodeStore
argument_list|,
operator|-
literal|1L
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new {@link FetchCommitsAction}      *      * @param nodeStore Node store.      * @param toRevisionId To revision id.      */
specifier|public
name|FetchCommitsAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|long
name|toRevisionId
parameter_list|)
block|{
name|this
argument_list|(
name|nodeStore
argument_list|,
operator|-
literal|1L
argument_list|,
name|toRevisionId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new {@link FetchCommitsAction}      *      * @param nodeStore Node store.      * @param fromRevisionId From revision id.      * @param toRevisionId To revision id.      */
specifier|public
name|FetchCommitsAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|long
name|fromRevisionId
parameter_list|,
name|long
name|toRevisionId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromRevisionId
operator|=
name|fromRevisionId
expr_stmt|;
name|this
operator|.
name|toRevisionId
operator|=
name|toRevisionId
expr_stmt|;
block|}
comment|/**      * Sets the max number of entries that should be fetched.      *      * @param maxEntries The max number of entries.      */
specifier|public
name|void
name|setMaxEntries
parameter_list|(
name|int
name|maxEntries
parameter_list|)
block|{
name|this
operator|.
name|maxEntries
operator|=
name|maxEntries
expr_stmt|;
block|}
comment|/**      * Sets whether the branch commits are included in the query.      *      * @param includeBranchCommits Whether the branch commits are included.      */
specifier|public
name|void
name|includeBranchCommits
parameter_list|(
name|boolean
name|includeBranchCommits
parameter_list|)
block|{
name|this
operator|.
name|includeBranchCommits
operator|=
name|includeBranchCommits
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|execute
parameter_list|()
block|{
if|if
condition|(
name|maxEntries
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|DBCursor
name|dbCursor
init|=
name|fetchListOfValidCommits
argument_list|()
decl_stmt|;
return|return
name|convertToCommits
argument_list|(
name|dbCursor
argument_list|)
return|;
block|}
specifier|private
name|DBCursor
name|fetchListOfValidCommits
parameter_list|()
block|{
name|DBCollection
name|commitCollection
init|=
name|nodeStore
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_FAILED
argument_list|)
operator|.
name|notEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|toRevisionId
operator|>
operator|-
literal|1
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|toRevisionId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|includeBranchCommits
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|MongoNode
operator|.
name|KEY_BRANCH_ID
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
literal|"$exists"
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DBObject
name|query
init|=
name|queryBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Executing query: {}"
argument_list|,
name|query
argument_list|)
expr_stmt|;
return|return
name|maxEntries
operator|>
literal|0
condition|?
name|commitCollection
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|limit
argument_list|(
name|maxEntries
argument_list|)
else|:
name|commitCollection
operator|.
name|find
argument_list|(
name|query
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|convertToCommits
parameter_list|(
name|DBCursor
name|dbCursor
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|MongoCommit
argument_list|>
name|commits
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|MongoCommit
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|dbCursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MongoCommit
name|commitMongo
init|=
operator|(
name|MongoCommit
operator|)
name|dbCursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|commits
operator|.
name|put
argument_list|(
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|,
name|commitMongo
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
init|=
operator|new
name|LinkedList
argument_list|<
name|MongoCommit
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|commits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|validCommits
return|;
block|}
name|Set
argument_list|<
name|Long
argument_list|>
name|revisions
init|=
name|commits
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|long
name|currentRevision
init|=
operator|(
name|toRevisionId
operator|!=
operator|-
literal|1
operator|&&
name|revisions
operator|.
name|contains
argument_list|(
name|toRevisionId
argument_list|)
operator|)
condition|?
name|toRevisionId
else|:
name|Collections
operator|.
name|max
argument_list|(
name|revisions
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|MongoCommit
name|commitMongo
init|=
name|commits
operator|.
name|get
argument_list|(
name|currentRevision
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitMongo
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|validCommits
operator|.
name|add
argument_list|(
name|commitMongo
argument_list|)
expr_stmt|;
name|long
name|baseRevision
init|=
name|commitMongo
operator|.
name|getBaseRevisionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentRevision
operator|==
literal|0L
operator|||
name|baseRevision
operator|<
name|fromRevisionId
condition|)
block|{
break|break;
block|}
name|currentRevision
operator|=
name|baseRevision
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found list of valid revisions for max revision {}: {}"
argument_list|,
name|toRevisionId
argument_list|,
name|validCommits
argument_list|)
expr_stmt|;
return|return
name|validCommits
return|;
block|}
block|}
end_class

end_unit

