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
name|PropertyType
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
name|nodetype
operator|.
name|PropertyDefinitionTemplate
import|;
end_import

begin_class
class|class
name|PropertyDefinitionTemplateImpl
extends|extends
name|ItemDefinitionTemplateImpl
implements|implements
name|PropertyDefinitionTemplate
block|{
specifier|private
name|boolean
name|isMultiple
init|=
literal|false
decl_stmt|;
specifier|private
name|Value
index|[]
name|defaultValues
init|=
literal|null
decl_stmt|;
specifier|private
name|String
index|[]
name|availableQueryOperators
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|int
name|requiredType
init|=
name|PropertyType
operator|.
name|STRING
decl_stmt|;
specifier|private
name|boolean
name|fullTextSearchable
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|queryOrderable
init|=
literal|true
decl_stmt|;
specifier|private
name|String
index|[]
name|valueConstraints
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
block|{
return|return
name|isMultiple
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMultiple
parameter_list|(
name|boolean
name|isMultiple
parameter_list|)
block|{
name|this
operator|.
name|isMultiple
operator|=
name|isMultiple
expr_stmt|;
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
name|valueConstraints
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValueConstraints
parameter_list|(
name|String
index|[]
name|constraints
parameter_list|)
block|{
name|this
operator|.
name|valueConstraints
operator|=
name|constraints
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryOrderable
parameter_list|()
block|{
return|return
name|queryOrderable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setQueryOrderable
parameter_list|(
name|boolean
name|queryOrderable
parameter_list|)
block|{
name|this
operator|.
name|queryOrderable
operator|=
name|queryOrderable
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFullTextSearchable
parameter_list|()
block|{
return|return
name|fullTextSearchable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setFullTextSearchable
parameter_list|(
name|boolean
name|fullTextSearchable
parameter_list|)
block|{
name|this
operator|.
name|fullTextSearchable
operator|=
name|fullTextSearchable
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
name|void
name|setRequiredType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|requiredType
operator|=
name|type
expr_stmt|;
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
name|defaultValues
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultValues
parameter_list|(
name|Value
index|[]
name|defaultValues
parameter_list|)
block|{
name|this
operator|.
name|defaultValues
operator|=
name|defaultValues
expr_stmt|;
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
name|availableQueryOperators
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAvailableQueryOperators
parameter_list|(
name|String
index|[]
name|operators
parameter_list|)
block|{
name|this
operator|.
name|availableQueryOperators
operator|=
name|operators
expr_stmt|;
block|}
block|}
end_class

end_unit

