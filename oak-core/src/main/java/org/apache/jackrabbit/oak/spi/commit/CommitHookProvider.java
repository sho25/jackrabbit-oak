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
name|commit
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * {@code CommitHookProvider} TODO  *  * FIXME: needs re-evaluation and review once we add support for multiple workspaces (OAK-118)  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommitHookProvider
block|{
comment|/**      * Create a new {@code CommitHook} to deal with modifications on the      * workspace with the specified {@code workspaceName}.      *      * @param workspaceName The name of the workspace.      * @return A CommitHook instance.      */
annotation|@
name|Nonnull
name|CommitHook
name|getCommitHook
parameter_list|(
name|String
name|workspaceName
parameter_list|)
function_decl|;
comment|/**      * Default implementation that returns an {@code EmptyHook}.      */
specifier|final
class|class
name|Empty
implements|implements
name|CommitHookProvider
block|{
annotation|@
name|Override
specifier|public
name|CommitHook
name|getCommitHook
parameter_list|(
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|EmptyHook
operator|.
name|INSTANCE
return|;
block|}
block|}
block|}
end_interface

end_unit

