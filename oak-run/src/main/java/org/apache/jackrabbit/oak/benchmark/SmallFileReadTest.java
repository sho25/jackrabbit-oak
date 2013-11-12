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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|io
operator|.
name|ByteStreams
import|;
end_import

begin_class
specifier|public
class|class
name|SmallFileReadTest
extends|extends
name|AbstractTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|FILE_COUNT
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|10
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|root
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
name|TEST_ID
argument_list|,
literal|"nt:folder"
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
name|FILE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|file
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"file"
operator|+
name|i
argument_list|,
literal|"nt:file"
argument_list|)
decl_stmt|;
name|Node
name|content
init|=
name|file
operator|.
name|addNode
argument_list|(
literal|"jcr:content"
argument_list|,
literal|"nt:resource"
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:mimeType"
argument_list|,
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
operator|new
name|TestInputStream
argument_list|(
name|FILE_SIZE
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FILE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|file
init|=
name|root
operator|.
name|getNode
argument_list|(
literal|"file"
operator|+
name|i
argument_list|)
decl_stmt|;
name|Node
name|content
init|=
name|file
operator|.
name|getNode
argument_list|(
literal|"jcr:content"
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|content
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
operator|.
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
name|ByteStreams
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|ByteStreams
operator|.
name|nullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|root
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

