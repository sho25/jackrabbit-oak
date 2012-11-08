begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|testing
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|util
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Represents a collection of microkernels.  *   *   *   */
end_comment

begin_class
specifier|public
class|class
name|MicroKernelCollection
block|{
name|ArrayList
argument_list|<
name|MicroKernel
argument_list|>
name|mks
decl_stmt|;
comment|/**      * Initialize a collection of microkernels.All microkernels have the same      * configuration.      *       * @param initializator      *            The initialization class of a particular microkernel type.      * @param conf      *            The microkernel configuration data.      * @throws Exception      */
specifier|public
name|MicroKernelCollection
parameter_list|(
name|MicroKernelInitializer
name|initializator
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|mks
operator|=
name|initializator
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a microkernel collection.      *       * @return An array of initialized microkernels.      */
specifier|public
name|ArrayList
argument_list|<
name|MicroKernel
argument_list|>
name|getMicroKernels
parameter_list|()
block|{
return|return
name|mks
return|;
block|}
block|}
end_class

end_unit

