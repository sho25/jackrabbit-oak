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
name|newHashMap
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
name|singleton
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|SegmentGraph
operator|.
name|createRegExpFilter
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
name|SegmentGraph
operator|.
name|parseSegmentGraph
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
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|base
operator|.
name|Predicate
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
name|Predicates
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
name|ImmutableMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multiset
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
name|SegmentGraph
operator|.
name|Graph
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentGraphTest
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|segments
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|references
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|filteredSegments
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|filteredReferences
init|=
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|storeFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
name|File
name|getStoreFolder
parameter_list|()
block|{
return|return
name|storeFolder
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|getStoreFolder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|SegmentNodeState
name|root
init|=
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
decl_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentWriter
name|w1
init|=
name|segmentWriterBuilder
argument_list|(
literal|"writer1"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SegmentWriter
name|w2
init|=
name|segmentWriterBuilder
argument_list|(
literal|"writer2"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SegmentWriter
name|w3
init|=
name|segmentWriterBuilder
argument_list|(
literal|"writer3"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SegmentPropertyState
name|p1
init|=
name|w1
operator|.
name|writeProperty
argument_list|(
name|createProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
decl_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentPropertyState
name|p2
init|=
name|w2
operator|.
name|writeProperty
argument_list|(
name|createProperty
argument_list|(
literal|"p2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
decl_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|filteredSegments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentPropertyState
name|p3
init|=
name|w3
operator|.
name|writeProperty
argument_list|(
name|createProperty
argument_list|(
literal|"p3"
argument_list|,
literal|"v3"
argument_list|)
argument_list|)
decl_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|p3
argument_list|)
argument_list|)
expr_stmt|;
name|filteredSegments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|p3
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|p2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|p3
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|n3
init|=
name|w3
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|n3
argument_list|)
argument_list|)
expr_stmt|;
name|filteredSegments
operator|.
name|add
argument_list|(
name|getSegmentId
argument_list|(
name|n3
argument_list|)
argument_list|)
expr_stmt|;
name|addReference
argument_list|(
name|references
argument_list|,
name|getSegmentId
argument_list|(
name|n3
argument_list|)
argument_list|,
name|getSegmentId
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|addReference
argument_list|(
name|references
argument_list|,
name|getSegmentId
argument_list|(
name|n3
argument_list|)
argument_list|,
name|getSegmentId
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|addReference
argument_list|(
name|filteredReferences
argument_list|,
name|getSegmentId
argument_list|(
name|n3
argument_list|)
argument_list|,
name|getSegmentId
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cyclic reference
name|SegmentNodeState
name|n1
init|=
name|w1
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|addReference
argument_list|(
name|references
argument_list|,
name|getSegmentId
argument_list|(
name|n1
argument_list|)
argument_list|,
name|getSegmentId
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|addReference
argument_list|(
name|references
argument_list|,
name|getSegmentId
argument_list|(
name|n1
argument_list|)
argument_list|,
name|getSegmentId
argument_list|(
name|p3
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|root
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|n3
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|w1
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w2
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w3
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addReference
parameter_list|(
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|references
parameter_list|,
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|tos
init|=
name|references
operator|.
name|get
argument_list|(
name|from
argument_list|)
decl_stmt|;
if|if
condition|(
name|tos
operator|==
literal|null
condition|)
block|{
name|tos
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
name|references
operator|.
name|put
argument_list|(
name|from
argument_list|,
name|tos
argument_list|)
expr_stmt|;
block|}
name|tos
operator|.
name|add
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|UUID
name|getSegmentId
parameter_list|(
name|SegmentPropertyState
name|p1
parameter_list|)
block|{
return|return
name|p1
operator|.
name|getSegment
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
specifier|static
name|UUID
name|getSegmentId
parameter_list|(
name|SegmentNodeState
name|root
parameter_list|)
block|{
return|return
name|root
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSegmentGraph
parameter_list|()
throws|throws
name|IOException
block|{
name|ReadOnlyStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|getStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
try|try
block|{
name|Graph
argument_list|<
name|UUID
argument_list|>
name|segmentGraph
init|=
name|parseSegmentGraph
argument_list|(
name|store
argument_list|,
name|Predicates
operator|.
expr|<
name|UUID
operator|>
name|alwaysTrue
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|segments
argument_list|,
name|newHashSet
argument_list|(
name|segmentGraph
operator|.
name|vertices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Multiset
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|entry
range|:
name|segmentGraph
operator|.
name|edges
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|elementSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|references
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
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
name|testSegmentGraphWithFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|ReadOnlyStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|getStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
try|try
block|{
name|Predicate
argument_list|<
name|UUID
argument_list|>
name|filter
init|=
name|createRegExpFilter
argument_list|(
literal|".*(writer2|writer3).*"
argument_list|,
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
decl_stmt|;
name|Graph
argument_list|<
name|UUID
argument_list|>
name|segmentGraph
init|=
name|parseSegmentGraph
argument_list|(
name|store
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|filteredSegments
argument_list|,
name|newHashSet
argument_list|(
name|segmentGraph
operator|.
name|vertices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Multiset
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|entry
range|:
name|segmentGraph
operator|.
name|edges
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|elementSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|filteredReferences
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
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
name|testGCGraph
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO Improve test coverage to non trivial cases with more than a single generation
comment|// This is quite tricky as there is no easy way to construct a file store with
comment|// a segment graphs having edges between generations (OAK-3348)
name|ReadOnlyStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|getStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
try|try
block|{
name|Graph
argument_list|<
name|String
argument_list|>
name|gcGraph
init|=
name|SegmentGraph
operator|.
name|parseGCGraph
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"0"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
name|gcGraph
operator|.
name|vertices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Multiset
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|gcGraph
operator|.
name|edges
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|elementSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"0"
argument_list|,
name|singleton
argument_list|(
literal|"0"
argument_list|)
argument_list|)
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

