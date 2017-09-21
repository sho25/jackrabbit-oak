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
name|oak
operator|.
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
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
name|NodeType
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

begin_class
class|class
name|NodeDefinitionTemplateImpl
extends|extends
name|ItemDefinitionTemplate
implements|implements
name|NodeDefinitionTemplate
block|{
specifier|private
name|boolean
name|allowSameNameSiblings
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|defaultPrimaryTypeOakName
init|=
literal|null
decl_stmt|;
specifier|private
name|String
index|[]
name|requiredPrimaryTypeOakNames
init|=
literal|null
decl_stmt|;
specifier|public
name|NodeDefinitionTemplateImpl
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
specifier|public
name|NodeDefinitionTemplateImpl
parameter_list|(
name|NameMapper
name|mapper
parameter_list|,
name|NodeDefinition
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
argument_list|)
expr_stmt|;
name|setSameNameSiblings
argument_list|(
name|definition
operator|.
name|allowsSameNameSiblings
argument_list|()
argument_list|)
expr_stmt|;
name|setDefaultPrimaryTypeName
argument_list|(
name|definition
operator|.
name|getDefaultPrimaryTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|setRequiredPrimaryTypeNames
argument_list|(
name|definition
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes the contents of this node definition to the given tree node.      * Used when registering new node types.      *      * @param tree an {@code nt:childNodeDefinition} node      * @throws RepositoryException if this definition could not be written      */
annotation|@
name|Override
name|void
name|writeTo
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_SAMENAMESIBLINGS
argument_list|,
name|allowSameNameSiblings
argument_list|)
expr_stmt|;
if|if
condition|(
name|requiredPrimaryTypeOakNames
operator|!=
literal|null
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_REQUIREDPRIMARYTYPES
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|requiredPrimaryTypeOakNames
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tree
operator|.
name|removeProperty
argument_list|(
name|JCR_REQUIREDPRIMARYTYPES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defaultPrimaryTypeOakName
operator|!=
literal|null
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_DEFAULTPRIMARYTYPE
argument_list|,
name|defaultPrimaryTypeOakName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tree
operator|.
name|removeProperty
argument_list|(
name|JCR_DEFAULTPRIMARYTYPE
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< public>--
annotation|@
name|Override
specifier|public
name|boolean
name|allowsSameNameSiblings
parameter_list|()
block|{
return|return
name|allowSameNameSiblings
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSameNameSiblings
parameter_list|(
name|boolean
name|allowSameNameSiblings
parameter_list|)
block|{
name|this
operator|.
name|allowSameNameSiblings
operator|=
name|allowSameNameSiblings
expr_stmt|;
block|}
comment|/**      * Returns {@code null} since an item definition template is not      * attached to a live, already registered node type.      *      * @return {@code null}      */
annotation|@
name|Override
specifier|public
name|NodeType
name|getDefaultPrimaryType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrimaryTypeName
parameter_list|()
block|{
return|return
name|getJcrNameAllowNull
argument_list|(
name|defaultPrimaryTypeOakName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultPrimaryTypeName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
operator|.
name|defaultPrimaryTypeOakName
operator|=
name|getOakNameAllowNullOrThrowConstraintViolation
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns {@code null} since an item definition template is not      * attached to a live, already registered node type.      *      * @return {@code null}      */
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getRequiredPrimaryTypes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getRequiredPrimaryTypeNames
parameter_list|()
block|{
return|return
name|getJcrNamesAllowNull
argument_list|(
name|requiredPrimaryTypeOakNames
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRequiredPrimaryTypeNames
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
name|requiredPrimaryTypeOakNames
operator|=
name|getOakNamesOrThrowConstraintViolation
argument_list|(
name|jcrNames
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"+ "
argument_list|)
decl_stmt|;
if|if
condition|(
name|getOakName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|RESIDUAL_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|getOakName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defaultPrimaryTypeOakName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|defaultPrimaryTypeOakName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isAutoCreated
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" a"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isProtected
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" p"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isMandatory
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getOnParentVersion
argument_list|()
operator|!=
name|OnParentVersionAction
operator|.
name|COPY
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|OnParentVersionAction
operator|.
name|nameFromValue
argument_list|(
name|getOnParentVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

