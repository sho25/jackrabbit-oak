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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_comment
comment|/**  * Provides helper methods for creating {@link MountInfoProvider} instances.  *  */
end_comment

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
specifier|private
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
name|isSupportFragmentUnder
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
comment|/**      * Returns a {@link MountInfoProvider} which is configured only with the default Mount      *       * @return the default MountInfoProvider      */
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
comment|/**      * Creates a new Builder instance for configuring more complex mount setups      *       * @return a new builder instance      */
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Provides a fluent API from creating {@link MountInfoProvider} instances      */
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Mount
argument_list|>
name|mounts
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{         }
comment|/**          * Adds a new read-write {@link Mount} with the specified name and paths          *           * @param name the name of the mount          * @param paths the paths handled by the mount          * @return this builder instance          */
specifier|public
name|Builder
name|mount
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountInfo
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|singletonList
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Adds a new read-only Mount with the specified name and paths          *           * @param name the name of the mount          * @param paths the paths handled by the mount          * @return this builder instance          */
specifier|public
name|Builder
name|readOnlyMount
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountInfo
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|singletonList
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Adds a new Mount instance with the specified parameters          *           * @param name the name of the mount          * @param readOnly true for read-only paths, false otherwise          * @param pathsSupportingFragments the paths supporting fragments, see {@link Mount#getPathFragmentName()}          * @param paths the paths handled by the mount          * @return this builder instance          */
specifier|public
name|Builder
name|mount
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|readOnly
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pathsSupportingFragments
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountInfo
argument_list|(
name|name
argument_list|,
name|readOnly
argument_list|,
name|pathsSupportingFragments
argument_list|,
name|paths
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Creates a new {@link MountInfoProvider}          *           * @return a newly-created MountInfoProvider          */
specifier|public
name|MountInfoProvider
name|build
parameter_list|()
block|{
return|return
operator|new
name|SimpleMountInfoProvider
argument_list|(
name|mounts
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

