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
name|security
operator|.
name|authentication
operator|.
name|token
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
name|List
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
name|ImmutableSet
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
name|AbstractSecurityTest
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
name|commit
operator|.
name|MoveTracker
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
name|commit
operator|.
name|ValidatorProvider
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
name|token
operator|.
name|TokenConfiguration
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
name|token
operator|.
name|TokenProvider
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
name|assertNotNull
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

begin_class
specifier|public
class|class
name|TokenConfigurationImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_EXPIRATION
init|=
literal|2
operator|*
literal|3600
operator|*
literal|1000
decl_stmt|;
specifier|private
name|TokenConfigurationImpl
name|tc
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|tc
operator|=
operator|new
name|TokenConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|config
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
literal|60
argument_list|,
name|TokenProvider
operator|.
name|PARAM_TOKEN_REFRESH
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|TokenConfiguration
operator|.
name|NAME
argument_list|,
name|config
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TokenConfiguration
operator|.
name|NAME
argument_list|,
name|tc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConfigOptions
parameter_list|()
block|{
name|int
name|exp
init|=
name|tc
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
name|DEFAULT_EXPIRATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConfigOptions2
parameter_list|()
block|{
name|int
name|exp
init|=
name|getConfig
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
name|DEFAULT_EXPIRATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRefresh
parameter_list|()
block|{
name|boolean
name|refresh
init|=
name|getConfig
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_REFRESH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetValidators
parameter_list|()
block|{
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|validators
init|=
name|tc
operator|.
name|getValidators
argument_list|(
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|validators
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validators
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|TokenValidatorProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTokenProvider
parameter_list|()
block|{
name|TokenProvider
name|tp
init|=
name|tc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|TokenProviderImpl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

