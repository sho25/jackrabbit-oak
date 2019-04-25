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
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|TreeType
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
name|TreeTypeProvider
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
name|composite
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
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
name|AggregatedPermissionProvider
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
name|Permissions
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
name|TreePermission
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
name|NodeState
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
import|import static
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
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
operator|.
name|AND
import|;
end_import

begin_import
import|import static
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
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
operator|.
name|OR
import|;
end_import

begin_comment
comment|/**  * {@code TreePermission} implementation that combines multiple {@code TreePermission}  * implementations.  */
end_comment

begin_class
specifier|final
class|class
name|CompositeTreePermission
implements|implements
name|TreePermission
block|{
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|TreeType
name|type
decl_stmt|;
specifier|private
specifier|final
name|CompositionType
name|compositionType
decl_stmt|;
specifier|private
specifier|final
name|TreeProvider
name|treeProvider
decl_stmt|;
specifier|private
specifier|final
name|TreeTypeProvider
name|typeProvider
decl_stmt|;
specifier|private
specifier|final
name|AggregatedPermissionProvider
index|[]
name|providers
decl_stmt|;
specifier|private
specifier|final
name|TreePermission
index|[]
name|treePermissions
decl_stmt|;
specifier|private
specifier|final
name|int
name|childSize
decl_stmt|;
specifier|private
name|Boolean
name|canRead
decl_stmt|;
specifier|private
name|Boolean
name|canReadProperties
decl_stmt|;
specifier|private
name|CompositeTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreeType
name|type
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|,
annotation|@
name|NotNull
name|TreeTypeProvider
name|typeProvider
parameter_list|,
annotation|@
name|NotNull
name|AggregatedPermissionProvider
index|[]
name|providers
parameter_list|,
annotation|@
name|NotNull
name|TreePermission
index|[]
name|treePermissions
parameter_list|,
name|int
name|cnt
parameter_list|,
annotation|@
name|NotNull
name|CompositionType
name|compositionType
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|treeProvider
operator|=
name|treeProvider
expr_stmt|;
name|this
operator|.
name|typeProvider
operator|=
name|typeProvider
expr_stmt|;
name|this
operator|.
name|providers
operator|=
name|providers
expr_stmt|;
name|this
operator|.
name|treePermissions
operator|=
name|treePermissions
expr_stmt|;
name|this
operator|.
name|childSize
operator|=
name|providers
operator|.
name|length
operator|-
name|cnt
expr_stmt|;
name|this
operator|.
name|compositionType
operator|=
name|compositionType
expr_stmt|;
block|}
specifier|static
name|TreePermission
name|create
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|rootTree
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|,
annotation|@
name|NotNull
name|TreeTypeProvider
name|typeProvider
parameter_list|,
annotation|@
name|NotNull
name|AggregatedPermissionProvider
index|[]
name|providers
parameter_list|,
annotation|@
name|NotNull
name|CompositionType
name|compositionType
parameter_list|)
block|{
switch|switch
condition|(
name|providers
operator|.
name|length
condition|)
block|{
case|case
literal|0
case|:
return|return
name|TreePermission
operator|.
name|EMPTY
return|;
case|case
literal|1
case|:
return|return
name|providers
index|[
literal|0
index|]
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
return|;
default|default :
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|TreePermission
index|[]
name|treePermissions
init|=
operator|new
name|TreePermission
index|[
name|providers
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|providers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TreePermission
name|tp
init|=
name|providers
index|[
name|i
index|]
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isValid
argument_list|(
name|tp
argument_list|)
condition|)
block|{
name|cnt
operator|++
expr_stmt|;
block|}
name|treePermissions
index|[
name|i
index|]
operator|=
name|tp
expr_stmt|;
block|}
return|return
operator|new
name|CompositeTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|treeProvider
argument_list|,
name|typeProvider
argument_list|,
name|providers
argument_list|,
name|treePermissions
argument_list|,
name|cnt
argument_list|,
name|compositionType
argument_list|)
return|;
block|}
block|}
specifier|static
name|TreePermission
name|create
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|,
annotation|@
name|NotNull
name|CompositeTreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|create
argument_list|(
parameter_list|()
lambda|->
name|tree
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|treeProvider
operator|.
name|asNodeState
argument_list|(
name|tree
argument_list|)
argument_list|,
name|parentPermission
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|static
name|TreePermission
name|create
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|,
annotation|@
name|NotNull
name|CompositeTreePermission
name|parentPermission
parameter_list|,
annotation|@
name|Nullable
name|TreeType
name|treeType
parameter_list|)
block|{
return|return
name|create
argument_list|(
parameter_list|()
lambda|->
name|tree
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|treeProvider
operator|.
name|asNodeState
argument_list|(
name|tree
argument_list|)
argument_list|,
name|parentPermission
argument_list|,
name|treeType
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|TreePermission
name|create
parameter_list|(
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|Tree
argument_list|>
name|lazyTree
parameter_list|,
annotation|@
name|NotNull
name|String
name|childName
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|childState
parameter_list|,
annotation|@
name|NotNull
name|CompositeTreePermission
name|parentPermission
parameter_list|,
annotation|@
name|Nullable
name|TreeType
name|treeType
parameter_list|)
block|{
switch|switch
condition|(
name|parentPermission
operator|.
name|childSize
condition|)
block|{
case|case
literal|0
case|:
return|return
name|TreePermission
operator|.
name|EMPTY
return|;
case|case
literal|1
case|:
name|TreePermission
name|parent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|TreePermission
name|tp
range|:
name|parentPermission
operator|.
name|treePermissions
control|)
block|{
if|if
condition|(
name|isValid
argument_list|(
name|tp
argument_list|)
condition|)
block|{
name|parent
operator|=
name|tp
expr_stmt|;
break|break;
block|}
block|}
return|return
operator|(
name|parent
operator|==
literal|null
operator|)
condition|?
name|TreePermission
operator|.
name|EMPTY
else|:
name|parent
operator|.
name|getChildPermission
argument_list|(
name|childName
argument_list|,
name|childState
argument_list|)
return|;
default|default:
name|Tree
name|tree
init|=
name|lazyTree
operator|.
name|get
argument_list|()
decl_stmt|;
name|TreeType
name|type
decl_stmt|;
if|if
condition|(
name|treeType
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|treeType
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|getType
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
expr_stmt|;
block|}
name|AggregatedPermissionProvider
index|[]
name|pvds
init|=
operator|new
name|AggregatedPermissionProvider
index|[
name|parentPermission
operator|.
name|childSize
index|]
decl_stmt|;
name|TreePermission
index|[]
name|tps
init|=
operator|new
name|TreePermission
index|[
name|parentPermission
operator|.
name|childSize
index|]
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|parentPermission
operator|.
name|providers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|parent
operator|=
name|parentPermission
operator|.
name|treePermissions
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|isValid
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|AggregatedPermissionProvider
name|provider
init|=
name|parentPermission
operator|.
name|providers
index|[
name|i
index|]
decl_stmt|;
name|TreePermission
name|tp
init|=
name|provider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|type
argument_list|,
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isValid
argument_list|(
name|tp
argument_list|)
condition|)
block|{
name|cnt
operator|++
expr_stmt|;
block|}
name|tps
index|[
name|j
index|]
operator|=
name|tp
expr_stmt|;
name|pvds
index|[
name|j
index|]
operator|=
name|provider
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|CompositeTreePermission
argument_list|(
name|tree
argument_list|,
name|type
argument_list|,
name|parentPermission
operator|.
name|treeProvider
argument_list|,
name|parentPermission
operator|.
name|typeProvider
argument_list|,
name|pvds
argument_list|,
name|tps
argument_list|,
name|cnt
argument_list|,
name|parentPermission
operator|.
name|compositionType
argument_list|)
return|;
block|}
block|}
comment|//-----------------------------------------------------< TreePermission>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|childName
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|NodeState
name|childState
parameter_list|)
block|{
return|return
name|create
argument_list|(
parameter_list|()
lambda|->
name|treeProvider
operator|.
name|createReadOnlyTree
argument_list|(
name|tree
argument_list|,
name|childName
argument_list|,
name|childState
argument_list|)
argument_list|,
name|childName
argument_list|,
name|childState
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
if|if
condition|(
name|canRead
operator|==
literal|null
condition|)
block|{
name|canRead
operator|=
name|grantsRead
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|canRead
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|grantsRead
argument_list|(
name|property
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadAll
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
name|canReadProperties
parameter_list|()
block|{
if|if
condition|(
name|canReadProperties
operator|==
literal|null
condition|)
block|{
name|boolean
name|readable
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|providers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TreePermission
name|tp
init|=
name|treePermissions
index|[
name|i
index|]
decl_stmt|;
name|long
name|supported
init|=
name|providers
index|[
name|i
index|]
operator|.
name|supportedPermissions
argument_list|(
name|tp
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
decl_stmt|;
if|if
condition|(
name|doEvaluate
argument_list|(
name|supported
argument_list|)
condition|)
block|{
name|readable
operator|=
name|tp
operator|.
name|canReadProperties
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|readable
operator|&&
name|compositionType
operator|==
name|AND
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|readable
operator|&&
name|compositionType
operator|==
name|OR
condition|)
block|{
break|break;
block|}
block|}
block|}
name|canReadProperties
operator|=
name|readable
expr_stmt|;
block|}
return|return
name|canReadProperties
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
name|grantsPermission
argument_list|(
name|permissions
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|grantsPermission
argument_list|(
name|permissions
argument_list|,
name|property
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|grantsPermission
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
name|boolean
name|isGranted
init|=
literal|false
decl_stmt|;
name|long
name|coveredPermissions
init|=
name|Permissions
operator|.
name|NO_PERMISSION
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|providers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TreePermission
name|tp
init|=
name|treePermissions
index|[
name|i
index|]
decl_stmt|;
name|long
name|supported
init|=
name|providers
index|[
name|i
index|]
operator|.
name|supportedPermissions
argument_list|(
name|tp
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
decl_stmt|;
if|if
condition|(
name|doEvaluate
argument_list|(
name|supported
argument_list|)
condition|)
block|{
if|if
condition|(
name|compositionType
operator|==
name|AND
condition|)
block|{
name|isGranted
operator|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|tp
operator|.
name|isGranted
argument_list|(
name|supported
argument_list|)
else|:
name|tp
operator|.
name|isGranted
argument_list|(
name|supported
argument_list|,
name|property
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isGranted
condition|)
block|{
return|return
literal|false
return|;
block|}
name|coveredPermissions
operator||=
name|supported
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|long
name|p
range|:
name|Permissions
operator|.
name|aggregates
argument_list|(
name|supported
argument_list|)
control|)
block|{
name|boolean
name|aGrant
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|tp
operator|.
name|isGranted
argument_list|(
name|p
argument_list|)
else|:
name|tp
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|aGrant
condition|)
block|{
name|coveredPermissions
operator||=
name|p
expr_stmt|;
name|isGranted
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|isGranted
operator|&&
name|coveredPermissions
operator|==
name|permissions
return|;
block|}
specifier|private
name|boolean
name|grantsRead
parameter_list|(
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|canReadProperties
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|readable
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|providers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TreePermission
name|tp
init|=
name|treePermissions
index|[
name|i
index|]
decl_stmt|;
name|long
name|supported
init|=
name|providers
index|[
name|i
index|]
operator|.
name|supportedPermissions
argument_list|(
name|tp
argument_list|,
name|property
argument_list|,
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|Permissions
operator|.
name|READ_NODE
else|:
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
decl_stmt|;
if|if
condition|(
name|doEvaluate
argument_list|(
name|supported
argument_list|)
condition|)
block|{
name|readable
operator|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|tp
operator|.
name|canRead
argument_list|()
else|:
name|tp
operator|.
name|canRead
argument_list|(
name|property
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|readable
operator|&&
name|compositionType
operator|==
name|AND
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|readable
operator|&&
name|compositionType
operator|==
name|OR
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
name|readable
return|;
block|}
specifier|private
specifier|static
name|boolean
name|doEvaluate
parameter_list|(
name|long
name|supportedPermissions
parameter_list|)
block|{
return|return
name|supportedPermissions
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isValid
parameter_list|(
annotation|@
name|NotNull
name|TreePermission
name|tp
parameter_list|)
block|{
return|return
name|NO_RECOURSE
operator|!=
name|tp
return|;
block|}
specifier|private
specifier|static
name|TreeType
name|getType
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|CompositeTreePermission
name|parent
parameter_list|)
block|{
return|return
name|parent
operator|.
name|typeProvider
operator|.
name|getType
argument_list|(
name|tree
argument_list|,
name|parent
operator|.
name|type
argument_list|)
return|;
block|}
block|}
end_class

end_unit

