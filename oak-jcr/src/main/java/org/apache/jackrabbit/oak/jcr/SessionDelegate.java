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
name|jcr
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
name|CoreValue
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
name|Result
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
name|ResultRow
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
name|api
operator|.
name|Tree
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
name|commons
operator|.
name|PathUtils
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
name|jcr
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|namepath
operator|.
name|AbstractNameMapper
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
name|namepath
operator|.
name|NameMapper
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
name|namepath
operator|.
name|NamePathMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|annotation
operator|.
name|CheckForNull
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
name|jcr
operator|.
name|ItemExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|text
operator|.
name|ParseException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|SessionDelegate
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SessionDelegate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|nameMapper
init|=
operator|new
name|SessionNameMapper
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|nameMapper
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|ValueFactoryImpl
name|valueFactory
decl_stmt|;
specifier|private
specifier|final
name|Workspace
name|workspace
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
name|boolean
name|isAlive
init|=
literal|true
decl_stmt|;
name|SessionDelegate
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|)
throws|throws
name|RepositoryException
block|{
assert|assert
name|repository
operator|!=
literal|null
assert|;
assert|assert
name|contentSession
operator|!=
literal|null
assert|;
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|contentSession
operator|.
name|getCoreValueFactory
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|workspace
operator|=
operator|new
name|WorkspaceImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|=
operator|new
name|SessionImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|isAlive
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
return|return
name|contentSession
operator|.
name|getAuthInfo
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
specifier|public
name|void
name|logout
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isAlive
condition|)
block|{
comment|// ignore
return|return;
block|}
name|isAlive
operator|=
literal|false
expr_stmt|;
comment|// TODO
try|try
block|{
name|contentSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error while closing connection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeDelegate
name|getRoot
parameter_list|()
block|{
name|Tree
name|root
init|=
name|getTree
argument_list|(
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No root node"
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeDelegate
argument_list|(
name|this
argument_list|,
name|root
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|this
argument_list|,
name|tree
argument_list|)
return|;
block|}
specifier|public
name|NodeDelegate
name|getNode
parameter_list|(
name|Node
name|jcrNode
parameter_list|)
block|{
if|if
condition|(
name|jcrNode
operator|instanceof
name|NodeImpl
condition|)
block|{
return|return
operator|(
operator|(
name|NodeImpl
operator|)
name|jcrNode
operator|)
operator|.
name|getNodeDelegate
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NodeImpl expected"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getNodeByIdentifier
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|this
argument_list|,
name|tree
argument_list|)
return|;
block|}
else|else
block|{
comment|// referenceable
return|return
name|findByJcrUuid
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|ValueFactoryImpl
name|getValueFactory
parameter_list|()
block|{
return|return
name|valueFactory
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
return|return
name|root
operator|.
name|hasPendingChanges
argument_list|()
return|;
block|}
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|refresh
parameter_list|(
name|boolean
name|keepChanges
parameter_list|)
block|{
if|if
condition|(
name|keepChanges
condition|)
block|{
name|root
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Shortcut for {@code SessionDelegate.getNamePathMapper().getOakPath(jcrPath)}.      *      * @param jcrPath JCR path      * @return Oak path, or {@code null}      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPathOrNull
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link PathNotFoundException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws PathNotFoundException if the path can not be mapped      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrNull
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link RepositoryException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws RepositoryException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrow
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrNull
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name or path: "
operator|+
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|//----------------------------------------------------------< Workspace>---
annotation|@
name|Nonnull
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|workspace
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getWorkspaceName
parameter_list|()
block|{
return|return
name|contentSession
operator|.
name|getWorkspaceName
argument_list|()
return|;
block|}
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|srcPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|srcAbsPath
argument_list|)
decl_stmt|;
name|String
name|destPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|destAbsPath
argument_list|)
decl_stmt|;
comment|// check destination
name|Tree
name|dest
init|=
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemExistsException
argument_list|(
name|destAbsPath
argument_list|)
throw|;
block|}
comment|// check parent of destination
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|Tree
name|destParent
init|=
name|getTree
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destAbsPath
argument_list|)
argument_list|)
throw|;
block|}
comment|// check source exists
name|Tree
name|src
init|=
name|getTree
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|srcAbsPath
argument_list|)
throw|;
block|}
try|try
block|{
name|Root
name|currentRoot
init|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|currentRoot
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|currentRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|move
parameter_list|(
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|,
name|boolean
name|transientOp
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|srcPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|srcAbsPath
argument_list|)
decl_stmt|;
name|String
name|destPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|destAbsPath
argument_list|)
decl_stmt|;
name|Root
name|moveRoot
init|=
name|transientOp
condition|?
name|root
else|:
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
comment|// check destination
name|Tree
name|dest
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemExistsException
argument_list|(
name|destAbsPath
argument_list|)
throw|;
block|}
comment|// check parent of destination
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|Tree
name|destParent
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destAbsPath
argument_list|)
argument_list|)
throw|;
block|}
comment|// check source exists
name|Tree
name|src
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|srcAbsPath
argument_list|)
throw|;
block|}
try|try
block|{
name|moveRoot
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|transientOp
condition|)
block|{
name|moveRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|LockManager
name|getLockManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getLockManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|QueryEngine
name|getQueryEngine
parameter_list|()
block|{
return|return
name|contentSession
operator|.
name|getQueryEngine
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|QueryManager
name|getQueryManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getQueryManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getNodeTypeManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
return|return
name|contentSession
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
annotation|@
name|CheckForNull
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
name|NodeDelegate
name|findByJcrUuid
parameter_list|(
name|String
name|id
parameter_list|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
name|bindings
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"id"
argument_list|,
name|getValueFactory
argument_list|()
operator|.
name|getCoreValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM [nt:base] WHERE [jcr:uuid] = $id"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|getContentSession
argument_list|()
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|bindings
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ResultRow
name|rr
range|:
name|result
operator|.
name|getRows
argument_list|()
control|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"multiple results for identifier lookup: "
operator|+
name|path
operator|+
literal|" vs. "
operator|+
name|rr
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
name|path
operator|=
name|rr
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|path
operator|==
literal|null
condition|?
literal|null
else|:
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|//--------------------------------------------------< SessionNameMapper>---
specifier|private
class|class
name|SessionNameMapper
extends|extends
name|AbstractNameMapper
block|{
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getJcrPrefix
parameter_list|(
name|String
name|oakPrefix
parameter_list|)
block|{
try|try
block|{
name|String
name|ns
init|=
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getURI
argument_list|(
name|oakPrefix
argument_list|)
decl_stmt|;
return|return
name|session
operator|.
name|getNamespacePrefix
argument_list|(
name|ns
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not get JCR prefix for OAK prefix "
operator|+
name|oakPrefix
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakPrefix
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
block|{
try|try
block|{
name|String
name|ns
init|=
name|getSession
argument_list|()
operator|.
name|getNamespaceURI
argument_list|(
name|jcrPrefix
argument_list|)
decl_stmt|;
return|return
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getPrefix
argument_list|(
name|ns
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not get OAK prefix for JCR prefix "
operator|+
name|jcrPrefix
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakPrefixFromURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
try|try
block|{
return|return
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getPrefix
argument_list|(
name|uri
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not get OAK prefix for URI "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
comment|// TODO OAK-61
comment|// TODO right now we would have to check whether AbstractSession.namespaces is empty
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

