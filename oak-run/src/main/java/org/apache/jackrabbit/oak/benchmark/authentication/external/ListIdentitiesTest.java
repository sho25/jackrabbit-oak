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
name|benchmark
operator|.
name|authentication
operator|.
name|external
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
name|Iterator
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
name|api
operator|.
name|JackrabbitSession
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
name|user
operator|.
name|UserManager
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
name|ConfigurationParameters
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
name|authentication
operator|.
name|ConfigurationUtil
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
name|authentication
operator|.
name|external
operator|.
name|SyncedIdentity
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|jmx
operator|.
name|SyncMBeanImpl
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|jmx
operator|.
name|SynchronizationMBean
import|;
end_import

begin_comment
comment|/**  * Benchmark for {@link org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler#listIdentities(UserManager)}  */
end_comment

begin_class
specifier|public
class|class
name|ListIdentitiesTest
extends|extends
name|AbstractExternalTest
block|{
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|AUTO_IDS
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
static|static
block|{
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
name|AUTO_IDS
operator|.
name|add
argument_list|(
literal|"autoGroup"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ListIdentitiesTest
parameter_list|(
name|int
name|numberOfUsers
parameter_list|)
block|{
name|super
argument_list|(
name|numberOfUsers
argument_list|,
literal|100
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|,
name|AUTO_IDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
return|return
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|beforeSuite
argument_list|()
expr_stmt|;
name|SynchronizationMBean
name|bean
init|=
operator|new
name|SyncMBeanImpl
argument_list|(
name|getContentRepository
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
name|syncManager
argument_list|,
literal|"default"
argument_list|,
name|idpManager
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|bean
operator|.
name|syncAllExternalUsers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|s
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|systemLogin
argument_list|()
operator|)
decl_stmt|;
try|try
block|{
name|UserManager
name|userManager
init|=
name|s
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|it
init|=
name|syncHandler
operator|.
name|listIdentities
argument_list|(
name|userManager
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
