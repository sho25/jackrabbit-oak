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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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

begin_comment
comment|/**  * Name mapper with no local prefix remappings. URI to prefix mappings  * are read from the repository when for transforming expanded JCR names  * to prefixed Oak names.  *<p>  * Note that even though this class could be used to verify that all prefixed  * names have valid prefixes, we explicitly don't do that since this is a  * fairly performance-sensitive part of the codebase and since normally the  * NameValidator and other consistency checks already ensure that all names  * being committed or already in the repository should be valid. A separate  * consistency check can be used if needed to locate and fix any Oak names  * with invalid namespace prefixes.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|GlobalNameMapper
implements|implements
name|NameMapper
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getJcrName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|)
block|{
comment|// Sanity checks, can be turned to assertions if needed for performance
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
argument_list|)
expr_stmt|;
comment|// hidden name
name|checkArgument
argument_list|(
operator|!
name|oakName
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
argument_list|)
expr_stmt|;
comment|// expanded name
return|return
name|oakName
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
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
return|return
name|getOakNameFromExpanded
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
return|return
name|jcrName
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getOakName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakName
init|=
name|getOakNameOrNull
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid jcr name "
operator|+
name|jcrName
argument_list|)
throw|;
block|}
return|return
name|oakName
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakNameFromExpanded
parameter_list|(
name|String
name|expandedName
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|expandedName
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|brace
init|=
name|expandedName
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|brace
operator|>
literal|0
condition|)
block|{
name|String
name|uri
init|=
name|expandedName
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|brace
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|expandedName
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
return|;
comment|// special case: {}name
block|}
elseif|else
if|if
condition|(
name|uri
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// It's an expanded name, look up the namespace prefix
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
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPrefix
operator|+
literal|':'
operator|+
name|expandedName
operator|.
name|substring
argument_list|(
name|brace
operator|+
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
comment|// no matching namespace prefix
block|}
block|}
block|}
return|return
name|expandedName
return|;
comment|// not an expanded name
block|}
specifier|protected
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|()
function_decl|;
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakPrefixOrNull
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
name|getNamespaceMap
argument_list|()
decl_stmt|;
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
name|namespaces
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
return|return
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

