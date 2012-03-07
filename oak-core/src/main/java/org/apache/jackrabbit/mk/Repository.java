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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|ChildNodeEntry
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
name|Commit
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
name|CommitBuilder
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
name|Node
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
name|StoredCommit
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
name|StoredNode
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
name|store
operator|.
name|DefaultRevisionStore
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
name|store
operator|.
name|NotFoundException
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
name|store
operator|.
name|RevisionStore
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
name|IOUtils
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
name|model
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|Repository
block|{
specifier|private
specifier|final
name|String
name|homeDir
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
specifier|private
name|RevisionStore
name|rs
decl_stmt|;
specifier|public
name|Repository
parameter_list|(
name|String
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|home
init|=
operator|new
name|File
argument_list|(
name|homeDir
operator|==
literal|null
condition|?
literal|"."
else|:
name|homeDir
argument_list|,
literal|".mk"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|home
operator|.
name|exists
argument_list|()
condition|)
block|{
name|home
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|homeDir
operator|=
name|home
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
comment|/**      * Alternate constructor, used for testing.      *       * @param rs revision store, already initialized      */
specifier|public
name|Repository
parameter_list|(
name|RevisionStore
name|rs
parameter_list|)
block|{
name|this
operator|.
name|homeDir
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|rs
operator|=
name|rs
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|initialized
condition|)
block|{
return|return;
block|}
name|DefaultRevisionStore
name|rs
init|=
operator|new
name|DefaultRevisionStore
argument_list|()
decl_stmt|;
name|rs
operator|.
name|initialize
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rs
operator|=
name|rs
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|rs
operator|instanceof
name|Closeable
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
operator|(
name|Closeable
operator|)
name|rs
argument_list|)
expr_stmt|;
block|}
name|initialized
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|RevisionStore
name|getRevisionStore
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
return|return
name|rs
return|;
block|}
specifier|public
name|String
name|getHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
return|return
name|rs
operator|.
name|getHeadCommitId
argument_list|()
return|;
block|}
specifier|public
name|StoredCommit
name|getHeadCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
return|return
name|rs
operator|.
name|getHeadCommit
argument_list|()
return|;
block|}
specifier|public
name|StoredCommit
name|getCommit
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
return|return
name|rs
operator|.
name|getCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|NodeState
name|getNodeState
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|rs
operator|.
name|getNodeState
argument_list|(
name|getNode
argument_list|(
name|revId
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
comment|/**      *      * @param revId      * @param path      * @return      * @throws NotFoundException if either path or revision doesn't exist      * @throws Exception if another error occurs      */
specifier|public
name|StoredNode
name|getNode
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
name|StoredNode
name|root
init|=
name|rs
operator|.
name|getRootNode
argument_list|(
name|revId
argument_list|)
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|root
return|;
block|}
comment|//return root.getNode(path.substring(1), pm);
name|String
index|[]
name|ids
init|=
name|resolvePath
argument_list|(
name|revId
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|rs
operator|.
name|getNode
argument_list|(
name|ids
index|[
name|ids
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|nodeExists
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal path"
argument_list|)
throw|;
block|}
try|try
block|{
name|String
index|[]
name|names
init|=
name|PathUtils
operator|.
name|split
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Node
name|parent
init|=
name|rs
operator|.
name|getRootNode
argument_list|(
name|revId
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ChildNodeEntry
name|cne
init|=
name|parent
operator|.
name|getChildNodeEntry
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cne
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|parent
operator|=
name|rs
operator|.
name|getNode
argument_list|(
name|cne
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|CommitBuilder
name|getCommitBuilder
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|CommitBuilder
argument_list|(
name|revId
argument_list|,
name|msg
argument_list|,
name|rs
argument_list|)
return|;
block|}
comment|/**      *      * @param revId      * @param nodePath      * @return      * @throws IllegalArgumentException if the specified path is not absolute      * @throws NotFoundException if either path or revision doesn't exist      * @throws Exception if another error occurs      */
name|String
index|[]
comment|/* array of node id's */
name|resolvePath
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|nodePath
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|nodePath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal path"
argument_list|)
throw|;
block|}
name|Commit
name|commit
init|=
name|rs
operator|.
name|getCommit
argument_list|(
name|revId
argument_list|)
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|nodePath
argument_list|)
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|commit
operator|.
name|getRootNodeId
argument_list|()
block|}
return|;
block|}
name|String
index|[]
name|names
init|=
name|PathUtils
operator|.
name|split
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|String
index|[]
name|ids
init|=
operator|new
name|String
index|[
name|names
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
comment|// get root node
name|ids
index|[
literal|0
index|]
operator|=
name|commit
operator|.
name|getRootNodeId
argument_list|()
expr_stmt|;
name|Node
name|parent
init|=
name|rs
operator|.
name|getNode
argument_list|(
name|ids
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// traverse path and remember id of each element
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ChildNodeEntry
name|cne
init|=
name|parent
operator|.
name|getChildNodeEntry
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cne
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|nodePath
argument_list|)
throw|;
block|}
name|ids
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|cne
operator|.
name|getId
argument_list|()
expr_stmt|;
name|parent
operator|=
name|rs
operator|.
name|getNode
argument_list|(
name|cne
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
block|}
end_class

end_unit

