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
name|nodetype
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

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
name|PropertyType
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
name|Value
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
name|ItemDefinition
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
name|NodeTypeExistsException
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
name|version
operator|.
name|OnParentVersionAction
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|commons
operator|.
name|cnd
operator|.
name|CompactNodeTypeDefReader
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
name|cnd
operator|.
name|ParseException
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
name|util
operator|.
name|NodeUtil
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
name|JCR_DEFAULTVALUES
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
name|NT_BASE
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
comment|/**  * {@code ReadWriteNodeTypeManager} extends the {@link ReadOnlyNodeTypeManager}  * and add support for operations that modify node types:  *<ul>  *<li>{@link #registerNodeType(NodeTypeDefinition, boolean)}</li>  *<li>{@link #registerNodeTypes(NodeTypeDefinition[], boolean)}</li>  *<li>{@link #unregisterNodeType(String)}</li>  *<li>{@link #unregisterNodeTypes(String[])}</li>  *</ul>  * Calling any of the above methods will result in a {@link #refresh()} callback  * to e.g. inform an associated session that it should refresh to make the  * changes visible.  *</p>  * Subclass responsibility is to provide an implementation of  * {@link #getTypes()} for read only access to the tree where node types are  * stored in content and {@link #getWriteRoot()} for write access to the  * repository in order to modify node types stored in content. A subclass may  * also want to override the default implementation of  * {@link ReadOnlyNodeTypeManager} for the following methods:  *<ul>  *<li>{@link #getValueFactory()}</li>  *<li>{@link #getCoreValueFactory()}</li>  *<li>{@link #getNameMapper()}</li>  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadWriteNodeTypeManager
extends|extends
name|ReadOnlyNodeTypeManager
block|{
comment|/**      * Called by the methods {@link #registerNodeType(NodeTypeDefinition,boolean)},      * {@link #registerNodeTypes(NodeTypeDefinition[], boolean)},      * {@link #unregisterNodeType(String)} and {@link #unregisterNodeTypes(String[])}      * to acquire a fresh {@link Root} instance that can be used to persist the      * requested node type changes (and nothing else).      *<p/>      * This default implementation throws an {@link UnsupportedOperationException}.      *      * @return fresh {@link Root} instance.      */
annotation|@
name|Nonnull
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Called by the {@link ReadWriteNodeTypeManager} implementation methods to      * refresh the state of the session associated with this instance.      * That way the session is kept in sync with the latest global state      * seen by the node type manager.      *      * @throws RepositoryException if the session could not be refreshed      */
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
comment|/**      * Utility method for registering node types from a CND format.      * @param cnd  reader for the CND      * @throws ParseException  if parsing the CND fails      * @throws RepositoryException  if registering the node types fails      */
specifier|public
name|void
name|registerNodeTypes
parameter_list|(
name|Reader
name|cnd
parameter_list|)
throws|throws
name|ParseException
throws|,
name|RepositoryException
block|{
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|CompactNodeTypeDefReader
argument_list|<
name|NodeTypeTemplate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|reader
init|=
operator|new
name|CompactNodeTypeDefReader
argument_list|<
name|NodeTypeTemplate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|(
name|cnd
argument_list|,
literal|null
argument_list|,
operator|new
name|DefBuilderFactory
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NodeTypeTemplate
argument_list|>
name|templates
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeTypeTemplate
name|template
range|:
name|reader
operator|.
name|getNodeTypeDefinitions
argument_list|()
control|)
block|{
name|templates
operator|.
name|put
argument_list|(
name|template
operator|.
name|getName
argument_list|()
argument_list|,
name|template
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|NodeTypeTemplate
name|template
range|:
name|templates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|template
operator|.
name|isMixin
argument_list|()
operator|&&
operator|!
name|NT_BASE
operator|.
name|equals
argument_list|(
name|template
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
index|[]
name|supertypes
init|=
name|template
operator|.
name|getDeclaredSupertypeNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|supertypes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|template
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|NT_BASE
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check whether we need to add the implicit "nt:base" supertype
name|boolean
name|needsNtBase
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|supertypes
control|)
block|{
name|NodeTypeDefinition
name|st
init|=
name|templates
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|st
operator|==
literal|null
condition|)
block|{
name|st
operator|=
name|getNodeType
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|st
operator|!=
literal|null
operator|&&
operator|!
name|st
operator|.
name|isMixin
argument_list|()
condition|)
block|{
name|needsNtBase
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|needsNtBase
condition|)
block|{
name|String
index|[]
name|withBase
init|=
operator|new
name|String
index|[
name|supertypes
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|withBase
index|[
literal|0
index|]
operator|=
name|NT_BASE
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|supertypes
argument_list|,
literal|0
argument_list|,
name|withBase
argument_list|,
literal|1
argument_list|,
name|supertypes
operator|.
name|length
argument_list|)
expr_stmt|;
name|template
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
name|withBase
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
try|try
block|{
name|internalRegister
argument_list|(
name|root
argument_list|,
name|templates
operator|.
name|values
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|//----------------------------------------------------< NodeTypeManager>---
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
comment|// TODO proper node type registration... (OAK-66)
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|getOrCreateNodeTypes
argument_list|(
name|root
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeType
name|type
init|=
name|internalRegister
argument_list|(
name|types
argument_list|,
name|ntd
argument_list|,
name|allowUpdate
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
return|return
name|type
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
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
comment|// TODO handle inter-type dependencies (OAK-66)
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|NodeType
argument_list|>
name|list
init|=
name|internalRegister
argument_list|(
name|root
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|ntds
argument_list|)
argument_list|,
name|allowUpdate
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|list
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|NodeType
argument_list|>
name|internalRegister
parameter_list|(
name|Root
name|root
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|NodeTypeDefinition
argument_list|>
name|ntds
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|types
init|=
name|getOrCreateNodeTypes
argument_list|(
name|root
argument_list|)
decl_stmt|;
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
for|for
control|(
name|NodeTypeDefinition
name|ntd
range|:
name|ntds
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|internalRegister
argument_list|(
name|types
argument_list|,
name|ntd
argument_list|,
name|allowUpdate
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
name|NodeType
name|internalRegister
parameter_list|(
name|Tree
name|types
parameter_list|,
name|NodeTypeDefinition
name|ntd
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|jcrName
init|=
name|ntd
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|oakName
init|=
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|allowUpdate
condition|)
block|{
name|type
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NodeTypeExistsException
argument_list|(
literal|"Node type "
operator|+
name|jcrName
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
name|type
operator|=
name|types
operator|.
name|addChild
argument_list|(
name|oakName
argument_list|)
expr_stmt|;
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|type
argument_list|,
name|getNameMapper
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|setName
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_NODETYPE
argument_list|)
expr_stmt|;
name|node
operator|.
name|setName
argument_list|(
name|JCR_NODETYPENAME
argument_list|,
name|jcrName
argument_list|)
expr_stmt|;
name|node
operator|.
name|setNames
argument_list|(
name|JCR_SUPERTYPES
argument_list|,
name|ntd
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_IS_ABSTRACT
argument_list|,
name|ntd
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_IS_QUERYABLE
argument_list|,
name|ntd
operator|.
name|isQueryable
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_ISMIXIN
argument_list|,
name|ntd
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO fail if not orderable but a supertype is orderable. See 3.7.6.7 Node Type Attribute Subtyping Rules
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_HASORDERABLECHILDNODES
argument_list|,
name|ntd
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|primaryItemName
init|=
name|ntd
operator|.
name|getPrimaryItemName
argument_list|()
decl_stmt|;
comment|// TODO fail if a supertype specifies a different primary item. See 3.7.6.7 Node Type Attribute Subtyping Rules
if|if
condition|(
name|primaryItemName
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|setName
argument_list|(
name|JCR_PRIMARYITEMNAME
argument_list|,
name|primaryItemName
argument_list|)
expr_stmt|;
block|}
comment|// TODO fail on invalid item definitions. See 3.7.6.8 Item Definitions in Subtypes
name|PropertyDefinition
index|[]
name|propertyDefinitions
init|=
name|ntd
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyDefinitions
operator|!=
literal|null
condition|)
block|{
name|int
name|pdn
init|=
literal|1
decl_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|propertyDefinitions
control|)
block|{
name|NodeUtil
name|def
init|=
name|node
operator|.
name|addChild
argument_list|(
name|JCR_PROPERTYDEFINITION
operator|+
name|pdn
operator|++
argument_list|,
name|NT_PROPERTYDEFINITION
argument_list|)
decl_stmt|;
name|internalRegisterPropertyDefinition
argument_list|(
name|def
argument_list|,
name|pd
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeDefinition
index|[]
name|nodeDefinitions
init|=
name|ntd
operator|.
name|getDeclaredChildNodeDefinitions
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeDefinitions
operator|!=
literal|null
condition|)
block|{
name|int
name|ndn
init|=
literal|1
decl_stmt|;
for|for
control|(
name|NodeDefinition
name|nd
range|:
name|nodeDefinitions
control|)
block|{
name|NodeUtil
name|def
init|=
name|node
operator|.
name|addChild
argument_list|(
name|JCR_CHILDNODEDEFINITION
operator|+
name|ndn
operator|++
argument_list|,
name|NT_CHILDNODEDEFINITION
argument_list|)
decl_stmt|;
name|internalRegisterNodeDefinition
argument_list|(
name|def
argument_list|,
name|nd
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|internalRegisterItemDefinition
parameter_list|(
name|NodeUtil
name|node
parameter_list|,
name|ItemDefinition
name|def
parameter_list|)
block|{
name|String
name|name
init|=
name|def
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|node
operator|.
name|setName
argument_list|(
name|JCR_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|// TODO avoid unbounded recursive auto creation. See 3.7.2.3.5 Chained Auto-creation
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_AUTOCREATED
argument_list|,
name|def
operator|.
name|isAutoCreated
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_MANDATORY
argument_list|,
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_PROTECTED
argument_list|,
name|def
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setString
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
block|}
specifier|private
specifier|static
name|void
name|internalRegisterPropertyDefinition
parameter_list|(
name|NodeUtil
name|node
parameter_list|,
name|PropertyDefinition
name|def
parameter_list|)
block|{
name|internalRegisterItemDefinition
argument_list|(
name|node
argument_list|,
name|def
argument_list|)
expr_stmt|;
name|node
operator|.
name|setString
argument_list|(
name|JCR_REQUIREDTYPE
argument_list|,
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_MULTIPLE
argument_list|,
name|def
operator|.
name|isMultiple
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_IS_FULLTEXT_SEARCHABLE
argument_list|,
name|def
operator|.
name|isFullTextSearchable
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_IS_QUERY_ORDERABLE
argument_list|,
name|def
operator|.
name|isQueryOrderable
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setStrings
argument_list|(
name|JCR_AVAILABLE_QUERY_OPERATORS
argument_list|,
name|def
operator|.
name|getAvailableQueryOperators
argument_list|()
argument_list|)
expr_stmt|;
name|String
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
condition|)
block|{
name|node
operator|.
name|setStrings
argument_list|(
name|JCR_VALUECONSTRAINTS
argument_list|,
name|constraints
argument_list|)
expr_stmt|;
block|}
name|Value
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
name|node
operator|.
name|setValues
argument_list|(
name|JCR_DEFAULTVALUES
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|internalRegisterNodeDefinition
parameter_list|(
name|NodeUtil
name|node
parameter_list|,
name|NodeDefinition
name|def
parameter_list|)
block|{
name|internalRegisterItemDefinition
argument_list|(
name|node
argument_list|,
name|def
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
name|JCR_SAMENAMESIBLINGS
argument_list|,
name|def
operator|.
name|allowsSameNameSiblings
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setNames
argument_list|(
name|JCR_REQUIREDPRIMARYTYPES
argument_list|,
name|def
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|defaultPrimaryType
init|=
name|def
operator|.
name|getDefaultPrimaryTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultPrimaryType
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|setName
argument_list|(
name|JCR_DEFAULTPRIMARYTYPE
argument_list|,
name|defaultPrimaryType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Tree
name|getOrCreateNodeTypes
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
name|Tree
name|system
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|'/'
operator|+
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
name|system
operator|==
literal|null
condition|)
block|{
name|system
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
name|JCR_SYSTEM
argument_list|)
expr_stmt|;
block|}
name|types
operator|=
name|system
operator|.
name|addChild
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
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
name|Tree
name|type
init|=
literal|null
decl_stmt|;
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|types
operator|.
name|getChild
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered."
argument_list|)
throw|;
block|}
try|try
block|{
name|type
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to unregister node type "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node types can not be unregistered."
argument_list|)
throw|;
block|}
try|try
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|Tree
name|type
init|=
name|types
operator|.
name|getChild
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered."
argument_list|)
throw|;
block|}
name|type
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to unregister node types"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

