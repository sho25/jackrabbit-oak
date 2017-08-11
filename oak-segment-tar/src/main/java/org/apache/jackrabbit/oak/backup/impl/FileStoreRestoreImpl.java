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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|FileStoreRestore
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
name|DefaultSegmentWriter
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
name|GCNodeWriteMonitor
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
name|ReadOnlyFileStore
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
name|tar
operator|.
name|GCGeneration
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
name|FileStoreRestoreImpl
implements|implements
name|FileStoreRestore
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
name|FileStoreRestoreImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JOURNAL_FILE_NAME
init|=
literal|"journal.log"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
if|if
condition|(
operator|!
name|validFileStore
argument_list|(
name|source
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Folder "
operator|+
name|source
operator|+
literal|" is not a valid FileStore directory"
argument_list|)
throw|;
block|}
name|ReadOnlyFileStore
name|restore
init|=
name|fileStoreBuilder
argument_list|(
name|source
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|destination
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|current
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
try|try
block|{
name|SegmentNodeState
name|head
init|=
name|restore
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|GCGeneration
name|gen
init|=
name|head
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
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
literal|"r"
argument_list|,
name|gen
argument_list|)
decl_stmt|;
name|SegmentWriter
name|writer
init|=
operator|new
name|DefaultSegmentWriter
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
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
name|SegmentGCOptions
name|gcOptions
init|=
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
decl_stmt|;
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|writer
argument_list|,
name|store
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
name|GCNodeWriteMonitor
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|after
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|current
argument_list|,
name|head
argument_list|,
name|current
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|current
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
finally|finally
block|{
name|restore
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
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
literal|"Restore finished in {}."
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|File
name|source
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Restore not available as an online operation."
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|validFileStore
parameter_list|(
name|File
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
operator|||
operator|!
name|source
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
index|[]
name|children
init|=
name|source
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|f
range|:
name|children
control|)
block|{
if|if
condition|(
name|JOURNAL_FILE_NAME
operator|.
name|equals
argument_list|(
name|f
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
end_class

end_unit

