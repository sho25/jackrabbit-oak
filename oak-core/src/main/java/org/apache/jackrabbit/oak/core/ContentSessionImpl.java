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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|login
operator|.
name|LoginException
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
name|Blob
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
name|spi
operator|.
name|commit
operator|.
name|ConflictHandlerProvider
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
name|authentication
operator|.
name|LoginContext
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
name|AccessControlProvider
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
comment|/**  * {@code MicroKernel}-based implementation of the {@link ContentSession} interface.  */
end_comment

begin_class
class|class
name|ContentSessionImpl
implements|implements
name|ContentSession
block|{
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
name|ContentSessionImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|LoginContext
name|loginContext
decl_stmt|;
specifier|private
specifier|final
name|AccessControlProvider
name|accProvider
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|ConflictHandlerProvider
name|conflictHandlerProvider
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
specifier|public
name|ContentSessionImpl
parameter_list|(
name|LoginContext
name|loginContext
parameter_list|,
name|AccessControlProvider
name|accProvider
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|ConflictHandlerProvider
name|conflictHandlerProvider
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|this
operator|.
name|loginContext
operator|=
name|loginContext
expr_stmt|;
name|this
operator|.
name|accProvider
operator|=
name|accProvider
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|conflictHandlerProvider
operator|=
name|conflictHandlerProvider
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
name|Set
argument_list|<
name|AuthInfo
argument_list|>
name|infoSet
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
operator|.
name|getPublicCredentials
argument_list|(
name|AuthInfo
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|AuthInfo
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
name|infoSet
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Root
name|getLatestRoot
parameter_list|()
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
name|workspaceName
argument_list|,
name|loginContext
operator|.
name|getSubject
argument_list|()
argument_list|,
name|accProvider
argument_list|,
name|indexProvider
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflictHandlerProvider
operator|!=
literal|null
condition|)
block|{
name|root
operator|.
name|setConflictHandler
argument_list|(
name|conflictHandlerProvider
operator|.
name|getConflictHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|loginContext
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during logout."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getWorkspaceName
parameter_list|()
block|{
return|return
name|workspaceName
return|;
block|}
block|}
end_class

end_unit

