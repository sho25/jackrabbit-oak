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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Collection
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
name|LinkedList
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
name|Queue
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
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
name|type
operator|.
name|constraint
operator|.
name|Constraints
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
name|security
operator|.
name|principal
operator|.
name|AdminPrincipal
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
name|oak
operator|.
name|plugins
operator|.
name|type
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
name|type
operator|.
name|NodeTypeConstants
operator|.
name|JCR_IS_QUERYABLE
import|;
end_import

begin_comment
comment|/**  *<pre>  * [nt:nodeType]  * - jcr:nodeTypeName (NAME) protected mandatory  * - jcr:supertypes (NAME) protected multiple  * - jcr:isAbstract (BOOLEAN) protected mandatory  * - jcr:isQueryable (BOOLEAN) protected mandatory  * - jcr:isMixin (BOOLEAN) protected mandatory  * - jcr:hasOrderableChildNodes (BOOLEAN) protected mandatory  * - jcr:primaryItemName (NAME) protected  * + jcr:propertyDefinition (nt:propertyDefinition) = nt:propertyDefinition protected sns  * + jcr:childNodeDefinition (nt:childNodeDefinition) = nt:childNodeDefinition protected sns  *</pre>  */
end_comment

begin_class
class|class
name|NodeTypeImpl
implements|implements
name|NodeType
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NodeTypeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeTypeManager
name|manager
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|NodeUtil
name|node
decl_stmt|;
specifier|public
name|NodeTypeImpl
parameter_list|(
name|NodeTypeManager
name|manager
parameter_list|,
name|ValueFactory
name|factory
parameter_list|,
name|NamePathMapper
name|mapper
parameter_list|,
name|NodeUtil
name|node
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
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
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|String
name|name
init|=
name|node
operator|.
name|getName
argument_list|(
name|JCR_NODETYPENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|node
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDeclaredSupertypeNames
parameter_list|()
block|{
return|return
name|node
operator|.
name|getNames
argument_list|(
name|JCR_SUPERTYPES
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbstract
parameter_list|()
block|{
return|return
name|node
operator|.
name|getBoolean
argument_list|(
name|JCR_IS_ABSTRACT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMixin
parameter_list|()
block|{
return|return
name|node
operator|.
name|getBoolean
argument_list|(
name|JCR_ISMIXIN
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOrderableChildNodes
parameter_list|()
block|{
return|return
name|node
operator|.
name|getBoolean
argument_list|(
name|JCR_HASORDERABLECHILDNODES
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryable
parameter_list|()
block|{
return|return
name|node
operator|.
name|getBoolean
argument_list|(
name|JCR_IS_QUERYABLE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryItemName
parameter_list|()
block|{
return|return
name|node
operator|.
name|getName
argument_list|(
name|JCR_PRIMARYITEMNAME
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinition
index|[]
name|getDeclaredPropertyDefinitions
parameter_list|()
block|{
name|List
argument_list|<
name|NodeUtil
argument_list|>
name|nodes
init|=
name|node
operator|.
name|getNodes
argument_list|(
name|JCR_PROPERTYDEFINITION
argument_list|)
decl_stmt|;
name|PropertyDefinition
index|[]
name|definitions
init|=
operator|new
name|PropertyDefinition
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
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
name|nodes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|definitions
index|[
name|i
index|]
operator|=
operator|new
name|PropertyDefinitionImpl
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|nodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|definitions
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
index|[]
name|getDeclaredChildNodeDefinitions
parameter_list|()
block|{
name|List
argument_list|<
name|NodeUtil
argument_list|>
name|nodes
init|=
name|node
operator|.
name|getNodes
argument_list|(
name|JCR_CHILDNODEDEFINITION
argument_list|)
decl_stmt|;
name|NodeDefinition
index|[]
name|definitions
init|=
operator|new
name|NodeDefinition
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
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
name|nodes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|definitions
index|[
name|i
index|]
operator|=
operator|new
name|NodeDefinitionImpl
argument_list|(
name|manager
argument_list|,
name|this
argument_list|,
name|nodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|definitions
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getSupertypes
parameter_list|()
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
argument_list|)
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
name|String
name|name
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
name|added
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
try|try
block|{
name|NodeType
name|type
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
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
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Inconsistent node type: "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|types
operator|.
name|toArray
argument_list|(
operator|new
name|NodeType
index|[
name|types
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getDeclaredSupertypes
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
name|getDeclaredSupertypeNames
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|(
name|names
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
try|try
block|{
name|NodeType
name|type
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to access declared supertype "
operator|+
name|name
operator|+
literal|" of "
operator|+
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|types
operator|.
name|toArray
argument_list|(
operator|new
name|NodeType
index|[
name|types
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getSubtypes
parameter_list|()
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeTypeIterator
name|iterator
init|=
name|manager
operator|.
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
name|isNodeType
argument_list|(
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|isNodeType
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to access subtypes of "
operator|+
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getDeclaredSubtypes
parameter_list|()
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeTypeIterator
name|iterator
init|=
name|manager
operator|.
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
name|type
operator|.
name|isNodeType
argument_list|(
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|isNodeType
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|declaredSuperTypeNames
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|declaredSuperTypeNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to access declared subtypes of "
operator|+
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNodeType
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
if|if
condition|(
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|NodeType
name|type
range|:
name|getDeclaredSupertypes
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isNodeType
argument_list|(
name|nodeTypeName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinition
index|[]
name|getPropertyDefinitions
parameter_list|()
block|{
comment|// TODO distinguish between additive and overriding property definitions. See 3.7.6.8 Item Definitions in Subtypes
name|Collection
argument_list|<
name|PropertyDefinition
argument_list|>
name|definitions
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|getSupertypes
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredPropertyDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|definitions
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyDefinition
index|[
name|definitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
index|[]
name|getChildNodeDefinitions
parameter_list|()
block|{
comment|// TODO distinguish between additive and overriding node definitions. See 3.7.6.8 Item Definitions in Subtypes
name|Collection
argument_list|<
name|NodeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|getSupertypes
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredChildNodeDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredChildNodeDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|definitions
operator|.
name|toArray
argument_list|(
operator|new
name|NodeDefinition
index|[
name|definitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canSetProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Value
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|canRemoveProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
for|for
control|(
name|PropertyDefinition
name|definition
range|:
name|getPropertyDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|propertyName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isProtected
argument_list|(
name|definition
argument_list|)
operator|)
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|definition
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
return|return
name|meetsTypeConstraints
argument_list|(
name|value
argument_list|,
name|definition
operator|.
name|getRequiredType
argument_list|()
argument_list|)
operator|&&
name|meetsValueConstraints
argument_list|(
name|value
argument_list|,
name|definition
operator|.
name|getValueConstraints
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canSetProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
name|canRemoveProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
for|for
control|(
name|PropertyDefinition
name|definition
range|:
name|getPropertyDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|propertyName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isProtected
argument_list|(
name|definition
argument_list|)
operator|)
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|definition
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
return|return
name|meetsTypeConstraints
argument_list|(
name|values
argument_list|,
name|definition
operator|.
name|getRequiredType
argument_list|()
argument_list|)
operator|&&
name|meetsValueConstraints
argument_list|(
name|values
argument_list|,
name|definition
operator|.
name|getValueConstraints
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|meetsTypeConstraints
parameter_list|(
name|Value
name|value
parameter_list|,
name|int
name|requiredType
parameter_list|)
block|{
try|try
block|{
switch|switch
condition|(
name|requiredType
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
name|value
operator|.
name|getString
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
name|value
operator|.
name|getBinary
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
name|value
operator|.
name|getLong
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
name|value
operator|.
name|getDouble
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
name|value
operator|.
name|getDate
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
name|value
operator|.
name|getBoolean
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
name|mapper
operator|.
name|getOakName
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
operator|!=
literal|null
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
name|int
name|type
init|=
name|value
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|type
operator|!=
name|PropertyType
operator|.
name|DOUBLE
operator|&&
name|type
operator|!=
name|PropertyType
operator|.
name|LONG
operator|&&
name|type
operator|!=
name|PropertyType
operator|.
name|BOOLEAN
operator|&&
name|mapper
operator|.
name|getOakPath
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
operator|!=
literal|null
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
name|IdentifierManager
operator|.
name|isValidUUID
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
operator|new
name|URI
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
name|value
operator|.
name|getDecimal
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
case|case
name|PropertyType
operator|.
name|UNDEFINED
case|:
return|return
literal|true
return|;
default|default:
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid property type value: "
operator|+
name|requiredType
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|boolean
name|meetsTypeConstraints
parameter_list|(
name|Value
index|[]
name|values
parameter_list|,
name|int
name|requiredType
parameter_list|)
block|{
comment|// Constraints must be met by all values
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
operator|!
name|meetsTypeConstraints
argument_list|(
name|value
argument_list|,
name|requiredType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|boolean
name|meetsValueConstraints
parameter_list|(
name|Value
name|value
parameter_list|,
name|String
index|[]
name|constraints
parameter_list|)
block|{
if|if
condition|(
name|constraints
operator|==
literal|null
operator|||
name|constraints
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Any of the constraints must be met
for|for
control|(
name|String
name|constraint
range|:
name|constraints
control|)
block|{
if|if
condition|(
name|Constraints
operator|.
name|valueConstraint
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|constraint
argument_list|)
operator|.
name|apply
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|meetsValueConstraints
parameter_list|(
name|Value
index|[]
name|values
parameter_list|,
name|String
index|[]
name|constraints
parameter_list|)
block|{
if|if
condition|(
name|constraints
operator|==
literal|null
operator|||
name|constraints
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Constraints must be met by all values
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
operator|!
name|meetsValueConstraints
argument_list|(
name|value
argument_list|,
name|constraints
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canAddChildNode
parameter_list|(
name|String
name|childNodeName
parameter_list|)
block|{
for|for
control|(
name|NodeDefinition
name|definition
range|:
name|getChildNodeDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|matches
argument_list|(
name|childNodeName
argument_list|,
name|name
argument_list|)
operator|&&
operator|!
name|isProtected
argument_list|(
name|definition
argument_list|)
operator|)
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|definition
operator|.
name|getDefaultPrimaryType
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canAddChildNode
parameter_list|(
name|String
name|childNodeName
parameter_list|,
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeType
name|type
decl_stmt|;
try|try
block|{
name|type
operator|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to access node type "
operator|+
name|nodeTypeName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
for|for
control|(
name|NodeDefinition
name|definition
range|:
name|getChildNodeDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|matches
argument_list|(
name|childNodeName
argument_list|,
name|name
argument_list|)
operator|&&
operator|!
name|isProtected
argument_list|(
name|definition
argument_list|)
operator|)
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|required
range|:
name|definition
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isNodeType
argument_list|(
name|required
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveItem
parameter_list|(
name|String
name|itemName
parameter_list|)
block|{
return|return
name|canRemoveNode
argument_list|(
name|itemName
argument_list|)
operator|||
name|canRemoveProperty
argument_list|(
name|itemName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveNode
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|NodeDefinition
index|[]
name|childNodeDefinitions
init|=
name|getChildNodeDefinitions
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeDefinition
name|definition
range|:
name|childNodeDefinitions
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|matches
argument_list|(
name|nodeName
argument_list|,
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|definition
operator|.
name|isMandatory
argument_list|()
operator|||
name|definition
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
name|childNodeDefinitions
operator|.
name|length
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|PropertyDefinition
index|[]
name|propertyDefinitions
init|=
name|getPropertyDefinitions
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyDefinition
name|definition
range|:
name|propertyDefinitions
control|)
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|definition
operator|.
name|isMandatory
argument_list|()
operator|||
name|definition
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
name|propertyDefinitions
operator|.
name|length
operator|>
literal|0
return|;
block|}
specifier|private
specifier|static
name|boolean
name|matches
parameter_list|(
name|String
name|childNodeName
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// TODO need a better way to handle SNS
return|return
name|childNodeName
operator|.
name|startsWith
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isProtected
parameter_list|(
name|ItemDefinition
name|definition
parameter_list|)
block|{
comment|// TODO need a better way for setting protected items internally
name|Subject
name|subject
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|subject
operator|==
literal|null
operator|||
operator|!
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|contains
argument_list|(
name|AdminPrincipal
operator|.
name|INSTANCE
argument_list|)
operator|)
operator|&&
name|definition
operator|.
name|isProtected
argument_list|()
return|;
block|}
block|}
end_class

end_unit

