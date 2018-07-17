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
name|Random
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
name|api
operator|.
name|PropertyState
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
name|Utils
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
name|stats
operator|.
name|Clock
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

begin_import
import|import static
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
name|TestUtils
operator|.
name|merge
import|;
end_import

begin_import
import|import static
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
name|Utils
operator|.
name|getAllDocuments
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

begin_class
specifier|public
class|class
name|RandomDocumentNodeStoreSweepTest
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
name|RandomDocumentNodeStoreSweepTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|42
argument_list|)
decl_stmt|;
specifier|private
name|int
name|numNodes
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|numProperties
init|=
literal|0
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|FailingDocumentStore
name|store
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|FailingDocumentStore
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|=
name|createDocumentNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|randomFailures
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|23
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|n
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|n
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
name|addNode
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
case|case
literal|6
case|:
name|addProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|removeProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|restartAndCheckStore
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
specifier|private
name|void
name|restartAndCheckStore
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"crashing DocumentNodeStore"
argument_list|)
expr_stmt|;
comment|// let it crash
name|store
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|0
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|store
operator|.
name|fail
argument_list|()
operator|.
name|never
argument_list|()
expr_stmt|;
comment|// wait until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_DURATION_MILLIS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"restarting DocumentNodeStore"
argument_list|)
expr_stmt|;
name|ns
operator|=
name|createDocumentNodeStore
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"checking for uncommitted changes"
argument_list|)
expr_stmt|;
name|assertCleanStore
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|removeProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|maybeFail
argument_list|(
operator|new
name|Operation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|numNodes
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// try ten times to find a node with a property
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
name|String
name|name
init|=
literal|"node-"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
argument_list|,
literal|"removing property"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|maybeFail
argument_list|(
operator|new
name|Operation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|numNodes
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|String
name|name
init|=
literal|"node-"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"property-"
operator|+
name|numProperties
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|numProperties
operator|++
expr_stmt|;
block|}
block|}
argument_list|,
literal|"adding property"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|maybeFail
argument_list|(
operator|new
name|Operation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|numNodes
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|numNodes
operator|++
expr_stmt|;
block|}
block|}
argument_list|,
literal|"adding node"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|maybeFail
parameter_list|(
name|Operation
name|op
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|failOperation
argument_list|()
condition|)
block|{
name|guardedFail
argument_list|(
name|op
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|op
operator|.
name|call
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|guardedFail
parameter_list|(
name|Operation
name|op
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|store
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|1
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|op
operator|.
name|call
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|message
operator|+
literal|" failed"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|fail
argument_list|()
operator|.
name|never
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|failOperation
parameter_list|()
block|{
comment|// fail 10% of the operations
return|return
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
return|;
block|}
specifier|private
name|DocumentNodeStore
name|createDocumentNodeStore
parameter_list|()
block|{
name|DocumentNodeStore
name|ns
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// do not retry commits
name|ns
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|ns
return|;
block|}
specifier|private
name|void
name|assertCleanStore
parameter_list|()
block|{
for|for
control|(
name|NodeDocument
name|doc
range|:
name|getAllDocuments
argument_list|(
name|store
argument_list|)
control|)
block|{
for|for
control|(
name|Revision
name|c
range|:
name|doc
operator|.
name|getAllChanges
argument_list|()
control|)
block|{
name|String
name|commitValue
init|=
name|ns
operator|.
name|getCommitValue
argument_list|(
name|c
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Revision "
operator|+
name|c
operator|+
literal|" on "
operator|+
name|doc
operator|.
name|getId
argument_list|()
operator|+
literal|" is not committed: "
operator|+
name|commitValue
argument_list|,
name|Utils
operator|.
name|isCommitted
argument_list|(
name|commitValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
interface|interface
name|Operation
block|{
name|void
name|call
parameter_list|()
throws|throws
name|CommitFailedException
function_decl|;
block|}
block|}
end_class

end_unit

