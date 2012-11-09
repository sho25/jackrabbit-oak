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
name|security
operator|.
name|authentication
operator|.
name|callback
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
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
name|Root
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
name|core
operator|.
name|RootImpl
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
name|query
operator|.
name|QueryIndexProvider
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
name|AccessControlConfiguration
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
name|OpenAccessControlConfiguration
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
name|principal
operator|.
name|SystemPrincipal
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
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * Callback implementation used to access the repository. It allows to set and  * get the {@code NodeStore} and the name of the workspace for which the login  * applies. In addition it provides access to a {@link Root} object based on  * the given node store and workspace name.  */
end_comment

begin_class
specifier|public
class|class
name|RepositoryCallback
implements|implements
name|Callback
block|{
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
specifier|private
name|String
name|workspaceName
decl_stmt|;
specifier|public
name|String
name|getWorkspaceName
parameter_list|()
block|{
return|return
name|workspaceName
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|Root
name|getRoot
parameter_list|()
block|{
if|if
condition|(
name|nodeStore
operator|!=
literal|null
condition|)
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|AccessControlConfiguration
name|acConfiguration
init|=
operator|new
name|OpenAccessControlConfiguration
argument_list|()
decl_stmt|;
return|return
operator|new
name|RootImpl
argument_list|(
name|nodeStore
argument_list|,
name|workspaceName
argument_list|,
name|subject
argument_list|,
name|acConfiguration
argument_list|,
name|indexProvider
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setNodeStore
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
specifier|public
name|void
name|setIndexProvider
parameter_list|(
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
block|}
specifier|public
name|void
name|setWorkspaceName
parameter_list|(
name|String
name|workspaceName
parameter_list|)
block|{
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
block|}
block|}
end_class

end_unit

