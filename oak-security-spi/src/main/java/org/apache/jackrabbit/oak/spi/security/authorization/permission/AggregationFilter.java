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
name|authorization
operator|.
name|permission
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

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
name|Set
import|;
end_import

begin_interface
specifier|public
interface|interface
name|AggregationFilter
block|{
comment|/**      *      * @param permissionProvider      * @param principals      * @return {@code true} if aggregation of permission providers should be stopped after the given {@code permissionProvider}      * created for the given set of {@code principals}.      */
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|AggregatedPermissionProvider
name|permissionProvider
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
function_decl|;
comment|/**      *      * @param accessControlManager      * @param principals      * @return {@code true} if aggregation of effective policies for the specified principals should be stopped after      * the given {@code accessControlManager}.      * @see AccessControlManager#getEffectivePolicies(String)      */
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|JackrabbitAccessControlManager
name|accessControlManager
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
function_decl|;
comment|/**      *      * @param accessControlManager      * @param absPath      * @return {@code true} if aggregation of effective policies for the specified effective path should be stopped after      * the given {@code accessControlManager}.      * @see JackrabbitAccessControlManager#getEffectivePolicies(Set)      */
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|AccessControlManager
name|accessControlManager
parameter_list|,
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|)
function_decl|;
comment|/**      * Default implementation of the {@code AggregationFilter} interface that handles all combinations of permission      * providers and principals and never aborts the evaluation.      */
name|AggregationFilter
name|DEFAULT
init|=
operator|new
name|AggregationFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|AggregatedPermissionProvider
name|permissionProvider
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|JackrabbitAccessControlManager
name|accessControlManager
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|stop
parameter_list|(
annotation|@
name|NotNull
name|AccessControlManager
name|accessControlManager
parameter_list|,
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

