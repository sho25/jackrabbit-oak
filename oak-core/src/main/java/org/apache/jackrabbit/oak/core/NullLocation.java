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
name|core
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
name|api
operator|.
name|Tree
operator|.
name|Status
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
name|TreeLocation
import|;
end_import

begin_comment
comment|/**  * This {@code TreeLocation} refers to an invalid location in a tree. That is  * to a location where no item resides.  */
end_comment

begin_class
specifier|final
class|class
name|NullLocation
implements|implements
name|TreeLocation
block|{
specifier|static
specifier|final
name|TreeLocation
name|NULL
init|=
operator|new
name|NullLocation
argument_list|()
decl_stmt|;
specifier|private
name|NullLocation
parameter_list|()
block|{     }
comment|/**      * @return  {@code NULL}      */
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|NULL
return|;
block|}
comment|/**      * @return  {@code NULL}      */
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
block|{
return|return
name|NULL
return|;
block|}
comment|/**      * @return {@code false}      */
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return Always {@code false}.      */
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return {@code false}      */
annotation|@
name|Override
specifier|public
name|boolean
name|set
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

