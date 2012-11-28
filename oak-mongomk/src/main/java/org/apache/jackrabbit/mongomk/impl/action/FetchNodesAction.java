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
name|ArrayList
import|;
end_import

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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * An action for fetching nodes.  */
end_comment

begin_class
specifier|public
class|class
name|FetchNodesAction
extends|extends
name|BaseAction
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|int
name|LIMITLESS_DEPTH
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
name|FetchNodesAction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
specifier|private
specifier|final
name|long
name|revisionId
decl_stmt|;
specifier|private
name|String
name|branchId
decl_stmt|;
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
decl_stmt|;
specifier|private
name|int
name|depth
init|=
name|LIMITLESS_DEPTH
decl_stmt|;
specifier|private
name|boolean
name|fetchDescendants
decl_stmt|;
comment|/**      * Constructs a new {@code FetchNodesAction} to fetch a node and optionally      * its descendants under the specified path.      *      * @param nodeStore Node store.      * @param path The path.      * @param fetchDescendants Determines whether the descendants of the path      * will be fetched as well.      * @param revisionId The revision id.      */
specifier|public
name|FetchNodesAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|fetchDescendants
parameter_list|,
name|long
name|revisionId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchDescendants
operator|=
name|fetchDescendants
expr_stmt|;
name|this
operator|.
name|revisionId
operator|=
name|revisionId
expr_stmt|;
block|}
comment|/**      * Constructs a new {@code FetchNodesAction} to fetch nodes with the exact      * specified paths.      *      * @param nodeStore Node store.      * @param paths The exact paths to fetch nodes for.      * @param revisionId The revision id.      */
specifier|public
name|FetchNodesAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|long
name|revisionId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|revisionId
operator|=
name|revisionId
expr_stmt|;
block|}
comment|/**      * Sets the branchId for the query.      *      * @param branchId Branch id.      */
specifier|public
name|void
name|setBranchId
parameter_list|(
name|String
name|branchId
parameter_list|)
block|{
name|this
operator|.
name|branchId
operator|=
name|branchId
expr_stmt|;
block|}
comment|/**      * Sets the last valid commits if already known. This is an optimization to      * speed up the fetch nodes action.      *      * @param commits The last valid commits.      */
specifier|public
name|void
name|setValidCommits
parameter_list|(
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
parameter_list|)
block|{
name|this
operator|.
name|validCommits
operator|=
name|validCommits
expr_stmt|;
block|}
comment|/**      * Sets the depth for the command. Only used when fetchDescendants is enabled.      *      * @param depth The depth for the command or -1 for limitless depth.      */
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|execute
parameter_list|()
block|{
if|if
condition|(
name|paths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
name|DBCursor
name|dbCursor
init|=
name|performQuery
argument_list|()
decl_stmt|;
return|return
name|getMostRecentValidNodes
argument_list|(
name|dbCursor
argument_list|)
return|;
block|}
specifier|private
name|DBCursor
name|performQuery
parameter_list|()
block|{
name|QueryBuilder
name|queryBuilder
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoNode
operator|.
name|KEY_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|in
argument_list|(
name|paths
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|path
init|=
name|paths
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|fetchDescendants
condition|)
block|{
name|Pattern
name|pattern
init|=
name|createPrefixRegExp
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|regex
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|is
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|revisionId
operator|>
literal|0
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoNode
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|branchId
operator|==
literal|null
condition|)
block|{
name|DBObject
name|query
init|=
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
decl_stmt|;
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Not only return nodes in the branch but also nodes in the trunk
comment|// before the branch was created.
name|long
name|headBranchRevisionId
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|branchId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|branchId
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DBObject
name|branchQuery
init|=
name|QueryBuilder
operator|.
name|start
argument_list|()
operator|.
name|or
argument_list|(
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoNode
operator|.
name|KEY_BRANCH_ID
argument_list|)
operator|.
name|is
argument_list|(
name|branchId
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoNode
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|headBranchRevisionId
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|branchQuery
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
name|nodeStore
operator|.
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
return|;
block|}
specifier|private
name|Pattern
name|createPrefixRegExp
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|quotedPath
init|=
name|Pattern
operator|.
name|quote
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|depth
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|quotedPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|depth
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|quotedPath
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|quotedPath
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"(/[^/]*)"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{0,"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}$"
argument_list|)
expr_stmt|;
block|}
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|getMostRecentValidNodes
parameter_list|(
name|DBCursor
name|dbCursor
parameter_list|)
block|{
if|if
condition|(
name|validCommits
operator|==
literal|null
condition|)
block|{
name|validCommits
operator|=
operator|new
name|FetchCommitsAction
argument_list|(
name|nodeStore
argument_list|,
name|revisionId
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|validRevisions
init|=
name|extractRevisionIds
argument_list|(
name|validCommits
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodeMongos
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MongoNode
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
name|MongoNode
name|nodeMongo
init|=
operator|(
name|MongoNode
operator|)
name|dbCursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|nodeMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|long
name|revisionId
init|=
name|nodeMongo
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converting node {} ({})"
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validRevisions
operator|.
name|contains
argument_list|(
name|revisionId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node will not be converted as it is not a valid commit {} ({})"
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|MongoNode
name|existingNodeMongo
init|=
name|nodeMongos
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingNodeMongo
operator|!=
literal|null
condition|)
block|{
name|long
name|existingRevId
init|=
name|existingNodeMongo
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|revisionId
operator|>
name|existingRevId
condition|)
block|{
name|nodeMongos
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|nodeMongo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converted nodes was put into map and replaced {} ({})"
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converted nodes was not put into map because a newer version"
operator|+
literal|" is available {} ({})"
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|nodeMongos
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|nodeMongo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converted node was put into map"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodeMongos
return|;
block|}
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|extractRevisionIds
parameter_list|(
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
parameter_list|)
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|validRevisions
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|(
name|validCommits
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MongoCommit
name|commitMongo
range|:
name|validCommits
control|)
block|{
name|validRevisions
operator|.
name|add
argument_list|(
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|validRevisions
return|;
block|}
block|}
end_class

end_unit

