begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|backup
operator|.
name|impl
package|;
end_package

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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|Stopwatch
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
name|Suppliers
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
name|backup
operator|.
name|FileStoreBackup
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
name|segment
operator|.
name|Compactor
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
name|segment
operator|.
name|Revisions
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
name|segment
operator|.
name|SegmentBufferWriter
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
name|segment
operator|.
name|SegmentNodeState
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
name|segment
operator|.
name|SegmentReader
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
name|segment
operator|.
name|SegmentWriter
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
name|segment
operator|.
name|WriterCacheManager
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|tooling
operator|.
name|BasicReadOnlyBlobStore
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

begin_class
specifier|public
class|class
name|FileStoreBackupImpl
implements|implements
name|FileStoreBackup
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FileStoreBackupImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|USE_FAKE_BLOBSTORE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.backup.UseFakeBlobStore"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|Revisions
name|revisions
parameter_list|,
annotation|@
name|Nonnull
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|SegmentGCOptions
name|gcOptions
init|=
name|SegmentGCOptions
operator|.
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
decl_stmt|;
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
name|destination
argument_list|)
operator|.
name|withDefaultMemoryMapping
argument_list|()
decl_stmt|;
if|if
condition|(
name|USE_FAKE_BLOBSTORE
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
operator|new
name|BasicReadOnlyBlobStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|withGCOptions
argument_list|(
name|gcOptions
argument_list|)
expr_stmt|;
name|FileStore
name|backup
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|current
init|=
name|reader
operator|.
name|readHeadState
argument_list|(
name|revisions
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|gen
init|=
name|current
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
decl_stmt|;
name|SegmentBufferWriter
name|bufferWriter
init|=
operator|new
name|SegmentBufferWriter
argument_list|(
name|backup
argument_list|,
name|backup
operator|.
name|getTracker
argument_list|()
operator|.
name|getSegmentCounter
argument_list|()
argument_list|,
name|backup
operator|.
name|getReader
argument_list|()
argument_list|,
literal|"b"
argument_list|,
name|gen
argument_list|)
decl_stmt|;
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|backup
argument_list|,
name|backup
operator|.
name|getReader
argument_list|()
argument_list|,
name|backup
operator|.
name|getBlobStore
argument_list|()
argument_list|,
operator|new
name|WriterCacheManager
operator|.
name|Default
argument_list|()
argument_list|,
name|bufferWriter
argument_list|)
decl_stmt|;
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|backup
operator|.
name|getReader
argument_list|()
argument_list|,
name|writer
argument_list|,
name|backup
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|false
argument_list|)
argument_list|,
name|gcOptions
argument_list|)
decl_stmt|;
name|compactor
operator|.
name|setContentEqualityCheck
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|head
init|=
name|backup
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|after
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|head
argument_list|,
name|current
argument_list|,
name|head
argument_list|)
decl_stmt|;
if|if
condition|(
name|after
operator|!=
literal|null
condition|)
block|{
name|backup
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|after
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|backup
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|backup
operator|=
name|fileStoreBuilder
argument_list|(
name|destination
argument_list|)
operator|.
name|withDefaultMemoryMapping
argument_list|()
operator|.
name|withGCOptions
argument_list|(
name|gcOptions
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cleanup
argument_list|(
name|backup
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|backup
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Backup finished in {}."
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|cleanup
parameter_list|(
name|FileStore
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|f
operator|.
name|cleanup
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

