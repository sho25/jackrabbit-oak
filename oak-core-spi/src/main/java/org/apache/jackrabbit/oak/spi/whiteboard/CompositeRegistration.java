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
name|spi
operator|.
name|whiteboard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * A composite of registrations that unregisters all its constituents  * upon {@link #unregister()}.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeRegistration
implements|implements
name|Registration
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Registration
argument_list|>
name|registrations
decl_stmt|;
specifier|public
name|CompositeRegistration
parameter_list|(
name|Registration
modifier|...
name|registrations
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|registrations
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositeRegistration
parameter_list|(
name|List
argument_list|<
name|Registration
argument_list|>
name|registrations
parameter_list|)
block|{
name|this
operator|.
name|registrations
operator|=
name|registrations
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
for|for
control|(
name|Registration
name|reg
range|:
name|registrations
control|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
