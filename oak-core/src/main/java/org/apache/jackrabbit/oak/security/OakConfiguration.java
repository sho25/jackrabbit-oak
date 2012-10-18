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
name|security
operator|.
name|authentication
operator|.
name|user
operator|.
name|LoginModuleImpl
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * OakConfiguration... tmp solution missing repo-configuration in test-setup.  * TODO: remove again once OAK-17 is addressed.  */
end_comment

begin_class
specifier|public
class|class
name|OakConfiguration
extends|extends
name|Configuration
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OakConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|ConfigurationParameters
name|loginConfiguration
decl_stmt|;
specifier|public
name|OakConfiguration
parameter_list|()
block|{
name|this
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OakConfiguration
parameter_list|(
name|ConfigurationParameters
name|loginConfiguration
parameter_list|)
block|{
name|this
operator|.
name|loginConfiguration
operator|=
name|loginConfiguration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|applicationName
parameter_list|)
block|{
name|AppConfigurationEntry
name|entry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|LoginModuleImpl
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
name|loginConfiguration
operator|.
name|getConfigValue
argument_list|(
name|applicationName
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
end_class

end_unit

