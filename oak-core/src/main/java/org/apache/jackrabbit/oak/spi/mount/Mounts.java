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
name|Collections
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|Mounts
block|{
specifier|private
name|Mounts
parameter_list|()
block|{     }
specifier|static
specifier|final
name|MountInfoProvider
name|DEFAULT_PROVIDER
init|=
operator|new
name|MountInfoProvider
argument_list|()
block|{
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
return|return
name|DEFAULT_MOUNT
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
name|Collections
operator|.
name|emptySet
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
name|DEFAULT_MOUNT
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|?
name|DEFAULT_MOUNT
else|:
literal|null
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
literal|false
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
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
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
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
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
name|DEFAULT_MOUNT
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Default Mount info which indicates that no explicit mount is created for      * given path      */
specifier|private
specifier|static
name|Mount
name|DEFAULT_MOUNT
init|=
operator|new
name|DefaultMount
argument_list|()
decl_stmt|;
specifier|static
specifier|final
class|class
name|DefaultMount
implements|implements
name|Mount
block|{
specifier|private
specifier|final
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
decl_stmt|;
name|DefaultMount
parameter_list|()
block|{
name|this
argument_list|(
name|Collections
operator|.
expr|<
name|Mount
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DefaultMount
parameter_list|(
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
parameter_list|)
block|{
name|this
operator|.
name|mounts
operator|=
name|mounts
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDefault
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathFragmentName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportFragment
parameter_list|(
name|String
name|path
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
name|isMounted
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
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUnder
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
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDirectlyUnder
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
control|)
block|{
if|if
condition|(
name|m
operator|.
name|isDirectlyUnder
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|public
specifier|static
name|MountInfoProvider
name|defaultMountInfoProvider
parameter_list|()
block|{
return|return
name|DEFAULT_PROVIDER
return|;
block|}
specifier|public
specifier|static
name|Mount
name|defaultMount
parameter_list|()
block|{
return|return
name|DEFAULT_MOUNT
return|;
block|}
specifier|public
specifier|static
name|Mount
name|defaultMount
parameter_list|(
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
parameter_list|)
block|{
return|return
operator|new
name|DefaultMount
argument_list|(
name|mounts
argument_list|)
return|;
block|}
block|}
end_class

end_unit

