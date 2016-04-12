begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|segment
operator|.
name|file
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|deleteDirectory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|ExecutionException
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
name|FutureTask
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
name|api
operator|.
name|CommitFailedException
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
name|segment
operator|.
name|SegmentNodeStore
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>Tests verifying if the repository gets corrupted or not: {@code OAK-2294 Corrupt repository after concurrent version operations}</p>  *  *<p>These tests are disabled by default due to their long running time. On the  * command line specify {@code -DSegmentReferenceLimitTestIT=true} to enable  * them.</p>  *  *<p>If you only want to run this test:<br>  * {@code mvn verify -Dsurefire.skip.ut=true -PintegrationTesting -Dit.test=SegmentReferenceLimitTestIT -DSegmentReferenceLimitTestIT=true}  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|SegmentReferenceLimitTestIT
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentReferenceLimitTestIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|SegmentReferenceLimitTestIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
name|ENABLED
argument_list|)
expr_stmt|;
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanDir
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error cleaning directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|corruption
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|FileStore
name|fileStore
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withNoCache
argument_list|()
operator|.
name|withMemoryMapping
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentNodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|setChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FutureTask
argument_list|<
name|Void
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|FutureTask
argument_list|<
name|Void
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|run
argument_list|(
operator|new
name|Worker
argument_list|(
name|nodeStore
argument_list|,
literal|"w"
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|FutureTask
argument_list|<
name|Void
argument_list|>
name|w
range|:
name|l
control|)
block|{
name|w
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|FutureTask
argument_list|<
name|T
argument_list|>
name|run
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
block|{
name|FutureTask
argument_list|<
name|T
argument_list|>
name|task
init|=
operator|new
name|FutureTask
argument_list|<
name|T
argument_list|>
argument_list|(
name|callable
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|task
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|task
return|;
block|}
specifier|private
specifier|static
class|class
name|Worker
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|Worker
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|400
condition|;
name|k
operator|++
control|)
block|{
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|name
operator|+
literal|' '
operator|+
name|k
argument_list|,
name|name
operator|+
literal|" value "
operator|+
name|k
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

