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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|PropertyState
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
name|memory
operator|.
name|PropertyValues
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

begin_comment
comment|/**  * {@code RestrictionImpl}  */
end_comment

begin_class
specifier|public
class|class
name|RestrictionImpl
implements|implements
name|Restriction
block|{
specifier|private
specifier|final
name|RestrictionDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|property
decl_stmt|;
specifier|private
name|int
name|hashCode
init|=
literal|0
decl_stmt|;
specifier|public
name|RestrictionImpl
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
annotation|@
name|NotNull
name|RestrictionDefinition
name|def
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|def
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
specifier|public
name|RestrictionImpl
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
name|boolean
name|isMandatory
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
operator|.
name|getType
argument_list|()
argument_list|,
name|isMandatory
argument_list|)
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
comment|//--------------------------------------------------------< Restriction>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionDefinition
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
name|property
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
name|hashCode
operator|=
name|Objects
operator|.
name|hashCode
argument_list|(
name|definition
argument_list|,
name|property
argument_list|,
name|PropertyValues
operator|.
name|create
argument_list|(
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|RestrictionImpl
condition|)
block|{
name|RestrictionImpl
name|other
init|=
operator|(
name|RestrictionImpl
operator|)
name|o
decl_stmt|;
return|return
name|definition
operator|.
name|equals
argument_list|(
name|other
operator|.
name|definition
argument_list|)
operator|&&
name|property
operator|.
name|equals
argument_list|(
name|other
operator|.
name|property
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

