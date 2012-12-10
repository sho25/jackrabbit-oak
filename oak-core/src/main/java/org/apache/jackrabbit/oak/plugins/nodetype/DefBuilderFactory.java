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
name|Map
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
name|cnd
operator|.
name|DefinitionBuilderFactory
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
name|namepath
operator|.
name|IdentityNameMapper
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
name|name
operator|.
name|NamespaceConstants
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
name|name
operator|.
name|Namespaces
import|;
end_import

begin_class
class|class
name|DefBuilderFactory
extends|extends
name|DefinitionBuilderFactory
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
block|{
specifier|private
specifier|final
name|Tree
name|root
decl_stmt|;
specifier|public
name|DefBuilderFactory
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplateImpl
name|newNodeTypeDefinitionBuilder
parameter_list|()
block|{
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
operator|new
name|IdentityNameMapper
argument_list|(
name|root
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMapping
parameter_list|()
block|{
return|return
name|Namespaces
operator|.
name|getNamespaceMap
argument_list|(
name|root
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamespaceMapping
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
if|if
condition|(
name|Namespaces
operator|.
name|getNamespaceMap
argument_list|(
name|root
argument_list|)
operator|.
name|containsValue
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return;
comment|// namespace already exists
block|}
name|Tree
name|namespaces
init|=
name|getOrCreate
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|,
name|NamespaceConstants
operator|.
name|REP_NAMESPACES
argument_list|)
decl_stmt|;
name|namespaces
operator|.
name|setProperty
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Tree
name|getOrCreate
parameter_list|(
name|String
modifier|...
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|path
control|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|child
operator|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|tree
operator|=
name|child
expr_stmt|;
block|}
return|return
name|tree
return|;
block|}
block|}
end_class

end_unit

