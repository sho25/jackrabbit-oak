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
name|lifecycle
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
comment|/**  * Initializer of repository content. A component that needs to add specific  * content to a new repository can implement this interface. Then when a  * repository becomes available, all the configured initializers are invoked  * in sequence.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RepositoryInitializer
block|{
comment|/**      * Default implementation makes no changes to the repository.      */
name|RepositoryInitializer
name|DEFAULT
init|=
operator|new
name|RepositoryInitializer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{         }
block|}
decl_stmt|;
comment|/**      * Initializes repository content. This method is called as soon as a      * repository becomes available. Note that the repository may already      * have been initialized, so the implementation of this method should      * check for that before blindly adding new content.      *      * @param builder builder for accessing and modifying repository content      */
name|void
name|initialize
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

