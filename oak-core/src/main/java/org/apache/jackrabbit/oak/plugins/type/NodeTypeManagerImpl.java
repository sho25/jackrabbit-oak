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
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|CoreValueFactory
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
name|DefaultConflictHandler
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

begin_class
specifier|public
class|class
name|NodeTypeManagerImpl
implements|implements
name|NodeTypeManager
block|{
specifier|private
specifier|final
name|ContentSession
name|session
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|factory
decl_stmt|;
specifier|public
name|NodeTypeManagerImpl
parameter_list|(
name|ContentSession
name|session
parameter_list|,
name|NameMapper
name|mapper
parameter_list|,
name|ValueFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
if|if
condition|(
name|session
operator|.
name|getCurrentRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
argument_list|)
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|NodeTypeManagerImpl
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"builtin_nodetypes.cnd"
argument_list|)
decl_stmt|;
try|try
block|{
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
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|DefBuilderFactory
argument_list|()
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
literal|"nt:base"
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
literal|"nt:base"
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
if|if
condition|(
operator|!
name|templates
operator|.
name|get
argument_list|(
name|name
argument_list|)
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
literal|"nt:base"
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
name|registerNodeTypes
argument_list|(
name|templates
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|NodeTypeTemplate
index|[
name|templates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to load built-in node types"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|String
name|getOakName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|name
return|;
comment|// TODO
block|}
comment|/**      * Called by the {@link NodeTypeManager} implementation methods to      * refresh the state of the session associated with this instance.      * That way the session is kept in sync with the latest global state      * seen by the node type manager.      *      * @throws RepositoryException if the session could not be refreshed      */
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
comment|//---------------------------------------------------< NodeTypeManager>--
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
name|session
operator|.
name|getCurrentRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
argument_list|)
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
name|mapper
operator|.
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
name|Tree
name|types
init|=
name|session
operator|.
name|getCurrentRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
argument_list|)
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
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
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
name|factory
argument_list|,
operator|new
name|NodeUtil
argument_list|(
name|session
operator|.
name|getCoreValueFactory
argument_list|()
argument_list|,
name|mapper
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
name|name
argument_list|)
throw|;
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
name|session
operator|.
name|getCurrentRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
argument_list|)
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
name|factory
argument_list|,
operator|new
name|NodeUtil
argument_list|(
name|session
operator|.
name|getCoreValueFactory
argument_list|()
argument_list|,
name|mapper
argument_list|,
name|type
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
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|this
argument_list|,
name|factory
argument_list|)
return|;
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
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|ntd
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinitionTemplate
name|createNodeDefinitionTemplate
parameter_list|()
block|{
return|return
operator|new
name|NodeDefinitionTemplateImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinitionTemplate
name|createPropertyDefinitionTemplate
parameter_list|()
block|{
return|return
operator|new
name|PropertyDefinitionTemplateImpl
argument_list|()
return|;
block|}
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
name|session
operator|.
name|getCurrentRoot
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
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
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
name|session
operator|.
name|getCurrentRoot
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntds
operator|.
name|length
condition|;
name|i
operator|++
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
name|ntds
index|[
name|i
index|]
argument_list|,
name|allowUpdate
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
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
name|mapper
operator|.
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
name|CoreValueFactory
name|factory
init|=
name|session
operator|.
name|getCoreValueFactory
argument_list|()
decl_stmt|;
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|factory
argument_list|,
name|mapper
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|node
operator|.
name|setName
argument_list|(
literal|"jcr:nodeTypeName"
argument_list|,
name|jcrName
argument_list|)
expr_stmt|;
name|node
operator|.
name|setNames
argument_list|(
literal|"jcr:supertypes"
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
literal|"jcr:isAbstract"
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
literal|"jcr:isQueryable"
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
literal|"jcr:isMixin"
argument_list|,
name|ntd
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
literal|"jcr:hasOrderableChildNodes"
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
literal|"jcr:primaryItemName"
argument_list|,
name|primaryItemName
argument_list|)
expr_stmt|;
block|}
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
name|ntd
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
control|)
block|{
name|Tree
name|def
init|=
name|type
operator|.
name|addChild
argument_list|(
literal|"jcr:propertyDefinition"
operator|+
name|pdn
operator|++
argument_list|)
decl_stmt|;
name|internalRegisterPropertyDefinition
argument_list|(
operator|new
name|NodeUtil
argument_list|(
name|factory
argument_list|,
name|mapper
argument_list|,
name|def
argument_list|)
argument_list|,
name|pd
argument_list|)
expr_stmt|;
block|}
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
name|ntd
operator|.
name|getDeclaredChildNodeDefinitions
argument_list|()
control|)
block|{
name|Tree
name|def
init|=
name|type
operator|.
name|addChild
argument_list|(
literal|"jcr:childNodeDefinition"
operator|+
name|ndn
operator|++
argument_list|)
decl_stmt|;
name|internalRegisterNodeDefinition
argument_list|(
operator|new
name|NodeUtil
argument_list|(
name|factory
argument_list|,
name|mapper
argument_list|,
name|def
argument_list|)
argument_list|,
name|nd
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|factory
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|private
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
literal|"jcr:name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|setBoolean
argument_list|(
literal|"jcr:autoCreated"
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
literal|"jcr:mandatory"
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
literal|"jcr:protected"
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
literal|"jcr:onParentVersion"
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
name|void
name|internalRegisterPropertyDefinition
parameter_list|(
name|NodeUtil
name|node
parameter_list|,
name|PropertyDefinition
name|def
parameter_list|)
throws|throws
name|RepositoryException
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
literal|"jcr:requiredType"
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
argument_list|)
expr_stmt|;
name|node
operator|.
name|setBoolean
argument_list|(
literal|"jcr:multiple"
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
literal|"jcr:isFullTextSearchable"
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
literal|"jcr:isQueryOrderable"
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
literal|"jcr:availableQueryOperators"
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
literal|"jcr:valueConstraints"
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
literal|"jcr:defaultValues"
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
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
literal|"jcr:sameNameSiblings"
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
literal|"jcr:requiredPrimaryTypes"
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
literal|"jcr:defaultPrimaryType"
argument_list|,
name|defaultPrimaryType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
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
literal|"/jcr:system/jcr:nodeTypes"
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
literal|"/jcr:system"
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
literal|"jcr:system"
argument_list|)
expr_stmt|;
block|}
name|types
operator|=
name|system
operator|.
name|addChild
argument_list|(
literal|"jcr:nodeTypes"
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
name|session
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
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
name|mapper
operator|.
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
comment|// TODO: Degrade gracefully? Or throw NoSuchNodeTypeException?
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered"
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
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
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
name|session
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/jcr:system/jcr:nodeTypes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
comment|// TODO: Degrade gracefully? Or throw NoSuchNodeTypeException?
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Node types can not be unregistered"
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
name|mapper
operator|.
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
comment|// TODO: Degrade gracefully? Or throw NoSuchNodeTypeException?
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered"
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
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
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

