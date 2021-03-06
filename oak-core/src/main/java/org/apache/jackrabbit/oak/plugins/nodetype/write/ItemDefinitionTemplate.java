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
name|JCR_PROTECTED
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Base class for the node and property definition template implementations  * in this package. Takes care of the shared item definition attributes and  * manages mappings between JCR and Oak names.  */
end_comment

begin_class
specifier|abstract
class|class
name|ItemDefinitionTemplate
extends|extends
name|NamedTemplate
implements|implements
name|ItemDefinition
block|{
specifier|private
name|boolean
name|residual
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isAutoCreated
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|onParentVersion
init|=
name|OnParentVersionAction
operator|.
name|COPY
decl_stmt|;
specifier|private
name|boolean
name|isProtected
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isMandatory
init|=
literal|false
decl_stmt|;
specifier|protected
name|ItemDefinitionTemplate
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
specifier|protected
name|ItemDefinitionTemplate
parameter_list|(
name|NameMapper
name|mapper
parameter_list|,
name|ItemDefinition
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
name|setProtected
argument_list|(
name|definition
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
name|setMandatory
argument_list|(
name|definition
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
name|setAutoCreated
argument_list|(
name|definition
operator|.
name|isAutoCreated
argument_list|()
argument_list|)
expr_stmt|;
name|setOnParentVersion
argument_list|(
name|definition
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes the contents of this item definition to the given tree node.      * Used when registering new node types.      *      * @param tree an {@code nt:propertyDefinition} or      *             {@code nt:childNodeDefinition} node      * @throws RepositoryException if this definition could not be written      */
name|void
name|writeTo
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|residual
condition|)
block|{
name|String
name|oakName
init|=
name|getOakName
argument_list|()
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unnamed item definition"
argument_list|)
throw|;
block|}
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_NAME
argument_list|,
name|oakName
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
name|JCR_NAME
argument_list|)
expr_stmt|;
block|}
comment|// TODO avoid (in validator?) unbounded recursive auto creation.
comment|// See 3.7.2.3.5 Chained Auto-creation (OAK-411)
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_AUTOCREATED
argument_list|,
name|isAutoCreated
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_MANDATORY
argument_list|,
name|isMandatory
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_PROTECTED
argument_list|,
name|isProtected
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_ONPARENTVERSION
argument_list|,
name|OnParentVersionAction
operator|.
name|nameFromValue
argument_list|(
name|onParentVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< public>--
comment|/**      * Returns the name of this template, or {@code null} if the name      * has not yet been set. The special name "*" is used for residual      * item definitions.      *      * @return JCR name, "*", or {@code null}      */
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|residual
condition|)
block|{
return|return
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
comment|/**      * Sets the name of this template. Use the special name "*" for a residual      * item definition.      *      * @param jcrName JCR name, or "*"      * @throws ConstraintViolationException if the name is invalid      */
annotation|@
name|Override
specifier|public
name|void
name|setName
parameter_list|(
annotation|@
name|NotNull
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|residual
operator|=
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
operator|.
name|equals
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|residual
condition|)
block|{
name|super
operator|.
name|setName
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns {@code null} since an item definition template is not      * attached to a live, already registered node type.      *      * @return {@code null}      */
annotation|@
name|Override
specifier|public
name|NodeType
name|getDeclaringNodeType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAutoCreated
parameter_list|()
block|{
return|return
name|isAutoCreated
return|;
block|}
specifier|public
name|void
name|setAutoCreated
parameter_list|(
name|boolean
name|isAutoCreated
parameter_list|)
block|{
name|this
operator|.
name|isAutoCreated
operator|=
name|isAutoCreated
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMandatory
parameter_list|()
block|{
return|return
name|isMandatory
return|;
block|}
specifier|public
name|void
name|setMandatory
parameter_list|(
name|boolean
name|isMandatory
parameter_list|)
block|{
name|this
operator|.
name|isMandatory
operator|=
name|isMandatory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOnParentVersion
parameter_list|()
block|{
return|return
name|onParentVersion
return|;
block|}
specifier|public
name|void
name|setOnParentVersion
parameter_list|(
name|int
name|onParentVersion
parameter_list|)
block|{
name|OnParentVersionAction
operator|.
name|nameFromValue
argument_list|(
name|onParentVersion
argument_list|)
expr_stmt|;
comment|// validate
name|this
operator|.
name|onParentVersion
operator|=
name|onParentVersion
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isProtected
parameter_list|()
block|{
return|return
name|isProtected
return|;
block|}
specifier|public
name|void
name|setProtected
parameter_list|(
name|boolean
name|isProtected
parameter_list|)
block|{
name|this
operator|.
name|isProtected
operator|=
name|isProtected
expr_stmt|;
block|}
block|}
end_class

end_unit

