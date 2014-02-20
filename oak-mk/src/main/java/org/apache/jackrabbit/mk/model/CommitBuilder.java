begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
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
name|List
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
name|json
operator|.
name|JsonObject
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
name|store
operator|.
name|NotFoundException
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
name|store
operator|.
name|RevisionStore
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|CommitBuilder
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
name|CommitBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** revision changes are based upon */
specifier|private
name|Id
name|baseRevId
decl_stmt|;
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
specifier|private
specifier|final
name|RevisionStore
name|store
decl_stmt|;
comment|// staging area
specifier|private
specifier|final
name|StagedNodeTree
name|stagedTree
decl_stmt|;
comment|// change log
specifier|private
specifier|final
name|List
argument_list|<
name|Change
argument_list|>
name|changeLog
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|CommitBuilder
parameter_list|(
name|Id
name|baseRevId
parameter_list|,
name|String
name|msg
parameter_list|,
name|RevisionStore
name|store
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|baseRevId
operator|=
name|baseRevId
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|stagedTree
operator|=
operator|new
name|StagedNodeTree
argument_list|(
name|store
argument_list|,
name|baseRevId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|JsonObject
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
name|change
init|=
operator|new
name|AddNode
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|String
name|nodePath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Change
name|change
init|=
operator|new
name|RemoveNode
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|moveNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Change
name|change
init|=
operator|new
name|MoveNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
decl_stmt|;
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Change
name|change
init|=
operator|new
name|CopyNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
decl_stmt|;
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|propValue
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
name|change
init|=
operator|new
name|SetProperty
argument_list|(
name|nodePath
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|)
decl_stmt|;
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Id
comment|/* new revId */
name|doCommit
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|doCommit
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|public
name|Id
comment|/* new revId */
name|doCommit
parameter_list|(
name|boolean
name|createBranch
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|stagedTree
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|createBranch
condition|)
block|{
comment|// nothing to commit
return|return
name|baseRevId
return|;
block|}
name|StoredCommit
name|baseCommit
init|=
name|store
operator|.
name|getCommit
argument_list|(
name|baseRevId
argument_list|)
decl_stmt|;
if|if
condition|(
name|createBranch
operator|&&
name|baseCommit
operator|.
name|getBranchRootId
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"cannot branch off a private branch"
argument_list|)
throw|;
block|}
name|boolean
name|privateCommit
init|=
name|createBranch
operator|||
name|baseCommit
operator|.
name|getBranchRootId
argument_list|()
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|privateCommit
condition|)
block|{
name|Id
name|currentHead
init|=
name|store
operator|.
name|getHeadCommitId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentHead
operator|.
name|equals
argument_list|(
name|baseRevId
argument_list|)
condition|)
block|{
comment|// todo gracefully handle certain conflicts (e.g. changes on moved sub-trees, competing deletes etc)
comment|// update base revision to more recent current head
name|baseRevId
operator|=
name|currentHead
expr_stmt|;
comment|// reset staging area
name|stagedTree
operator|.
name|reset
argument_list|(
name|baseRevId
argument_list|)
expr_stmt|;
comment|// replay change log on new base revision
for|for
control|(
name|Change
name|change
range|:
name|changeLog
control|)
block|{
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|RevisionStore
operator|.
name|PutToken
name|token
init|=
name|store
operator|.
name|createPutToken
argument_list|()
decl_stmt|;
name|Id
name|rootNodeId
init|=
name|changeLog
operator|.
name|isEmpty
argument_list|()
condition|?
name|baseCommit
operator|.
name|getRootNodeId
argument_list|()
else|:
name|stagedTree
operator|.
name|persist
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|Id
name|newRevId
decl_stmt|;
if|if
condition|(
operator|!
name|privateCommit
condition|)
block|{
name|store
operator|.
name|lockHead
argument_list|()
expr_stmt|;
try|try
block|{
name|Id
name|currentHead
init|=
name|store
operator|.
name|getHeadCommitId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentHead
operator|.
name|equals
argument_list|(
name|baseRevId
argument_list|)
condition|)
block|{
comment|// there's a more recent head revision
comment|// perform a three-way merge
name|rootNodeId
operator|=
name|stagedTree
operator|.
name|merge
argument_list|(
name|store
operator|.
name|getNode
argument_list|(
name|rootNodeId
argument_list|)
argument_list|,
name|currentHead
argument_list|,
name|baseRevId
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// update base revision to more recent current head
name|baseRevId
operator|=
name|currentHead
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|.
name|getCommit
argument_list|(
name|currentHead
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|rootNodeId
argument_list|)
condition|)
block|{
comment|// the commit didn't cause any changes,
comment|// no need to create new commit object/update head revision
return|return
name|currentHead
return|;
block|}
comment|// persist new commit
name|MutableCommit
name|newCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setParentId
argument_list|(
name|baseRevId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|StringBuilder
name|diff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|changeLog
control|)
block|{
if|if
condition|(
name|diff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|diff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|diff
operator|.
name|append
argument_list|(
name|change
operator|.
name|asDiff
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newCommit
operator|.
name|setChanges
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setBranchRootId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|newRevId
operator|=
name|store
operator|.
name|putHeadCommit
argument_list|(
name|token
argument_list|,
name|newCommit
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// private commit/branch
name|MutableCommit
name|newCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setParentId
argument_list|(
name|baseCommit
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|StringBuilder
name|diff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|changeLog
control|)
block|{
if|if
condition|(
name|diff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|diff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|diff
operator|.
name|append
argument_list|(
name|change
operator|.
name|asDiff
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newCommit
operator|.
name|setChanges
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|createBranch
condition|)
block|{
name|newCommit
operator|.
name|setBranchRootId
argument_list|(
name|baseCommit
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newCommit
operator|.
name|setBranchRootId
argument_list|(
name|baseCommit
operator|.
name|getBranchRootId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newRevId
operator|=
name|store
operator|.
name|putCommit
argument_list|(
name|token
argument_list|,
name|newCommit
argument_list|)
expr_stmt|;
block|}
comment|// reset instance
name|stagedTree
operator|.
name|reset
argument_list|(
name|newRevId
argument_list|)
expr_stmt|;
name|changeLog
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|newRevId
return|;
block|}
specifier|public
name|Id
name|rebase
parameter_list|(
name|Id
name|fromId
parameter_list|,
name|Id
name|toId
parameter_list|)
throws|throws
name|Exception
block|{
name|RevisionStore
operator|.
name|PutToken
name|token
init|=
name|store
operator|.
name|createPutToken
argument_list|()
decl_stmt|;
name|Id
name|rebasedId
init|=
name|stagedTree
operator|.
name|rebase
argument_list|(
name|baseRevId
argument_list|,
name|fromId
argument_list|,
name|toId
argument_list|,
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|.
name|getCommit
argument_list|(
name|toId
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|rebasedId
argument_list|)
condition|)
block|{
comment|// the rebase didn't cause any changes,
comment|// no need to create new commit object/update head revision
return|return
name|toId
return|;
block|}
name|StoredCommit
name|baseCommit
init|=
name|store
operator|.
name|getCommit
argument_list|(
name|baseRevId
argument_list|)
decl_stmt|;
name|MutableCommit
name|newCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setParentId
argument_list|(
name|baseRevId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// dynamically build diff for rebased commit
name|String
name|diff
init|=
operator|new
name|DiffBuilder
argument_list|(
name|store
operator|.
name|getRootNode
argument_list|(
name|toId
argument_list|)
argument_list|,
name|store
operator|.
name|getNode
argument_list|(
name|rebasedId
argument_list|)
argument_list|,
literal|"/"
argument_list|,
operator|-
literal|1
argument_list|,
name|store
argument_list|,
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setChanges
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setRootNodeId
argument_list|(
name|rebasedId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setBranchRootId
argument_list|(
name|baseCommit
operator|.
name|getBranchRootId
argument_list|()
argument_list|)
expr_stmt|;
name|Id
name|newRevId
init|=
name|store
operator|.
name|putCommit
argument_list|(
name|token
argument_list|,
name|newCommit
argument_list|)
decl_stmt|;
comment|// reset instance
name|stagedTree
operator|.
name|reset
argument_list|(
name|newRevId
argument_list|)
expr_stmt|;
name|changeLog
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|newRevId
return|;
block|}
specifier|public
name|Id
comment|/* new revId */
name|doMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|StoredCommit
name|branchCommit
init|=
name|store
operator|.
name|getCommit
argument_list|(
name|baseRevId
argument_list|)
decl_stmt|;
name|Id
name|branchRootId
init|=
name|branchCommit
operator|.
name|getBranchRootId
argument_list|()
decl_stmt|;
if|if
condition|(
name|branchRootId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"can only merge a private branch commit"
argument_list|)
throw|;
block|}
name|RevisionStore
operator|.
name|PutToken
name|token
init|=
name|store
operator|.
name|createPutToken
argument_list|()
decl_stmt|;
name|Id
name|rootNodeId
init|=
name|changeLog
operator|.
name|isEmpty
argument_list|()
condition|?
name|branchCommit
operator|.
name|getRootNodeId
argument_list|()
else|:
name|stagedTree
operator|.
name|persist
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|Id
name|newRevId
decl_stmt|;
name|store
operator|.
name|lockHead
argument_list|()
expr_stmt|;
try|try
block|{
name|Id
name|currentHead
init|=
name|store
operator|.
name|getHeadCommitId
argument_list|()
decl_stmt|;
name|StoredNode
name|ourRoot
init|=
name|store
operator|.
name|getNode
argument_list|(
name|rootNodeId
argument_list|)
decl_stmt|;
name|rootNodeId
operator|=
name|stagedTree
operator|.
name|merge
argument_list|(
name|ourRoot
argument_list|,
name|currentHead
argument_list|,
name|branchRootId
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|getCommit
argument_list|(
name|currentHead
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|rootNodeId
argument_list|)
condition|)
block|{
comment|// the merge didn't cause any changes,
comment|// no need to create new commit object/update head revision
return|return
name|currentHead
return|;
block|}
name|MutableCommit
name|newCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setParentId
argument_list|(
name|currentHead
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// dynamically build diff of merged commit
name|String
name|diff
init|=
operator|new
name|DiffBuilder
argument_list|(
name|store
operator|.
name|getRootNode
argument_list|(
name|currentHead
argument_list|)
argument_list|,
name|store
operator|.
name|getNode
argument_list|(
name|rootNodeId
argument_list|)
argument_list|,
literal|"/"
argument_list|,
operator|-
literal|1
argument_list|,
name|store
argument_list|,
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"merge of empty branch {} with differing content hashes encountered, ignore and keep current head {}"
argument_list|,
name|baseRevId
argument_list|,
name|currentHead
argument_list|)
expr_stmt|;
return|return
name|currentHead
return|;
block|}
name|newCommit
operator|.
name|setChanges
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setBranchRootId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|newRevId
operator|=
name|store
operator|.
name|putHeadCommit
argument_list|(
name|token
argument_list|,
name|newCommit
argument_list|,
name|branchRootId
argument_list|,
name|baseRevId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
comment|// reset instance
name|stagedTree
operator|.
name|reset
argument_list|(
name|newRevId
argument_list|)
expr_stmt|;
name|changeLog
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|newRevId
return|;
block|}
comment|//--------------------------------------------------------< inner classes>
specifier|abstract
class|class
name|Change
block|{
specifier|abstract
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|abstract
name|String
name|asDiff
parameter_list|()
function_decl|;
block|}
class|class
name|AddNode
extends|extends
name|Change
block|{
name|String
name|parentNodePath
decl_stmt|;
name|String
name|nodeName
decl_stmt|;
name|JsonObject
name|node
decl_stmt|;
name|AddNode
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|JsonObject
name|node
parameter_list|)
block|{
name|this
operator|.
name|parentNodePath
operator|=
name|parentNodePath
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|stagedTree
operator|.
name|add
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|asDiff
parameter_list|()
block|{
name|JsopBuilder
name|diff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|toJson
argument_list|(
name|diff
argument_list|)
expr_stmt|;
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
class|class
name|RemoveNode
extends|extends
name|Change
block|{
name|String
name|nodePath
decl_stmt|;
name|RemoveNode
parameter_list|(
name|String
name|nodePath
parameter_list|)
block|{
name|this
operator|.
name|nodePath
operator|=
name|nodePath
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|stagedTree
operator|.
name|remove
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|asDiff
parameter_list|()
block|{
name|JsopBuilder
name|diff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
class|class
name|MoveNode
extends|extends
name|Change
block|{
name|String
name|srcPath
decl_stmt|;
name|String
name|destPath
decl_stmt|;
name|MoveNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
name|this
operator|.
name|destPath
operator|=
name|destPath
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|stagedTree
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|asDiff
parameter_list|()
block|{
name|JsopBuilder
name|diff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
operator|.
name|key
argument_list|(
name|srcPath
argument_list|)
operator|.
name|value
argument_list|(
name|destPath
argument_list|)
expr_stmt|;
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
class|class
name|CopyNode
extends|extends
name|Change
block|{
name|String
name|srcPath
decl_stmt|;
name|String
name|destPath
decl_stmt|;
name|CopyNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
name|this
operator|.
name|destPath
operator|=
name|destPath
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|stagedTree
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|asDiff
parameter_list|()
block|{
name|JsopBuilder
name|diff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|tag
argument_list|(
literal|'*'
argument_list|)
operator|.
name|key
argument_list|(
name|srcPath
argument_list|)
operator|.
name|value
argument_list|(
name|destPath
argument_list|)
expr_stmt|;
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
class|class
name|SetProperty
extends|extends
name|Change
block|{
name|String
name|nodePath
decl_stmt|;
name|String
name|propName
decl_stmt|;
name|String
name|propValue
decl_stmt|;
name|SetProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|propValue
parameter_list|)
block|{
name|this
operator|.
name|nodePath
operator|=
name|nodePath
expr_stmt|;
name|this
operator|.
name|propName
operator|=
name|propName
expr_stmt|;
name|this
operator|.
name|propValue
operator|=
name|propValue
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|stagedTree
operator|.
name|setProperty
argument_list|(
name|nodePath
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|asDiff
parameter_list|()
block|{
name|JsopBuilder
name|diff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|nodePath
argument_list|,
name|propName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|propValue
operator|!=
literal|null
condition|)
block|{
name|diff
operator|.
name|encodedValue
argument_list|(
name|propValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diff
operator|.
name|value
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

