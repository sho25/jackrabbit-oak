begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|mount
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
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Lists
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
name|spi
operator|.
name|mount
operator|.
name|Mount
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

begin_comment
comment|/**  * A simple and inefficient implementation to manage mount points  */
end_comment

begin_class
specifier|final
class|class
name|SimpleMountInfoProvider
implements|implements
name|MountInfoProvider
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Mount
argument_list|>
name|mounts
decl_stmt|;
specifier|private
specifier|final
name|Mount
name|defMount
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|hasMounts
decl_stmt|;
name|SimpleMountInfoProvider
parameter_list|(
name|List
argument_list|<
name|Mount
argument_list|>
name|mountInfos
parameter_list|)
block|{
name|this
operator|.
name|mounts
operator|=
name|getMounts
argument_list|(
name|mountInfos
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasMounts
operator|=
operator|!
name|this
operator|.
name|mounts
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|this
operator|.
name|defMount
operator|=
name|defaultMount
argument_list|(
name|this
operator|.
name|mounts
argument_list|)
expr_stmt|;
comment|//TODO add validation of mountpoints
block|}
annotation|@
name|Override
specifier|public
name|Mount
name|getMountByPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|Mount
name|m
range|:
name|mounts
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|isMounted
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|m
return|;
block|}
block|}
return|return
name|defMount
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Mount
argument_list|>
name|getNonDefaultMounts
parameter_list|()
block|{
return|return
name|mounts
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Mount
name|getMountByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mounts
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNonDefaultMounts
parameter_list|()
block|{
return|return
name|hasMounts
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Mount
argument_list|>
name|getMountsPlacedUnder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Mount
name|mount
range|:
name|this
operator|.
name|mounts
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|mount
operator|.
name|isUnder
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|mounts
operator|.
name|add
argument_list|(
name|mount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mounts
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Mount
argument_list|>
name|getMountsPlacedDirectlyUnder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Mount
name|mount
range|:
name|this
operator|.
name|mounts
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|mount
operator|.
name|isDirectlyUnder
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|mounts
operator|.
name|add
argument_list|(
name|mount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mounts
return|;
block|}
annotation|@
name|Override
specifier|public
name|Mount
name|getDefaultMount
parameter_list|()
block|{
return|return
name|defMount
return|;
block|}
comment|//~----------------------------------------< builder>
comment|//~----------------------------------------< private>
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Mount
argument_list|>
name|getMounts
parameter_list|(
name|List
argument_list|<
name|Mount
argument_list|>
name|mountInfos
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Mount
argument_list|>
name|mounts
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Mount
name|mi
range|:
name|mountInfos
control|)
block|{
name|mounts
operator|.
name|put
argument_list|(
name|mi
operator|.
name|getName
argument_list|()
argument_list|,
name|mi
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mounts
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Mount
name|defaultMount
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Mount
argument_list|>
name|mounts
parameter_list|)
block|{
return|return
name|Mounts
operator|.
name|defaultMount
argument_list|(
name|mounts
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

