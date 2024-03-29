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
name|oak
operator|.
name|jcr
package|;
end_package

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
operator|.
name|REFRESH_INTERVAL
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|ObjectOutputStream
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
name|CountDownLatch
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
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemExistsException
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
name|PropertyType
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
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|ConstraintViolationException
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
name|NodeDefinition
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
name|NodeDefinitionTemplate
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
name|nodetype
operator|.
name|PropertyDefinition
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
name|PropertyDefinitionTemplate
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|IOUtils
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|JcrUtils
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|fixture
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * This class contains test cases which demonstrate changes in behaviour wrt. to Jackrabbit 2.  *   * @see<a href="https://issues.apache.org/jira/browse/OAK-14">OAK-14: Identify and document changes in behaviour wrt. Jackrabbit 2</a>  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|CompatibilityIssuesTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|CompatibilityIssuesTest
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
comment|/**      * Trans-session isolation differs from Jackrabbit 2. Snapshot isolation can      * result in write skew as this test demonstrates: the check method enforces      * an application logic constraint which says that the sum of the properties      * p1 and p2 must not be negative. While session1 and session2 each enforce      * this constraint before saving, the constraint might not hold globally as      * can be seen in session3.      *      * @see<a href="http://wiki.apache.org/jackrabbit/Transactional%20model%20of%20the%20Microkernel%20based%20Jackrabbit%20prototype">      *     Transactional model of the Microkernel based Jackrabbit prototype</a>      */
annotation|@
name|Test
specifier|public
name|void
name|sessionIsolation
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|// Execute all operations in serial but on different threads to ensure
comment|// same thread session refreshing doesn't come into the way
specifier|final
name|Session
name|session0
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|run
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|testNode
init|=
name|session0
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"testNode"
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"p2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|session0
operator|.
name|save
argument_list|()
expr_stmt|;
name|check
argument_list|(
name|getAdminSession
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session0
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Session
name|session1
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
specifier|final
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|run
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|session1
operator|.
name|getNode
argument_list|(
literal|"/testNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|session1
argument_list|)
expr_stmt|;
name|session1
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|session2
operator|.
name|getNode
argument_list|(
literal|"/testNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|session2
argument_list|)
expr_stmt|;
comment|// Throws on JR2, not on Oak
name|session2
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|Session
name|session3
init|=
name|createAnonymousSession
argument_list|()
decl_stmt|;
try|try
block|{
name|check
argument_list|(
name|session3
argument_list|)
expr_stmt|;
comment|// Throws on Oak
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|session3
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|run
parameter_list|(
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|FutureTask
argument_list|<
name|Void
argument_list|>
name|task
init|=
operator|new
name|FutureTask
argument_list|<
name|Void
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
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|check
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|session
operator|.
name|getNode
argument_list|(
literal|"/testNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"p1"
argument_list|)
operator|.
name|getLong
argument_list|()
operator|+
name|session
operator|.
name|getNode
argument_list|(
literal|"/testNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"p2"
argument_list|)
operator|.
name|getLong
argument_list|()
operator|<
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"p1 + p2< 0"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|move
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
name|Node
name|node
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|node
operator|.
name|addNode
argument_list|(
literal|"source"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"node"
argument_list|)
expr_stmt|;
name|node
operator|.
name|addNode
argument_list|(
literal|"target"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Node
name|sourceNode
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/source/node"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/source/node"
argument_list|,
literal|"/target/moved"
argument_list|)
expr_stmt|;
comment|// assertEquals("/target/moved", sourceNode.getPath());  // passes on JR2, fails on Oak
try|try
block|{
name|sourceNode
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|expected
parameter_list|)
block|{
comment|// sourceNode is stale
block|}
block|}
comment|/**      * Type checks are deferred to the Session#save call instead of the      * Node#addNode method like in Jackrabbit2.      *<p>Stacktrace in JR2:</p>      *<pre>      * {@code      * javax.jcr.nodetype.ConstraintViolationException: No child node definition for fail found in node /f1362578560413      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1276)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:111)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:1)      *     at org.apache.jackrabbit.core.session.SessionState.perform(SessionState.java:216)      *     at org.apache.jackrabbit.core.ItemImpl.perform(ItemImpl.java:91)      *     at org.apache.jackrabbit.core.NodeImpl.addNodeWithUuid(NodeImpl.java:1814)      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1774)      * }      *<pre>      *<p>Stacktrace in Oak:</p>      *<pre>      * {@code      *javax.jcr.nodetype.ConstraintViolationException      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)      *    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)      *    at java.lang.reflect.Constructor.newInstance(Constructor.java:513)      *    at org.apache.jackrabbit.oak.api.CommitFailedException.throwRepositoryException(CommitFailedException.java:57)      *    at org.apache.jackrabbit.oak.jcr.delegate.SessionDelegate.save(SessionDelegate.java:258)      *    at org.apache.jackrabbit.oak.jcr.session.SessionImpl.save(SessionImpl.java:277)      *    ...      *Caused by: org.apache.jackrabbit.oak.api.CommitFailedException: Cannot add node 'f1362578685631' at /      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:128)      *    at org.apache.jackrabbit.oak.spi.commit.CompositeValidator.childNodeAdded(CompositeValidator.java:68)      *    at org.apache.jackrabbit.oak.spi.commit.ValidatingHook$ValidatorDiff.childNodeAdded(ValidatingHook.java:159)      *    at org.apache.jackrabbit.oak.core.RootImpl.commit(RootImpl.java:250)      *    at org.apache.jackrabbit.oak.jcr.delegate.SessionDelegate.save(SessionDelegate.java:255)      *    ...      *Caused by: javax.jcr.nodetype.ConstraintViolationException: Node 'jcr:content' in 'nt:file' is mandatory      *    at org.apache.jackrabbit.oak.plugins.nodetype.EffectiveNodeTypeImpl.checkMandatoryItems(EffectiveNodeTypeImpl.java:288)      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:125)      *    ...      * }      *<pre>      */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|typeChecksOnSave
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
name|Node
name|f
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"f"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"nt:file"
argument_list|)
decl_stmt|;
name|f
operator|.
name|addNode
argument_list|(
literal|"fail"
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
comment|// this is where JR2 throws ConstraintViolationException
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// // this is where OAK throws ConstraintViolationException
block|}
comment|/**      * OAK-939 - Change in behaviour from JR2. Following test case leads to      * CommitFailedException but it passes in JR2      */
annotation|@
name|Test
specifier|public
name|void
name|removeNodeInDifferentSession
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|String
name|testNode
init|=
literal|"test_node"
decl_stmt|;
specifier|final
name|String
name|testNodePath
init|=
literal|'/'
operator|+
name|testNode
decl_stmt|;
comment|// Create the test node
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|testNode
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Test case would pass if the sessionRefreshInterval is set to zero
name|boolean
name|refreshIntervalZero
init|=
literal|false
decl_stmt|;
name|Session
name|s3
init|=
name|newSession
argument_list|(
name|refreshIntervalZero
argument_list|)
decl_stmt|;
name|Session
name|s2
init|=
name|newSession
argument_list|(
name|refreshIntervalZero
argument_list|)
decl_stmt|;
name|s2
operator|.
name|getNode
argument_list|(
name|testNodePath
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|s3
operator|.
name|getNode
argument_list|(
name|testNodePath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|s3
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
block|}
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s3
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Session
name|newSession
parameter_list|(
name|boolean
name|refreshIntervalZero
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Credentials
name|creds
init|=
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
decl_stmt|;
if|if
condition|(
name|refreshIntervalZero
condition|)
block|{
return|return
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
name|creds
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|singletonMap
argument_list|(
name|REFRESH_INTERVAL
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|creds
argument_list|)
return|;
block|}
block|}
comment|/**      * OAK-948 - JR2 generates propertyChange event for touched properties while Oak does not      */
annotation|@
name|Test
specifier|public
name|void
name|noEventsForTouchedProperties
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|testNodeName
init|=
literal|"test_touched_node"
decl_stmt|;
specifier|final
name|String
name|testNodePath
init|=
literal|'/'
operator|+
name|testNodeName
decl_stmt|;
comment|// Create the test node
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|testNode
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|testNodeName
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar0"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|observingSession
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|ObservationManager
name|observationManager
init|=
name|observingSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<
name|Event
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|EventListener
name|listener
init|=
operator|new
name|EventListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onEvent
parameter_list|(
name|EventIterator
name|eventIt
parameter_list|)
block|{
while|while
condition|(
name|eventIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|events
operator|.
name|add
argument_list|(
name|eventIt
operator|.
name|nextEvent
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|events
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|PROPERTY_CHANGED
argument_list|,
name|testNodePath
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
comment|//Now touch foo and modify foo2
name|session
operator|.
name|getNode
argument_list|(
name|testNodePath
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|session
operator|.
name|getNode
argument_list|(
name|testNodePath
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|//Only one event is recorded for foo2 modification
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|events
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observingSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|noSNSSupport
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
name|Node
name|testNode
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|testNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
name|testNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
comment|// This would fail on JR2 since there SNSs are supported
name|fail
argument_list|(
literal|"Expected ItemExistsException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|//ItemExistsException is expected to be thrown
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|testNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
comment|// This would fail on JR2 since there SNSs are supported
name|fail
argument_list|(
literal|"Expected ItemExistsException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|//ItemExistsException is expected to be thrown
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeTest
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
comment|// node type with default child-node type of to nt:base
name|String
name|ntName
init|=
literal|"test"
decl_stmt|;
name|NodeTypeManager
name|ntm
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
name|ntt
init|=
name|ntm
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|ntt
operator|.
name|setName
argument_list|(
name|ntName
argument_list|)
expr_stmt|;
name|NodeDefinitionTemplate
name|child
init|=
name|ntm
operator|.
name|createNodeDefinitionTemplate
argument_list|()
decl_stmt|;
name|child
operator|.
name|setName
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setDefaultPrimaryTypeName
argument_list|(
literal|"nt:base"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setRequiredPrimaryTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"nt:base"
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeDefinition
argument_list|>
name|children
init|=
name|ntt
operator|.
name|getNodeDefinitionTemplates
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|ntm
operator|.
name|registerNodeType
argument_list|(
name|ntt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// try to create a node with the default nt:base
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"defaultNtBase"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|node
operator|.
name|addNode
argument_list|(
literal|"nothrow"
argument_list|)
expr_stmt|;
comment|// See OAK-1013
try|try
block|{
name|node
operator|.
name|addNode
argument_list|(
literal|"throw"
argument_list|,
literal|"nt:hierarchyNode"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Abstract primary type should cause ConstraintViolationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|expected
parameter_list|)
block|{         }
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryCoercion
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
comment|// node type with default child-node type of to nt:base
name|String
name|ntName
init|=
literal|"binaryCoercionTest"
decl_stmt|;
name|NodeTypeManager
name|ntm
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
name|ntt
init|=
name|ntm
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|ntt
operator|.
name|setName
argument_list|(
name|ntName
argument_list|)
expr_stmt|;
name|PropertyDefinitionTemplate
name|propertyWithType
init|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|propertyWithType
operator|.
name|setName
argument_list|(
literal|"javaObject"
argument_list|)
expr_stmt|;
name|propertyWithType
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|PropertyDefinitionTemplate
name|unnamed
init|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|unnamed
operator|.
name|setName
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|unnamed
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|UNDEFINED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PropertyDefinition
argument_list|>
name|properties
init|=
name|ntt
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
decl_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|propertyWithType
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|unnamed
argument_list|)
expr_stmt|;
name|ntm
operator|.
name|registerNodeType
argument_list|(
name|ntt
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testNodeForBinary"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
name|serializeObject
argument_list|(
literal|"testValue"
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"javaObject"
argument_list|,
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createBinary
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
literal|"javaObject"
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ByteArrayOutputStream
name|serializeObject
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|ObjectOutputStream
name|objectStream
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|objectStream
operator|.
name|writeObject
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|objectStream
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSearchDescendentUsingXPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|adminSession
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|String
name|testNodePath
init|=
literal|"/home/users/geometrixx-outdoors/emily.andrews@mailinator.com/social/relationships/following/aaron.mcdonald@mailinator.com"
decl_stmt|;
name|Node
name|testNode
init|=
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
name|testNodePath
argument_list|,
literal|null
argument_list|,
name|adminSession
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"aaron.mcdonald@mailinator.com"
argument_list|)
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|adminSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/home/users/geometrixx-outdoors"
argument_list|)
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|adminSession
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
name|restrictions
operator|.
name|put
argument_list|(
literal|"rep:glob"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"*/social/relationships/following/*"
argument_list|)
argument_list|)
expr_stmt|;
name|tmpl
operator|.
name|addEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|Privilege
index|[]
block|{
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
block|}
argument_list|,
literal|true
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|,
name|tmpl
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|anonymousSession
init|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|QueryManager
name|qm
init|=
name|anonymousSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"/jcr:root/home//social/relationships/following//*[@id='aaron.mcdonald@mailinator.com']"
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
decl_stmt|;
name|QueryResult
name|r
init|=
name|q
operator|.
name|execute
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|r
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|anonymousSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

