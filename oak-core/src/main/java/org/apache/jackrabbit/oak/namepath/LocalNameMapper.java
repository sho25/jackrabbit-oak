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
name|namepath
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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

begin_comment
comment|/**  * Name mapper with local namespace mappings.  */
end_comment

begin_class
specifier|public
class|class
name|LocalNameMapper
extends|extends
name|GlobalNameMapper
block|{
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
decl_stmt|;
specifier|public
name|LocalNameMapper
parameter_list|(
name|Root
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
specifier|public
name|LocalNameMapper
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|global
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
parameter_list|)
block|{
name|super
argument_list|(
name|global
argument_list|)
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSessionLocalMappings
parameter_list|()
block|{
return|return
name|local
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|String
name|getJcrName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|oakName
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|oakName
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
argument_list|,
name|oakName
argument_list|)
expr_stmt|;
comment|// hidden name
name|checkArgument
argument_list|(
operator|!
name|isExpandedName
argument_list|(
name|oakName
argument_list|)
argument_list|,
name|oakName
argument_list|)
expr_stmt|;
comment|// expanded name
if|if
condition|(
operator|!
name|local
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|colon
init|=
name|oakName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|>
literal|0
condition|)
block|{
name|String
name|oakPrefix
init|=
name|oakName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|getNamespacesProperty
argument_list|(
name|oakPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No namespace mapping found for "
operator|+
name|oakName
argument_list|)
throw|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|local
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|jcrPrefix
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|jcrPrefix
operator|.
name|equals
argument_list|(
name|oakPrefix
argument_list|)
condition|)
block|{
return|return
name|oakName
return|;
block|}
else|else
block|{
return|return
name|jcrPrefix
operator|+
name|oakName
operator|.
name|substring
argument_list|(
name|colon
argument_list|)
return|;
block|}
block|}
block|}
comment|// local mapping not found for this URI, make sure there
comment|// is no conflicting local mapping for the prefix
if|if
condition|(
name|local
operator|.
name|containsKey
argument_list|(
name|oakPrefix
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
literal|true
condition|;
name|i
operator|++
control|)
block|{
name|String
name|jcrPrefix
init|=
name|oakPrefix
operator|+
name|i
decl_stmt|;
if|if
condition|(
operator|!
name|local
operator|.
name|containsKey
argument_list|(
name|jcrPrefix
argument_list|)
condition|)
block|{
return|return
name|jcrPrefix
operator|+
name|oakName
operator|.
name|substring
argument_list|(
name|colon
argument_list|)
return|;
block|}
block|}
block|}
block|}
block|}
return|return
name|oakName
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
if|if
condition|(
name|jcrName
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|String
name|oakName
init|=
name|getOakNameFromExpanded
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|!=
name|jcrName
condition|)
block|{
return|return
name|oakName
return|;
block|}
comment|// else not an expanded name, so fall through to local mapping
block|}
if|if
condition|(
operator|!
name|local
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|colon
init|=
name|jcrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|>
literal|0
condition|)
block|{
name|String
name|jcrPrefix
init|=
name|jcrName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|local
operator|.
name|get
argument_list|(
name|jcrPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|String
name|oakPrefix
init|=
name|getOakPrefixOrNull
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPrefix
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|jcrPrefix
operator|.
name|equals
argument_list|(
name|oakPrefix
argument_list|)
condition|)
block|{
return|return
name|jcrName
return|;
block|}
else|else
block|{
return|return
name|oakPrefix
operator|+
name|jcrName
operator|.
name|substring
argument_list|(
name|colon
argument_list|)
return|;
block|}
block|}
comment|// Check that a global mapping is present and not remapped
name|String
name|mapping
init|=
name|getNamespacesProperty
argument_list|(
name|jcrPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapping
operator|!=
literal|null
operator|&&
name|local
operator|.
name|values
argument_list|()
operator|.
name|contains
argument_list|(
name|mapping
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|return
name|jcrName
return|;
block|}
block|}
end_class

end_unit

