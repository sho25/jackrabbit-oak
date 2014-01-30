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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Root
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
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
name|name
operator|.
name|Namespaces
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|plugins
operator|.
name|name
operator|.
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
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
name|plugins
operator|.
name|name
operator|.
name|NamespaceConstants
operator|.
name|REP_NSDATA
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
name|plugins
operator|.
name|name
operator|.
name|Namespaces
operator|.
name|encodeUri
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_REP_UNSTRUCTURED
import|;
end_import

begin_comment
comment|/**  * Name mapper with no local prefix remappings. URI to prefix mappings  * are read from the repository when for transforming expanded JCR names  * to prefixed Oak names.  *<p>  * Note that even though this class could be used to verify that all prefixed  * names have valid prefixes, we explicitly don't do that since this is a  * fairly performance-sensitive part of the codebase and since normally the  * NameValidator and other consistency checks already ensure that all names  * being committed or already in the repository should be valid. A separate  * consistency check can be used if needed to locate and fix any Oak names  * with invalid namespace prefixes.  */
end_comment

begin_class
specifier|public
class|class
name|GlobalNameMapper
implements|implements
name|NameMapper
block|{
specifier|protected
specifier|static
name|boolean
name|isHiddenName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|isExpandedName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|int
name|brace
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
return|return
name|brace
operator|!=
operator|-
literal|1
operator|&&
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|brace
argument_list|)
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|Tree
name|setupNamespaces
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|global
parameter_list|)
block|{
name|NodeBuilder
name|namespaces
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|global
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|namespaces
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|escapePropertyKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Namespaces
operator|.
name|buildIndexNode
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutableTree
argument_list|(
name|namespaces
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
specifier|final
name|Tree
name|namespaces
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|nsdata
decl_stmt|;
specifier|public
name|GlobalNameMapper
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|this
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|NAMESPACES_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GlobalNameMapper
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
name|this
argument_list|(
name|setupNamespaces
argument_list|(
name|namespaces
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|GlobalNameMapper
parameter_list|(
name|Tree
name|namespaces
parameter_list|)
block|{
name|this
operator|.
name|namespaces
operator|=
name|namespaces
expr_stmt|;
name|this
operator|.
name|nsdata
operator|=
name|namespaces
operator|.
name|getChild
argument_list|(
name|REP_NSDATA
argument_list|)
expr_stmt|;
block|}
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
name|isHiddenName
argument_list|(
name|oakName
argument_list|)
argument_list|,
name|oakName
argument_list|)
expr_stmt|;
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
name|Collections
operator|.
name|emptyMap
argument_list|()
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
name|PropertyState
name|mapping
init|=
name|nsdata
operator|.
name|getProperty
argument_list|(
name|encodeUri
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapping
operator|!=
literal|null
operator|&&
name|mapping
operator|.
name|getType
argument_list|()
operator|==
name|STRING
condition|)
block|{
return|return
name|mapping
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

