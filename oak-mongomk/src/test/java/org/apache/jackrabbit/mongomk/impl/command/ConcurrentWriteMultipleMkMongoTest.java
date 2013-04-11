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
operator|.
name|command
package|;
end_package

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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
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
name|mongomk
operator|.
name|MongoMK
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

begin_comment
comment|/**  * Tests for multiple MongoMKs writing against the same DB in separate trees.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentWriteMultipleMkMongoTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSmall
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// Ignored only because it takes a while to complete.
annotation|@
name|Ignore
specifier|public
name|void
name|testLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTest
parameter_list|(
name|int
name|numberOfNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numberOfChildren
init|=
literal|10
decl_stmt|;
name|int
name|numberOfMks
init|=
literal|3
decl_stmt|;
name|String
index|[]
name|prefixes
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|,
literal|"f"
block|}
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numberOfMks
argument_list|)
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
name|numberOfMks
condition|;
name|i
operator|++
control|)
block|{
name|String
name|diff
init|=
name|buildPyramidDiff
argument_list|(
literal|"/"
argument_list|,
literal|0
argument_list|,
name|numberOfChildren
argument_list|,
name|numberOfNodes
argument_list|,
name|prefixes
index|[
name|i
index|]
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|MongoMK
name|mk
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
name|GenericWriteTask
name|task
init|=
operator|new
name|GenericWriteTask
argument_list|(
literal|"mk-"
operator|+
name|i
argument_list|,
name|mk
argument_list|,
name|diff
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|task
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
name|MINUTES
argument_list|)
expr_stmt|;
block|}
specifier|private
name|StringBuilder
name|buildPyramidDiff
parameter_list|(
name|String
name|startingPoint
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|numberOfChildren
parameter_list|,
name|long
name|nodesNumber
parameter_list|,
name|String
name|nodePrefixName
parameter_list|,
name|StringBuilder
name|diff
parameter_list|)
block|{
if|if
condition|(
name|numberOfChildren
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesNumber
condition|;
name|i
operator|++
control|)
block|{
name|diff
operator|.
name|append
argument_list|(
name|addNodeToDiff
argument_list|(
name|startingPoint
argument_list|,
name|nodePrefixName
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
if|if
condition|(
name|index
operator|>=
name|nodesNumber
condition|)
block|{
return|return
name|diff
return|;
block|}
name|diff
operator|.
name|append
argument_list|(
name|addNodeToDiff
argument_list|(
name|startingPoint
argument_list|,
name|nodePrefixName
operator|+
name|index
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numberOfChildren
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|startingPoint
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|startingPoint
operator|=
name|startingPoint
operator|+
literal|"/"
expr_stmt|;
block|}
name|buildPyramidDiff
argument_list|(
name|startingPoint
operator|+
name|nodePrefixName
operator|+
name|index
argument_list|,
name|index
operator|*
name|numberOfChildren
operator|+
name|i
argument_list|,
name|numberOfChildren
argument_list|,
name|nodesNumber
argument_list|,
name|nodePrefixName
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
specifier|private
specifier|static
name|String
name|addNodeToDiff
parameter_list|(
name|String
name|startingPoint
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|startingPoint
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|startingPoint
operator|=
name|startingPoint
operator|+
literal|"/"
expr_stmt|;
block|}
return|return
literal|"+\""
operator|+
name|startingPoint
operator|+
name|nodeName
operator|+
literal|"\" : {} \n"
return|;
block|}
comment|/**      * A simple write task.      */
specifier|private
specifier|static
class|class
name|GenericWriteTask
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|id
decl_stmt|;
specifier|private
name|MicroKernel
name|mk
decl_stmt|;
specifier|private
name|String
name|diff
decl_stmt|;
specifier|private
name|int
name|nodesPerCommit
decl_stmt|;
specifier|public
name|GenericWriteTask
parameter_list|(
name|String
name|id
parameter_list|,
name|MongoMK
name|mk
parameter_list|,
name|String
name|diff
parameter_list|,
name|int
name|nodesPerCommit
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|nodesPerCommit
operator|=
name|nodesPerCommit
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|nodesPerCommit
operator|==
literal|0
condition|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|currentCommit
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|diffs
init|=
name|diff
operator|.
name|split
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|diff
range|:
name|diffs
control|)
block|{
name|currentCommit
operator|.
name|append
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|nodesPerCommit
condition|)
block|{
comment|//System.out.println("[" + id + "] Committing: " + currentCommit.toString());
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|currentCommit
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("[" + id + "] Committed-" + rev + ":" + currentCommit.toString());
name|currentCommit
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// Commit remaining nodes
if|if
condition|(
name|currentCommit
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|currentCommit
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

