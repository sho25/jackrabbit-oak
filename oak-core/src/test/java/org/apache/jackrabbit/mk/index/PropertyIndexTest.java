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
name|mk
operator|.
name|index
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|Indexer
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
name|PropertyIndex
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
comment|/**  * Test the property index.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyIndexTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|Indexer
name|indexer
init|=
name|Indexer
operator|.
name|getInstance
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|indexer
operator|.
name|init
argument_list|()
expr_stmt|;
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
comment|// meta data
name|String
name|meta
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|Indexer
operator|.
name|INDEX_CONFIG_PATH
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":2,\":data\":{\":childNodeCount\":0},"
operator|+
literal|"\"property@id,unique\":{\":childNodeCount\":1,\":data\":{}}}"
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|String
name|oldHead
init|=
name|head
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\" : {\"id\":\"1\"}"
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test2\" : {\"id\":\"2\"}"
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|oldHead
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|oldHead
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"test2\""
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"test\" : {\"id\":\"3\"}"
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"3"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
comment|// Recreate the indexer
name|indexer
operator|=
name|Indexer
operator|.
name|getInstance
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|init
argument_list|()
expr_stmt|;
name|index
operator|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"3"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"0"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^ \"test/id\": 100"
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"100"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"3"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"- \"test\""
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"100"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"3"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

