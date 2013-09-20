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
name|upgrade
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
name|Lists
operator|.
name|newArrayListWithCapacity
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|JCR_AUTOCREATED
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
name|JCR_CHILDNODEDEFINITION
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
name|JCR_DEFAULTPRIMARYTYPE
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
name|JCR_HASORDERABLECHILDNODES
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
name|JCR_ISMIXIN
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
name|JCR_MANDATORY
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
name|JCR_MULTIPLE
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
name|JCR_NAME
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
name|JCR_NODETYPENAME
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
name|JCR_ONPARENTVERSION
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
name|JCR_PRIMARYITEMNAME
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
name|JCR_PROPERTYDEFINITION
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
name|JCR_PROTECTED
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
name|JCR_REQUIREDPRIMARYTYPES
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
name|JCR_REQUIREDTYPE
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
name|JCR_SAMENAMESIBLINGS
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
name|JCR_SUPERTYPES
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
name|JcrConstants
operator|.
name|JCR_VALUECONSTRAINTS
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
name|JCR_VERSIONSTORAGE
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
name|NT_CHILDNODEDEFINITION
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
name|NT_NODETYPE
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
name|NT_PROPERTYDEFINITION
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
name|NAMES
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
name|STRINGS
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
name|JCR_AVAILABLE_QUERY_OPERATORS
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
name|JCR_IS_ABSTRACT
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
name|JCR_IS_FULLTEXT_SEARCHABLE
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
name|JCR_IS_QUERYABLE
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
name|JCR_IS_QUERY_ORDERABLE
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
name|JCR_NODE_TYPES
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
name|spi
operator|.
name|commons
operator|.
name|name
operator|.
name|NameConstants
operator|.
name|ANY_NAME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|NamespaceRegistry
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
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|OnParentVersionAction
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
name|core
operator|.
name|RepositoryContext
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
name|core
operator|.
name|RepositoryImpl
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
name|core
operator|.
name|config
operator|.
name|RepositoryConfig
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
name|core
operator|.
name|nodetype
operator|.
name|NodeTypeRegistry
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
name|plugins
operator|.
name|name
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
name|plugins
operator|.
name|nodetype
operator|.
name|RegistrationEditorProvider
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
name|commit
operator|.
name|EditorHook
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
name|commit
operator|.
name|PostCommitHook
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
name|NodeStore
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
name|NodeStoreBranch
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
name|spi
operator|.
name|Name
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
name|spi
operator|.
name|QItemDefinition
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
name|spi
operator|.
name|QNodeDefinition
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
name|spi
operator|.
name|QNodeTypeDefinition
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
name|spi
operator|.
name|QPropertyDefinition
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
name|spi
operator|.
name|QValue
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
name|spi
operator|.
name|QValueConstraint
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
name|ImmutableSet
import|;
end_import

begin_class
specifier|public
class|class
name|RepositoryUpgrade
block|{
comment|/**      * Logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RepositoryUpgrade
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Source repository context.      */
specifier|private
specifier|final
name|RepositoryContext
name|source
decl_stmt|;
comment|/**      * Target node store.      */
specifier|private
specifier|final
name|NodeStore
name|target
decl_stmt|;
comment|/**      * Copies the contents of the repository in the given source directory      * to the given target node store.      *      * @param source source repository directory      * @param target target node store      * @throws RepositoryException if the copy operation fails      */
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|File
name|source
parameter_list|,
name|NodeStore
name|target
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|copy
argument_list|(
name|RepositoryConfig
operator|.
name|create
argument_list|(
name|source
argument_list|)
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copies the contents of the repository with the given configuration      * to the given target node builder.      *      * @param source source repository configuration      * @param target target node store      * @throws RepositoryException if the copy operation fails      */
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|RepositoryConfig
name|source
parameter_list|,
name|NodeStore
name|target
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|RepositoryContext
name|context
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|source
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|RepositoryUpgrade
argument_list|(
name|context
argument_list|,
name|target
argument_list|)
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Creates a tool for copying the full contents of the source repository      * to the given target repository. Any existing content in the target      * repository will be overwritten.      *      * @param source source repository context      * @param target target node store      */
specifier|public
name|RepositoryUpgrade
parameter_list|(
name|RepositoryContext
name|source
parameter_list|,
name|NodeStore
name|target
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
comment|/**      * Copies the full content from the source to the target repository.      *<p>      * The source repository<strong>must not be modified</strong> while      * the copy operation is running to avoid an inconsistent copy.      *<p>      * This method leaves the search indexes of the target repository in      * an       * Note that both the source and the target repository must be closed      * during the copy operation as this method requires exclusive access      * to the repositories.      *      * @throws RepositoryException if the copy operation fails      */
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Copying repository content from {} to Oak"
argument_list|,
name|source
operator|.
name|getRepositoryConfig
argument_list|()
operator|.
name|getHomeDir
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|NodeBuilder
name|builder
init|=
name|target
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|copyNamespaces
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|copyNodeTypes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|copyVersionStore
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|copyWorkspaces
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|target
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|RegistrationEditorProvider
argument_list|()
argument_list|)
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// TODO: default hooks?
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to copy content"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|getOakName
parameter_list|(
name|Name
name|name
parameter_list|)
throws|throws
name|NamespaceException
block|{
name|String
name|uri
init|=
name|name
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|String
name|local
init|=
name|name
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|local
return|;
block|}
else|else
block|{
return|return
name|source
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getPrefix
argument_list|(
name|uri
argument_list|)
operator|+
literal|":"
operator|+
name|local
return|;
block|}
block|}
specifier|private
name|void
name|copyNamespaces
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NamespaceRegistry
name|sourceRegistry
init|=
name|source
operator|.
name|getNamespaceRegistry
argument_list|()
decl_stmt|;
name|NodeBuilder
name|system
init|=
name|root
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|NodeBuilder
name|namespaces
init|=
name|system
operator|.
name|child
argument_list|(
literal|"rep:namespaces"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|defaults
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NamespaceRegistry
operator|.
name|NAMESPACE_EMPTY
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_MIX
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_NT
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_XML
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_REP
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Copying registered namespaces"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|uri
range|:
name|sourceRegistry
operator|.
name|getURIs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|defaults
operator|.
name|contains
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|namespaces
operator|.
name|setProperty
argument_list|(
name|sourceRegistry
operator|.
name|getPrefix
argument_list|(
name|uri
argument_list|)
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|copyNodeTypes
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeTypeRegistry
name|sourceRegistry
init|=
name|source
operator|.
name|getNodeTypeRegistry
argument_list|()
decl_stmt|;
name|NodeBuilder
name|system
init|=
name|root
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|NodeBuilder
name|types
init|=
name|system
operator|.
name|child
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Copying registered node types"
argument_list|)
expr_stmt|;
for|for
control|(
name|Name
name|name
range|:
name|sourceRegistry
operator|.
name|getRegisteredNodeTypes
argument_list|()
control|)
block|{
name|QNodeTypeDefinition
name|def
init|=
name|sourceRegistry
operator|.
name|getNodeTypeDef
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NodeBuilder
name|type
init|=
name|types
operator|.
name|child
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|copyNodeType
argument_list|(
name|def
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyNodeType
parameter_list|(
name|QNodeTypeDefinition
name|def
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|NamespaceException
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_NODETYPE
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// - jcr:nodeTypeName (NAME) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_NODETYPENAME
argument_list|,
name|getOakName
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// - jcr:supertypes (NAME) protected multiple
name|Name
index|[]
name|supertypes
init|=
name|def
operator|.
name|getSupertypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|supertypes
operator|!=
literal|null
operator|&&
name|supertypes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newArrayListWithCapacity
argument_list|(
name|supertypes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Name
name|supertype
range|:
name|supertypes
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|getOakName
argument_list|(
name|supertype
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_SUPERTYPES
argument_list|,
name|names
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
comment|// - jcr:isAbstract (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_IS_ABSTRACT
argument_list|,
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:isQueryable (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_IS_QUERYABLE
argument_list|,
name|def
operator|.
name|isQueryable
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:isMixin (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_ISMIXIN
argument_list|,
name|def
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:hasOrderableChildNodes (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_HASORDERABLECHILDNODES
argument_list|,
name|def
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:primaryItemName (NAME) protected
name|Name
name|primary
init|=
name|def
operator|.
name|getPrimaryItemName
argument_list|()
decl_stmt|;
if|if
condition|(
name|primary
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYITEMNAME
argument_list|,
name|getOakName
argument_list|(
name|primary
argument_list|)
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|// + jcr:propertyDefinition (nt:propertyDefinition) = nt:propertyDefinition protected sns
name|QPropertyDefinition
index|[]
name|properties
init|=
name|def
operator|.
name|getPropertyDefs
argument_list|()
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
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|JCR_PROPERTYDEFINITION
operator|+
literal|'['
operator|+
name|i
operator|+
literal|']'
decl_stmt|;
name|copyPropertyDefinition
argument_list|(
name|properties
index|[
name|i
index|]
argument_list|,
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// + jcr:childNodeDefinition (nt:childNodeDefinition) = nt:childNodeDefinition protected sns
name|QNodeDefinition
index|[]
name|childNodes
init|=
name|def
operator|.
name|getChildNodeDefs
argument_list|()
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
name|childNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|JCR_CHILDNODEDEFINITION
operator|+
literal|'['
operator|+
name|i
operator|+
literal|']'
decl_stmt|;
name|copyChildNodeDefinition
argument_list|(
name|childNodes
index|[
name|i
index|]
argument_list|,
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyPropertyDefinition
parameter_list|(
name|QPropertyDefinition
name|def
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|NamespaceException
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_PROPERTYDEFINITION
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|copyItemDefinition
argument_list|(
name|def
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// - jcr:requiredType (STRING) protected mandatory
comment|//< 'STRING', 'URI', 'BINARY', 'LONG', 'DOUBLE',
comment|//     'DECIMAL', 'BOOLEAN', 'DATE', 'NAME', 'PATH',
comment|//     'REFERENCE', 'WEAKREFERENCE', 'UNDEFINED'
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_REQUIREDTYPE
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:valueConstraints (STRING) protected multiple
name|QValueConstraint
index|[]
name|constraints
init|=
name|def
operator|.
name|getValueConstraints
argument_list|()
decl_stmt|;
if|if
condition|(
name|constraints
operator|!=
literal|null
operator|&&
name|constraints
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|newArrayListWithCapacity
argument_list|(
name|constraints
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|QValueConstraint
name|constraint
range|:
name|constraints
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|constraint
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_VALUECONSTRAINTS
argument_list|,
name|strings
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
block|}
comment|// - jcr:defaultValues (UNDEFINED) protected multiple
name|QValue
index|[]
name|values
init|=
name|def
operator|.
name|getDefaultValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
comment|// TODO
block|}
comment|// - jcr:multiple (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_MULTIPLE
argument_list|,
name|def
operator|.
name|isMultiple
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:availableQueryOperators (NAME) protected mandatory multiple
name|List
argument_list|<
name|String
argument_list|>
name|operators
init|=
name|asList
argument_list|(
name|def
operator|.
name|getAvailableQueryOperators
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_AVAILABLE_QUERY_OPERATORS
argument_list|,
name|operators
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
comment|// - jcr:isFullTextSearchable (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_IS_FULLTEXT_SEARCHABLE
argument_list|,
name|def
operator|.
name|isFullTextSearchable
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:isQueryOrderable (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_IS_QUERY_ORDERABLE
argument_list|,
name|def
operator|.
name|isQueryOrderable
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyChildNodeDefinition
parameter_list|(
name|QNodeDefinition
name|def
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|NamespaceException
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_CHILDNODEDEFINITION
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|copyItemDefinition
argument_list|(
name|def
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// - jcr:requiredPrimaryTypes (NAME) = 'nt:base' protected mandatory multiple
name|Name
index|[]
name|types
init|=
name|def
operator|.
name|getRequiredPrimaryTypes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newArrayListWithCapacity
argument_list|(
name|types
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Name
name|type
range|:
name|types
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|getOakName
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_REQUIREDPRIMARYTYPES
argument_list|,
name|names
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
comment|// - jcr:defaultPrimaryType (NAME) protected
name|Name
name|type
init|=
name|def
operator|.
name|getDefaultPrimaryType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_DEFAULTPRIMARYTYPE
argument_list|,
name|getOakName
argument_list|(
name|type
argument_list|)
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|// - jcr:sameNameSiblings (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_SAMENAMESIBLINGS
argument_list|,
name|def
operator|.
name|allowsSameNameSiblings
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyItemDefinition
parameter_list|(
name|QItemDefinition
name|def
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|NamespaceException
block|{
comment|// - jcr:name (NAME) protected
name|Name
name|name
init|=
name|def
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|ANY_NAME
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_NAME
argument_list|,
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|// - jcr:autoCreated (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_AUTOCREATED
argument_list|,
name|def
operator|.
name|isAutoCreated
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:mandatory (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_MANDATORY
argument_list|,
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
comment|// - jcr:onParentVersion (STRING) protected mandatory
comment|//< 'COPY', 'VERSION', 'INITIALIZE', 'COMPUTE', 'IGNORE', 'ABORT'
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_ONPARENTVERSION
argument_list|,
name|OnParentVersionAction
operator|.
name|nameFromValue
argument_list|(
name|def
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// - jcr:protected (BOOLEAN) protected mandatory
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PROTECTED
argument_list|,
name|def
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyVersionStore
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Copying version histories"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|system
init|=
name|root
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|NodeBuilder
name|versionStorage
init|=
name|system
operator|.
name|child
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|activities
init|=
name|system
operator|.
name|child
argument_list|(
literal|"jcr:activities"
argument_list|)
decl_stmt|;
name|PersistenceCopier
name|copier
init|=
operator|new
name|PersistenceCopier
argument_list|(
name|source
operator|.
name|getInternalVersionManager
argument_list|()
operator|.
name|getPersistenceManager
argument_list|()
argument_list|,
name|source
operator|.
name|getNamespaceRegistry
argument_list|()
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|copier
operator|.
name|copy
argument_list|(
name|RepositoryImpl
operator|.
name|VERSION_STORAGE_NODE_ID
argument_list|,
name|versionStorage
argument_list|)
expr_stmt|;
name|copier
operator|.
name|copy
argument_list|(
name|RepositoryImpl
operator|.
name|ACTIVITIES_NODE_ID
argument_list|,
name|activities
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyWorkspaces
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Copying default workspace"
argument_list|)
expr_stmt|;
comment|// Copy all the default workspace content
name|RepositoryConfig
name|config
init|=
name|source
operator|.
name|getRepositoryConfig
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|config
operator|.
name|getDefaultWorkspaceName
argument_list|()
decl_stmt|;
name|PersistenceCopier
name|copier
init|=
operator|new
name|PersistenceCopier
argument_list|(
name|source
operator|.
name|getWorkspaceInfo
argument_list|(
name|name
argument_list|)
operator|.
name|getPersistenceManager
argument_list|()
argument_list|,
name|source
operator|.
name|getNamespaceRegistry
argument_list|()
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|copier
operator|.
name|excludeNode
argument_list|(
name|RepositoryImpl
operator|.
name|SYSTEM_ROOT_NODE_ID
argument_list|)
expr_stmt|;
name|copier
operator|.
name|copy
argument_list|(
name|RepositoryImpl
operator|.
name|ROOT_NODE_ID
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|// TODO: Copy all the active open-scoped locks
block|}
block|}
end_class

end_unit

