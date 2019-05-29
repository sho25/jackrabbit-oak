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
package|;
end_package

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
name|Maps
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
name|osgi
operator|.
name|OsgiUtil
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
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Constants
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
import|import static
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
name|AuthenticationConfiguration
operator|.
name|PARAM_APP_NAME
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|AuthenticationConfigurationImplOSGiTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AuthenticationConfigurationImpl
name|authenticationConfiguration
init|=
operator|new
name|AuthenticationConfigurationImpl
argument_list|()
decl_stmt|;
specifier|private
name|SimpleCredentials
name|sc
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
name|authenticationConfiguration
operator|.
name|setSecurityProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParameters
parameter_list|()
block|{
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|authenticationConfiguration
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PARAM_APP_NAME
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|expected
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PARAM_APP_NAME
argument_list|,
literal|"name"
argument_list|,
name|Constants
operator|.
name|SERVICE_PID
argument_list|,
name|authenticationConfiguration
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Maps
operator|.
name|difference
argument_list|(
name|expected
argument_list|,
name|authenticationConfiguration
operator|.
name|getParameters
argument_list|()
argument_list|)
operator|.
name|areEqual
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
