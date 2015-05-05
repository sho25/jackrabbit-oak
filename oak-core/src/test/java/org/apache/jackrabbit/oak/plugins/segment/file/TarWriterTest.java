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
name|io
operator|.
name|File
operator|.
name|createTempFile
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
operator|.
name|allocate
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
name|nio
operator|.
name|ByteBuffer
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
name|ImmutableList
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
name|SegmentStore
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
name|SegmentTracker
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|TarWriterTest
operator|.
name|SegmentGraphBuilder
operator|.
name|Node
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|TarWriterTest
block|{
comment|/**      * Regression test for OAK-2800      */
annotation|@
name|Test
specifier|public
name|void
name|collectReferences
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentGraphBuilder
name|graphBuilder
init|=
operator|new
name|SegmentGraphBuilder
argument_list|()
decl_stmt|;
comment|// a -> b -> c
name|Node
name|c
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|Node
name|b
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"b"
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|Node
name|a
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"a"
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"n"
argument_list|)
decl_stmt|;
comment|// y -> z
name|Node
name|z
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"z"
argument_list|)
decl_stmt|;
name|Node
name|y
init|=
name|graphBuilder
operator|.
name|createNode
argument_list|(
literal|"y"
argument_list|,
name|z
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|singleton
argument_list|(
name|b
argument_list|)
argument_list|,
name|a
operator|.
name|getReferences
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|b
operator|.
name|getReferences
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|getReferences
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|singleton
argument_list|(
name|z
argument_list|)
argument_list|,
name|y
operator|.
name|getReferences
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|z
operator|.
name|getReferences
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tar
init|=
name|createTempFile
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"tar"
argument_list|)
decl_stmt|;
name|TarWriter
name|tarWriter
init|=
operator|new
name|TarWriter
argument_list|(
name|tar
argument_list|)
decl_stmt|;
try|try
block|{
name|y
operator|.
name|write
argument_list|(
name|tarWriter
argument_list|)
expr_stmt|;
name|b
operator|.
name|write
argument_list|(
name|tarWriter
argument_list|)
expr_stmt|;
name|a
operator|.
name|write
argument_list|(
name|tarWriter
argument_list|)
expr_stmt|;
name|n
operator|.
name|write
argument_list|(
name|tarWriter
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|references
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|references
operator|.
name|add
argument_list|(
name|a
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c
operator|+
literal|" must be in references as "
operator|+
name|a
operator|+
literal|" has an transitive reference to "
operator|+
name|c
operator|+
literal|" through "
operator|+
name|b
operator|+
literal|", "
operator|+
name|a
operator|+
literal|" must not be in references as "
operator|+
name|a
operator|+
literal|" is in the TarWriter, "
operator|+
literal|"no other elements must be in references."
argument_list|,
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|toNodes
argument_list|(
name|graphBuilder
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|b
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
operator|+
literal|" must be in references as "
operator|+
name|a
operator|+
literal|" has a direct reference to "
operator|+
name|b
operator|+
literal|", "
operator|+
name|a
operator|+
literal|" must not be in references as "
operator|+
name|a
operator|+
literal|" is in the TarWriter, "
operator|+
literal|"no other elements must be in references."
argument_list|,
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|toNodes
argument_list|(
name|graphBuilder
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|y
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|z
operator|+
literal|" must be in references as "
operator|+
name|y
operator|+
literal|" has a direct reference to "
operator|+
name|z
operator|+
literal|", "
operator|+
name|y
operator|+
literal|" must not be in references as "
operator|+
name|y
operator|+
literal|" is in the TarWriter, "
operator|+
literal|"no other elements must be in references."
argument_list|,
name|singleton
argument_list|(
name|z
argument_list|)
argument_list|,
name|toNodes
argument_list|(
name|graphBuilder
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|c
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c
operator|+
literal|" must be in references as "
operator|+
name|c
operator|+
literal|" is not in the TarWriter, "
operator|+
literal|"no other elements must be in references."
argument_list|,
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|toNodes
argument_list|(
name|graphBuilder
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|z
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|z
operator|+
literal|" must be in references as "
operator|+
name|z
operator|+
literal|" is not in the TarWriter "
operator|+
literal|"no other elements must be in references."
argument_list|,
name|singleton
argument_list|(
name|z
argument_list|)
argument_list|,
name|toNodes
argument_list|(
name|graphBuilder
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|n
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|tarWriter
operator|.
name|collectReferences
argument_list|(
name|references
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"references must be empty as "
operator|+
name|n
operator|+
literal|" has no references "
operator|+
literal|"and "
operator|+
name|n
operator|+
literal|" is in the TarWriter"
argument_list|,
name|references
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tarWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|Node
argument_list|>
name|toNodes
parameter_list|(
name|SegmentGraphBuilder
name|graphBuilder
parameter_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
parameter_list|)
block|{
name|Set
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|uuid
range|:
name|uuids
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|graphBuilder
operator|.
name|getNode
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
specifier|public
specifier|static
class|class
name|SegmentGraphBuilder
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|SegmentId
argument_list|,
name|ByteBuffer
argument_list|>
name|segments
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Node
argument_list|>
name|nodes
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buffer
init|=
name|allocate
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|segments
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|SegmentTracker
name|tracker
init|=
operator|new
name|SegmentTracker
argument_list|(
name|store
argument_list|,
name|V_11
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|tracker
argument_list|,
name|V_11
argument_list|)
decl_stmt|;
specifier|private
name|int
name|nextNodeNo
decl_stmt|;
specifier|public
class|class
name|Node
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|RecordId
name|selfId
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|final
name|Segment
name|segment
decl_stmt|;
name|Node
parameter_list|(
name|String
name|name
parameter_list|,
name|RecordId
name|selfId
parameter_list|,
name|ByteBuffer
name|data
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|selfId
operator|=
name|selfId
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
operator|.
name|array
argument_list|()
expr_stmt|;
name|segment
operator|=
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|selfId
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|TarWriter
name|tarWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|msb
init|=
name|getSegmentId
argument_list|()
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|getSegmentId
argument_list|()
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|tarWriter
operator|.
name|writeEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UUID
name|getUUID
parameter_list|()
block|{
return|return
name|newUUID
argument_list|(
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|SegmentId
name|getSegmentId
parameter_list|()
block|{
return|return
name|selfId
operator|.
name|getSegmentId
argument_list|()
return|;
block|}
specifier|public
name|Set
argument_list|<
name|Node
argument_list|>
name|getReferences
parameter_list|()
block|{
name|Set
argument_list|<
name|Node
argument_list|>
name|references
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|segmentId
range|:
name|segment
operator|.
name|getReferencedIds
argument_list|()
control|)
block|{
name|references
operator|.
name|add
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
name|newUUID
argument_list|(
name|segmentId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|references
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|references
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
name|void
name|addReference
parameter_list|(
name|SegmentWriter
name|writer
parameter_list|)
block|{
comment|// Need to write a proper list as singleton lists are optimised
comment|// to just returning the recordId of its single element
name|writer
operator|.
name|writeList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|selfId
argument_list|,
name|selfId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Node
name|createNode
parameter_list|(
name|String
name|name
parameter_list|,
name|Node
modifier|...
name|refs
parameter_list|)
block|{
name|RecordId
name|selfId
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|"id-"
operator|+
name|nextNodeNo
operator|++
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|ref
range|:
name|refs
control|)
block|{
name|ref
operator|.
name|addReference
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|SegmentId
name|segmentId
init|=
name|selfId
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
operator|new
name|Node
argument_list|(
name|name
argument_list|,
name|selfId
argument_list|,
name|segments
operator|.
name|get
argument_list|(
name|segmentId
argument_list|)
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|newUUID
argument_list|(
name|segmentId
argument_list|)
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|getNode
parameter_list|(
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|UUID
name|newUUID
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
block|{
return|return
operator|new
name|UUID
argument_list|(
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

