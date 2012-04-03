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
name|model
operator|.
name|NodeState
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
name|model
operator|.
name|NodeStateEditor
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
name|model
operator|.
name|NodeStore
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
name|CommitFailedException
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
name|Connection
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
name|query
operator|.
name|QueryEngineImpl
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
name|NoSuchWorkspaceException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * ConnectionImpl...  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionImpl
implements|implements
name|Connection
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
name|ConnectionImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SimpleCredentials
name|credentials
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
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|public
name|ConnectionImpl
parameter_list|(
name|SimpleCredentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|QueryEngine
name|queryEngine
parameter_list|)
block|{
name|this
operator|.
name|credentials
operator|=
name|credentials
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
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|queryEngine
operator|=
name|queryEngine
expr_stmt|;
block|}
specifier|static
name|Connection
name|createWorkspaceConnection
parameter_list|(
name|SimpleCredentials
name|credentials
parameter_list|,
name|String
name|workspace
parameter_list|,
name|MicroKernel
name|microKernel
parameter_list|,
name|String
name|revision
parameter_list|)
throws|throws
name|NoSuchWorkspaceException
block|{
name|NodeStore
name|store
init|=
operator|new
name|KernelNodeStore
argument_list|(
name|microKernel
argument_list|)
decl_stmt|;
comment|// TODO: pass revision?
name|NodeState
name|wspRoot
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|workspace
argument_list|)
decl_stmt|;
if|if
condition|(
name|wspRoot
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchWorkspaceException
argument_list|(
name|workspace
argument_list|)
throw|;
block|}
name|QueryEngine
name|queryEngine
init|=
operator|new
name|QueryEngineImpl
argument_list|(
name|microKernel
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConnectionImpl
argument_list|(
name|credentials
argument_list|,
name|workspace
argument_list|,
name|store
argument_list|,
name|wspRoot
argument_list|,
name|queryEngine
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
comment|// todo implement getAuthInfo
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
name|credentials
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
name|credentials
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
name|credentials
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
name|NodeState
name|getCurrentRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|commit
parameter_list|(
name|NodeStateEditor
name|editor
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|workspaceName
operator|==
literal|null
condition|)
block|{
return|return
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|workspaceName
argument_list|)
return|;
block|}
comment|// todo return store.merge(editor, editor.getBaseNodeState());
block|}
annotation|@
name|Override
specifier|public
name|NodeStateEditor
name|getNodeStateEditor
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
return|return
name|store
operator|.
name|branch
argument_list|(
name|state
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
name|Connection
name|getRepositoryConnection
parameter_list|()
block|{
return|return
operator|new
name|ConnectionImpl
argument_list|(
name|credentials
argument_list|,
literal|null
argument_list|,
name|store
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|queryEngine
argument_list|)
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
block|}
end_class

end_unit

