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
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|document
operator|.
name|AbstractMongoConnectionTest
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
name|document
operator|.
name|DocumentMK
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
name|document
operator|.
name|MongoUtils
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
name|document
operator|.
name|util
operator|.
name|MongoConnection
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * Test for OAK-566.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentMKConcurrentAddTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|CACHE_SIZE
init|=
literal|8
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NB_THREADS
init|=
literal|16
decl_stmt|;
specifier|private
name|List
argument_list|<
name|DocumentMK
argument_list|>
name|mks
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentMK
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|DocumentMK
name|createMicroKernel
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoConnection
name|connection
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|DB
name|mongoDB
init|=
name|connection
operator|.
name|getDB
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|memoryCacheSize
argument_list|(
name|CACHE_SIZE
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|closeMKs
parameter_list|()
block|{
for|for
control|(
name|DocumentMK
name|mk
range|:
name|mks
control|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|mks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates NB_THREADS microkernels, each commiting two nodes (one parent,      * one child) in its own thread. The nodes being committed by separate      * threads do not overlap / conflict.      *      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentAdd
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create workers
name|List
argument_list|<
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|>
name|cs
init|=
operator|new
name|LinkedList
argument_list|<
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|>
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
name|NB_THREADS
condition|;
name|i
operator|++
control|)
block|{
comment|// each callable has its own microkernel
specifier|final
name|DocumentMK
name|mk
init|=
name|createMicroKernel
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
comment|// diff for adding one node and one child node
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|stmts
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|stmts
operator|.
name|add
argument_list|(
literal|"+\"node"
operator|+
name|i
operator|+
literal|"\":{}"
argument_list|)
expr_stmt|;
name|stmts
operator|.
name|add
argument_list|(
literal|"+\"node"
operator|+
name|i
operator|+
literal|"/child\":{}"
argument_list|)
expr_stmt|;
comment|// create callable
name|Callable
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|// commit all statements, one at a time
name|String
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|stmt
range|:
name|stmts
control|)
block|{
name|r
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|stmt
argument_list|,
literal|null
argument_list|,
literal|"msg"
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
decl_stmt|;
name|cs
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|// run workers concurrently
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|NB_THREADS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
name|fs
init|=
operator|new
name|LinkedList
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Callable
argument_list|<
name|String
argument_list|>
name|c
range|:
name|cs
control|)
block|{
name|fs
operator|.
name|add
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// get all results in order to verify if any of the threads has thrown
comment|// an exception
for|for
control|(
name|Future
argument_list|<
name|String
argument_list|>
name|f
range|:
name|fs
control|)
block|{
name|f
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

