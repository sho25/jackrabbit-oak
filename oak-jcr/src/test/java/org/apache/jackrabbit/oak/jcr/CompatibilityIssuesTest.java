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
comment|/**  * This class contains test cases which demonstrate changes in behaviour wrt. to Jackrabbit 2.  * See OAK-14: Identify and document changes in behaviour wrt. Jackrabbit 2  */
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
name|createAnonymousSession
argument_list|()
decl_stmt|;
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
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
name|Session
name|session1
init|=
name|createAnonymousSession
argument_list|()
decl_stmt|;
name|Session
name|session2
init|=
name|createAnonymousSession
argument_list|()
decl_stmt|;
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
comment|// Throws on JR2, not on JR3
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
comment|// Throws on JR3
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
name|session0
operator|.
name|logout
argument_list|()
expr_stmt|;
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
name|session3
operator|.
name|logout
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
block|}
end_class

end_unit

