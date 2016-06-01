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
name|explorer
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|reverseOrder
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|sort
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
name|java
operator|.
name|util
operator|.
name|AbstractMap
operator|.
name|SimpleImmutableEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|Map
operator|.
name|Entry
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
name|UUID
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
name|collect
operator|.
name|Maps
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
name|PropertyState
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
name|SegmentBlob
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
name|SegmentNodeStateHelper
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
name|SegmentPropertyState
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
name|JournalReader
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

begin_class
class|class
name|SegmentTarExplorerBackend
implements|implements
name|ExplorerBackend
block|{
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
name|FileStore
operator|.
name|ReadOnlyStore
name|store
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|index
decl_stmt|;
name|SegmentTarExplorerBackend
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|path
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
name|index
operator|=
name|store
operator|.
name|getTarReaderIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
name|index
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|readRevisions
parameter_list|()
block|{
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
if|if
condition|(
operator|!
name|journal
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|newArrayList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|revs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|JournalReader
name|journalReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|journalReader
operator|=
operator|new
name|JournalReader
argument_list|(
name|journal
argument_list|)
expr_stmt|;
try|try
block|{
name|revs
operator|=
name|newArrayList
argument_list|(
name|journalReader
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|journalReader
operator|!=
literal|null
condition|)
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|revs
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarReaderIndex
parameter_list|()
block|{
return|return
name|store
operator|.
name|getTarReaderIndex
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarGraph
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|getTarGraph
argument_list|(
name|file
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getTarFiles
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|store
operator|.
name|getTarReaderIndex
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|files
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|p
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sort
argument_list|(
name|files
argument_list|,
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|files
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|getGcRoots
parameter_list|(
name|UUID
name|uuidIn
parameter_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|links
parameter_list|)
throws|throws
name|IOException
block|{
name|Deque
argument_list|<
name|UUID
argument_list|>
name|todos
init|=
operator|new
name|ArrayDeque
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|todos
operator|.
name|add
argument_list|(
name|uuidIn
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|visited
init|=
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|todos
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|UUID
name|uuid
init|=
name|todos
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|visited
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|String
name|f
range|:
name|getTarFiles
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
name|store
operator|.
name|getTarGraph
argument_list|(
name|f
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|g
range|:
name|graph
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|g
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
name|g
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|UUID
name|uuidP
init|=
name|g
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|todos
operator|.
name|contains
argument_list|(
name|uuidP
argument_list|)
condition|)
block|{
name|todos
operator|.
name|add
argument_list|(
name|uuidP
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
name|deps
init|=
name|links
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|deps
operator|==
literal|null
condition|)
block|{
name|deps
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
name|links
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|deps
argument_list|)
expr_stmt|;
block|}
name|deps
operator|.
name|add
argument_list|(
operator|new
name|SimpleImmutableEntry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|(
name|uuidP
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|UUID
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|ids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|id
range|:
name|store
operator|.
name|getTracker
argument_list|()
operator|.
name|getReferencedSegmentIds
argument_list|()
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|id
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|readNodeState
parameter_list|(
name|String
name|recordId
parameter_list|)
block|{
return|return
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|RecordId
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|recordId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRevision
parameter_list|(
name|String
name|revision
parameter_list|)
block|{
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPersisted
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
return|return
name|state
operator|instanceof
name|SegmentNodeState
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPersisted
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
return|return
name|state
operator|instanceof
name|SegmentPropertyState
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRecordId
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getRecordId
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|UUID
name|getSegmentId
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getSegmentId
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRecordId
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
return|return
name|getRecordId
argument_list|(
operator|(
name|SegmentPropertyState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|UUID
name|getSegmentId
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
return|return
name|getSegmentId
argument_list|(
operator|(
name|SegmentPropertyState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTemplateRecordId
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getTemplateRecordId
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|UUID
name|getTemplateSegmentId
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getTemplateSegmentId
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFile
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getFile
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFile
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
return|return
name|getFile
argument_list|(
operator|(
name|SegmentPropertyState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTemplateFile
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
name|getTemplateFile
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|state
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
name|getBulkSegmentIds
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|segmentId
range|:
name|SegmentBlob
operator|.
name|getBulkSegmentIds
argument_list|(
name|blob
argument_list|)
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|segmentId
operator|.
name|asUUID
argument_list|()
argument_list|,
name|getFile
argument_list|(
name|segmentId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPersistedCompactionMapStats
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExternal
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
if|if
condition|(
name|blob
operator|instanceof
name|SegmentBlob
condition|)
block|{
return|return
name|isExternal
argument_list|(
operator|(
name|SegmentBlob
operator|)
name|blob
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|isExternal
parameter_list|(
name|SegmentBlob
name|blob
parameter_list|)
block|{
return|return
name|blob
operator|.
name|isExternal
argument_list|()
return|;
block|}
specifier|private
name|String
name|getRecordId
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|UUID
name|getSegmentId
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
return|;
block|}
specifier|private
name|String
name|getRecordId
parameter_list|(
name|SegmentPropertyState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|UUID
name|getSegmentId
parameter_list|(
name|SegmentPropertyState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
return|;
block|}
specifier|private
name|String
name|getTemplateRecordId
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
name|RecordId
name|recordId
init|=
name|SegmentNodeStateHelper
operator|.
name|getTemplateId
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|recordId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|recordId
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|UUID
name|getTemplateSegmentId
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
name|RecordId
name|recordId
init|=
name|SegmentNodeStateHelper
operator|.
name|getTemplateId
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|recordId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|recordId
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
return|;
block|}
specifier|private
name|String
name|getFile
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
return|return
name|getFile
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getFile
parameter_list|(
name|SegmentPropertyState
name|state
parameter_list|)
block|{
return|return
name|getFile
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getTemplateFile
parameter_list|(
name|SegmentNodeState
name|state
parameter_list|)
block|{
name|RecordId
name|recordId
init|=
name|SegmentNodeStateHelper
operator|.
name|getTemplateId
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|recordId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getFile
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getFile
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|path2Uuid
range|:
name|index
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|UUID
name|uuid
range|:
name|path2Uuid
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|uuid
operator|.
name|equals
argument_list|(
name|segmentId
operator|.
name|asUUID
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|File
argument_list|(
name|path2Uuid
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

