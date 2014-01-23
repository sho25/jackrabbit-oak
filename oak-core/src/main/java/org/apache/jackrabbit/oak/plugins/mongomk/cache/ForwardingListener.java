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
name|mongomk
operator|.
name|cache
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
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalNotification
import|;
end_import

begin_comment
comment|/**  * Listener which forwards the notifications to a delegate. It is used to bridge  * multiple instances.  *  */
end_comment

begin_class
specifier|public
class|class
name|ForwardingListener
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
specifier|private
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
decl_stmt|;
specifier|public
name|ForwardingListener
parameter_list|()
block|{     }
specifier|public
name|ForwardingListener
parameter_list|(
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|notification
parameter_list|)
block|{
if|if
condition|(
name|delegate
operator|!=
literal|null
condition|)
block|{
name|delegate
operator|.
name|onRemoval
argument_list|(
name|notification
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setDelegate
parameter_list|(
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|ForwardingListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ForwardingListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|ForwardingListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newInstance
parameter_list|(
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
parameter_list|)
block|{
return|return
operator|new
name|ForwardingListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|delegate
argument_list|)
return|;
block|}
block|}
end_class

end_unit

