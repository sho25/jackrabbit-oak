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
name|InvalidItemStateException
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
name|ConstraintViolationException
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
name|junit
operator|.
name|Test
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
name|Map
import|;
end_import

begin_comment
comment|/**  * This class contains test cases which demonstrate changes in behaviour wrt. to Jackrabbit 2.  *   * @see<a href="https://issues.apache.org/jira/browse/OAK-14">OAK-14: Identify and document changes in behaviour wrt. Jackrabbit 2</a>  */
end_comment

begin_class
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
comment|/**      * Type checks are deferred to the Session#save call instead of the      * Node#addNode method like in Jackrabbit2.      *<p>Stacktrace in JR2:</p>      *<pre>      * {@code      * javax.jcr.nodetype.ConstraintViolationException: No child node definition for fail found in node /f1362578560413      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1276)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:111)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:1)      *     at org.apache.jackrabbit.core.session.SessionState.perform(SessionState.java:216)      *     at org.apache.jackrabbit.core.ItemImpl.perform(ItemImpl.java:91)      *     at org.apache.jackrabbit.core.NodeImpl.addNodeWithUuid(NodeImpl.java:1814)      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1774)      * }      *<pre>      *<p>Stacktrace in Oak:</p>      *<pre>      * {@code      *javax.jcr.nodetype.ConstraintViolationException      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)      *    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)      *    at java.lang.reflect.Constructor.newInstance(Constructor.java:513)      *    at org.apache.jackrabbit.oak.api.CommitFailedException.throwRepositoryException(CommitFailedException.java:57)      *    at org.apache.jackrabbit.oak.jcr.delegate.SessionDelegate.save(SessionDelegate.java:258)      *    at org.apache.jackrabbit.oak.jcr.SessionImpl.save(SessionImpl.java:277)      *    ...      *Caused by: org.apache.jackrabbit.oak.api.CommitFailedException: Cannot add node 'f1362578685631' at /      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:128)      *    at org.apache.jackrabbit.oak.spi.commit.CompositeValidator.childNodeAdded(CompositeValidator.java:68)      *    at org.apache.jackrabbit.oak.spi.commit.ValidatingHook$ValidatorDiff.childNodeAdded(ValidatingHook.java:159)      *    at org.apache.jackrabbit.oak.core.RootImpl.commit(RootImpl.java:250)      *    at org.apache.jackrabbit.oak.jcr.delegate.SessionDelegate.save(SessionDelegate.java:255)      *    ...      *Caused by: javax.jcr.nodetype.ConstraintViolationException: Node 'jcr:content' in 'nt:file' is mandatory      *    at org.apache.jackrabbit.oak.plugins.nodetype.EffectiveNodeTypeImpl.checkMandatoryItems(EffectiveNodeTypeImpl.java:288)      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:125)      *    ...      * }      *<pre>      */
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
comment|/**      * OAK-939 - Change in behaviour from JR2. Following testcase leads to      * CommitFailedException but it passes in JR2      */
annotation|@
name|Test
specifier|public
name|void
name|removeNodeInDifferentSession
parameter_list|()
throws|throws
name|RepositoryException
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
literal|"/"
operator|+
name|testNode
decl_stmt|;
comment|//Create the test node
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
comment|//TestCase would pass if the sessionRefreshInterval is set to zero
name|boolean
name|refreshIntervalZero
init|=
literal|false
decl_stmt|;
name|Session
name|s3
init|=
name|createSessionWithRefreshInterval
argument_list|(
name|refreshIntervalZero
argument_list|)
decl_stmt|;
name|Session
name|s2
init|=
name|createSessionWithRefreshInterval
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
name|s3
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Session
name|createSessionWithRefreshInterval
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
name|attrs
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
block|}
end_class

end_unit

