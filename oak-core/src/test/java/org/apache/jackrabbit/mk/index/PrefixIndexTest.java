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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test the prefix index.  */
end_comment

begin_class
specifier|public
class|class
name|PrefixIndexTest
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
operator|new
name|Indexer
argument_list|(
name|mk
argument_list|,
name|mk
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
name|indexer
operator|.
name|init
argument_list|()
expr_stmt|;
name|PrefixIndex
name|index
init|=
name|indexer
operator|.
name|createPrefixIndex
argument_list|(
literal|"d:"
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
literal|"/index"
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
literal|"{\":childNodeCount\":1,\"prefix@d:\":{\":childNodeCount\":0}}"
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:1"
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
literal|"+\"test\" : {\"blob\":\"d:1\"}"
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
literal|"+\"test2\" : {\"blob2\":\"d:2\"}"
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
literal|"/test/blob"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test2/blob2"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:2"
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
literal|"^\"test2/blob2\" : null"
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
literal|""
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:2"
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
literal|"^\"test2/blob2\" : \"d:2\" "
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
literal|"/test2/blob2"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:2"
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
literal|"+\"test3\" : {\"blob3\":\"d:1\"}"
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
literal|"+\"test4\" : {\"blob4\":\"d:2\"}"
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
literal|"/test/blob, /test3/blob3"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:1"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test2/blob2, /test4/blob4"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:2"
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
literal|"+\"test5\" : {\"blobs\":[\"a:0\",\"d:2\"]}"
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
literal|"+\"test6\" : {\"data\":[true, false, null, 1, -1]}"
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
literal|"/test2/blob2, /test4/blob4, /test5/blobs"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:2"
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
literal|"+\"test7\" : {\"a\":\"d:4\", \"b\":\"d:4\"}"
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
literal|"/test7/a, /test7/b"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:4"
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
literal|"^\"test7/a\" : null"
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
literal|"/test7/b"
argument_list|,
name|getPathList
argument_list|(
name|index
argument_list|,
literal|"d:4"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getPathList
parameter_list|(
name|PrefixIndex
name|index
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|revision
parameter_list|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|index
operator|.
name|getPaths
argument_list|(
name|value
argument_list|,
name|revision
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

