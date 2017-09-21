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
name|plugins
operator|.
name|tree
package|;
end_package

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
name|JcrConstants
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
name|impl
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
name|version
operator|.
name|VersionConstants
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
name|NodeStateUtils
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
name|version
operator|.
name|VersionConstants
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|TreeTypeProvider
block|{
specifier|private
specifier|final
name|TreeContext
name|ctx
decl_stmt|;
specifier|public
name|TreeTypeProvider
parameter_list|(
annotation|@
name|Nonnull
name|TreeContext
name|authorizationContext
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|authorizationContext
expr_stmt|;
block|}
specifier|public
name|TreeType
name|getType
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|tree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
name|TreeType
operator|.
name|DEFAULT
return|;
block|}
else|else
block|{
name|TreeType
name|type
decl_stmt|;
if|if
condition|(
name|tree
operator|instanceof
name|ImmutableTree
condition|)
block|{
name|type
operator|=
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|getType
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|internalGetType
argument_list|(
name|tree
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|type
operator|=
name|internalGetType
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
block|}
specifier|public
name|TreeType
name|getType
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreeType
name|parentType
parameter_list|)
block|{
if|if
condition|(
name|tree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
name|TreeType
operator|.
name|DEFAULT
return|;
block|}
name|TreeType
name|type
decl_stmt|;
if|if
condition|(
name|tree
operator|instanceof
name|ImmutableTree
condition|)
block|{
name|type
operator|=
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|getType
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|internalGetType
argument_list|(
name|tree
argument_list|,
name|parentType
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|type
operator|=
name|internalGetType
argument_list|(
name|tree
argument_list|,
name|parentType
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
specifier|private
name|TreeType
name|internalGetType
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|Tree
name|t
init|=
name|tree
decl_stmt|;
while|while
condition|(
operator|!
name|t
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|TreeType
name|type
init|=
name|internalGetType
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|t
argument_list|)
decl_stmt|;
comment|// stop walking up the hierarchy as soon as a special type is found
if|if
condition|(
name|TreeType
operator|.
name|DEFAULT
operator|!=
name|type
condition|)
block|{
return|return
name|type
return|;
block|}
name|t
operator|=
name|t
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
name|TreeType
operator|.
name|DEFAULT
return|;
block|}
specifier|private
name|TreeType
name|internalGetType
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreeType
name|parentType
parameter_list|)
block|{
name|TreeType
name|type
decl_stmt|;
switch|switch
condition|(
name|parentType
condition|)
block|{
case|case
name|HIDDEN
case|:
name|type
operator|=
name|TreeType
operator|.
name|HIDDEN
expr_stmt|;
break|break;
case|case
name|VERSION
case|:
name|type
operator|=
name|TreeType
operator|.
name|VERSION
expr_stmt|;
break|break;
case|case
name|INTERNAL
case|:
name|type
operator|=
name|TreeType
operator|.
name|INTERNAL
expr_stmt|;
break|break;
case|case
name|ACCESS_CONTROL
case|:
name|type
operator|=
name|TreeType
operator|.
name|ACCESS_CONTROL
expr_stmt|;
break|break;
default|default:
name|type
operator|=
name|internalGetType
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|tree
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
specifier|private
name|TreeType
name|internalGetType
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|TreeType
name|type
decl_stmt|;
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|type
operator|=
name|TreeType
operator|.
name|HIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_STORE_ROOT_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|type
operator|=
operator|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
condition|?
name|TreeType
operator|.
name|VERSION
else|:
name|TreeType
operator|.
name|DEFAULT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ctx
operator|.
name|definesInternal
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|type
operator|=
name|TreeType
operator|.
name|INTERNAL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|type
operator|=
name|TreeType
operator|.
name|ACCESS_CONTROL
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|TreeType
operator|.
name|DEFAULT
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

