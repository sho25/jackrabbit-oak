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
name|security
operator|.
name|user
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
name|List
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
name|security
operator|.
name|user
operator|.
name|AuthorizableNodeName
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
name|whiteboard
operator|.
name|AbstractServiceTracker
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
comment|/**  * Dynamic {@link AuthorizableNodeName} based on the available  * whiteboard services.  */
end_comment

begin_class
specifier|public
class|class
name|WhiteboardAuthorizableNodeName
extends|extends
name|AbstractServiceTracker
argument_list|<
name|AuthorizableNodeName
argument_list|>
implements|implements
name|AuthorizableNodeName
block|{
specifier|public
name|WhiteboardAuthorizableNodeName
parameter_list|()
block|{
name|super
argument_list|(
name|AuthorizableNodeName
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|generateNodeName
parameter_list|(
annotation|@
name|NotNull
name|String
name|authorizableId
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizableNodeName
argument_list|>
name|services
init|=
name|getServices
argument_list|()
decl_stmt|;
if|if
condition|(
name|services
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|AuthorizableNodeName
operator|.
name|DEFAULT
operator|.
name|generateNodeName
argument_list|(
name|authorizableId
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|services
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|generateNodeName
argument_list|(
name|authorizableId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

