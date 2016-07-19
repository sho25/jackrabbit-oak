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
name|segment
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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
name|assertNotEquals
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
name|assertNotNull
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
name|Supplier
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
name|EmptyNodeState
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
name|NodeRecordTest
block|{
specifier|private
specifier|static
class|class
name|Generation
implements|implements
name|Supplier
argument_list|<
name|Integer
argument_list|>
block|{
specifier|private
name|int
name|generation
decl_stmt|;
specifier|public
name|void
name|set
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|get
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
block|}
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|root
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
name|FileStore
name|newFileStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|unreferencedNodeRecordShouldBeRoot
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|SegmentWriter
name|writer
init|=
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|state
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|isRootRecord
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|stableIdShouldPersistAcrossGenerations
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|SegmentWriter
name|writer
decl_stmt|;
name|writer
operator|=
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
argument_list|(
literal|"1"
argument_list|)
operator|.
name|withGeneration
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|one
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|=
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
argument_list|(
literal|"2"
argument_list|)
operator|.
name|withGeneration
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|two
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|one
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|=
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
argument_list|(
literal|"3"
argument_list|)
operator|.
name|withGeneration
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|three
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|two
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|three
operator|.
name|getStableIdBytes
argument_list|()
argument_list|,
name|two
operator|.
name|getStableIdBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|two
operator|.
name|getStableIdBytes
argument_list|()
argument_list|,
name|one
operator|.
name|getStableIdBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|baseNodeStateShouldBeReusedAcrossGenerations
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|Generation
name|generation
init|=
operator|new
name|Generation
argument_list|()
decl_stmt|;
comment|// Create a new SegmentWriter. It's necessary not to have any cache,
comment|// otherwise the write of some records (in this case, template
comment|// records) will be cached and prevent this test to fail.
name|SegmentWriter
name|writer
init|=
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|withGeneration
argument_list|(
name|generation
argument_list|)
operator|.
name|withWriterPool
argument_list|()
operator|.
name|with
argument_list|(
name|nodesOnlyCache
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|generation
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Write a new node with a non trivial template. This record will
comment|// belong to generation 1.
name|SegmentNodeState
name|base
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"k"
argument_list|,
literal|"v1"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|generation
operator|.
name|set
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// Compact that same record to generation 2.
name|SegmentNodeState
name|compacted
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Assert that even if the two records have the same stable ID,
comment|// their physical ID and the ID of their templates are different.
name|assertEquals
argument_list|(
name|base
operator|.
name|getStableId
argument_list|()
argument_list|,
name|compacted
operator|.
name|getStableId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|compacted
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|base
operator|.
name|getTemplateId
argument_list|()
argument_list|,
name|compacted
operator|.
name|getTemplateId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create a new builder from the base, pre-compaction node state.
comment|// The base node state is from generation 1, but this builder will
comment|// be from generation 2 because every builder in the pool is
comment|// affected by the change of generation. Writing a node state from
comment|// this builder should perform a partial compaction.
name|SegmentNodeState
name|modified
init|=
operator|(
name|SegmentNodeState
operator|)
name|base
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"k"
argument_list|,
literal|"v2"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Assert that the stable ID of this node state is different from
comment|// the one in the base state. This is expected, since we have
comment|// modified the value of a property.
name|assertNotEquals
argument_list|(
name|modified
operator|.
name|getStableId
argument_list|()
argument_list|,
name|base
operator|.
name|getStableId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|modified
operator|.
name|getStableId
argument_list|()
argument_list|,
name|compacted
operator|.
name|getStableId
argument_list|()
argument_list|)
expr_stmt|;
comment|// The node state should have reused the template from the compacted
comment|// node state, since this template didn't change and the code should
comment|// have detected that the base state of this builder was compacted
comment|// to a new generation.
name|assertEquals
argument_list|(
name|modified
operator|.
name|getTemplateId
argument_list|()
argument_list|,
name|compacted
operator|.
name|getTemplateId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Similarly the node state should have reused the property from
comment|// the compacted node state, since this property didn't change.
name|Record
name|modifiedProperty
init|=
operator|(
name|Record
operator|)
name|modified
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Record
name|compactedProperty
init|=
operator|(
name|Record
operator|)
name|compacted
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|modifiedProperty
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|compactedProperty
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|modifiedProperty
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|compactedProperty
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|WriterCacheManager
name|nodesOnlyCache
parameter_list|()
block|{
return|return
operator|new
name|WriterCacheManager
argument_list|()
block|{
name|WriterCacheManager
name|defaultCache
init|=
operator|new
name|WriterCacheManager
operator|.
name|Default
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|String
argument_list|>
name|getStringCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|Empty
operator|.
name|INSTANCE
operator|.
name|getStringCache
argument_list|(
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|Template
argument_list|>
name|getTemplateCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|Empty
operator|.
name|INSTANCE
operator|.
name|getTemplateCache
argument_list|(
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeCache
name|getNodeCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|defaultCache
operator|.
name|getNodeCache
argument_list|(
name|generation
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
name|boolean
name|isRootRecord
parameter_list|(
name|SegmentNodeState
name|sns
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|sns
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegment
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segment
operator|.
name|getRootCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|segment
operator|.
name|getRootType
argument_list|(
name|i
argument_list|)
operator|!=
name|RecordType
operator|.
name|NODE
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|segment
operator|.
name|getRootOffset
argument_list|(
name|i
argument_list|)
operator|!=
name|sns
operator|.
name|getRecordId
argument_list|()
operator|.
name|getOffset
argument_list|()
condition|)
block|{
continue|continue;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

