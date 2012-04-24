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
name|HashSet
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
name|CommitFailedException
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
name|ContentSession
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

begin_comment
comment|/**  * Prove of concept implementation for OAK-61.  *  * For each registered mapping from a jcr prefix to a namespace a  * a mk prefix is generated. The mk prefixes are in one to one relation  * ship with the registered namespaces and should be used as shorthands  * in place of the actual namespaces in all further name and path handling.  *  * TODO: expose the relevant methods through the Oak API.  */
end_comment

begin_class
specifier|public
class|class
name|NamespaceMappings
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|defaults
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
static|static
block|{
comment|// Standard namespace specified by JCR (default one not included)
name|defaults
operator|.
name|put
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"jcr"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"nt"
argument_list|,
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"mix"
argument_list|,
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
literal|"http://www.w3.org/XML/1998/namespace"
argument_list|)
expr_stmt|;
comment|// Namespace included in Jackrabbit 2.x
name|defaults
operator|.
name|put
argument_list|(
literal|"sv"
argument_list|,
literal|"http://www.jcp.org/jcr/sv/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"rep"
argument_list|,
literal|"internal"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|ContentSession
name|session
decl_stmt|;
specifier|public
name|NamespaceMappings
parameter_list|(
name|ContentSession
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
comment|/**      * Returns all registered namespace prefixes.      *      * @return newly allocated and sorted array of namespace prefixes      */
specifier|public
name|String
index|[]
name|getPrefixes
parameter_list|()
block|{
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
name|prefixes
operator|.
name|addAll
argument_list|(
name|defaults
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|session
operator|.
name|getCurrentRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
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
name|prefixes
operator|.
name|add
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|array
init|=
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
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|array
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
comment|/**      * Returns all registered namespace URIs.      *      * @return newly allocated and sorted array of namespace URIs      */
specifier|public
name|String
index|[]
name|getURIs
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|uris
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|uris
operator|.
name|addAll
argument_list|(
name|defaults
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|session
operator|.
name|getCurrentRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
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
name|uris
operator|.
name|add
argument_list|(
name|property
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|array
init|=
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
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|array
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
comment|/**      * Returns the namespace URI associated with the given prefix,      * or {@code null} if such a mapping does not exist.      *      * @param uri namespace URI      * @return matching namespace prefix, or {@code null}      */
specifier|public
name|String
name|getURI
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|String
name|uri
init|=
name|defaults
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
return|return
name|uri
return|;
block|}
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|session
operator|.
name|getCurrentRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
name|PropertyState
name|property
init|=
name|namespaces
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
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the namespace prefix associated with the given URI,      * or {@code null} if such a mapping does not exist.      *      * @param prefix namespace prefix      * @return matching namespace URI, or {@code null}      */
specifier|public
name|String
name|getPrefix
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|RepositoryException
block|{
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
name|defaults
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
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|session
operator|.
name|getCurrentRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
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
name|uri
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|property
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Adds the specified namespace mapping.      *      * @param prefix namespace prefix      * @param uri namespace URI      * @throws CommitFailedException if the registration failed      */
specifier|public
name|void
name|registerNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Root
name|root
init|=
name|session
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|prefix
argument_list|,
name|session
operator|.
name|getCoreValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/**      * Removes the namespace mapping for the given prefix.      *      * @param prefix namespace prefix      * @throws CommitFailedException if the unregistration failed      */
specifier|public
name|void
name|unregisterNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Root
name|root
init|=
name|session
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|Tree
name|namespaces
init|=
name|getNamespaces
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|namespaces
operator|.
name|removeProperty
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Tree
name|getNamespaces
parameter_list|(
name|Root
name|root
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|system
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
if|if
condition|(
name|system
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|create
condition|)
block|{
name|system
operator|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"jcr:system"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|Tree
name|namespaces
init|=
name|system
operator|.
name|getChild
argument_list|(
literal|"jcr:namespaces"
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|==
literal|null
operator|&&
name|create
condition|)
block|{
name|namespaces
operator|=
name|system
operator|.
name|addChild
argument_list|(
literal|"jcr:namespaces"
argument_list|)
expr_stmt|;
block|}
return|return
name|namespaces
return|;
block|}
block|}
end_class

end_unit

