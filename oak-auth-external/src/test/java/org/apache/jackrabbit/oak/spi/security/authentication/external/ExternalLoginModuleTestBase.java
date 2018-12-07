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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncConfig
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
name|DefaultSyncHandler
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
name|ExternalIDPManagerImpl
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
name|ExternalLoginModule
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
name|SyncManagerImpl
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|Before
import|;
end_import

begin_comment
comment|/**  * Abstract base test for external-authentication including proper OSGi service  * registrations required for repository login respecting the {@link ExternalLoginModule}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ExternalLoginModuleTestBase
extends|extends
name|AbstractExternalAuthTest
block|{
specifier|private
name|Registration
name|testIdpReg
decl_stmt|;
specifier|private
name|Registration
name|syncHandlerReg
decl_stmt|;
specifier|protected
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|protected
name|SyncManager
name|syncManager
decl_stmt|;
specifier|protected
name|ExternalIdentityProviderManager
name|idpManager
decl_stmt|;
annotation|@
name|Before
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
name|testIdpReg
operator|=
name|whiteboard
operator|.
name|register
argument_list|(
name|ExternalIdentityProvider
operator|.
name|class
argument_list|,
name|idp
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|setSyncConfig
argument_list|(
name|syncConfig
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_HANDLER_NAME
argument_list|,
name|syncConfig
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_IDP_NAME
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testIdpReg
operator|!=
literal|null
condition|)
block|{
name|testIdpReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|testIdpReg
operator|=
literal|null
expr_stmt|;
block|}
name|setSyncConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Oak
name|withEditors
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|super
operator|.
name|withEditors
argument_list|(
name|oak
argument_list|)
expr_stmt|;
comment|// register non-OSGi managers
name|whiteboard
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
name|syncManager
operator|=
operator|new
name|SyncManagerImpl
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|whiteboard
operator|.
name|register
argument_list|(
name|SyncManager
operator|.
name|class
argument_list|,
name|syncManager
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|idpManager
operator|=
operator|new
name|ExternalIDPManagerImpl
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|whiteboard
operator|.
name|register
argument_list|(
name|ExternalIdentityProviderManager
operator|.
name|class
argument_list|,
name|idpManager
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|oak
return|;
block|}
specifier|protected
name|void
name|setSyncConfig
parameter_list|(
name|DefaultSyncConfig
name|cfg
parameter_list|)
block|{
if|if
condition|(
name|syncHandlerReg
operator|!=
literal|null
condition|)
block|{
name|syncHandlerReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|syncHandlerReg
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|syncHandlerReg
operator|=
name|whiteboard
operator|.
name|register
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|(
name|cfg
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|AppConfigurationEntry
name|entry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|ExternalLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|options
argument_list|)
decl_stmt|;
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
name|entry
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

