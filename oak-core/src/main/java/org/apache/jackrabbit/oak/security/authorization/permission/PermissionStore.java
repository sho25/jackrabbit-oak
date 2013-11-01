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
name|authorization
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * The permission store is used to store and provide access control permissions for principals. It is responsible to  * load and store the permissions in an optimal form in the repository and must not cache them.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PermissionStore
block|{
name|void
name|load
parameter_list|(
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
parameter_list|,
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
function_decl|;
name|void
name|load
parameter_list|(
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|entries
parameter_list|,
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
name|PrincipalPermissionEntries
name|load
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
function_decl|;
name|boolean
name|hasPermissionEntries
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
function_decl|;
name|long
name|getNumEntries
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
function_decl|;
name|long
name|getTimestamp
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

