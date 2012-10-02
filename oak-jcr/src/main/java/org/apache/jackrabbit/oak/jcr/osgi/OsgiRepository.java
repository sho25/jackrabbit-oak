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
name|jcr
operator|.
name|osgi
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
name|ScheduledExecutorService
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
name|ContentRepository
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
name|RepositoryImpl
import|;
end_import

begin_comment
comment|/**  * Workaround to a JAAS class loading issue in OSGi environments.  *  * @see<a href="https://issues.apache.org/jira/browse/OAK-256">OAK-256</a>  */
end_comment

begin_class
specifier|public
class|class
name|OsgiRepository
extends|extends
name|RepositoryImpl
block|{
specifier|public
name|OsgiRepository
parameter_list|(
name|ContentRepository
name|repository
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|repository
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspace
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO: The context class loader hack below shouldn't be needed
comment|// with a properly OSGi-compatible JAAS implementation
name|Thread
name|thread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|thread
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
try|try
block|{
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|Oak
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|login
argument_list|(
name|credentials
argument_list|,
name|workspace
argument_list|)
return|;
block|}
finally|finally
block|{
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

