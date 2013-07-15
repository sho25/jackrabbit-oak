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
name|spi
operator|.
name|commit
operator|.
name|EmptyObserver
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
name|AbstractNodeStore
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

begin_class
specifier|public
class|class
name|SegmentNodeStore
extends|extends
name|AbstractNodeStore
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
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|Observer
name|observer
decl_stmt|;
specifier|private
name|SegmentNodeState
name|head
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
name|reader
operator|=
operator|new
name|SegmentReader
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|observer
operator|=
name|EmptyObserver
operator|.
name|INSTANCE
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|this
operator|.
name|journal
operator|.
name|getHead
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
specifier|synchronized
name|SegmentNodeState
name|getHead
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
decl_stmt|;
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|journal
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
decl_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|before
argument_list|,
name|after
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
parameter_list|)
block|{
return|return
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
annotation|@
name|Nonnull
specifier|public
name|NodeStoreBranch
name|branch
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeStoreBranch
argument_list|(
name|this
argument_list|,
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
argument_list|,
name|getHead
argument_list|()
argument_list|)
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
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|RecordId
name|recordId
init|=
name|writer
operator|.
name|writeStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
operator|new
name|SegmentBlob
argument_list|(
name|reader
argument_list|,
name|recordId
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
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
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

