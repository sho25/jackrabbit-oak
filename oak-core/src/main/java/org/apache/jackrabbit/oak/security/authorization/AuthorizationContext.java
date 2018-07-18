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
name|Iterables
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
name|plugins
operator|.
name|tree
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|Context
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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

begin_class
specifier|final
class|class
name|AuthorizationContext
implements|implements
name|Context
implements|,
name|AccessControlConstants
implements|,
name|PermissionConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NODE_NAMES
init|=
name|POLICY_NODE_NAMES
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|POLICY_NODE_NAMES
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|PROPERTY_NAMES
init|=
name|ACE_PROPERTY_NAMES
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|ACE_PROPERTY_NAMES
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NT_NAMES
init|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|AC_NODETYPE_NAMES
argument_list|,
name|PERMISSION_NODETYPE_NAMES
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Context
name|INSTANCE
init|=
operator|new
name|AuthorizationContext
argument_list|()
decl_stmt|;
specifier|private
name|AuthorizationContext
parameter_list|()
block|{     }
specifier|static
name|Context
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|//------------------------------------------------------------< Context>---
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|definesTree
argument_list|(
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
name|String
name|name
init|=
name|tree
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isNodeName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|NT_REP_ACL
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|REP_PERMISSION_STORE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
decl_stmt|;
return|return
name|ntName
operator|!=
literal|null
operator|&&
name|isNtName
argument_list|(
name|ntName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|)
block|{
name|PropertyState
name|p
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
operator|(
name|p
operator|==
literal|null
operator|)
condition|?
name|location
operator|.
name|getTree
argument_list|()
else|:
name|location
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|p
operator|==
literal|null
operator|)
condition|?
name|definesTree
argument_list|(
name|tree
argument_list|)
else|:
name|definesProperty
argument_list|(
name|tree
argument_list|,
name|p
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|isItemName
argument_list|(
name|location
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|location
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PERMISSIONS_STORE_PATH
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|PermissionConstants
operator|.
name|REP_PERMISSION_STORE
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isNodeName
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|String
name|n
range|:
name|NODE_NAMES
control|)
block|{
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isItemName
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|isNodeName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|n
range|:
name|PROPERTY_NAMES
control|)
block|{
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isNtName
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|String
name|n
range|:
name|NT_NAMES
control|)
block|{
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

