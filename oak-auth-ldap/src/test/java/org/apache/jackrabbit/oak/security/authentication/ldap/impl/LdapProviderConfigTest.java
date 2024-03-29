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
name|ldap
operator|.
name|impl
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
name|LdapProviderConfigTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testOfEmptyConfigurationParameters
parameter_list|()
block|{
name|LdapProviderConfig
name|config
init|=
name|LdapProviderConfig
operator|.
name|of
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LdapProviderConfig
operator|.
name|PARAM_NAME_DEFAULT
argument_list|,
name|config
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
name|testOfConfigurationParameters
parameter_list|()
block|{
name|LdapProviderConfig
name|config
init|=
name|LdapProviderConfig
operator|.
name|of
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|LdapProviderConfig
operator|.
name|PARAM_NAME
argument_list|,
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
name|config
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
name|testOfConfigurationParametersIncludingSearchTimeout
parameter_list|()
block|{
name|LdapProviderConfig
name|config
init|=
name|LdapProviderConfig
operator|.
name|of
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|LdapProviderConfig
operator|.
name|PARAM_SEARCH_TIMEOUT
argument_list|,
literal|25
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|config
operator|.
name|getSearchTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

