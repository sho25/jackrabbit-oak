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
name|segment
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|api
operator|.
name|CommitFailedException
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeBuilder
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
name|spi
operator|.
name|commit
operator|.
name|CommitHook
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|spi
operator|.
name|state
operator|.
name|NodeStoreBranch
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
name|spi
operator|.
name|state
operator|.
name|RebaseDiff
import|;
end_import

begin_class
class|class
name|SegmentNodeStoreBranch
implements|implements
name|NodeStoreBranch
block|{
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Journal
name|journal
decl_stmt|;
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
name|RecordId
name|baseId
decl_stmt|;
specifier|private
name|RecordId
name|rootId
decl_stmt|;
name|SegmentNodeStoreBranch
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|Journal
name|journal
parameter_list|,
name|SegmentReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|journal
operator|=
name|journal
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseId
operator|=
name|journal
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|this
operator|.
name|rootId
operator|=
name|baseId
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|baseId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|rootId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|this
operator|.
name|rootId
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|newRoot
argument_list|)
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|rebase
parameter_list|()
block|{
name|RecordId
name|newBaseId
init|=
name|journal
operator|.
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|baseId
operator|.
name|equals
argument_list|(
name|newBaseId
argument_list|)
condition|)
block|{
name|NodeBuilder
name|builder
init|=
operator|new
name|MemoryNodeBuilder
argument_list|(
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|newBaseId
argument_list|)
argument_list|)
decl_stmt|;
name|getHead
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|getBase
argument_list|()
argument_list|,
operator|new
name|RebaseDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseId
operator|=
name|newBaseId
expr_stmt|;
name|this
operator|.
name|rootId
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
name|CommitHook
name|hook
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|RecordId
name|originalBaseId
init|=
name|baseId
decl_stmt|;
name|RecordId
name|originalRootId
init|=
name|rootId
decl_stmt|;
name|long
name|backoff
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|baseId
operator|!=
name|rootId
condition|)
block|{
comment|// apply commit hooks on the rebased changes
name|RecordId
name|headId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|hook
operator|.
name|processCommit
argument_list|(
name|getBase
argument_list|()
argument_list|,
name|getHead
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// use optimistic locking to update the journal
if|if
condition|(
name|journal
operator|.
name|setHead
argument_list|(
name|baseId
argument_list|,
name|headId
argument_list|)
condition|)
block|{
name|baseId
operator|=
name|headId
expr_stmt|;
name|rootId
operator|=
name|headId
expr_stmt|;
block|}
else|else
block|{
comment|// someone else was faster, so restore state and retry later
name|baseId
operator|=
name|originalBaseId
expr_stmt|;
name|rootId
operator|=
name|originalRootId
expr_stmt|;
comment|// use exponential backoff to reduce contention
if|if
condition|(
name|backoff
operator|<
literal|10000
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|backoff
argument_list|,
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|backoff
operator|*=
literal|2
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Commit was interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"System overloaded, try again later"
argument_list|)
throw|;
block|}
comment|// rebase to latest head before trying again
name|rebase
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|getHead
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|source
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|NodeBuilder
name|builder
init|=
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|.
name|removeNode
argument_list|(
name|sourceName
argument_list|)
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copy
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

