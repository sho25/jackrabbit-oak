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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|core
operator|.
name|MicroKernelImpl
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
name|security
operator|.
name|OakConfiguration
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
name|NotExecutableException
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
name|RepositoryStub
import|;
end_import

begin_class
specifier|public
class|class
name|OakRepositoryStubBase
extends|extends
name|RepositoryStub
block|{
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
comment|/**      * Constructor as required by the JCR TCK.      *       * @param settings repository settings      * @throws javax.jcr.RepositoryException If an error occurs.      */
specifier|public
name|OakRepositoryStubBase
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
comment|// TODO: OAK-17. workaround for missing test configuration
name|Configuration
operator|.
name|setConfiguration
argument_list|(
operator|new
name|OakConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|dir
init|=
literal|"target/mk-tck-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|(
name|dir
argument_list|)
argument_list|)
decl_stmt|;
name|jcr
operator|.
name|with
argument_list|(
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|preCreateRepository
argument_list|(
name|jcr
argument_list|)
expr_stmt|;
name|repository
operator|=
name|jcr
operator|.
name|createRepository
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|preCreateRepository
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{     }
comment|/**      * Returns the configured repository instance.      *       * @return the configured repository instance.      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
annotation|@
name|Override
specifier|public
name|Credentials
name|getReadOnlyCredentials
parameter_list|()
block|{
return|return
operator|new
name|GuestCredentials
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getKnownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
specifier|private
specifier|static
specifier|final
name|Principal
name|UNKNOWN_PRINCIPAL
init|=
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"an_unknown_user"
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Principal
name|getUnknownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
return|return
name|UNKNOWN_PRINCIPAL
return|;
block|}
block|}
end_class

end_unit

