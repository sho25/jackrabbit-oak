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
name|upgrade
operator|.
name|cli
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
name|IOException
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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|jcr
operator|.
name|Jcr
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|index
operator|.
name|reference
operator|.
name|ReferenceIndexProvider
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|NodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|SegmentNodeStoreContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|Jcr2ToSegmentTest
block|{
specifier|private
specifier|final
name|NodeStoreContainer
name|destinationContainer
init|=
operator|new
name|SegmentNodeStoreContainer
argument_list|()
decl_stmt|;
specifier|private
name|NodeStore
name|destination
decl_stmt|;
specifier|private
name|RepositoryImpl
name|repository
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|public
name|Jcr2ToSegmentTest
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Before
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"test-jcr2"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tempDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|Util
operator|.
name|unzip
argument_list|(
name|AbstractOak2OakTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/jcr2.zip"
argument_list|)
argument_list|,
name|tempDir
argument_list|)
expr_stmt|;
block|}
name|OakUpgrade
operator|.
name|main
argument_list|(
literal|"--copy-binaries"
argument_list|,
name|tempDir
operator|.
name|getPath
argument_list|()
argument_list|,
name|destinationContainer
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|=
name|destinationContainer
operator|.
name|open
argument_list|()
expr_stmt|;
name|repository
operator|=
operator|(
name|RepositoryImpl
operator|)
operator|new
name|Jcr
argument_list|(
name|destination
argument_list|)
operator|.
name|with
argument_list|(
literal|"oak.sling"
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ReferenceIndexProvider
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|clean
parameter_list|()
throws|throws
name|IOException
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|destinationContainer
operator|.
name|close
argument_list|()
expr_stmt|;
name|destinationContainer
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|validateMigration
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|AbstractOak2OakTest
operator|.
name|verifyContent
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|AbstractOak2OakTest
operator|.
name|verifyBlob
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

