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
name|composite
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|PropertyUnbounded
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
name|commons
operator|.
name|PropertiesUtil
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
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
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

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak MountInfoProvider"
argument_list|)
specifier|public
class|class
name|MountInfoProviderService
block|{
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Mounted paths"
argument_list|,
name|unbounded
operator|=
name|PropertyUnbounded
operator|.
name|ARRAY
argument_list|,
name|description
operator|=
literal|"Paths which are part of private mount"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_MOUNT_PATHS
init|=
literal|"mountedPaths"
decl_stmt|;
specifier|static
specifier|final
name|String
name|PROP_MOUNT_NAME_DEFAULT
init|=
literal|"private"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Mount name"
argument_list|,
name|description
operator|=
literal|"Name of the mount"
argument_list|,
name|value
operator|=
name|PROP_MOUNT_NAME_DEFAULT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_MOUNT_NAME
init|=
literal|"mountName"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|PROP_MOUNT_READONLY_DEFAULT
init|=
literal|false
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Readonly"
argument_list|,
name|description
operator|=
literal|"If enabled then mount would be considered as readonly"
argument_list|,
name|boolValue
operator|=
name|PROP_MOUNT_READONLY_DEFAULT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_MOUNT_READONLY
init|=
literal|"readOnlyMount"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|PROP_PATHS_SUPPORTING_FRAGMENTS_DEFAULT
init|=
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Paths supporting fragments"
argument_list|,
name|description
operator|=
literal|"oak:mount-* under this paths will be included to mounts"
argument_list|,
name|value
operator|=
block|{
literal|"/"
block|}
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_PATHS_SUPPORTING_FRAGMENTS
init|=
literal|"pathsSupportingFragments"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ServiceRegistration
name|reg
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|String
index|[]
name|paths
init|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MOUNT_PATHS
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|mountName
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MOUNT_NAME
argument_list|)
argument_list|,
name|PROP_MOUNT_NAME_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|readOnly
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MOUNT_READONLY
argument_list|)
argument_list|,
name|PROP_MOUNT_READONLY_DEFAULT
argument_list|)
decl_stmt|;
name|String
index|[]
name|pathsSupportingFragments
init|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_PATHS_SUPPORTING_FRAGMENTS
argument_list|)
argument_list|,
name|PROP_PATHS_SUPPORTING_FRAGMENTS_DEFAULT
argument_list|)
decl_stmt|;
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|paths
operator|!=
literal|null
condition|)
block|{
name|mip
operator|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
name|mountName
operator|.
name|trim
argument_list|()
argument_list|,
name|readOnly
argument_list|,
name|trim
argument_list|(
name|pathsSupportingFragments
argument_list|)
argument_list|,
name|trim
argument_list|(
name|paths
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Enabling mount for {}"
argument_list|,
name|mip
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No mount config provided. Mounting would be disabled"
argument_list|)
expr_stmt|;
block|}
name|reg
operator|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|MountInfoProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|mip
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|trim
parameter_list|(
name|String
index|[]
name|array
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|array
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|array
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|s
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|reg
operator|!=
literal|null
condition|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|reg
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
