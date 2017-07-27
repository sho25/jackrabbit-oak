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
name|file
operator|.
name|tar
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
name|Charsets
operator|.
name|UTF_8
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
name|Maps
operator|.
name|newHashMap
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|collections
operator|.
name|Sets
operator|.
name|newSet
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
name|TarFileTest
block|{
specifier|private
specifier|static
name|GCGeneration
name|generation
parameter_list|(
name|int
name|full
parameter_list|)
block|{
return|return
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
name|full
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|File
name|file
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
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
name|file
operator|=
name|folder
operator|.
name|newFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteAndRead
parameter_list|()
throws|throws
name|IOException
block|{
name|UUID
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
operator|&
operator|(
operator|-
literal|1
operator|>>>
literal|4
operator|)
decl_stmt|;
comment|// OAK-1672
name|byte
index|[]
name|data
init|=
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
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
argument_list|,
name|generation
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|writer
operator|.
name|readEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5120
argument_list|,
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|reader
operator|.
name|readEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGCGeneration
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|String
name|data
init|=
literal|"test"
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|data
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|TarEntry
index|[]
name|entries
init|=
name|reader
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
name|entries
index|[
literal|0
index|]
operator|.
name|generation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGCGenerationIsTailFlagNotErased
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|String
name|data
init|=
literal|"test"
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|data
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|TarEntry
index|[]
name|entries
init|=
name|reader
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|,
name|entries
index|[
literal|0
index|]
operator|.
name|generation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteAndReadBinaryReferences
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
literal|0x00
argument_list|,
literal|0x00
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|0x01
block|,
literal|0x02
block|,
literal|0x3
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|generation
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"r0"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"r1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"r2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"r3"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"r4"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"r5"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"r6"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|3
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"r7"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|3
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"r8"
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|one
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|one
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r0"
argument_list|)
argument_list|)
expr_stmt|;
name|one
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r1"
argument_list|)
argument_list|)
expr_stmt|;
name|one
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r2"
argument_list|)
argument_list|)
expr_stmt|;
name|one
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r3"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|two
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|two
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r4"
argument_list|)
argument_list|)
expr_stmt|;
name|two
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r5"
argument_list|)
argument_list|)
expr_stmt|;
name|two
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r6"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|three
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|three
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r7"
argument_list|)
argument_list|)
expr_stmt|;
name|three
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"r8"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|GCGeneration
argument_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|expected
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|generation
argument_list|(
literal|3
argument_list|)
argument_list|,
name|three
argument_list|)
expr_stmt|;
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|reader
operator|.
name|getBinaryReferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|binaryReferencesIndexShouldBeTrimmedDownOnSweep
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|UUID
argument_list|>
name|sweep
init|=
name|newSet
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
name|TarReader
name|swept
init|=
name|reader
operator|.
name|sweep
argument_list|(
name|sweep
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|swept
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|one
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|one
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|two
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|two
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newSet
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|GCGeneration
argument_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|references
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|references
operator|.
name|put
argument_list|(
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|references
operator|.
name|put
argument_list|(
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|references
argument_list|,
name|swept
operator|.
name|getBinaryReferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|binaryReferencesIndexShouldContainCompleteGCGeneration
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
literal|0x00
argument_list|,
literal|0x00
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|0x01
block|,
literal|0x02
block|,
literal|0x3
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|generation
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"r1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"r2"
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|Set
argument_list|<
name|GCGeneration
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|reader
operator|.
name|getBinaryReferences
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|graphShouldBeTrimmedDownOnSweep
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeEntry
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|generation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addGraphEdge
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addGraphEdge
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addGraphEdge
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addGraphEdge
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|UUID
argument_list|>
name|sweep
init|=
name|newSet
argument_list|(
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|TarReader
name|reader
init|=
name|TarReader
operator|.
name|open
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
name|TarReader
name|swept
init|=
name|reader
operator|.
name|sweep
argument_list|(
name|sweep
argument_list|,
operator|new
name|HashSet
argument_list|<
name|UUID
argument_list|>
argument_list|()
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|swept
argument_list|)
expr_stmt|;
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
name|newHashMap
argument_list|()
decl_stmt|;
name|graph
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|graph
argument_list|,
name|swept
operator|.
name|getGraph
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

