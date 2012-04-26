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
name|PropertyDefinition
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
name|PropertyDefinitionImpl
extends|extends
name|ItemDefinitionImpl
implements|implements
name|PropertyDefinition
block|{
specifier|private
specifier|final
name|int
name|requiredType
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|multiple
decl_stmt|;
specifier|public
name|PropertyDefinitionImpl
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|NameMapper
name|mapper
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|autoCreated
parameter_list|,
name|boolean
name|mandatory
parameter_list|,
name|int
name|onParentRevision
parameter_list|,
name|boolean
name|isProtected
parameter_list|,
name|int
name|requiredType
parameter_list|,
name|boolean
name|multiple
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|mapper
argument_list|,
name|name
argument_list|,
name|autoCreated
argument_list|,
name|mandatory
argument_list|,
name|onParentRevision
argument_list|,
name|isProtected
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredType
operator|=
name|requiredType
expr_stmt|;
name|this
operator|.
name|multiple
operator|=
name|multiple
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRequiredType
parameter_list|()
block|{
return|return
name|requiredType
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getValueConstraints
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getDefaultValues
parameter_list|()
block|{
return|return
operator|new
name|Value
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
block|{
return|return
name|multiple
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAvailableQueryOperators
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFullTextSearchable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryOrderable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

