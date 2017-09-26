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
name|plugins
operator|.
name|document
package|;
end_package

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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|blob
operator|.
name|MemoryBlobStore
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

begin_comment
comment|/**  * A set of simple cluster tests.  */
end_comment

begin_class
specifier|public
class|class
name|Cluster2Test
block|{
annotation|@
name|Test
specifier|public
name|void
name|twoNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryDocumentStore
name|ds
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|MemoryBlobStore
name|bs
init|=
operator|new
name|MemoryBlobStore
argument_list|()
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
decl_stmt|;
name|builder
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk1
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk2
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"x\": 1}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|String
name|b1
init|=
name|mk2
operator|.
name|branch
argument_list|(
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"test\""
argument_list|,
name|b1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|b2
init|=
name|mk2
operator|.
name|branch
argument_list|(
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|b2b
init|=
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"test\""
argument_list|,
name|b2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mk2
operator|.
name|merge
argument_list|(
name|b2b
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{\"x\": 1}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|String
name|n1
init|=
name|mk1
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|mk1
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|n2
init|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// mk1 now sees both changes
name|assertEquals
argument_list|(
literal|"{\"x\":1,\":childNodeCount\":0}"
argument_list|,
name|n1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"x\":1,\":childNodeCount\":0}"
argument_list|,
name|n2
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

