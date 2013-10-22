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
name|security
operator|.
name|Principal
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|PropertyState
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
name|Tree
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
name|Type
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
name|PathUtils
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
name|core
operator|.
name|ImmutableRoot
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
name|core
operator|.
name|ImmutableTree
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
name|PermissionConstants
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
name|state
operator|.
name|NodeBuilder
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
name|Strings
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
name|util
operator|.
name|TreeLocation
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
name|util
operator|.
name|Text
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
comment|/**  * PermissionUtil... TODO  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PermissionUtil
implements|implements
name|PermissionConstants
block|{
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
name|PermissionUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PermissionUtil
parameter_list|()
block|{}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getParentPathOrNull
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|int
name|idx
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|==
literal|0
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|getEntryName
parameter_list|(
annotation|@
name|Nullable
name|String
name|accessControlledPath
parameter_list|)
block|{
name|String
name|path
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|accessControlledPath
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|path
operator|.
name|hashCode
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|long
name|getNumPermissions
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|node
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
decl_stmt|;
return|return
name|property
operator|==
literal|null
condition|?
literal|0
else|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|long
name|getNumPermissions
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|node
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
decl_stmt|;
return|return
name|property
operator|==
literal|null
condition|?
literal|0
else|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|checkACLPath
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|node
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|checkACLPath
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|node
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|ImmutableTree
name|getPermissionsRoot
parameter_list|(
name|ImmutableRoot
name|immutableRoot
parameter_list|,
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|immutableRoot
operator|.
name|getTree
argument_list|(
name|PERMISSIONS_STORE_PATH
operator|+
literal|'/'
operator|+
name|workspaceName
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Tree
name|getPrincipalRoot
parameter_list|(
name|Tree
name|permissionsTree
parameter_list|,
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|permissionsTree
operator|.
name|getChild
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|getType
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
comment|// TODO: OAK-753 decide on where to filter out hidden items.
comment|// TODO: deal with hidden properties
return|return
name|tree
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|TreeLocation
name|createLocation
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableRoot
name|immutableRoot
parameter_list|,
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|!=
literal|null
operator|&&
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
return|return
name|TreeLocation
operator|.
name|create
argument_list|(
name|immutableRoot
argument_list|,
name|oakPath
argument_list|)
return|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to create location for path "
operator|+
name|oakPath
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|TreeLocation
name|createLocation
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
return|return
name|TreeLocation
operator|.
name|create
argument_list|(
name|tree
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|TreeLocation
operator|.
name|create
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

