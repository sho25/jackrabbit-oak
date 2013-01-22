begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|nodetype
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
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NoSuchNodeTypeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinitionTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinitionTemplate
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
name|Lists
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
name|Queues
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
name|commons
operator|.
name|iterator
operator|.
name|NodeTypeIteratorAdapter
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
name|ReadOnlyTree
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
name|NameMapper
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
name|NamePathMapper
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
name|NamePathMapperImpl
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
name|MemoryNodeState
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|NodeUtil
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|UNDEFINED
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
name|JCR_MIXINTYPES
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
name|NODE_TYPES_PATH
import|;
end_import

begin_comment
comment|/**  * Base implementation of a {@link NodeTypeManager} with support for reading  * node types from the {@link Tree} returned by {@link #getTypes()}. Methods  * related to node type modifications throw  * {@link UnsupportedRepositoryOperationException}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadOnlyNodeTypeManager
implements|implements
name|NodeTypeManager
implements|,
name|EffectiveNodeTypeProvider
implements|,
name|DefinitionProvider
block|{
comment|/**      * Returns the internal name for the specified JCR name.      *      * @param jcrName JCR node type name.      * @return the internal representation of the given JCR name.      * @throws javax.jcr.RepositoryException If there is no valid internal representation      * of the specified JCR name.      */
annotation|@
name|Nonnull
specifier|protected
specifier|final
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getNamePathMapper
argument_list|()
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
comment|/**      * @return  {@link org.apache.jackrabbit.oak.api.Tree} instance where the node types      * are stored or {@code null} if none.      */
annotation|@
name|CheckForNull
specifier|protected
specifier|abstract
name|Tree
name|getTypes
parameter_list|()
function_decl|;
comment|/**      * The value factory to be used by {@link org.apache.jackrabbit.oak.plugins.nodetype.PropertyDefinitionImpl#getDefaultValues()}.      * If {@code null} the former returns {@code null}.      * @return  {@code ValueFactory} instance or {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Returns a {@link NameMapper} to be used by this node type manager. This      * implementation returns the {@link NamePathMapperImpl#DEFAULT} instance. A      * subclass may override this method and provide a different      * implementation.      *      * @return {@link NameMapper} instance.      */
annotation|@
name|Nonnull
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|NamePathMapperImpl
operator|.
name|DEFAULT
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Return a new instance of {@code ReadOnlyNodeTypeManager} that reads node      * type information from the tree at {@link NodeTypeConstants#NODE_TYPES_PATH}.      *      * @param root The root to read node types from.      * @param namePathMapper The {@code NamePathMapper} to use.      * @return a new instance of {@code ReadOnlyNodeTypeManager}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|ReadOnlyNodeTypeManager
name|getInstance
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|,
specifier|final
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|ReadOnlyNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns a read-only node type manager based on the types stored within      * the content tree starting at the given root node state.      *      * @param root root node state      * @return read-only node type manager      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|ReadOnlyNodeTypeManager
name|getInstance
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|Tree
name|tree
init|=
operator|new
name|ReadOnlyTree
argument_list|(
name|root
argument_list|)
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|NODE_TYPES_PATH
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
specifier|final
name|Tree
name|types
init|=
name|tree
operator|==
literal|null
condition|?
operator|new
name|ReadOnlyTree
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
comment|// No node types in content, use an empty node
else|:
name|tree
decl_stmt|;
return|return
operator|new
name|ReadOnlyNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|types
return|;
block|}
block|}
return|;
block|}
comment|//----------------------------------------------------< NodeTypeManager>---
annotation|@
name|Override
specifier|public
name|boolean
name|hasNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|types
init|=
name|getTypes
argument_list|()
decl_stmt|;
return|return
name|types
operator|!=
literal|null
operator|&&
name|types
operator|.
name|hasChild
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
name|getNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|internalGetNodeType
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getAllNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|NodeType
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|getTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Tree
name|type
range|:
name|types
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
operator|new
name|NodeUtil
argument_list|(
name|type
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getPrimaryNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|NodeType
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|NodeTypeIterator
name|iterator
init|=
name|getAllNodeTypes
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeType
name|type
init|=
name|iterator
operator|.
name|nextNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|isMixin
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getMixinNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|NodeType
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|NodeTypeIterator
name|iterator
init|=
name|getAllNodeTypes
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeType
name|type
init|=
name|iterator
operator|.
name|nextNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isMixin
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinitionTemplate
name|createNodeDefinitionTemplate
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinitionTemplate
name|createPropertyDefinitionTemplate
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|/**      * This implementation always throws a {@link UnsupportedRepositoryOperationException}.      */
annotation|@
name|Override
specifier|public
name|NodeType
name|registerNodeType
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|/**      * This implementation always throws a {@link UnsupportedRepositoryOperationException}.      */
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|registerNodeTypes
parameter_list|(
name|NodeTypeDefinition
index|[]
name|ntds
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|/**      * This implementation always throws a {@link UnsupportedRepositoryOperationException}.      */
annotation|@
name|Override
specifier|public
name|void
name|unregisterNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|/**      * This implementation always throws a {@link UnsupportedRepositoryOperationException}.      */
annotation|@
name|Override
specifier|public
name|void
name|unregisterNodeTypes
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|//------------------------------------------< EffectiveNodeTypeProvider>---
annotation|@
name|Override
specifier|public
name|boolean
name|isNodeType
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|oakNtName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeTypeImpl
name|nodeType
init|=
name|internalGetNodeType
argument_list|(
name|oakNtName
argument_list|)
decl_stmt|;
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
name|node
operator|.
name|getPrimaryNodeTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ntName
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|oakNtName
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|internalGetNodeType
argument_list|(
name|ntName
argument_list|)
operator|.
name|isNodeType
argument_list|(
name|oakNtName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
index|[]
name|mixinNames
init|=
name|node
operator|.
name|getStrings
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixinNames
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|mixinName
range|:
name|mixinNames
control|)
block|{
if|if
condition|(
name|oakNtName
operator|.
name|equals
argument_list|(
name|mixinName
argument_list|)
operator|||
name|internalGetNodeType
argument_list|(
name|mixinName
argument_list|)
operator|.
name|isNodeType
argument_list|(
name|oakNtName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns all the node types of the given node, in a breadth-first      * traversal order of the type hierarchy.      *      * @param node node instance      * @return all types of the given node      * @throws RepositoryException if the type information can not be accessed      * @param node      * @return      * @throws RepositoryException      */
annotation|@
name|Override
specifier|public
name|EffectiveNodeType
name|getEffectiveNodeType
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Queue
argument_list|<
name|NodeType
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|node
operator|.
name|getPrimaryNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|node
operator|.
name|getMixinNodeTypes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|getEffectiveNodeType
argument_list|(
name|queue
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EffectiveNodeType
name|getEffectiveNodeType
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Queue
argument_list|<
name|NodeType
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|()
decl_stmt|;
name|NodeType
name|primaryType
decl_stmt|;
name|PropertyState
name|jcrPrimaryType
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrPrimaryType
operator|!=
literal|null
condition|)
block|{
name|String
name|ntName
init|=
name|jcrPrimaryType
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
name|primaryType
operator|=
name|internalGetNodeType
argument_list|(
name|ntName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Node at "
operator|+
name|tree
operator|.
name|getPath
argument_list|()
operator|+
literal|" has no primary type."
argument_list|)
throw|;
block|}
name|queue
operator|.
name|add
argument_list|(
name|primaryType
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeType
argument_list|>
name|mixinTypes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|PropertyState
name|jcrMixinType
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrMixinType
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|ntName
range|:
name|jcrMixinType
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|mixinTypes
operator|.
name|add
argument_list|(
name|internalGetNodeType
argument_list|(
name|ntName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|queue
operator|.
name|addAll
argument_list|(
name|mixinTypes
argument_list|)
expr_stmt|;
return|return
name|getEffectiveNodeType
argument_list|(
name|queue
argument_list|)
return|;
block|}
comment|//-------------------------------------------------< DefinitionProvider>---
annotation|@
name|Override
specifier|public
name|NodeDefinition
name|getRootDefinition
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|RootNodeDefinition
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeDefinition
name|getDefinition
parameter_list|(
annotation|@
name|Nonnull
name|Node
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|nodeName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
return|return
name|getNodeDefinition
argument_list|(
name|getEffectiveNodeType
argument_list|(
name|parent
argument_list|)
argument_list|,
name|nodeName
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
name|getDefinition
parameter_list|(
annotation|@
name|Nonnull
name|Node
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|Node
name|targetNode
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|targetNode
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|targetNode
operator|.
name|getName
argument_list|()
decl_stmt|;
name|EffectiveNodeType
name|eff
init|=
name|getEffectiveNodeType
argument_list|(
name|parent
argument_list|)
decl_stmt|;
return|return
name|getNodeDefinition
argument_list|(
name|eff
argument_list|,
name|name
argument_list|,
name|getEffectiveNodeType
argument_list|(
name|targetNode
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
name|getDefinition
parameter_list|(
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|parentNodeTypes
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|EffectiveNodeType
name|eff
init|=
name|getEffectiveNodeType
argument_list|(
name|Queues
operator|.
name|newArrayDeque
argument_list|(
name|parentNodeTypes
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|getNodeDefinition
argument_list|(
name|eff
argument_list|,
name|nodeName
argument_list|,
name|getEffectiveNodeType
argument_list|(
name|Queues
operator|.
name|newArrayDeque
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|nodeType
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Node
name|parent
parameter_list|,
name|Property
name|targetProperty
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|targetProperty
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|isMultiple
init|=
name|targetProperty
operator|.
name|isMultiple
argument_list|()
decl_stmt|;
name|int
name|type
init|=
name|UNDEFINED
decl_stmt|;
if|if
condition|(
name|isMultiple
condition|)
block|{
name|Value
index|[]
name|values
init|=
name|targetProperty
operator|.
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|type
operator|=
name|values
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|type
operator|=
name|targetProperty
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
return|return
name|getPropertyDefinition
argument_list|(
name|getEffectiveNodeType
argument_list|(
name|parent
argument_list|)
argument_list|,
name|name
argument_list|,
name|isMultiple
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|propertyState
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getDefinition
argument_list|(
name|parent
argument_list|,
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|,
name|propertyState
operator|.
name|isArray
argument_list|()
argument_list|,
name|propertyState
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|boolean
name|isMultiple
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getPropertyDefinition
argument_list|(
name|getEffectiveNodeType
argument_list|(
name|parent
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|isMultiple
argument_list|,
name|type
argument_list|,
name|exactTypeMatch
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|boolean
name|isMultiple
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getPropertyDefinition
argument_list|(
name|getEffectiveNodeType
argument_list|(
name|parent
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|isMultiple
argument_list|,
name|type
argument_list|,
name|exactTypeMatch
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|boolean
name|isMultiple
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Queue
argument_list|<
name|NodeType
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|(
name|nodeTypes
argument_list|)
decl_stmt|;
return|return
name|getPropertyDefinition
argument_list|(
name|getEffectiveNodeType
argument_list|(
name|queue
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|isMultiple
argument_list|,
name|type
argument_list|,
name|exactTypeMatch
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
name|NodeTypeImpl
name|internalGetNodeType
parameter_list|(
name|String
name|oakName
parameter_list|)
throws|throws
name|NoSuchNodeTypeException
block|{
name|Tree
name|types
init|=
name|getTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
name|Tree
name|type
init|=
name|types
operator|.
name|getChild
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
operator|new
name|NodeUtil
argument_list|(
name|type
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
argument_list|)
throw|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|EffectiveNodeType
name|getEffectiveNodeType
parameter_list|(
name|Queue
argument_list|<
name|NodeType
argument_list|>
name|queue
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NodeType
argument_list|>
name|types
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeType
name|type
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|type
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|types
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|types
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredSupertypes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|EffectiveNodeTypeImpl
operator|.
name|create
argument_list|(
name|types
operator|.
name|values
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**      *      * @param effectiveNodeType      * @param propertyName The internal oak name of the property.      * @param isMultiple      * @param type      * @param exactTypeMatch      * @return      * @throws ConstraintViolationException      */
specifier|private
specifier|static
name|PropertyDefinition
name|getPropertyDefinition
parameter_list|(
name|EffectiveNodeType
name|effectiveNodeType
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|boolean
name|isMultiple
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
comment|// TODO: This may need to be optimized
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|effectiveNodeType
operator|.
name|getNamedPropertyDefinitions
argument_list|(
name|propertyName
argument_list|)
control|)
block|{
name|int
name|defType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMultiple
operator|==
name|def
operator|.
name|isMultiple
argument_list|()
operator|&&
operator|(
operator|!
name|exactTypeMatch
operator|||
operator|(
name|type
operator|==
name|defType
operator|||
name|UNDEFINED
operator|==
name|type
operator|||
name|UNDEFINED
operator|==
name|defType
operator|)
operator|)
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
comment|// try if there is a residual definition
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|effectiveNodeType
operator|.
name|getResidualPropertyDefinitions
argument_list|()
control|)
block|{
name|int
name|defType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMultiple
operator|==
name|def
operator|.
name|isMultiple
argument_list|()
operator|&&
operator|(
operator|!
name|exactTypeMatch
operator|||
operator|(
name|type
operator|==
name|defType
operator|||
name|UNDEFINED
operator|==
name|type
operator|||
name|UNDEFINED
operator|==
name|defType
operator|)
operator|)
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching property definition found for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
comment|/**      *      * @param effectiveNodeType      * @param childName The internal oak name of the target node.      * @param childEffective      * @return      * @throws ConstraintViolationException      */
specifier|private
specifier|static
name|NodeDefinition
name|getNodeDefinition
parameter_list|(
name|EffectiveNodeType
name|effectiveNodeType
parameter_list|,
name|String
name|childName
parameter_list|,
name|EffectiveNodeType
name|childEffective
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
for|for
control|(
name|NodeDefinition
name|def
range|:
name|effectiveNodeType
operator|.
name|getNamedNodeDefinitions
argument_list|(
name|childName
argument_list|)
control|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|childEffective
operator|!=
literal|null
operator|&&
operator|!
name|childEffective
operator|.
name|includesNodeTypes
argument_list|(
name|def
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
for|for
control|(
name|NodeDefinition
name|def
range|:
name|effectiveNodeType
operator|.
name|getResidualNodeDefinitions
argument_list|()
control|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|childEffective
operator|!=
literal|null
operator|&&
operator|!
name|childEffective
operator|.
name|includesNodeTypes
argument_list|(
name|def
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching node definition found for "
operator|+
name|childName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

