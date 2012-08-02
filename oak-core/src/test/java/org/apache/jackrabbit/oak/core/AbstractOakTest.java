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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|kernel
operator|.
name|KernelNodeStore
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
name|authorization
operator|.
name|AccessControlContext
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * AbstractOakTest...  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractOakTest
block|{
comment|/**      * logger instance      */
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
name|AbstractOakTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: use regular oak-repo setup
specifier|protected
name|KernelNodeStore
name|store
decl_stmt|;
specifier|protected
name|CoreValueFactory
name|valueFactory
decl_stmt|;
specifier|protected
name|AccessControlContext
name|acContext
decl_stmt|;
specifier|protected
name|NodeState
name|state
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|MicroKernel
name|microKernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|store
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
name|valueFactory
operator|=
name|store
operator|.
name|getValueFactory
argument_list|()
expr_stmt|;
name|acContext
operator|=
operator|new
name|TestAcContext
argument_list|()
expr_stmt|;
name|state
operator|=
name|createInitialState
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|NodeState
name|createInitialState
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|)
function_decl|;
specifier|protected
name|RootImpl
name|createRootImpl
parameter_list|(
name|String
name|workspaceName
parameter_list|)
block|{
return|return
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
name|workspaceName
argument_list|,
name|acContext
argument_list|)
return|;
block|}
block|}
end_class

end_unit

