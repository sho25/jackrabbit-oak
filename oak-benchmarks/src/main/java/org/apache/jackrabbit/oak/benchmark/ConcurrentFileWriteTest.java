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
name|Random
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|NullOutputStream
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
name|commons
operator|.
name|JcrUtils
import|;
end_import

begin_comment
comment|/**  * Test case that writes blobs concurrently and concurrently reads  * the blobs back when available.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentFileWriteTest
extends|extends
name|AbstractTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileSize"
argument_list|,
literal|1900
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|WRITERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileWriters"
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|READERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileReaders"
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
literal|"concurrentFileWriteTest"
operator|+
name|TEST_ID
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|Writer
name|writer
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
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|Writer
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|run
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|WRITERS
condition|;
name|i
operator|++
control|)
block|{
name|addBackgroundJob
argument_list|(
operator|new
name|Writer
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|READERS
condition|;
name|i
operator|++
control|)
block|{
name|addBackgroundJob
argument_list|(
operator|new
name|Reader
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|writer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|String
name|getRandomPath
parameter_list|()
block|{
return|return
name|paths
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|addPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|Reader
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|String
name|path
init|=
name|getRandomPath
argument_list|()
decl_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|readFile
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|NullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|Writer
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Node
name|parent
decl_stmt|;
specifier|private
name|long
name|counter
init|=
literal|0
decl_stmt|;
name|Writer
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|parent
operator|=
name|loginWriter
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"writer-"
operator|+
name|id
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|parent
operator|.
name|getSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|file
init|=
name|JcrUtils
operator|.
name|putFile
argument_list|(
name|parent
argument_list|,
literal|"file-"
operator|+
name|counter
operator|++
argument_list|,
literal|"application/octet-stream"
argument_list|,
operator|new
name|TestInputStream
argument_list|(
name|FILE_SIZE
operator|*
literal|1024
argument_list|)
argument_list|)
decl_stmt|;
name|parent
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|addPath
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

