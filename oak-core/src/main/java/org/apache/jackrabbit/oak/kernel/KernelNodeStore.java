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
name|kernel
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|CoreValueFactory
import|;
end_import

begin_comment
comment|/**  * {@link MicroKernel}-based {@link NodeStore} implementation.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStore
implements|implements
name|NodeStore
block|{
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|final
name|CoreValueFactory
name|valueFactory
decl_stmt|;
specifier|public
name|KernelNodeStore
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|valueFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|valueFactory
argument_list|,
literal|"/"
argument_list|,
name|kernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compare
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
block|}
end_class

end_unit

