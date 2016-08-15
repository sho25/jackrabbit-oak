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
name|plugins
operator|.
name|segment
operator|.
name|file
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
name|Maps
operator|.
name|newTreeMap
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
name|difference
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
import|import static
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
operator|.
name|FileStoreHelper
operator|.
name|readRevisions
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
name|FilenameFilter
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
name|SortedMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|plugins
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
name|plugins
operator|.
name|segment
operator|.
name|Segment
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
name|plugins
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
name|plugins
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentVersion
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
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
import|;
end_import

begin_class
specifier|public
class|class
name|FileStoreRevisionRecovery
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"java -jar oak-run-*.jar tarmkrecovery<path/to/repository> [--version-v10]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeO
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"Path to segment store (required)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|v10
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"version-v10"
argument_list|,
literal|"Use V10 for reading"
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|File
name|store
init|=
name|storeO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|SegmentVersion
name|version
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|v10
argument_list|)
condition|)
block|{
name|version
operator|=
name|SegmentVersion
operator|.
name|V_10
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Store(V10) "
operator|+
name|store
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|version
operator|=
name|SegmentVersion
operator|.
name|V_11
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Store "
operator|+
name|store
argument_list|)
expr_stmt|;
block|}
name|SortedMap
argument_list|<
name|String
argument_list|,
name|UUID
argument_list|>
name|candidates
init|=
name|candidateSegments
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|roots
init|=
name|extractRoots
argument_list|(
name|store
argument_list|,
name|candidates
argument_list|,
name|version
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|r
range|:
name|roots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|r
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// + " @ " + r.getKey());
block|}
block|}
specifier|private
specifier|static
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extractRoots
parameter_list|(
name|File
name|dir
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|UUID
argument_list|>
name|candidates
parameter_list|,
specifier|final
name|SegmentVersion
name|version
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|ReadOnlyStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|dir
argument_list|)
operator|.
name|withSegmentVersion
argument_list|(
name|version
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|roots
init|=
name|newTreeMap
argument_list|(
name|reverseOrder
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|UUID
argument_list|>
name|c
range|:
name|candidates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|UUID
name|uid
init|=
name|c
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
operator|new
name|SegmentId
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|uid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|uid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Segment
name|s
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
name|s
operator|.
name|getRootCount
argument_list|()
condition|;
name|r
operator|++
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getRootType
argument_list|(
name|r
argument_list|)
operator|==
name|RecordType
operator|.
name|NODE
condition|)
block|{
name|int
name|offset
init|=
name|s
operator|.
name|getRootOffset
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|RecordId
name|nodeId
init|=
operator|new
name|RecordId
argument_list|(
name|s
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|isRoot
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
name|roots
operator|.
name|put
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|+
literal|"."
operator|+
name|offset
argument_list|,
name|nodeId
operator|.
name|toString10
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|roots
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|ROOT_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"root"
argument_list|,
literal|"checkpoints"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|isRoot
parameter_list|(
name|RecordId
name|nodeId
parameter_list|)
block|{
name|SegmentNodeState
name|sns
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|sns
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|sns
operator|.
name|getPropertyCount
argument_list|()
operator|==
literal|0
operator|&&
name|childNames
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|difference
argument_list|(
name|ROOT_NAMES
argument_list|,
name|childNames
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|SortedMap
argument_list|<
name|String
argument_list|,
name|UUID
argument_list|>
name|candidateSegments
parameter_list|(
name|File
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|revs
init|=
name|readRevisions
argument_list|(
name|store
argument_list|)
decl_stmt|;
if|if
condition|(
name|revs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No revisions found."
argument_list|)
expr_stmt|;
return|return
name|newTreeMap
argument_list|()
return|;
block|}
name|String
name|head
init|=
name|revs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Current head revision "
operator|+
name|head
argument_list|)
expr_stmt|;
specifier|final
name|UUID
name|headSegment
init|=
name|extractSegmentId
argument_list|(
name|head
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tars
init|=
name|listTars
argument_list|(
name|store
argument_list|)
decl_stmt|;
comment|//<tar+offset, UUID>
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|UUID
argument_list|>
name|candidates
init|=
name|newTreeMap
argument_list|(
name|reverseOrder
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|tar
range|:
name|tars
control|)
block|{
specifier|final
name|AtomicLong
name|threshold
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|TarReader
name|r
init|=
name|TarReader
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|store
argument_list|,
name|tar
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// first identify the offset beyond which we need to include
comment|// segments
name|r
operator|.
name|accept
argument_list|(
operator|new
name|TarEntryVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|File
name|file
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|msb
operator|==
name|headSegment
operator|.
name|getMostSignificantBits
argument_list|()
operator|&&
name|lsb
operator|==
name|headSegment
operator|.
name|getLeastSignificantBits
argument_list|()
condition|)
block|{
name|threshold
operator|.
name|set
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|r
operator|.
name|accept
argument_list|(
operator|new
name|TarEntryVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|File
name|file
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|>=
name|threshold
operator|.
name|get
argument_list|()
operator|&&
name|SegmentId
operator|.
name|isDataSegmentId
argument_list|(
name|lsb
argument_list|)
condition|)
block|{
name|candidates
operator|.
name|put
argument_list|(
name|tar
operator|+
literal|"."
operator|+
name|offset
argument_list|,
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|threshold
operator|.
name|get
argument_list|()
operator|>=
literal|0
condition|)
block|{
break|break;
block|}
block|}
return|return
name|candidates
return|;
block|}
specifier|private
specifier|static
name|UUID
name|extractSegmentId
parameter_list|(
name|String
name|record
parameter_list|)
throws|throws
name|IOException
block|{
name|RecordId
name|head
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
operator|new
name|MemoryStore
argument_list|()
operator|.
name|getTracker
argument_list|()
argument_list|,
name|record
argument_list|)
decl_stmt|;
return|return
name|UUID
operator|.
name|fromString
argument_list|(
name|head
operator|.
name|getSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|listTars
parameter_list|(
name|File
name|store
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|asList
argument_list|(
name|store
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
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
block|}
end_class

end_unit

