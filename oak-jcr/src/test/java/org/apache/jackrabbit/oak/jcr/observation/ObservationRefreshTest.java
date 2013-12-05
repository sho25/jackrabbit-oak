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
name|jcr
operator|.
name|observation
package|;
end_package

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
name|Map
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
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|JcrConstants
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
name|api
operator|.
name|JackrabbitRepository
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
name|jcr
operator|.
name|NodeStoreFixture
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|test
operator|.
name|api
operator|.
name|util
operator|.
name|Text
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
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_MOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_REMOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PERSIST
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_CHANGED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_REMOVED
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

begin_class
specifier|public
class|class
name|ObservationRefreshTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
specifier|static
specifier|final
name|int
name|ALL_EVENTS
init|=
name|NODE_ADDED
operator||
name|NODE_REMOVED
operator||
name|NODE_MOVED
operator||
name|PROPERTY_ADDED
operator||
name|PROPERTY_REMOVED
operator||
name|PROPERTY_CHANGED
operator||
name|PERSIST
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NODE
init|=
literal|"test_node"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REFERENCEABLE_NODE
init|=
literal|"\"referenceable\""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|'/'
operator|+
name|TEST_NODE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_TYPE
init|=
literal|"mix:test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|CONDITION_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|private
name|Session
name|observingSession
decl_stmt|;
specifier|private
name|ObservationManager
name|observationManager
decl_stmt|;
specifier|public
name|ObservationRefreshTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|NodeTypeManager
name|ntMgr
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|NodeTypeTemplate
name|mixTest
init|=
name|ntMgr
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|mixTest
operator|.
name|setName
argument_list|(
name|TEST_TYPE
argument_list|)
expr_stmt|;
name|mixTest
operator|.
name|setMixin
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ntMgr
operator|.
name|registerNodeType
argument_list|(
name|mixTest
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|TEST_NODE
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
name|TEST_TYPE
argument_list|)
expr_stmt|;
name|Node
name|refNode
init|=
name|n
operator|.
name|addNode
argument_list|(
name|REFERENCEABLE_NODE
argument_list|)
decl_stmt|;
name|refNode
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
name|RepositoryImpl
operator|.
name|REFRESH_INTERVAL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|observingSession
operator|=
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|getRepository
argument_list|()
operator|)
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|observationManager
operator|=
name|observingSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|observingSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1267"
argument_list|)
comment|// FIXME: OAK-1267
annotation|@
name|Test
specifier|public
name|void
name|observation
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|MyListener
name|listener
init|=
operator|new
name|MyListener
argument_list|()
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|ALL_EVENTS
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|n
init|=
name|getAdminSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|TEST_PATH
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
name|n
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|numAdded
operator|==
literal|1000
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"added nodes"
argument_list|,
literal|1000
argument_list|,
name|listener
operator|.
name|numAdded
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|getNode
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|numRemoved
operator|==
literal|1000
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"removed nodes"
argument_list|,
literal|1000
argument_list|,
name|listener
operator|.
name|numRemoved
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"test"
operator|+
name|i
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|numPropsAdded
operator|==
literal|1100
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"properties added"
argument_list|,
literal|1100
argument_list|,
name|listener
operator|.
name|numPropsAdded
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"test"
operator|+
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|numPropsModified
operator|==
literal|100
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"properties modified"
argument_list|,
literal|100
argument_list|,
name|listener
operator|.
name|numPropsModified
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"test100"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|test100Exists
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|n
operator|.
name|getProperty
argument_list|(
literal|"test100"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
operator|!
name|listener
operator|.
name|test100Exists
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|getProperty
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Timer
operator|.
name|waitFor
argument_list|(
name|CONDITION_TIMEOUT
argument_list|,
operator|new
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|listener
operator|.
name|numPropsRemoved
operator|==
literal|1100
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"properties removed"
argument_list|,
literal|1100
argument_list|,
name|listener
operator|.
name|numPropsRemoved
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
interface|interface
name|Condition
block|{
name|boolean
name|evaluate
parameter_list|()
function_decl|;
block|}
specifier|private
specifier|static
class|class
name|Timer
block|{
specifier|public
specifier|static
name|boolean
name|waitFor
parameter_list|(
name|long
name|timeout
parameter_list|,
name|Condition
name|c
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|end
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|evaluate
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
class|class
name|MyListener
implements|implements
name|EventListener
block|{
specifier|private
name|String
name|error
init|=
literal|""
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|numAdded
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|numRemoved
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|numPropsAdded
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|numPropsRemoved
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|numPropsModified
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|test100Exists
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onEvent
parameter_list|(
name|EventIterator
name|events
parameter_list|)
block|{
try|try
block|{
while|while
condition|(
name|events
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Event
name|event
init|=
name|events
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|NODE_ADDED
condition|)
block|{
name|numAdded
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|observingSession
operator|.
name|nodeExists
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|error
operator|=
literal|"node missing: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|NODE_REMOVED
condition|)
block|{
name|numRemoved
operator|++
expr_stmt|;
if|if
condition|(
name|observingSession
operator|.
name|nodeExists
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|error
operator|=
literal|"node not missing: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|PROPERTY_ADDED
condition|)
block|{
name|Node
name|node
init|=
name|observingSession
operator|.
name|getNode
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyIterator
name|iter
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Property
name|p
init|=
name|iter
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|error
operator|=
literal|"property missing: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"test100"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|test100Exists
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|numPropsAdded
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|observingSession
operator|.
name|propertyExists
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|error
operator|=
literal|"property missing: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|PROPERTY_REMOVED
condition|)
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"test100"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|test100Exists
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|numPropsRemoved
operator|++
expr_stmt|;
if|if
condition|(
name|observingSession
operator|.
name|propertyExists
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|error
operator|=
literal|"property not missing: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|PROPERTY_CHANGED
condition|)
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"test100"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{                          }
else|else
block|{
name|numPropsModified
operator|++
expr_stmt|;
name|long
name|v
init|=
name|observingSession
operator|.
name|getProperty
argument_list|(
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
name|Long
operator|.
name|valueOf
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|name
argument_list|)
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
argument_list|)
condition|)
block|{
name|error
operator|=
literal|"property has wrong content: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

