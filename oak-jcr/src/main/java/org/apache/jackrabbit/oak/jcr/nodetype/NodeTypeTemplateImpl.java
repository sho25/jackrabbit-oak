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

begin_class
class|class
name|NodeTypeTemplateImpl
implements|implements
name|NodeTypeTemplate
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isAbstract
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isMixin
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isOrderable
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isQueryable
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|primaryItemName
init|=
literal|null
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
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinitionTemplate
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|NodeDefinitionTemplate
argument_list|>
name|nodeDefinitionTemplates
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDefinitionTemplate
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeTypeTemplateImpl
parameter_list|()
block|{     }
specifier|public
name|NodeTypeTemplateImpl
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|ntd
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|isAbstract
operator|=
name|ntd
operator|.
name|isAbstract
argument_list|()
expr_stmt|;
name|this
operator|.
name|isMixin
operator|=
name|ntd
operator|.
name|isMixin
argument_list|()
expr_stmt|;
name|this
operator|.
name|isOrderable
operator|=
name|ntd
operator|.
name|hasOrderableChildNodes
argument_list|()
expr_stmt|;
name|this
operator|.
name|isQueryable
operator|=
name|ntd
operator|.
name|isQueryable
argument_list|()
expr_stmt|;
name|this
operator|.
name|primaryItemName
operator|=
name|ntd
operator|.
name|getPrimaryItemName
argument_list|()
expr_stmt|;
name|this
operator|.
name|superTypeNames
operator|=
name|ntd
operator|.
name|getDeclaredSupertypeNames
argument_list|()
expr_stmt|;
comment|// TODO: child item templates?
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
name|isQueryable
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
name|isQueryable
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
block|{
name|this
operator|.
name|primaryItemName
operator|=
name|name
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
block|{
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
name|List
argument_list|<
name|PropertyDefinitionTemplate
argument_list|>
name|getPropertyDefinitionTemplates
parameter_list|()
block|{
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
literal|null
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
literal|null
return|;
block|}
block|}
end_class

end_unit

