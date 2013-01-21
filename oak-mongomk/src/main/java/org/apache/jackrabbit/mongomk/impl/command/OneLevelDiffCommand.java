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
name|command
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
name|mk
operator|.
name|json
operator|.
name|JsopBuilder
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
name|model
operator|.
name|tree
operator|.
name|DiffBuilder
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
name|action
operator|.
name|FetchCommitAction
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
name|action
operator|.
name|FetchNodesActionNew
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
name|NodeImpl
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
name|tree
operator|.
name|SimpleMongoNodeStore
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
name|util
operator|.
name|MongoUtil
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
comment|/**  *<code>OneLevelDiffCommand</code> implements a specialized {@link DiffCommand}  * with a fixed depth of 0.  */
end_comment

begin_class
specifier|public
class|class
name|OneLevelDiffCommand
extends|extends
name|BaseCommand
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|long
name|fromRevision
decl_stmt|;
specifier|private
specifier|final
name|long
name|toRevision
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|int
name|pathDepth
decl_stmt|;
specifier|public
name|OneLevelDiffCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|fromRevision
parameter_list|,
name|String
name|toRevision
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromRevision
operator|=
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|checkNotNull
argument_list|(
name|fromRevision
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|toRevision
operator|=
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|checkNotNull
argument_list|(
name|toRevision
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|MongoUtil
operator|.
name|adjustPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|pathDepth
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoCommit
name|fromCommit
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|fromRevision
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|MongoCommit
name|toCommit
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|toRevision
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|FetchNodesActionNew
name|action
init|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
name|fromRevision
argument_list|)
decl_stmt|;
name|action
operator|.
name|setBranchId
argument_list|(
name|fromCommit
operator|.
name|getBranchId
argument_list|()
argument_list|)
expr_stmt|;
name|NodeImpl
name|fromNode
init|=
name|MongoNode
operator|.
name|toNode
argument_list|(
name|action
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|action
operator|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
name|toRevision
argument_list|)
expr_stmt|;
name|action
operator|.
name|setBranchId
argument_list|(
name|toCommit
operator|.
name|getBranchId
argument_list|()
argument_list|)
expr_stmt|;
name|NodeImpl
name|toNode
init|=
name|MongoNode
operator|.
name|toNode
argument_list|(
name|action
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|diff
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
name|fromNode
operator|.
name|getRevisionId
argument_list|()
operator|.
name|equals
argument_list|(
name|toNode
operator|.
name|getRevisionId
argument_list|()
argument_list|)
condition|)
block|{
comment|// diff of node at given path
name|DiffBuilder
name|diffBuilder
init|=
operator|new
name|DiffBuilder
argument_list|(
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|fromNode
argument_list|)
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|toNode
argument_list|)
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|diff
operator|=
name|diffBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|// find out what changed below path
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commits
init|=
name|getCommits
argument_list|(
name|fromCommit
argument_list|,
name|toCommit
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|affectedPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|from
init|=
operator|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|?
name|path
else|:
name|path
operator|+
literal|"/"
operator|)
operator|+
literal|"\u0000"
decl_stmt|;
name|String
name|to
init|=
operator|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|?
name|path
else|:
name|path
operator|+
literal|"/"
operator|)
operator|+
literal|"\uFFFF"
decl_stmt|;
for|for
control|(
name|MongoCommit
name|mc
range|:
name|commits
control|)
block|{
for|for
control|(
name|String
name|p
range|:
name|mc
operator|.
name|getAffectedPaths
argument_list|()
operator|.
name|subSet
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
control|)
block|{
name|int
name|d
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|>
name|pathDepth
condition|)
block|{
name|affectedPaths
operator|.
name|add
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|p
argument_list|,
name|d
operator|-
name|pathDepth
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|affectedPaths
control|)
block|{
name|builder
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|key
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|builder
operator|.
name|object
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
return|return
name|diff
operator|+
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Retrieves the commits within the range of<code>c1</code> and      *<code>c2</code>. The given bounding commits are included in the list as      * well.      *      * @param c1 a MongoCommit      * @param c2 a MongoCommit      * @return the commits from<code>c1</code> to<code>c2</code>.      * @throws Exception if an error occurs.      */
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|getCommits
parameter_list|(
name|MongoCommit
name|c1
parameter_list|,
name|MongoCommit
name|c2
parameter_list|)
throws|throws
name|Exception
block|{
comment|// this implementation does not use the multi commit fetch action
comment|// FetchCommitsAction because that action does not leverage the
comment|// commit cache in NodeStore. Retrieving each commit individually
comment|// results in good cache hit ratios when the revision range is recent
comment|// and not too big.
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commits
init|=
operator|new
name|ArrayList
argument_list|<
name|MongoCommit
argument_list|>
argument_list|()
decl_stmt|;
name|MongoCommit
name|fromCommit
init|=
name|c1
operator|.
name|getRevisionId
argument_list|()
operator|<
name|c2
operator|.
name|getRevisionId
argument_list|()
condition|?
name|c1
else|:
name|c2
decl_stmt|;
name|MongoCommit
name|toCommit
init|=
name|c1
operator|.
name|getRevisionId
argument_list|()
operator|<
name|c2
operator|.
name|getRevisionId
argument_list|()
condition|?
name|c2
else|:
name|c1
decl_stmt|;
name|Long
name|revision
init|=
name|toCommit
operator|.
name|getBaseRevisionId
argument_list|()
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
name|toCommit
argument_list|)
expr_stmt|;
while|while
condition|(
name|revision
operator|!=
literal|null
operator|&&
name|revision
operator|>
name|fromCommit
operator|.
name|getRevisionId
argument_list|()
condition|)
block|{
name|MongoCommit
name|c
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|revision
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|revision
operator|=
name|c
operator|.
name|getBaseRevisionId
argument_list|()
expr_stmt|;
block|}
name|commits
operator|.
name|add
argument_list|(
name|fromCommit
argument_list|)
expr_stmt|;
return|return
name|commits
return|;
block|}
block|}
end_class

end_unit

