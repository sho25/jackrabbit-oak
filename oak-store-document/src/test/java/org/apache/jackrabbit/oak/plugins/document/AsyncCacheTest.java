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
name|java
operator|.
name|io
operator|.
name|File
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
name|cache
operator|.
name|Cache
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
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|AsyncCacheTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|invalidateWhileInQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setPersistentCache
argument_list|(
literal|"target/cacheTest"
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|nodeStore
init|=
name|builder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|Cache
argument_list|<
name|NamePathRev
argument_list|,
name|DocumentNodeState
operator|.
name|Children
argument_list|>
name|cache
init|=
name|builder
operator|.
name|buildChildrenCache
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|DocumentNodeState
operator|.
name|Children
name|c
init|=
operator|new
name|DocumentNodeState
operator|.
name|Children
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|.
name|children
operator|.
name|add
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|Path
name|path
init|=
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|NamePathRev
name|key
init|=
literal|null
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|key
operator|=
operator|new
name|NamePathRev
argument_list|(
literal|""
argument_list|,
name|path
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
comment|// give the write queue some time to write back entries
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getPersistentCache
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

