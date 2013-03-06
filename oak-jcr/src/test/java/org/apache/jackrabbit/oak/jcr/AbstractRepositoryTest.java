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
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_comment
comment|/**  * Abstract base class for repository tests providing methods for accessing  * the repository, a session and nodes and properties from that session.  *  * Users of this class must call clear to close the session associated with  * this instance and clean up the repository when done.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRepositoryTest
block|{
specifier|protected
name|ScheduledExecutorService
name|executor
init|=
literal|null
decl_stmt|;
specifier|private
name|Repository
name|repository
init|=
literal|null
decl_stmt|;
specifier|private
name|Session
name|adminSession
init|=
literal|null
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|logout
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// release session field
if|if
condition|(
name|adminSession
operator|!=
literal|null
condition|)
block|{
name|adminSession
operator|.
name|logout
argument_list|()
expr_stmt|;
name|adminSession
operator|=
literal|null
expr_stmt|;
block|}
comment|// release repository field
name|repository
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|Repository
name|getRepository
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|repository
operator|=
operator|new
name|Jcr
argument_list|()
operator|.
name|with
argument_list|(
name|getExecutor
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
block|}
return|return
name|repository
return|;
block|}
specifier|private
name|ScheduledExecutorService
name|getExecutor
parameter_list|()
block|{
return|return
operator|(
name|executor
operator|==
literal|null
operator|)
condition|?
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|0
argument_list|)
else|:
name|executor
return|;
block|}
specifier|protected
name|Session
name|getAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|adminSession
operator|==
literal|null
condition|)
block|{
name|adminSession
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
block|}
return|return
name|adminSession
return|;
block|}
specifier|protected
name|Session
name|createAnonymousSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// FIXME: provider proper permission setup for the anonymous session (e.g. full read access)
comment|// return getRepository().login(new GuestCredentials());
return|return
name|createAdminSession
argument_list|()
return|;
block|}
specifier|protected
name|Session
name|createAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getRepository
argument_list|()
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
argument_list|)
return|;
block|}
block|}
end_class

end_unit

