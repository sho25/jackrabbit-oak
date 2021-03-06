begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|observation
operator|.
name|filter
package|;
end_package

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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|ConstantFilter
implements|implements
name|EventFilter
block|{
specifier|public
specifier|static
specifier|final
name|ConstantFilter
name|INCLUDE_ALL
init|=
operator|new
name|ConstantFilter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ConstantFilter
name|EXCLUDE_ALL
init|=
operator|new
name|ConstantFilter
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|include
decl_stmt|;
specifier|public
name|ConstantFilter
parameter_list|(
name|boolean
name|include
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|include
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeChange
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|moved
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeReorder
parameter_list|(
name|String
name|destName
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|reordered
parameter_list|)
block|{
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|EventFilter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|include
condition|?
name|this
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

