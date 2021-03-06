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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ExternalIdentityRef
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
comment|/**  * Benchmark for {@link SynchronizationMBean#syncExternalUsers(String[])}  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalNameResolutionTest
extends|extends
name|AbstractExternalTest
block|{
specifier|private
name|SynchronizationMBean
name|bean
decl_stmt|;
specifier|public
name|PrincipalNameResolutionTest
parameter_list|(
name|int
name|numberOfUsers
parameter_list|,
name|int
name|membershipSize
parameter_list|,
name|long
name|expTime
parameter_list|,
name|int
name|roundtripDelay
parameter_list|)
block|{
name|super
argument_list|(
name|numberOfUsers
argument_list|,
name|membershipSize
argument_list|,
name|expTime
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
name|roundtripDelay
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
name|bean
operator|=
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
name|syncConfig
operator|.
name|getName
argument_list|()
argument_list|,
name|idpManager
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
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
name|bean
operator|.
name|syncExternalUsers
argument_list|(
operator|new
name|String
index|[]
block|{
operator|new
name|ExternalIdentityRef
argument_list|(
name|getRandomUserId
argument_list|()
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

