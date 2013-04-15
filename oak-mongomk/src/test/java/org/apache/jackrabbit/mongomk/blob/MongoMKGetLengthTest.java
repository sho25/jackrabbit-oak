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
name|fail
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|AbstractMongoConnectionTest
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
comment|/**  * Tests for {@code MongoMicroKernel#getLength(String)}  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKGetLengthTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|nonExistent
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|mk
operator|.
name|getLength
argument_list|(
literal|"nonExistentBlob"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|small
parameter_list|()
throws|throws
name|Exception
block|{
name|getLength
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
name|getLength
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|large
parameter_list|()
throws|throws
name|Exception
block|{
name|getLength
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
name|getLength
parameter_list|(
name|int
name|blobLength
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|blobId
init|=
name|createAndWriteBlob
argument_list|(
name|blobLength
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|mk
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobLength
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|createAndWriteBlob
parameter_list|(
name|int
name|blobLength
parameter_list|)
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
return|return
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
return|;
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

