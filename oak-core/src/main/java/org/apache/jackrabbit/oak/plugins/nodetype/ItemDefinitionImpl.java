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
name|checkNotNull
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
comment|/**  *<pre>  * [nt:{propertyDefinition,childNodeDefinition}]  * - jcr:name (NAME) protected   * - jcr:autoCreated (BOOLEAN) protected mandatory  * - jcr:mandatory (BOOLEAN) protected mandatory  * - jcr:onParentVersion (STRING) protected mandatory  *< 'COPY', 'VERSION', 'INITIALIZE', 'COMPUTE', 'IGNORE', 'ABORT'  * - jcr:protected (BOOLEAN) protected mandatory  *   ...  *</pre>  */
end_comment

begin_class
class|class
name|ItemDefinitionImpl
extends|extends
name|AbstractTypeDefinition
implements|implements
name|ItemDefinition
block|{
specifier|private
specifier|final
name|NodeType
name|type
decl_stmt|;
specifier|protected
name|ItemDefinitionImpl
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
name|mapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|String
name|getOakName
parameter_list|()
block|{
name|String
name|oakName
init|=
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
condition|)
block|{
name|oakName
operator|=
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
expr_stmt|;
block|}
return|return
name|oakName
return|;
block|}
comment|//-----------------------------------------------------< ItemDefinition>---
annotation|@
name|Override
specifier|public
name|NodeType
name|getDeclaringNodeType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|String
name|oakName
init|=
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_NAME
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
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAutoCreated
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_AUTOCREATED
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMandatory
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_MANDATORY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOnParentVersion
parameter_list|()
block|{
name|String
name|action
init|=
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_ONPARENTVERSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
return|return
name|OnParentVersionAction
operator|.
name|valueFromName
argument_list|(
name|action
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|OnParentVersionAction
operator|.
name|COPY
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isProtected
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_PROTECTED
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

