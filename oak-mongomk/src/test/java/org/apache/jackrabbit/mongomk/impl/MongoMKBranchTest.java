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
name|impl
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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  *<code>MongoMKBranchTest</code> performs a test to check if commits  * to a branch are not visible to other branches.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKBranchTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
comment|/**      * Creates the following revision history:      *<pre>      *   + rev1 (first commit with /child1)      *   |\      *   | + branchRev1 (branch from rev1)      *   | + branchRev11 (branch commit /child1/foo:1)      *   |      *   + rev2 (second commit with /child2)      *   |\      *   | + branchRev2 (brach from rev2)      *</pre>      * The test reads /child from<code>branchRev2</code> and expects      * the version from the first commit.      */
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|isolatedBranches
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/child1\":{}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|branchRev1
init|=
name|mk
operator|.
name|branch
argument_list|(
name|rev1
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/child1"
argument_list|,
literal|"^\"foo\":1"
argument_list|,
name|branchRev1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/child2\":{}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|branchRev2
init|=
name|mk
operator|.
name|branch
argument_list|(
name|rev2
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/child1"
argument_list|,
name|branchRev2
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|JSONObject
name|obj
init|=
operator|(
name|JSONObject
operator|)
name|parser
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|movesInBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|branchRev
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branchRev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branchRev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"^\"foo\":1"
argument_list|,
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branchRev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|">\"a\" : \"b\""
argument_list|,
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branchRev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|">\"b\" : \"a\""
argument_list|,
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|merge
argument_list|(
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|JSONObject
name|obj
init|=
operator|(
name|JSONObject
operator|)
name|parser
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

