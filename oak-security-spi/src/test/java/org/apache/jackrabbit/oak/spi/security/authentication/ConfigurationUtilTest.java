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
name|spi
operator|.
name|security
operator|.
name|authentication
package|;
end_package

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
name|junit
operator|.
name|Test
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
name|AppConfigurationEntry
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

begin_class
specifier|public
class|class
name|ConfigurationUtilTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGetDefaultConfiguration
parameter_list|()
block|{
name|Configuration
name|c
init|=
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|AppConfigurationEntry
index|[]
name|entries
init|=
name|c
operator|.
name|getAppConfigurationEntry
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
name|AppConfigurationEntry
name|entry
init|=
name|entries
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl"
argument_list|,
name|entry
operator|.
name|getLoginModuleName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|entry
operator|.
name|getControlFlag
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetJr2Configuration
parameter_list|()
block|{
name|Configuration
name|c
init|=
name|ConfigurationUtil
operator|.
name|getJackrabbit2Configuration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|AppConfigurationEntry
index|[]
name|entries
init|=
name|c
operator|.
name|getAppConfigurationEntry
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GuestLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|entries
index|[
literal|0
index|]
operator|.
name|getLoginModuleName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|OPTIONAL
argument_list|,
name|entries
index|[
literal|0
index|]
operator|.
name|getControlFlag
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.jackrabbit.oak.security.authentication.token.TokenLoginModule"
argument_list|,
name|entries
index|[
literal|1
index|]
operator|.
name|getLoginModuleName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|SUFFICIENT
argument_list|,
name|entries
index|[
literal|1
index|]
operator|.
name|getControlFlag
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl"
argument_list|,
name|entries
index|[
literal|2
index|]
operator|.
name|getLoginModuleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
