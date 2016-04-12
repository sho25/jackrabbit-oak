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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|deleteDirectory
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
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|api
operator|.
name|Type
operator|.
name|LONGS
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|SegmentVersion
operator|.
name|LATEST_VERSION
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
name|SegmentVersion
operator|.
name|V_10
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
name|SegmentVersion
operator|.
name|V_11
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
name|compaction
operator|.
name|CompactionStrategy
operator|.
name|CleanupType
operator|.
name|CLEAN_NONE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|concurrent
operator|.
name|Callable
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
name|collect
operator|.
name|ImmutableList
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
name|Lists
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
name|plugins
operator|.
name|segment
operator|.
name|compaction
operator|.
name|CompactionStrategy
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
name|EmptyHook
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
name|ChildNodeEntry
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
name|NodeStateDiff
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|SegmentVersionTest
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
name|SegmentVersionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"VersionTest"
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanDir
parameter_list|()
block|{
try|try
block|{
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error cleaning directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|latestVersion
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|V_11
argument_list|,
name|LATEST_VERSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compareOldRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|fileStoreV10
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withSegmentVersion
argument_list|(
name|V_10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeState
name|content
init|=
name|addTestContent
argument_list|(
name|fileStoreV10
argument_list|,
literal|"content"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|assertVersion
argument_list|(
name|content
argument_list|,
name|SegmentVersion
operator|.
name|V_10
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|content
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|content
operator|.
name|compareAgainstBaseState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV10
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|readOldVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|fileStoreV10
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withSegmentVersion
argument_list|(
name|V_10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeState
name|content
init|=
name|addTestContent
argument_list|(
name|fileStoreV10
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|assertVersion
argument_list|(
name|content
argument_list|,
name|SegmentVersion
operator|.
name|V_10
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV10
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileStore
name|fileStoreV11
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|verifyContent
argument_list|(
name|fileStoreV11
argument_list|,
literal|"content"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV11
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|mixedVersions
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|FileStore
name|fileStoreV10
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withSegmentVersion
argument_list|(
name|V_10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeState
name|content10
init|=
name|addTestContent
argument_list|(
name|fileStoreV10
argument_list|,
literal|"content10"
argument_list|)
decl_stmt|;
name|assertVersion
argument_list|(
name|content10
argument_list|,
name|SegmentVersion
operator|.
name|V_10
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV10
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileStore
name|fileStoreV11
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeState
name|content11
init|=
name|addTestContent
argument_list|(
name|fileStoreV11
argument_list|,
literal|"content11"
argument_list|)
decl_stmt|;
name|assertVersion
argument_list|(
name|content11
argument_list|,
name|V_11
argument_list|)
expr_stmt|;
name|verifyContent
argument_list|(
name|fileStoreV11
argument_list|,
literal|"content10"
argument_list|)
expr_stmt|;
name|verifyContent
argument_list|(
name|fileStoreV11
argument_list|,
literal|"content11"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV11
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|migrate
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|FileStore
name|fileStoreV10
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withSegmentVersion
argument_list|(
name|V_10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|addTestContent
argument_list|(
name|fileStoreV10
argument_list|,
literal|"content10"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV10
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileStore
name|fileStoreV11
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|fileStoreV11
operator|.
name|setCompactionStrategy
argument_list|(
operator|new
name|CompactionStrategy
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|CLEAN_NONE
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|setHead
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|setHead
operator|.
name|call
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|checkAllVersions
argument_list|(
name|fileStoreV11
operator|.
name|getHead
argument_list|()
argument_list|,
name|SegmentVersion
operator|.
name|V_10
argument_list|)
expr_stmt|;
name|fileStoreV11
operator|.
name|compact
argument_list|()
expr_stmt|;
name|checkAllVersions
argument_list|(
name|fileStoreV11
operator|.
name|getHead
argument_list|()
argument_list|,
name|V_11
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStoreV11
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|checkAllVersions
parameter_list|(
name|SegmentNodeState
name|head
parameter_list|,
name|SegmentVersion
name|version
parameter_list|)
block|{
name|assertVersion
argument_list|(
name|head
argument_list|,
name|version
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|childNodeEntry
range|:
name|head
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|checkAllVersions
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|childNodeEntry
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertVersion
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|SegmentVersion
name|version
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|node
operator|instanceof
name|SegmentNodeState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|version
argument_list|,
operator|(
operator|(
name|SegmentNodeState
operator|)
name|node
operator|)
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
specifier|static
name|NodeState
name|addTestContent
parameter_list|(
name|FileStore
name|fs
parameter_list|,
name|String
name|nodeName
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|content
init|=
name|builder
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"aM"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|3L
argument_list|,
literal|4L
argument_list|)
argument_list|,
name|LONGS
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"azerty"
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"bM"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"z"
argument_list|,
literal|"e"
argument_list|,
literal|"r"
argument_list|,
literal|"t"
argument_list|,
literal|"y"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
comment|// add blobs?
return|return
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|verifyContent
parameter_list|(
name|FileStore
name|fs
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|NodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|content
init|=
operator|(
name|SegmentNodeState
operator|)
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1
argument_list|)
argument_list|,
name|content
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|3L
argument_list|,
literal|4L
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|content
operator|.
name|getProperty
argument_list|(
literal|"aM"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"azerty"
argument_list|,
name|content
operator|.
name|getProperty
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"azerty"
argument_list|,
name|content
operator|.
name|getString
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"z"
argument_list|,
literal|"e"
argument_list|,
literal|"r"
argument_list|,
literal|"t"
argument_list|,
literal|"y"
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|content
operator|.
name|getProperty
argument_list|(
literal|"bM"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"z"
argument_list|,
literal|"e"
argument_list|,
literal|"r"
argument_list|,
literal|"t"
argument_list|,
literal|"y"
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|content
operator|.
name|getStrings
argument_list|(
literal|"bM"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

