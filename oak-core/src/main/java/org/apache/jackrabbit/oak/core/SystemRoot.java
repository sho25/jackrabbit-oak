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
name|oak
operator|.
name|Oak
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
name|AuthInfo
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
name|ContentSession
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
name|security
operator|.
name|authentication
operator|.
name|SystemSubject
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|EmptyHook
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
name|commit
operator|.
name|PostCommitHook
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
name|CompositeQueryIndexProvider
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
name|OpenSecurityProvider
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
name|SecurityProvider
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
name|authentication
operator|.
name|AuthInfoImpl
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
comment|/**  *  Internal extension of the {@link RootImpl} to be used  *  when an usage of the system internal subject is needed.  */
end_comment

begin_class
specifier|public
class|class
name|SystemRoot
extends|extends
name|RootImpl
block|{
specifier|private
specifier|final
name|ContentSession
name|contentSession
init|=
operator|new
name|ContentSession
argument_list|()
block|{
specifier|private
specifier|final
name|AuthInfoImpl
name|authInfo
init|=
operator|new
name|AuthInfoImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|SystemSubject
operator|.
name|INSTANCE
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|getWorkspaceName
parameter_list|()
block|{
return|return
name|SystemRoot
operator|.
name|this
operator|.
name|getWorkspaceName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Root
name|getLatestRoot
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
return|return
name|authInfo
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|SystemRoot
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|CommitHook
name|hook
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|SecurityProvider
name|securityProvider
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|hook
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|,
name|workspaceName
argument_list|,
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
name|securityProvider
argument_list|,
name|indexProvider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SystemRoot
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SystemRoot
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|CommitHook
name|hook
parameter_list|)
block|{
comment|// FIXME: define proper default or pass workspace name with the
comment|// constructor
name|this
argument_list|(
name|store
argument_list|,
name|hook
argument_list|,
name|Oak
operator|.
name|DEFAULT_WORKSPACE_NAME
argument_list|,
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|,
operator|new
name|CompositeQueryIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
return|return
name|contentSession
return|;
block|}
block|}
end_class

end_unit

