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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Blob
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
name|spi
operator|.
name|commit
operator|.
name|ChangeDispatcher
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
name|commit
operator|.
name|CommitInfo
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
name|Observable
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
name|Observer
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
name|ConflictAnnotatingRebaseDiff
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
name|NodeStore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|"root"
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Journal
name|journal
decl_stmt|;
specifier|private
specifier|final
name|ChangeDispatcher
name|changeDispatcher
decl_stmt|;
specifier|private
name|SegmentNodeState
name|head
decl_stmt|;
specifier|private
name|long
name|maximumBackoff
init|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|SECONDS
argument_list|)
decl_stmt|;
specifier|public
name|SegmentNodeStore
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|String
name|journal
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|journal
operator|=
name|store
operator|.
name|getJournal
argument_list|(
name|journal
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|this
operator|.
name|journal
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|changeDispatcher
operator|=
operator|new
name|ChangeDispatcher
argument_list|(
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentNodeStore
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
block|}
name|void
name|setMaximumBackoff
parameter_list|(
name|long
name|max
parameter_list|)
block|{
name|this
operator|.
name|maximumBackoff
operator|=
name|max
expr_stmt|;
block|}
specifier|synchronized
name|SegmentNodeState
name|getHead
parameter_list|()
block|{
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|journal
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|head
return|;
block|}
name|boolean
name|setHead
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|,
name|SegmentNodeState
name|head
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|journal
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|getRoot
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
return|return
name|changeDispatcher
operator|.
name|addObserver
argument_list|(
name|observer
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
name|getRoot
parameter_list|()
block|{
return|return
name|getHead
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|commitHook
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|head
init|=
name|getHead
argument_list|()
decl_stmt|;
name|rebase
argument_list|(
name|builder
argument_list|,
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: can we avoid this?
name|SegmentNodeStoreBranch
name|branch
init|=
operator|new
name|SegmentNodeStoreBranch
argument_list|(
name|this
argument_list|,
name|store
operator|.
name|getWriter
argument_list|()
argument_list|,
name|head
argument_list|,
name|maximumBackoff
argument_list|)
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|merged
init|=
name|branch
operator|.
name|merge
argument_list|(
name|commitHook
argument_list|,
name|info
argument_list|)
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|merged
argument_list|)
expr_stmt|;
return|return
name|merged
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|rebase
argument_list|(
name|builder
argument_list|,
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
name|NodeState
name|newBase
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|oldBase
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fastEquals
argument_list|(
name|oldBase
argument_list|,
name|newBase
argument_list|)
condition|)
block|{
name|NodeState
name|head
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|oldBase
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|fastEquals
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|store
operator|.
name|isInstance
argument_list|(
name|a
argument_list|,
name|Record
operator|.
name|class
argument_list|)
operator|&&
name|store
operator|.
name|isInstance
argument_list|(
name|b
argument_list|,
name|Record
operator|.
name|class
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
operator|(
operator|(
name|Record
operator|)
name|a
operator|)
operator|.
name|getRecordId
argument_list|()
argument_list|,
operator|(
operator|(
name|Record
operator|)
name|b
operator|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|getRoot
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|writeStream
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// TODO: Guard the checkpoint from garbage collection
return|return
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
comment|// TODO: Verify validity of the checkpoint
name|RecordId
name|id
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|root
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|id
argument_list|)
decl_stmt|;
return|return
name|root
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

