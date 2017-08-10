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
name|index
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
name|file
operator|.
name|tar
operator|.
name|index
operator|.
name|IndexWriter
operator|.
name|newIndexWriter
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
name|assertArrayEquals
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
name|IndexWriterTest
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
name|IndexWriter
name|writer
init|=
name|newIndexWriter
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|,
literal|12
argument_list|,
literal|true
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
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV2
operator|.
name|SIZE
operator|+
name|IndexV2
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putLong
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|3
argument_list|)
operator|.
name|putInt
argument_list|(
literal|4
argument_list|)
operator|.
name|putInt
argument_list|(
literal|5
argument_list|)
operator|.
name|putInt
argument_list|(
literal|6
argument_list|)
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|7
argument_list|)
operator|.
name|putLong
argument_list|(
literal|8
argument_list|)
operator|.
name|putInt
argument_list|(
literal|9
argument_list|)
operator|.
name|putInt
argument_list|(
literal|10
argument_list|)
operator|.
name|putInt
argument_list|(
literal|11
argument_list|)
operator|.
name|putInt
argument_list|(
literal|12
argument_list|)
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0xE2138EB4
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
name|IndexEntryV2
operator|.
name|SIZE
operator|+
name|IndexV2
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV2
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|writer
operator|.
name|write
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPadding
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
name|newIndexWriter
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addEntry
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|,
literal|12
argument_list|,
literal|true
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
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|dataSize
init|=
literal|2
operator|*
name|IndexEntryV2
operator|.
name|SIZE
operator|+
name|IndexV2
operator|.
name|FOOTER_SIZE
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|ByteBuffer
name|duplicate
init|=
name|buffer
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|duplicate
operator|.
name|position
argument_list|(
name|duplicate
operator|.
name|limit
argument_list|()
operator|-
name|dataSize
argument_list|)
expr_stmt|;
name|duplicate
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putLong
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|3
argument_list|)
operator|.
name|putInt
argument_list|(
literal|4
argument_list|)
operator|.
name|putInt
argument_list|(
literal|5
argument_list|)
operator|.
name|putInt
argument_list|(
literal|6
argument_list|)
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|7
argument_list|)
operator|.
name|putLong
argument_list|(
literal|8
argument_list|)
operator|.
name|putInt
argument_list|(
literal|9
argument_list|)
operator|.
name|putInt
argument_list|(
literal|10
argument_list|)
operator|.
name|putInt
argument_list|(
literal|11
argument_list|)
operator|.
name|putInt
argument_list|(
literal|12
argument_list|)
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0xE2138EB4
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|256
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV2
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|writer
operator|.
name|write
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
