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
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|atomic
operator|.
name|AtomicInteger
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
name|Before
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
comment|/**  * Updates multiple nodes in the same commit with multiple threads and verifies  * the commit is atomic.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentConflictTest
extends|extends
name|BaseDocumentMKTest
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|USE_LOGGER
init|=
literal|true
decl_stmt|;
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
name|ConcurrentConflictTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WRITERS
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_NODES
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_TRANSFERS_PER_THREAD
init|=
literal|100
decl_stmt|;
specifier|private
name|DocumentStore
name|store
decl_stmt|;
specifier|private
name|List
argument_list|<
name|DocumentMK
argument_list|>
name|kernels
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentMK
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|logBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|initDocumentMK
parameter_list|()
block|{
name|logBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|new
name|MemoryDocumentStore
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk
init|=
name|openDocumentMK
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
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"node-"
operator|+
name|i
operator|+
literal|"\":{\"value\":100}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_WRITERS
condition|;
name|i
operator|++
control|)
block|{
name|kernels
operator|.
name|add
argument_list|(
name|openDocumentMK
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|DocumentMK
name|openDocumentMK
parameter_list|()
block|{
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|10
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|concurrentUpdatesWithBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|concurrentUpdates
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1788"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|concurrentUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|concurrentUpdates
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|concurrentUpdates
parameter_list|(
specifier|final
name|boolean
name|useBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Start test ======="
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|conflicts
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|MicroKernel
name|mk
range|:
name|kernels
control|)
block|{
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|JSONObject
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|JSONObject
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|BitSet
name|conflictSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|int
name|numTransfers
init|=
name|NUM_TRANSFERS_PER_THREAD
decl_stmt|;
try|try
block|{
while|while
condition|(
name|numTransfers
operator|>
literal|0
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|transfer
argument_list|()
condition|)
block|{
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"Failed transfer @"
operator|+
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
comment|// assume conflict
name|conflicts
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|conflictSet
operator|.
name|set
argument_list|(
name|numTransfers
argument_list|)
expr_stmt|;
block|}
name|numTransfers
operator|--
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"conflicts ("
operator|+
name|conflictSet
operator|.
name|cardinality
argument_list|()
operator|+
literal|"): "
operator|+
name|conflictSet
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|transfer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// read 3 random nodes and re-distribute values
name|nodes
operator|.
name|clear
argument_list|()
expr_stmt|;
while|while
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|NUM_NODES
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|String
name|rev
decl_stmt|;
if|if
condition|(
name|useBranch
condition|)
block|{
name|rev
operator|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rev
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
block|}
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|JSONObject
argument_list|>
name|entry
range|:
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/node-"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1000
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
name|entry
operator|.
name|setValue
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|sum
operator|+=
operator|(
name|Long
operator|)
name|obj
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sum
operator|<
literal|60
condition|)
block|{
comment|// retry with other nodes
return|return
literal|false
return|;
block|}
name|StringBuilder
name|jsop
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|withdrawn
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|JSONObject
argument_list|>
name|entry
range|:
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|value
init|=
operator|(
name|Long
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"^\"/node-"
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"/value\":"
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|>=
literal|20
operator|&&
operator|!
name|withdrawn
condition|)
block|{
name|jsop
operator|.
name|append
argument_list|(
name|value
operator|-
literal|20
argument_list|)
expr_stmt|;
name|withdrawn
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|jsop
operator|.
name|append
argument_list|(
name|value
operator|+
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|oldRev
init|=
name|rev
decl_stmt|;
name|rev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|jsop
operator|.
name|toString
argument_list|()
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|useBranch
condition|)
block|{
name|rev
operator|=
name|mk
operator|.
name|merge
argument_list|(
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Successful transfer @"
operator|+
name|oldRev
operator|+
literal|": "
operator|+
name|jsop
operator|.
name|toString
argument_list|()
operator|+
literal|" (new rev: "
operator|+
name|rev
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// dispose will flush all pending revisions
for|for
control|(
name|DocumentMK
name|mk
range|:
name|kernels
control|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|DocumentMK
name|mk
init|=
name|openDocumentMK
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|long
name|sum
init|=
literal|0
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
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/node-"
operator|+
name|i
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1000
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
name|sum
operator|+=
operator|(
name|Long
operator|)
name|obj
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Conflict rate: "
operator|+
name|conflicts
operator|.
name|get
argument_list|()
operator|+
literal|"/"
operator|+
operator|(
name|NUM_WRITERS
operator|*
name|NUM_TRANSFERS_PER_THREAD
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|logBuffer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_NODES
operator|*
literal|100
argument_list|,
name|sum
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
block|}
name|void
name|log
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|USE_LOGGER
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|logBuffer
init|)
block|{
name|logBuffer
operator|.
name|append
argument_list|(
name|msg
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

