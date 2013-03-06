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
name|oak
operator|.
name|Oak
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
name|ContentSession
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
name|Root
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
name|Tree
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
name|assertFalse
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
comment|/**      * Trans-session isolation differs from Jackrabbit 2. Snapshot isolation can      * result in write skew as this test demonstrates: the check method enforces      * an application logic constraint which says that the sum of the properties      * p1 and p2 must not be negative. While session1 and session2 each enforce      * this constraint before saving, the constraint might not hold globally as      * can be seen in session3.      *      * @see<a href="http://wiki.apache.org/jackrabbit/Transactional%20model%20of%20the%20Microkernel%20based%20Jackrabbit%20prototype">      *     Transactional model of the Microkernel based Jackrabbit prototype</a>      */
annotation|@
name|Test
specifier|public
name|void
name|sessionIsolation
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session0
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Session
name|session1
init|=
literal|null
decl_stmt|;
name|Session
name|session2
init|=
literal|null
decl_stmt|;
try|try
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
name|session1
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
name|session2
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
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
finally|finally
block|{
name|session0
operator|.
name|logout
argument_list|()
expr_stmt|;
if|if
condition|(
name|session1
operator|!=
literal|null
condition|)
block|{
name|session1
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|session2
operator|!=
literal|null
condition|)
block|{
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|move2
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|ContentSession
name|session
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|createContentSession
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|r
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|x
init|=
name|r
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|r
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|x
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|move
argument_list|(
literal|"/x"
argument_list|,
literal|"/y/x"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// assertEquals("y", x.getParent().getName());  // passed on JR2, fails on Oak
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|x
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// fails on JR2, passes on Oak
block|}
comment|/**      * Type checks are deferred to the Session#save call instead of the      * Node#addNode method like in Jackrabbit2.      *<p>Stacktrace in JR2:</p>      *<pre>      * {@code      * javax.jcr.nodetype.ConstraintViolationException: No child node definition for fail found in node /f1362578560413      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1276)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:111)      *     at org.apache.jackrabbit.core.session.AddNodeOperation.perform(AddNodeOperation.java:1)      *     at org.apache.jackrabbit.core.session.SessionState.perform(SessionState.java:216)      *     at org.apache.jackrabbit.core.ItemImpl.perform(ItemImpl.java:91)      *     at org.apache.jackrabbit.core.NodeImpl.addNodeWithUuid(NodeImpl.java:1814)      *     at org.apache.jackrabbit.core.NodeImpl.addNode(NodeImpl.java:1774)      * }      *<pre>      *<p>Stacktrace in Oak:</p>      *<pre>      * {@code      *javax.jcr.nodetype.ConstraintViolationException      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)      *    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)      *    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)      *    at java.lang.reflect.Constructor.newInstance(Constructor.java:513)      *    at org.apache.jackrabbit.oak.api.CommitFailedException.throwRepositoryException(CommitFailedException.java:57)      *    at org.apache.jackrabbit.oak.jcr.SessionDelegate.save(SessionDelegate.java:258)      *    at org.apache.jackrabbit.oak.jcr.SessionImpl.save(SessionImpl.java:277)      *    ...      *Caused by: org.apache.jackrabbit.oak.api.CommitFailedException: Cannot add node 'f1362578685631' at /      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:128)      *    at org.apache.jackrabbit.oak.spi.commit.CompositeValidator.childNodeAdded(CompositeValidator.java:68)      *    at org.apache.jackrabbit.oak.spi.commit.ValidatingHook$ValidatorDiff.childNodeAdded(ValidatingHook.java:159)      *    at org.apache.jackrabbit.oak.core.RootImpl.commit(RootImpl.java:250)      *    at org.apache.jackrabbit.oak.jcr.SessionDelegate.save(SessionDelegate.java:255)      *    ...      *Caused by: javax.jcr.nodetype.ConstraintViolationException: Node 'jcr:content' in 'nt:file' is mandatory      *    at org.apache.jackrabbit.oak.plugins.nodetype.EffectiveNodeTypeImpl.checkMandatoryItems(EffectiveNodeTypeImpl.java:288)      *    at org.apache.jackrabbit.oak.plugins.nodetype.TypeValidator.childNodeAdded(TypeValidator.java:125)      *    ...      * }      *<pre>      */
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
block|}
end_class

end_unit

