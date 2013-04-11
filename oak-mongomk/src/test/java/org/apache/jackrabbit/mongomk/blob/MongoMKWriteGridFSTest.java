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
name|mongomk
operator|.
name|blob
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests for {@code MongoMicroKernel#write(java.io.InputStream)}  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKWriteGridFSTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|small
parameter_list|()
throws|throws
name|Exception
block|{
name|write
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|medium
parameter_list|()
throws|throws
name|Exception
block|{
name|write
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|large
parameter_list|()
throws|throws
name|Exception
block|{
name|write
argument_list|(
literal|20
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|write
parameter_list|(
name|int
name|blobLength
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|blob
init|=
name|createBlob
argument_list|(
name|blobLength
argument_list|)
decl_stmt|;
name|String
name|blobId
init|=
name|mk
operator|.
name|write
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|blob
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
name|byte
index|[]
name|readBlob
init|=
operator|new
name|byte
index|[
name|blobLength
index|]
decl_stmt|;
name|mk
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
literal|0
argument_list|,
name|readBlob
argument_list|,
literal|0
argument_list|,
name|readBlob
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blob
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|blob
index|[
name|i
index|]
operator|!=
name|readBlob
index|[
name|i
index|]
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" "
operator|+
name|blob
index|[
name|i
index|]
operator|+
literal|"==>"
operator|+
name|readBlob
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|blob
argument_list|,
name|readBlob
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|createBlob
parameter_list|(
name|int
name|blobLength
parameter_list|)
block|{
name|byte
index|[]
name|blob
init|=
operator|new
name|byte
index|[
name|blobLength
index|]
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
name|blob
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blob
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
return|return
name|blob
return|;
block|}
block|}
end_class

end_unit

