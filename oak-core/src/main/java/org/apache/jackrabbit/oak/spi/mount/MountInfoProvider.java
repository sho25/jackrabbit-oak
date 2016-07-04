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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_interface
specifier|public
interface|interface
name|MountInfoProvider
block|{
name|MountInfoProvider
name|DEFAULT
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
name|Mount
operator|.
name|DEFAULT
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
empty_stmt|;
block|}
decl_stmt|;
comment|/**      * Maps a given path to logical store name.      *      * @param path node path for which backing store location is to be determined      * @return mountInfo for the given path. If no explicit mount configured then      * default mount would be returned      */
name|Mount
name|getMountByPath
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Set of non default mount points configured for the setup      */
name|Collection
argument_list|<
name|Mount
argument_list|>
name|getNonDefaultMounts
parameter_list|()
function_decl|;
comment|/**      * Returns the mount instance for given mount name      *      * @param name name of the mount      * @return mount instance for given mount name. If no mount exists for given name      * null would be returned      */
annotation|@
name|CheckForNull
name|Mount
name|getMountByName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Return true if there are explicit mounts configured      */
name|boolean
name|hasNonDefaultMounts
parameter_list|()
function_decl|;
comment|/**      * Returns all mounts placed under the specified path      *       * @param path the path under which mounts are to be found      * @return a collection of mounts, possibly empty      */
name|Collection
argument_list|<
name|Mount
argument_list|>
name|getMountsPlacedUnder
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

