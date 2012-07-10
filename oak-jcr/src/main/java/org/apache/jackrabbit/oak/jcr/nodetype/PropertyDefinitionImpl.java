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
name|List
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
name|ValueFormatException
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
name|jcr
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|PropertyDefinitionImpl
extends|extends
name|ItemDefinitionImpl
implements|implements
name|PropertyDefinition
block|{
specifier|private
specifier|final
name|PropertyDefinitionDelegate
name|dlg
decl_stmt|;
specifier|private
specifier|final
name|ValueFactoryImpl
name|vfac
decl_stmt|;
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
name|PropertyDefinitionImpl
operator|.
name|class
argument_list|)
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
name|ValueFactoryImpl
name|vfac
parameter_list|,
name|PropertyDefinitionDelegate
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|mapper
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|vfac
operator|=
name|vfac
expr_stmt|;
name|this
operator|.
name|dlg
operator|=
name|delegate
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
name|dlg
operator|.
name|getRequiredType
argument_list|()
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
name|List
argument_list|<
name|String
argument_list|>
name|defaults
init|=
name|dlg
operator|.
name|getDefaultValues
argument_list|()
decl_stmt|;
name|Value
index|[]
name|result
init|=
operator|new
name|Value
index|[
name|defaults
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|defaults
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|result
index|[
name|i
index|]
operator|=
name|vfac
operator|.
name|createValue
argument_list|(
name|defaults
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|dlg
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValueFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Converting value "
operator|+
name|defaults
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|return
name|result
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
name|dlg
operator|.
name|isMultiple
argument_list|()
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

