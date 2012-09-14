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
name|List
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
name|DefinitionBuilderFactory
operator|.
name|AbstractNodeTypeDefinitionBuilder
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
name|JcrNameParser
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
name|value
operator|.
name|ValueFactoryImpl
import|;
end_import

begin_class
specifier|final
class|class
name|NodeTypeTemplateImpl
extends|extends
name|AbstractNodeTypeDefinitionBuilder
argument_list|<
name|NodeTypeTemplate
argument_list|>
implements|implements
name|NodeTypeTemplate
block|{
specifier|private
specifier|final
name|NodeTypeManager
name|manager
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|factory
decl_stmt|;
specifier|private
name|String
name|primaryItemName
decl_stmt|;
specifier|private
name|String
index|[]
name|superTypeNames
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
name|PropertyDefinitionTemplate
argument_list|>
name|propertyDefinitionTemplates
decl_stmt|;
specifier|private
name|List
argument_list|<
name|NodeDefinitionTemplate
argument_list|>
name|nodeDefinitionTemplates
decl_stmt|;
specifier|public
name|NodeTypeTemplateImpl
parameter_list|(
name|NodeTypeManager
name|manager
parameter_list|,
name|ValueFactory
name|factory
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
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
name|NodeTypeTemplateImpl
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|ValueFactoryImpl
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeTypeTemplateImpl
parameter_list|(
name|NodeTypeManager
name|manager
parameter_list|,
name|ValueFactory
name|factory
parameter_list|,
name|NodeTypeDefinition
name|ntd
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
argument_list|(
name|manager
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|setName
argument_list|(
name|ntd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|setAbstract
argument_list|(
name|ntd
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|setMixin
argument_list|(
name|ntd
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
name|setOrderableChildNodes
argument_list|(
name|ntd
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
expr_stmt|;
name|setQueryable
argument_list|(
name|ntd
operator|.
name|isQueryable
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|ntd
operator|.
name|getPrimaryItemName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|setPrimaryItemName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|setDeclaredSuperTypeNames
argument_list|(
name|ntd
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
expr_stmt|;
name|getPropertyDefinitionTemplates
argument_list|()
expr_stmt|;
comment|// Make sure propertyDefinitionTemplates is initialised
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
name|PropertyDefinitionTemplateImpl
name|pdt
init|=
name|newPropertyDefinitionBuilder
argument_list|()
decl_stmt|;
name|pdt
operator|.
name|setDeclaringNodeType
argument_list|(
name|pd
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setName
argument_list|(
name|pd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setProtected
argument_list|(
name|pd
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setMandatory
argument_list|(
name|pd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setAutoCreated
argument_list|(
name|pd
operator|.
name|isAutoCreated
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setOnParentVersion
argument_list|(
name|pd
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setMultiple
argument_list|(
name|pd
operator|.
name|isMultiple
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setRequiredType
argument_list|(
name|pd
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setDefaultValues
argument_list|(
name|pd
operator|.
name|getDefaultValues
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setValueConstraints
argument_list|(
name|pd
operator|.
name|getValueConstraints
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setFullTextSearchable
argument_list|(
name|pd
operator|.
name|isFullTextSearchable
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setAvailableQueryOperators
argument_list|(
name|pd
operator|.
name|getAvailableQueryOperators
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|setQueryOrderable
argument_list|(
name|pd
operator|.
name|isQueryOrderable
argument_list|()
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|getNodeDefinitionTemplates
argument_list|()
expr_stmt|;
comment|// Make sure nodeDefinitionTemplates is initialised
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
name|NodeDefinitionTemplateImpl
name|ndt
init|=
name|newNodeDefinitionBuilder
argument_list|()
decl_stmt|;
name|ndt
operator|.
name|setDeclaringNodeType
argument_list|(
name|nd
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setName
argument_list|(
name|nd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setProtected
argument_list|(
name|nd
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setMandatory
argument_list|(
name|nd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setAutoCreated
argument_list|(
name|nd
operator|.
name|isAutoCreated
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setOnParentVersion
argument_list|(
name|nd
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setSameNameSiblings
argument_list|(
name|nd
operator|.
name|allowsSameNameSiblings
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setDefaultPrimaryTypeName
argument_list|(
name|nd
operator|.
name|getDefaultPrimaryTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setRequiredPrimaryTypeNames
argument_list|(
name|nd
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|build
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinitionTemplateImpl
name|newPropertyDefinitionBuilder
parameter_list|()
block|{
return|return
operator|new
name|PropertyDefinitionTemplateImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Value
name|createValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|build
parameter_list|()
block|{
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinitionTemplateImpl
name|newNodeDefinitionBuilder
parameter_list|()
block|{
return|return
operator|new
name|NodeDefinitionTemplateImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NodeType
name|getNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|manager
operator|!=
literal|null
condition|)
block|{
return|return
name|manager
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|build
parameter_list|()
block|{
name|getNodeDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|JcrNameParser
operator|.
name|checkName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
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
name|primaryItemName
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryItemName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|primaryItemName
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|JcrNameParser
operator|.
name|checkName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryItemName
operator|=
name|name
expr_stmt|;
block|}
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
name|superTypeNames
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
name|names
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"null is not a valid array of JCR names"
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|JcrNameParser
operator|.
name|checkName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|superTypeNames
operator|=
name|names
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addSupertype
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|JcrNameParser
operator|.
name|checkName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|superTypeNames
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|superTypeNames
argument_list|,
literal|0
argument_list|,
name|names
argument_list|,
literal|0
argument_list|,
name|superTypeNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|names
index|[
name|superTypeNames
operator|.
name|length
index|]
operator|=
name|name
expr_stmt|;
name|superTypeNames
operator|=
name|names
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
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
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinitionTemplate
argument_list|>
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
name|List
argument_list|<
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
operator|new
name|ArrayList
argument_list|<
name|NodeDefinitionTemplate
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeDefinitionTemplates
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
return|return
name|propertyDefinitionTemplates
operator|==
literal|null
condition|?
literal|null
else|:
name|propertyDefinitionTemplates
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyDefinition
index|[
name|propertyDefinitionTemplates
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
name|getDeclaredChildNodeDefinitions
parameter_list|()
block|{
return|return
name|nodeDefinitionTemplates
operator|==
literal|null
condition|?
literal|null
else|:
name|nodeDefinitionTemplates
operator|.
name|toArray
argument_list|(
operator|new
name|NodeDefinition
index|[
name|nodeDefinitionTemplates
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

