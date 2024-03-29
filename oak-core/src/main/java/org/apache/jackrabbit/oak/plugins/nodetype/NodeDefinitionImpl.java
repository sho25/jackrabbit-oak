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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|JCR_NODE_TYPES
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
name|NamePathMapper
import|;
end_import

begin_comment
comment|/**  *<pre>  * [nt:childNodeDefinition]  *   ...  * - jcr:requiredPrimaryTypes (NAME) = 'nt:base' protected mandatory multiple  * - jcr:defaultPrimaryType (NAME) protected  * - jcr:sameNameSiblings (BOOLEAN) protected mandatory  *</pre>  */
end_comment

begin_class
class|class
name|NodeDefinitionImpl
extends|extends
name|ItemDefinitionImpl
implements|implements
name|NodeDefinition
block|{
name|NodeDefinitionImpl
parameter_list|(
name|Tree
name|definition
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|NamePathMapper
name|mapper
parameter_list|)
block|{
name|super
argument_list|(
name|definition
argument_list|,
name|type
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------< NodeDefinition>---
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getRequiredPrimaryTypeNames
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
name|getNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_REQUIREDPRIMARYTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
name|names
operator|=
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|NT_BASE
block|}
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|names
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|getJcrName
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getRequiredPrimaryTypes
parameter_list|()
block|{
name|String
index|[]
name|oakNames
init|=
name|getNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_REQUIREDPRIMARYTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakNames
operator|==
literal|null
condition|)
block|{
name|oakNames
operator|=
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|NT_BASE
block|}
expr_stmt|;
block|}
name|NodeType
index|[]
name|types
init|=
operator|new
name|NodeType
index|[
name|oakNames
operator|.
name|length
index|]
decl_stmt|;
name|Tree
name|root
init|=
name|definition
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|root
operator|=
name|root
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|oakNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Tree
name|type
init|=
name|root
operator|.
name|getChild
argument_list|(
name|oakNames
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|type
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|types
index|[
name|i
index|]
operator|=
operator|new
name|NodeTypeImpl
argument_list|(
name|type
argument_list|,
name|mapper
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
name|String
name|getDefaultPrimaryTypeName
parameter_list|()
block|{
name|String
name|oakName
init|=
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_DEFAULTPRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|!=
literal|null
condition|)
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|oakName
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
name|NodeType
name|getDefaultPrimaryType
parameter_list|()
block|{
name|String
name|oakName
init|=
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_DEFAULTPRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|!=
literal|null
condition|)
block|{
name|Tree
name|types
init|=
name|definition
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|types
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|types
operator|=
name|types
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
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
name|checkState
argument_list|(
name|type
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeTypeImpl
argument_list|(
name|type
argument_list|,
name|mapper
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
name|boolean
name|allowsSameNameSiblings
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_SAMENAMESIBLINGS
argument_list|)
return|;
block|}
block|}
end_class

end_unit

