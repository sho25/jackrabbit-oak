begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|name
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|collect
operator|.
name|Sets
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
name|spi
operator|.
name|namespace
operator|.
name|NamespaceConstants
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|checkState
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
name|collect
operator|.
name|Maps
operator|.
name|newConcurrentMap
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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|NAMESPACE_MIX
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|NAMESPACE_NT
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|NAMESPACE_XML
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|PREFIX_JCR
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|PREFIX_MIX
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|PREFIX_NT
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
operator|.
name|PREFIX_XML
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
name|JcrConstants
operator|.
name|JCR_SYSTEM
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
name|util
operator|.
name|Text
operator|.
name|escapeIllegalJcrChars
import|;
end_import

begin_comment
comment|/**  * Internal static utility class for managing the persisted namespace registry.  */
end_comment

begin_class
specifier|public
class|class
name|Namespaces
implements|implements
name|NamespaceConstants
block|{
comment|/**      * Global cache of encoded URIs.      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ENCODED_URIS
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
comment|/**      * By default node names with non space whitespace chars are not allowed.      * However initial Oak release did allowed that and this flag is provided      * to revert back to old behaviour if required for some case temporarily      */
specifier|private
specifier|static
specifier|final
name|boolean
name|allowOtherWhitespaceChars
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.allowOtherWhitespaceChars"
argument_list|)
decl_stmt|;
specifier|private
name|Namespaces
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|setupNamespaces
parameter_list|(
name|NodeBuilder
name|system
parameter_list|)
block|{
if|if
condition|(
operator|!
name|system
operator|.
name|hasChildNode
argument_list|(
name|REP_NAMESPACES
argument_list|)
condition|)
block|{
name|NodeBuilder
name|namespaces
init|=
name|createStandardMappings
argument_list|(
name|system
argument_list|)
decl_stmt|;
name|buildIndexNode
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
comment|// index node for faster lookup
block|}
block|}
specifier|public
specifier|static
name|NodeBuilder
name|createStandardMappings
parameter_list|(
name|NodeBuilder
name|system
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|system
operator|.
name|hasChildNode
argument_list|(
name|REP_NAMESPACES
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|namespaces
init|=
name|system
operator|.
name|setChildNode
argument_list|(
name|REP_NAMESPACES
argument_list|)
decl_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_REP_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// Standard namespace specified by JCR (default one not included)
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_JCR
argument_list|,
name|NAMESPACE_JCR
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_NT
argument_list|,
name|NAMESPACE_NT
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_MIX
argument_list|,
name|NAMESPACE_MIX
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_XML
argument_list|,
name|NAMESPACE_XML
argument_list|)
expr_stmt|;
comment|// Namespace included in Jackrabbit 2.x
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_SV
argument_list|,
name|NAMESPACE_SV
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|PREFIX_REP
argument_list|,
name|NAMESPACE_REP
argument_list|)
expr_stmt|;
return|return
name|namespaces
return|;
block|}
specifier|public
specifier|static
name|String
name|addCustomMapping
parameter_list|(
name|NodeBuilder
name|namespaces
parameter_list|,
name|String
name|uri
parameter_list|,
name|String
name|prefixHint
parameter_list|)
block|{
comment|// first look for an existing mapping for the given URI
for|for
control|(
name|PropertyState
name|property
range|:
name|namespaces
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
operator|==
name|STRING
condition|)
block|{
name|String
name|prefix
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
operator|&&
name|uri
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|prefix
return|;
block|}
block|}
block|}
comment|// no existing mapping found for the URI, make sure prefix is unique
name|String
name|prefix
init|=
name|prefixHint
decl_stmt|;
name|int
name|iteration
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|namespaces
operator|.
name|hasProperty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|prefixHint
operator|+
operator|++
name|iteration
expr_stmt|;
block|}
comment|// add the new mapping with its unique prefix
name|namespaces
operator|.
name|setProperty
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
return|return
name|prefix
return|;
block|}
specifier|public
specifier|static
name|void
name|buildIndexNode
parameter_list|(
name|NodeBuilder
name|namespaces
parameter_list|)
block|{
comment|// initialize prefix and URI sets with the defaults namespace
comment|// that's not stored along with the other mappings
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
name|newHashSet
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|uris
init|=
name|newHashSet
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|reverse
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|namespaces
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|STRING
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
name|uris
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|reverse
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeBuilder
name|data
init|=
name|namespaces
operator|.
name|setChildNode
argument_list|(
name|REP_NSDATA
argument_list|)
decl_stmt|;
name|data
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_REP_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|data
operator|.
name|setProperty
argument_list|(
name|REP_PREFIXES
argument_list|,
name|prefixes
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|data
operator|.
name|setProperty
argument_list|(
name|REP_URIS
argument_list|,
name|uris
argument_list|,
name|Type
operator|.
name|STRINGS
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
name|e
range|:
name|reverse
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|data
operator|.
name|setProperty
argument_list|(
name|encodeUri
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Tree
name|getNamespaceTree
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
return|return
name|root
operator|.
name|getChild
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_NAMESPACES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// default namespace, not included in tree
name|Tree
name|namespaces
init|=
name|getNamespaceTree
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|namespaces
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|STRING
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|static
name|String
index|[]
name|getNamespacePrefixes
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|prefSet
init|=
name|getNamespacePrefixesAsSet
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|String
index|[]
name|prefixes
init|=
name|prefSet
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|prefSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
return|return
name|prefixes
return|;
block|}
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getNamespacePrefixesAsSet
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
return|return
name|safeGet
argument_list|(
name|getNamespaceTree
argument_list|(
name|root
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_NSDATA
argument_list|)
argument_list|,
name|REP_PREFIXES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getNamespacePrefix
parameter_list|(
name|Tree
name|root
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
if|if
condition|(
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|uri
return|;
block|}
name|Tree
name|nsdata
init|=
name|getNamespaceTree
argument_list|(
name|root
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_NSDATA
argument_list|)
decl_stmt|;
name|PropertyState
name|ps
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
name|ps
operator|!=
literal|null
condition|)
block|{
return|return
name|ps
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
specifier|static
name|String
index|[]
name|getNamespaceURIs
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|uris
init|=
name|safeGet
argument_list|(
name|getNamespaceTree
argument_list|(
name|root
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_NSDATA
argument_list|)
argument_list|,
name|REP_URIS
argument_list|)
decl_stmt|;
return|return
name|uris
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|uris
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getNamespaceURI
parameter_list|(
name|Tree
name|root
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|prefix
return|;
block|}
if|if
condition|(
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|PropertyState
name|property
init|=
name|getNamespaceTree
argument_list|(
name|root
argument_list|)
operator|.
name|getProperty
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|STRING
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|// utils
comment|/**      * encodes the uri value to be used as a property      *       * @param uri      * @return encoded uri      */
specifier|public
specifier|static
name|String
name|encodeUri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|String
name|encoded
init|=
name|ENCODED_URIS
operator|.
name|get
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoded
operator|==
literal|null
condition|)
block|{
name|encoded
operator|=
name|escapeIllegalJcrChars
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|ENCODED_URIS
operator|.
name|size
argument_list|()
operator|>
literal|1000
condition|)
block|{
name|ENCODED_URIS
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// prevents DoS attacks
block|}
name|ENCODED_URIS
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|encoded
argument_list|)
expr_stmt|;
block|}
return|return
name|encoded
return|;
block|}
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|safeGet
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
return|return
name|Sets
operator|.
name|newHashSet
argument_list|()
return|;
block|}
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
return|;
block|}
comment|// validation
specifier|public
specifier|static
name|boolean
name|isValidPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|// TODO: Other prefix rules?
return|return
name|prefix
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
operator|-
literal|1
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isValidLocalName
parameter_list|(
name|String
name|local
parameter_list|)
block|{
if|if
condition|(
name|local
operator|.
name|isEmpty
argument_list|()
operator|||
literal|"."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|local
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|local
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|spaceChar
init|=
name|allowOtherWhitespaceChars
condition|?
name|Character
operator|.
name|isSpaceChar
argument_list|(
name|ch
argument_list|)
else|:
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
decl_stmt|;
if|if
condition|(
name|spaceChar
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
comment|// leading whitespace
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|local
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
comment|// trailing whitespace
block|}
elseif|else
if|if
condition|(
name|ch
operator|!=
literal|' '
condition|)
block|{
return|return
literal|false
return|;
comment|// only spaces are allowed as whitespace
block|}
block|}
elseif|else
if|if
condition|(
literal|"/:[]|*"
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// TODO: XMLChar check
return|return
literal|false
return|;
comment|// invalid name character
block|}
block|}
comment|// TODO: Other name rules?
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

