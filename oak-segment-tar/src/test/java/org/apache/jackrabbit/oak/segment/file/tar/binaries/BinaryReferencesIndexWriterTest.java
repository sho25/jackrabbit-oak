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
operator|.
name|file
operator|.
name|tar
operator|.
name|binaries
package|;
end_package

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
name|binaries
operator|.
name|BinaryReferencesIndexLoader
operator|.
name|loadBinaryReferencesIndex
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
name|tar
operator|.
name|binaries
operator|.
name|BinaryReferencesIndexLoader
operator|.
name|parseBinaryReferencesIndex
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
name|tar
operator|.
name|binaries
operator|.
name|BinaryReferencesIndexWriter
operator|.
name|newBinaryReferencesIndexWriter
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
name|util
operator|.
name|HashMap
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|Buffer
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
name|BinaryReferencesIndexWriterTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|s1
init|=
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|UUID
name|s2
init|=
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|UUID
name|s3
init|=
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|UUID
name|s4
init|=
operator|new
name|UUID
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|BinaryReferencesIndexWriter
name|writer
init|=
name|newBinaryReferencesIndexWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|s1
argument_list|,
literal|"1.1.1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|s1
argument_list|,
literal|"1.1.2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|s2
argument_list|,
literal|"1.2.1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|s2
argument_list|,
literal|"1.2.2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|s3
argument_list|,
literal|"2.1.1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|s3
argument_list|,
literal|"2.1.2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|s4
argument_list|,
literal|"2.2.1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|s4
argument_list|,
literal|"2.2.2"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|writer
operator|.
name|write
argument_list|()
decl_stmt|;
name|Buffer
name|buffer
init|=
name|loadBinaryReferencesIndex
argument_list|(
parameter_list|(
name|whence
parameter_list|,
name|length
parameter_list|)
lambda|->
name|Buffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
operator|-
name|whence
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|BinaryReferencesIndex
name|index
init|=
name|parseBinaryReferencesIndex
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|Generation
name|g1
init|=
operator|new
name|Generation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Generation
name|g2
init|=
operator|new
name|Generation
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Generation
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
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|g1
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|g2
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g1
argument_list|)
operator|.
name|put
argument_list|(
name|s1
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g1
argument_list|)
operator|.
name|put
argument_list|(
name|s2
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g2
argument_list|)
operator|.
name|put
argument_list|(
name|s3
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g2
argument_list|)
operator|.
name|put
argument_list|(
name|s4
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g1
argument_list|)
operator|.
name|get
argument_list|(
name|s1
argument_list|)
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
literal|"1.1.1"
argument_list|,
literal|"1.1.2"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g1
argument_list|)
operator|.
name|get
argument_list|(
name|s2
argument_list|)
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
literal|"1.2.1"
argument_list|,
literal|"1.2.2"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g2
argument_list|)
operator|.
name|get
argument_list|(
name|s3
argument_list|)
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
literal|"2.1.1"
argument_list|,
literal|"2.1.2"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|get
argument_list|(
name|g2
argument_list|)
operator|.
name|get
argument_list|(
name|s4
argument_list|)
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
literal|"2.2.1"
argument_list|,
literal|"2.2.2"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Generation
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
name|actual
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|index
operator|.
name|forEach
argument_list|(
parameter_list|(
name|generation
parameter_list|,
name|full
parameter_list|,
name|compacted
parameter_list|,
name|id
parameter_list|,
name|reference
parameter_list|)
lambda|->
block|{
name|actual
operator|.
name|computeIfAbsent
argument_list|(
operator|new
name|Generation
argument_list|(
name|generation
argument_list|,
name|full
argument_list|,
name|compacted
argument_list|)
argument_list|,
name|k
lambda|->
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|computeIfAbsent
argument_list|(
name|id
argument_list|,
name|k
lambda|->
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

