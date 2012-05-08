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
name|api
operator|.
name|QueryEngine
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|LoginContext
import|;
end_import

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
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * {@link MicroKernel}-based implementation of the {@link ContentSession} interface.  */
end_comment

begin_class
class|class
name|ContentSessionImpl
implements|implements
name|ContentSession
block|{
comment|/** Logger instance */
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
name|QueryEngine
name|queryEngine
decl_stmt|;
specifier|public
name|ContentSessionImpl
parameter_list|(
name|LoginContext
name|loginContext
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|QueryEngine
name|queryEngine
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
name|queryEngine
operator|=
name|queryEngine
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
comment|// todo implement properly with extension point or pass it with the constructor...
name|Set
argument_list|<
name|SimpleCredentials
argument_list|>
name|creds
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
operator|.
name|getPublicCredentials
argument_list|(
name|SimpleCredentials
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SimpleCredentials
name|sc
init|=
operator|(
name|creds
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
operator|new
name|SimpleCredentials
argument_list|(
literal|null
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
else|:
name|creds
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|AuthInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|sc
operator|.
name|getUserID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
return|return
name|sc
operator|.
name|getAttributeNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|sc
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Root
name|getCurrentRoot
parameter_list|()
block|{
return|return
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
name|workspaceName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// todo implement close
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
annotation|@
name|Override
specifier|public
name|QueryEngine
name|getQueryEngine
parameter_list|()
block|{
return|return
name|queryEngine
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValueFactory
name|getCoreValueFactory
parameter_list|()
block|{
return|return
name|store
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

