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
name|composite
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
name|Collections
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
name|Root
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
name|SecurityProviderImpl
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
name|authorization
operator|.
name|permission
operator|.
name|AbstractPermissionRandomTestIT
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|base
operator|.
name|Preconditions
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
name|Iterators
import|;
end_import

begin_class
specifier|public
class|class
name|MutiplexingProviderRandomTestIT
extends|extends
name|AbstractPermissionRandomTestIT
block|{
specifier|private
name|MountInfoProvider
name|mountInfoProvider
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
name|String
index|[]
name|mpxs
init|=
operator|new
name|String
index|[]
block|{
name|Iterators
operator|.
name|get
argument_list|(
name|allowU
operator|.
name|iterator
argument_list|()
argument_list|,
name|allowU
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
block|}
decl_stmt|;
name|Mounts
operator|.
name|Builder
name|builder
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|mpxs
control|)
block|{
name|builder
operator|.
name|mount
argument_list|(
literal|"m"
operator|+
name|i
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|mountInfoProvider
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|PermissionProvider
name|candidatePermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|ConfigurationParameters
name|authConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|AccessControlConstants
operator|.
name|PARAM_MOUNT_PROVIDER
argument_list|,
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|mountInfoProvider
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SecurityProviderImpl
name|sp
init|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|authConfig
argument_list|)
decl_stmt|;
name|AuthorizationConfiguration
name|acConfig
init|=
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|acConfig
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|)
return|;
block|}
block|}
end_class

end_unit
