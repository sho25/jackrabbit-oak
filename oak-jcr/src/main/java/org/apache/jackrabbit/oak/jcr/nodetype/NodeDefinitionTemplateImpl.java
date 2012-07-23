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
name|jcr
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
name|UnsupportedRepositoryOperationException
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
name|NodeTypeTemplate
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
name|AbstractNodeDefinitionBuilder
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

begin_class
class|class
name|NodeDefinitionTemplateImpl
extends|extends
name|AbstractNodeDefinitionBuilder
argument_list|<
name|NodeTypeTemplate
argument_list|>
implements|implements
name|NodeDefinitionTemplate
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
name|NodeDefinitionTemplateImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|defaultPrimaryTypeName
decl_stmt|;
specifier|private
name|String
index|[]
name|requiredPrimaryTypeNames
decl_stmt|;
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
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|build
parameter_list|()
block|{
comment|// do nothing by default
block|}
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
name|void
name|setDeclaringNodeType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// ignore
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
block|{
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
name|isAutoCreated
parameter_list|()
block|{
return|return
name|autocreate
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAutoCreated
parameter_list|(
name|boolean
name|autocreate
parameter_list|)
block|{
name|this
operator|.
name|autocreate
operator|=
name|autocreate
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
annotation|@
name|Override
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
annotation|@
name|Override
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
name|onParent
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOnParentVersion
parameter_list|(
name|int
name|onParent
parameter_list|)
block|{
name|this
operator|.
name|onParent
operator|=
name|onParent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowsSameNameSiblings
parameter_list|()
block|{
return|return
name|allowSns
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSameNameSiblings
parameter_list|(
name|boolean
name|allowSns
parameter_list|)
block|{
name|this
operator|.
name|allowSns
operator|=
name|allowSns
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAllowsSameNameSiblings
parameter_list|(
name|boolean
name|allowSns
parameter_list|)
block|{
name|setSameNameSiblings
argument_list|(
name|allowSns
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
name|getDefaultPrimaryType
parameter_list|()
block|{
if|if
condition|(
name|defaultPrimaryTypeName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|getNodeType
argument_list|(
name|defaultPrimaryTypeName
argument_list|)
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
literal|"Unable to access default primary type "
operator|+
name|defaultPrimaryTypeName
operator|+
literal|" of "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
name|defaultPrimaryTypeName
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultPrimaryTypeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|defaultPrimaryTypeName
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultPrimaryType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setDefaultPrimaryTypeName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getRequiredPrimaryTypes
parameter_list|()
block|{
if|if
condition|(
name|requiredPrimaryTypeNames
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|NodeType
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
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
name|requiredPrimaryTypeNames
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|requiredPrimaryTypeName
range|:
name|requiredPrimaryTypeNames
control|)
block|{
try|try
block|{
name|types
operator|.
name|add
argument_list|(
name|getNodeType
argument_list|(
name|requiredPrimaryTypeName
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
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to required primary primary type "
operator|+
name|requiredPrimaryTypeName
operator|+
literal|" of "
operator|+
name|name
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
name|requiredPrimaryTypeNames
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
name|names
parameter_list|)
block|{
name|this
operator|.
name|requiredPrimaryTypeNames
operator|=
name|names
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addRequiredPrimaryType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|requiredPrimaryTypeNames
operator|==
literal|null
condition|)
block|{
name|requiredPrimaryTypeNames
operator|=
operator|new
name|String
index|[]
block|{
name|name
block|}
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|requiredPrimaryTypeNames
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
name|requiredPrimaryTypeNames
argument_list|,
literal|0
argument_list|,
name|names
argument_list|,
literal|0
argument_list|,
name|requiredPrimaryTypeNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|names
index|[
name|requiredPrimaryTypeNames
operator|.
name|length
index|]
operator|=
name|name
expr_stmt|;
name|requiredPrimaryTypeNames
operator|=
name|names
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

