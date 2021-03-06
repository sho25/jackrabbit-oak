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
name|jcr
operator|.
name|session
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
name|collect
operator|.
name|Iterables
operator|.
name|toArray
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
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|namepath
operator|.
name|impl
operator|.
name|LocalNameMapper
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
name|XMLChar
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * {@code SessionNamespaces} implements namespace handling on the JCR  * Session level. That is, it maintains a map of session local namespace  * re-mappings and takes a snapshot of the namespace registry when initialized  * (see JCR 2.0 specification, section 3.5.1).  */
end_comment

begin_class
specifier|public
class|class
name|SessionNamespaces
extends|extends
name|LocalNameMapper
block|{
specifier|public
name|SessionNamespaces
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|Maps
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|newHashMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// The code below was initially copied from JCR Commons AbstractSession,
comment|// but has since been radically modified
comment|/**      * @see Session#setNamespacePrefix(String, String)      */
specifier|synchronized
name|void
name|setNamespacePrefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|NamespaceException
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Prefix must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Namespace must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Empty prefix is reserved and can not be remapped"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Default namespace is reserved and can not be remapped"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"XML prefixes are reserved: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Prefix is not a valid XML NCName: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
comment|// remove the possible existing mapping for the given prefix
name|local
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
comment|// remove the possible existing mapping(s) for the given URI
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|local
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|local
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
comment|// add the new mapping
name|local
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Session#getNamespacePrefixes()      */
specifier|synchronized
name|String
index|[]
name|getNamespacePrefixes
parameter_list|()
block|{
comment|// get registered namespace prefixes
name|Iterable
argument_list|<
name|String
argument_list|>
name|global
init|=
name|getPrefixes
argument_list|()
decl_stmt|;
comment|// unless there are local remappings just use the registered ones
if|if
condition|(
name|local
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|toArray
argument_list|(
name|global
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
name|newHashSet
argument_list|(
name|global
argument_list|)
decl_stmt|;
comment|// remove the prefixes of the namespaces that have been remapped
for|for
control|(
name|String
name|uri
range|:
name|local
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|getOakPrefixOrNull
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|prefixes
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add the prefixes in local remappings
name|prefixes
operator|.
name|addAll
argument_list|(
name|local
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|prefixes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|prefixes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * @see Session#getNamespaceURI(String)      */
specifier|synchronized
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|NamespaceException
block|{
comment|// first check local remappings
name|String
name|uri
init|=
name|local
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
comment|// Not in snapshot mappings, try the global ones
name|uri
operator|=
name|getOakURIOrNull
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|local
operator|.
name|containsValue
argument_list|(
name|uri
argument_list|)
condition|)
block|{
comment|// URI is either not registered or locally mapped to some
comment|// other prefix, so there are no mappings for this prefix
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Unknown namespace prefix: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
block|}
return|return
name|uri
return|;
block|}
comment|/**      * @see Session#getNamespacePrefix(String)      */
specifier|synchronized
name|String
name|getNamespacePrefix
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|NamespaceException
block|{
comment|// first check local remappings
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
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
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
comment|// then try the global mappings
name|String
name|prefix
init|=
name|getOakPrefixOrNull
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Unknown namespace URI: "
operator|+
name|uri
argument_list|)
throw|;
block|}
comment|// Generate a new prefix if already locally mapped to something else
name|String
name|base
init|=
name|prefix
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|local
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|prefix
operator|=
name|base
operator|+
name|i
expr_stmt|;
block|}
if|if
condition|(
name|base
operator|!=
name|prefix
condition|)
block|{
name|local
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|prefix
return|;
block|}
comment|/**      * Clears the re-mapped namespaces map.      */
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|local
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

