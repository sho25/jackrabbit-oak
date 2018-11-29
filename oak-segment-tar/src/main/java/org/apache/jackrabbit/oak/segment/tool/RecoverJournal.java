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
name|segment
operator|.
name|tool
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
name|checkNotNull
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
name|checkState
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
name|tool
operator|.
name|Utils
operator|.
name|openReadOnlyFileStore
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
name|tool
operator|.
name|Utils
operator|.
name|parseSegmentInfoTimestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileWriter
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

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
name|oak
operator|.
name|segment
operator|.
name|RecordId
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
name|RecordType
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
name|SegmentId
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
name|SegmentNotFoundException
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

begin_class
specifier|public
class|class
name|RecoverJournal
block|{
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|path
decl_stmt|;
specifier|private
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
specifier|private
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|,
literal|"path"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withOut
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"out"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withErr
parameter_list|(
name|PrintStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|err
operator|=
name|checkNotNull
argument_list|(
name|err
argument_list|,
literal|"err"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|RecoverJournal
name|build
parameter_list|()
block|{
name|checkState
argument_list|(
name|path
operator|!=
literal|null
argument_list|,
literal|"path not specified"
argument_list|)
expr_stmt|;
return|return
operator|new
name|RecoverJournal
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
specifier|final
name|PrintStream
name|out
decl_stmt|;
specifier|private
specifier|final
name|PrintStream
name|err
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|notFoundSegments
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|RecoverJournal
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|builder
operator|.
name|out
expr_stmt|;
name|this
operator|.
name|err
operator|=
name|builder
operator|.
name|err
expr_stmt|;
block|}
specifier|public
name|int
name|run
parameter_list|()
block|{
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|ReadOnlyFileStore
name|store
init|=
name|openReadOnlyFileStore
argument_list|(
name|path
argument_list|)
init|)
block|{
name|recoverEntries
argument_list|(
name|store
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Unable to recover the journal entries, aborting"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|File
name|journalBackup
init|=
name|journalBackupName
argument_list|()
decl_stmt|;
if|if
condition|(
name|journalBackup
operator|==
literal|null
condition|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Too many journal backups, please cleanup"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|File
name|journal
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"journal.log"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|journal
operator|.
name|toPath
argument_list|()
argument_list|,
name|journalBackup
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Unable to backup old journal, aborting"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|out
operator|.
name|printf
argument_list|(
literal|"Old journal backed up at %s\n"
argument_list|,
name|journalBackup
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|rollback
decl_stmt|;
try|try
init|(
name|PrintWriter
name|w
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|journal
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|Entry
name|e
range|:
name|entries
control|)
block|{
name|w
operator|.
name|printf
argument_list|(
literal|"%s root %d\n"
argument_list|,
name|e
operator|.
name|recordId
operator|.
name|toString10
argument_list|()
argument_list|,
name|e
operator|.
name|timestamp
argument_list|)
expr_stmt|;
block|}
name|rollback
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Unable to write the recovered journal, rolling back"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|rollback
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|rollback
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|journal
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Unable to delete the recovered journal, aborting"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|journalBackup
operator|.
name|toPath
argument_list|()
argument_list|,
name|journal
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Unable to roll back the old journal, aborting"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"Old journal rolled back"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"Journal recovered"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|private
name|File
name|journalBackupName
parameter_list|()
block|{
for|for
control|(
name|int
name|attempt
init|=
literal|0
init|;
name|attempt
operator|<
literal|1000
condition|;
name|attempt
operator|++
control|)
block|{
name|File
name|backup
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"journal.log.bak.%03d"
argument_list|,
name|attempt
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|backup
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
return|return
name|backup
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
class|class
name|Entry
block|{
name|long
name|timestamp
decl_stmt|;
name|RecordId
name|recordId
decl_stmt|;
name|Entry
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|recordId
operator|=
name|recordId
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|recoverEntries
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|,
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|)
block|{
for|for
control|(
name|SegmentId
name|segmentId
range|:
name|fileStore
operator|.
name|getSegmentIds
argument_list|()
control|)
block|{
try|try
block|{
name|recoverEntries
argument_list|(
name|fileStore
argument_list|,
name|segmentId
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|handle
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|entries
operator|.
name|sort
argument_list|(
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
block|{
comment|// Two entries with different timestamp will be sorted in ascending
comment|// order of timestamp.
name|int
name|timestampComparison
init|=
name|Long
operator|.
name|compare
argument_list|(
name|left
operator|.
name|timestamp
argument_list|,
name|right
operator|.
name|timestamp
argument_list|)
decl_stmt|;
if|if
condition|(
name|timestampComparison
operator|!=
literal|0
condition|)
block|{
return|return
name|timestampComparison
return|;
block|}
comment|// Comparing segment IDs with the same timestamp is totally
comment|// arbitrary. The relative order of two segments with the same
comment|// timestamp is unimportant.
name|SegmentId
name|leftSegmentId
init|=
name|left
operator|.
name|recordId
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|SegmentId
name|rightSegmentId
init|=
name|right
operator|.
name|recordId
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|int
name|segmentIdComparison
init|=
name|leftSegmentId
operator|.
name|compareTo
argument_list|(
name|rightSegmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentIdComparison
operator|!=
literal|0
condition|)
block|{
return|return
name|segmentIdComparison
return|;
block|}
comment|// Records from the same segments are sorted in decreasing order
comment|// of their record number. This builds on the assumption that a
comment|// record with a higher record number was added after a record
comment|// with a lower one, and therefor is more recent.
name|int
name|leftRecordNumber
init|=
name|left
operator|.
name|recordId
operator|.
name|getRecordNumber
argument_list|()
decl_stmt|;
name|int
name|rightRecordNumber
init|=
name|right
operator|.
name|recordId
operator|.
name|getRecordNumber
argument_list|()
decl_stmt|;
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|rightRecordNumber
argument_list|,
name|leftRecordNumber
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|recoverEntries
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|,
name|SegmentId
name|segmentId
parameter_list|,
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|)
block|{
if|if
condition|(
name|segmentId
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
return|return;
block|}
name|Long
name|timestamp
init|=
name|parseSegmentInfoTimestamp
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|timestamp
operator|==
literal|null
condition|)
block|{
name|err
operator|.
name|printf
argument_list|(
literal|"No timestamp found in segment %s\n"
argument_list|,
name|segmentId
argument_list|)
expr_stmt|;
return|return;
block|}
name|segmentId
operator|.
name|getSegment
argument_list|()
operator|.
name|forEachRecord
argument_list|(
parameter_list|(
name|number
parameter_list|,
name|type
parameter_list|,
name|offset
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|type
operator|!=
name|RecordType
operator|.
name|NODE
condition|)
block|{
return|return;
block|}
try|try
block|{
name|recoverEntries
argument_list|(
name|fileStore
argument_list|,
name|timestamp
argument_list|,
operator|new
name|RecordId
argument_list|(
name|segmentId
argument_list|,
name|number
argument_list|)
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|handle
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|recoverEntries
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|RecordId
name|recordId
parameter_list|,
name|List
argument_list|<
name|Entry
argument_list|>
name|entries
parameter_list|)
block|{
name|SegmentNodeState
name|nodeState
init|=
name|fileStore
operator|.
name|getReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|recordId
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeState
operator|.
name|hasChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|&&
name|nodeState
operator|.
name|hasChildNode
argument_list|(
literal|"root"
argument_list|)
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|timestamp
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handle
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notFoundSegments
operator|.
name|add
argument_list|(
name|e
operator|.
name|getSegmentId
argument_list|()
argument_list|)
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
