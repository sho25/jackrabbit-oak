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
operator|.
name|write
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
name|List
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
name|namepath
operator|.
name|NameMapper
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
name|JCR_IS_QUERYABLE
import|;
end_import

begin_class
class|class
name|NodeTypeTemplateImpl
extends|extends
name|NamedTemplate
implements|implements
name|NodeTypeTemplate
block|{
specifier|private
specifier|static
specifier|final
name|PropertyDefinition
index|[]
name|EMPTY_PROPERTY_DEFINITION_ARRAY
init|=
operator|new
name|PropertyDefinition
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|NodeDefinition
index|[]
name|EMPTY_NODE_DEFINITION_ARRAY
init|=
operator|new
name|NodeDefinition
index|[
literal|0
index|]
decl_stmt|;
specifier|protected
name|boolean
name|isMixin
decl_stmt|;
specifier|protected
name|boolean
name|isOrderable
decl_stmt|;
specifier|protected
name|boolean
name|isAbstract
decl_stmt|;
specifier|protected
name|boolean
name|queryable
decl_stmt|;
specifier|private
name|String
name|primaryItemOakName
init|=
literal|null
decl_stmt|;
comment|// not defined by default
annotation|@
name|Nonnull
specifier|private
name|String
index|[]
name|superTypeOakNames
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|List
argument_list|<
name|PropertyDefinitionTemplateImpl
argument_list|>
name|propertyDefinitionTemplates
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|NodeDefinitionTemplateImpl
argument_list|>
name|nodeDefinitionTemplates
init|=
literal|null
decl_stmt|;
name|NodeTypeTemplateImpl
parameter_list|(
name|NameMapper
name|mapper
parameter_list|)
block|{
name|super
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
block|}
name|NodeTypeTemplateImpl
parameter_list|(
name|NameMapper
name|mapper
parameter_list|,
name|NodeTypeDefinition
name|definition
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|super
argument_list|(
name|mapper
argument_list|,
name|definition
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|setMixin
argument_list|(
name|definition
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
name|setOrderableChildNodes
argument_list|(
name|definition
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
expr_stmt|;
name|setAbstract
argument_list|(
name|definition
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|setQueryable
argument_list|(
name|definition
operator|.
name|isQueryable
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|primaryItemName
init|=
name|definition
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
name|setPrimaryItemName
argument_list|(
name|primaryItemName
argument_list|)
expr_stmt|;
block|}
name|setDeclaredSuperTypeNames
argument_list|(
name|definition
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyDefinition
index|[]
name|pds
init|=
name|definition
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
decl_stmt|;
if|if
condition|(
name|pds
operator|!=
literal|null
condition|)
block|{
name|propertyDefinitionTemplates
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|pds
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|pds
control|)
block|{
name|propertyDefinitionTemplates
operator|.
name|add
argument_list|(
operator|new
name|PropertyDefinitionTemplateImpl
argument_list|(
name|mapper
argument_list|,
name|pd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeDefinition
index|[]
name|nds
init|=
name|definition
operator|.
name|getDeclaredChildNodeDefinitions
argument_list|()
decl_stmt|;
if|if
condition|(
name|nds
operator|!=
literal|null
condition|)
block|{
name|nodeDefinitionTemplates
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|nds
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeDefinition
name|nd
range|:
name|nds
control|)
block|{
name|nodeDefinitionTemplates
operator|.
name|add
argument_list|(
operator|new
name|NodeDefinitionTemplateImpl
argument_list|(
name|mapper
argument_list|,
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Writes this node type as an {@code nt:nodeType} child of the given      * parent node. An exception is thrown if the child node already exists,      * unless the {@code allowUpdate} flag is set, in which case the existing      * node is overwritten.      *      * @param parent parent node under which to write this node type      * @param allowUpdate whether to overwrite an existing type      * @return The node type tree.      * @throws RepositoryException if this type could not be written      */
name|Tree
name|writeTo
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakName
init|=
name|getOakName
argument_list|()
decl_stmt|;
name|Tree
name|type
init|=
name|parent
operator|.
name|getChildOrNull
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
name|getName
argument_list|()
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
name|type
operator|=
name|parent
operator|.
name|addChild
argument_list|(
name|oakName
argument_list|)
expr_stmt|;
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_NODETYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_NODETYPENAME
argument_list|,
name|oakName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|superTypeOakNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_SUPERTYPES
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|superTypeOakNames
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_IS_ABSTRACT
argument_list|,
name|isAbstract
argument_list|)
expr_stmt|;
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_IS_QUERYABLE
argument_list|,
name|queryable
argument_list|)
expr_stmt|;
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_ISMIXIN
argument_list|,
name|isMixin
argument_list|)
expr_stmt|;
comment|// TODO fail (in validator?) if not orderable but a supertype is orderable
comment|// See 3.7.6.7 Node Type Attribute Subtyping Rules (OAK-411)
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_HASORDERABLECHILDNODES
argument_list|,
name|isOrderable
argument_list|)
expr_stmt|;
comment|// TODO fail (in validator?) if a supertype specifies a different primary item
comment|// See 3.7.6.7 Node Type Attribute Subtyping Rules (OAK-411)
if|if
condition|(
name|primaryItemOakName
operator|!=
literal|null
condition|)
block|{
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYITEMNAME
argument_list|,
name|primaryItemOakName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|// TODO fail (in validator?) on invalid item definitions
comment|// See 3.7.6.8 Item Definitions in Subtypes (OAK-411)
if|if
condition|(
name|propertyDefinitionTemplates
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
name|PropertyDefinitionTemplateImpl
name|pdt
range|:
name|propertyDefinitionTemplates
control|)
block|{
name|Tree
name|tree
init|=
name|type
operator|.
name|addChild
argument_list|(
name|JCR_PROPERTYDEFINITION
operator|+
literal|"["
operator|+
name|pdn
operator|++
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_PROPERTYDEFINITION
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|writeTo
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nodeDefinitionTemplates
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
name|NodeDefinitionTemplateImpl
name|ndt
range|:
name|nodeDefinitionTemplates
control|)
block|{
name|Tree
name|tree
init|=
name|type
operator|.
name|addChild
argument_list|(
name|JCR_CHILDNODEDEFINITION
operator|+
literal|"["
operator|+
name|ndn
operator|++
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_CHILDNODEDEFINITION
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|writeTo
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
comment|//------------------------------------------------------------< public>--
annotation|@
name|Override
specifier|public
name|boolean
name|isMixin
parameter_list|()
block|{
return|return
name|isMixin
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMixin
parameter_list|(
name|boolean
name|mixin
parameter_list|)
block|{
name|this
operator|.
name|isMixin
operator|=
name|mixin
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOrderableChildNodes
parameter_list|()
block|{
return|return
name|isOrderable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderableChildNodes
parameter_list|(
name|boolean
name|orderable
parameter_list|)
block|{
name|this
operator|.
name|isOrderable
operator|=
name|orderable
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbstract
parameter_list|()
block|{
return|return
name|isAbstract
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAbstract
parameter_list|(
name|boolean
name|abstractStatus
parameter_list|)
block|{
name|this
operator|.
name|isAbstract
operator|=
name|abstractStatus
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryable
parameter_list|()
block|{
return|return
name|queryable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setQueryable
parameter_list|(
name|boolean
name|queryable
parameter_list|)
block|{
name|this
operator|.
name|queryable
operator|=
name|queryable
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryItemName
parameter_list|()
block|{
return|return
name|getJcrNameAllowNull
argument_list|(
name|primaryItemOakName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryItemName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
operator|.
name|primaryItemOakName
operator|=
name|getOakNameAllowNullOrThrowConstraintViolation
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
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
name|getJcrNamesAllowNull
argument_list|(
name|superTypeOakNames
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDeclaredSuperTypeNames
parameter_list|(
name|String
index|[]
name|jcrNames
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
operator|.
name|superTypeOakNames
operator|=
name|getOakNamesOrThrowConstraintViolation
argument_list|(
name|jcrNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinition
index|[]
name|getDeclaredPropertyDefinitions
parameter_list|()
block|{
if|if
condition|(
name|propertyDefinitionTemplates
operator|!=
literal|null
condition|)
block|{
return|return
name|propertyDefinitionTemplates
operator|.
name|toArray
argument_list|(
name|EMPTY_PROPERTY_DEFINITION_ARRAY
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|PropertyDefinitionTemplate
argument_list|>
name|getPropertyDefinitionTemplates
parameter_list|()
block|{
if|if
condition|(
name|propertyDefinitionTemplates
operator|==
literal|null
condition|)
block|{
name|propertyDefinitionTemplates
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
return|return
name|propertyDefinitionTemplates
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
if|if
condition|(
name|nodeDefinitionTemplates
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeDefinitionTemplates
operator|.
name|toArray
argument_list|(
name|EMPTY_NODE_DEFINITION_ARRAY
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|NodeDefinitionTemplate
argument_list|>
name|getNodeDefinitionTemplates
parameter_list|()
block|{
if|if
condition|(
name|nodeDefinitionTemplates
operator|==
literal|null
condition|)
block|{
name|nodeDefinitionTemplates
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeDefinitionTemplates
return|;
block|}
comment|//------------------------------------------------------------< Object>--
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"NodeTypeTemplate(%s)"
argument_list|,
name|getOakName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

