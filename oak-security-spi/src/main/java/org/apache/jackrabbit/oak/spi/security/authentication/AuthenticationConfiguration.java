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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|spi
operator|.
name|security
operator|.
name|SecurityConfiguration
import|;
end_import

begin_comment
comment|/**  * Interface for the authentication setup.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthenticationConfiguration
extends|extends
name|SecurityConfiguration
block|{
name|String
name|NAME
init|=
literal|"org.apache.jackrabbit.oak.authentication"
decl_stmt|;
name|String
name|PARAM_APP_NAME
init|=
literal|"org.apache.jackrabbit.oak.authentication.appName"
decl_stmt|;
name|String
name|DEFAULT_APP_NAME
init|=
literal|"jackrabbit.oak"
decl_stmt|;
name|String
name|PARAM_CONFIG_SPI_NAME
init|=
literal|"org.apache.jackrabbit.oak.authentication.configSpiName"
decl_stmt|;
annotation|@
name|Nonnull
name|LoginContextProvider
name|getLoginContextProvider
parameter_list|(
name|ContentRepository
name|contentRepository
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
