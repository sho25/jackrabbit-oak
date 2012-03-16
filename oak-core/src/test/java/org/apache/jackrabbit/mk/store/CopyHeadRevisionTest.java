begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|store
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
name|File
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
name|mk
operator|.
name|core
operator|.
name|Repository
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
name|api
operator|.
name|MicroKernelException
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
name|fs
operator|.
name|FileUtils
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
name|json
operator|.
name|fast
operator|.
name|Jsop
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
name|json
operator|.
name|fast
operator|.
name|JsopArray
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

begin_comment
comment|/**  * Use-case: start off a new revision store that contains just the head revision  * and its nodes.  *   * TODO: make the test concurrent  */
end_comment

begin_class
specifier|public
class|class
name|CopyHeadRevisionTest
block|{
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteRecursive
argument_list|(
literal|"target/mk1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteRecursive
argument_list|(
literal|"target/mk2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Test
specifier|public
name|void
name|testCopyHeadRevisionToNewStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|revs
init|=
operator|new
name|String
index|[
literal|5
index|]
decl_stmt|;
name|DefaultRevisionStore
name|rsFrom
init|=
operator|new
name|DefaultRevisionStore
argument_list|()
decl_stmt|;
name|rsFrom
operator|.
name|initialize
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/mk1"
argument_list|)
argument_list|)
expr_stmt|;
name|DefaultRevisionStore
name|rsTo
init|=
operator|new
name|DefaultRevisionStore
argument_list|()
decl_stmt|;
name|rsTo
operator|.
name|initialize
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/mk2"
argument_list|)
argument_list|)
expr_stmt|;
name|CopyingGC
name|gc
init|=
operator|new
name|CopyingGC
argument_list|(
name|rsFrom
argument_list|,
name|rsTo
argument_list|)
decl_stmt|;
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|(
operator|new
name|Repository
argument_list|(
name|gc
argument_list|)
argument_list|)
decl_stmt|;
name|revs
index|[
literal|0
index|]
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"c\":{}, \"d\":{} }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|revs
index|[
literal|1
index|]
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|revs
index|[
literal|2
index|]
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/b"
argument_list|,
literal|"+\"e\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|revs
index|[
literal|3
index|]
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/c"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Simulate a GC cycle start
name|gc
operator|.
name|start
argument_list|()
expr_stmt|;
name|revs
index|[
literal|4
index|]
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/b/e"
argument_list|,
literal|"+\"g\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Simulate a GC cycle stop
name|gc
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Assert head revision is contained after GC
name|assertEquals
argument_list|(
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
name|revs
index|[
name|revs
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// Assert unused revisions were GCed
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|revs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Revision should have been GCed: "
operator|+
name|revs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|// Assert MK contains 3 revisions only
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|JsopArray
operator|)
name|Jsop
operator|.
name|parse
argument_list|(
name|mk
operator|.
name|getRevisions
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

