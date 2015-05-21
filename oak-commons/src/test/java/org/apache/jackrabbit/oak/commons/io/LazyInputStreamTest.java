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
name|commons
operator|.
name|io
package|;
end_package

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
name|FileOutputStream
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
operator|.
name|asByteSource
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
name|assertFalse
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

begin_comment
comment|/**  * Tests the LazyInputStream class.  */
end_comment

begin_class
specifier|public
class|class
name|LazyInputStreamTest
block|{
specifier|private
name|File
name|file
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|createFile
argument_list|()
expr_stmt|;
comment|// test open / close (without reading)
name|LazyInputStream
name|in
init|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test reading too much and closing too much
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test markSupported, mark, and reset
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|in
operator|.
name|markSupported
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test read(byte[])
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|test
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|(
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test read(byte[],int,int)
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|(
name|test
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test skip
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|in
operator|.
name|skip
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|(
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|createFile
argument_list|()
expr_stmt|;
comment|// test that the file is closed after reading the last byte
name|in
operator|=
operator|new
name|LazyInputStream
argument_list|(
name|asByteSource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createFile
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|=
name|temporaryFolder
operator|.
name|newFile
argument_list|()
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

