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
name|store
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
name|blobs
operator|.
name|BlobStore
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
name|blobs
operator|.
name|FileBlobStore
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
name|ChildNodeEntriesMap
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
name|Id
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
name|MutableCommit
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
name|MutableNode
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
name|persistence
operator|.
name|H2Persistence
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
name|persistence
operator|.
name|Persistence
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
name|SimpleLRUCache
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
name|StringUtils
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Default revision store implementation, passing calls to a<code>Persistence</code>  * and a<code>BlobStore</code>, respectively and providing caching.   */
end_comment

begin_class
specifier|public
class|class
name|DefaultRevisionStore
implements|implements
name|RevisionStore
implements|,
name|Closeable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SIZE
init|=
literal|"mk.cacheSize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE_SIZE
init|=
literal|10000
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
specifier|private
name|String
name|headId
decl_stmt|;
specifier|private
name|long
name|headCounter
decl_stmt|;
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|headLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
name|Persistence
name|pm
decl_stmt|;
specifier|private
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
name|boolean
name|blobStoreNeedsClose
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Id
argument_list|,
name|Object
argument_list|>
name|cache
decl_stmt|;
specifier|public
name|void
name|initialize
parameter_list|(
name|File
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already initialized"
argument_list|)
throw|;
block|}
name|cache
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|SimpleLRUCache
operator|.
expr|<
name|Id
argument_list|,
name|Object
operator|>
name|newInstance
argument_list|(
name|determineInitialCacheSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|=
operator|new
name|H2Persistence
argument_list|()
expr_stmt|;
comment|//pm = new InMemPersistence();
comment|//pm = new MongoPersistence();
comment|//pm = new BDbPersistence();
comment|//pm = new FSPersistence();
name|pm
operator|.
name|initialize
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|pm
operator|instanceof
name|BlobStore
condition|)
block|{
name|blobStore
operator|=
operator|(
name|BlobStore
operator|)
name|pm
expr_stmt|;
block|}
else|else
block|{
name|blobStore
operator|=
operator|new
name|FileBlobStore
argument_list|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"blobs"
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|blobStoreNeedsClose
operator|=
literal|true
expr_stmt|;
block|}
comment|// make sure we've got a HEAD commit
name|headId
operator|=
name|pm
operator|.
name|readHead
argument_list|()
expr_stmt|;
if|if
condition|(
name|headId
operator|==
literal|null
operator|||
name|headId
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// assume virgin repository
name|byte
index|[]
name|rawHeadId
init|=
name|longToBytes
argument_list|(
operator|++
name|headCounter
argument_list|)
decl_stmt|;
name|headId
operator|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|rawHeadId
argument_list|)
expr_stmt|;
name|Id
name|rootNodeId
init|=
name|pm
operator|.
name|writeNode
argument_list|(
operator|new
name|MutableNode
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|MutableCommit
name|initialCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|initialCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|initialCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeCommit
argument_list|(
name|rawHeadId
argument_list|,
name|initialCommit
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeHead
argument_list|(
name|headId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|headCounter
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|headId
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|blobStoreNeedsClose
condition|)
block|{
name|blobStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|pm
operator|.
name|close
argument_list|()
expr_stmt|;
name|initialized
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|void
name|verifyInitialized
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
block|}
specifier|protected
name|int
name|determineInitialCacheSize
parameter_list|()
block|{
name|String
name|val
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|CACHE_SIZE
argument_list|)
decl_stmt|;
return|return
operator|(
name|val
operator|!=
literal|null
operator|)
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
else|:
name|DEFAULT_CACHE_SIZE
return|;
block|}
comment|/**      * Convert a long value into a fixed-size byte array of size 8.      *       * @param value value      * @return byte array      */
specifier|private
specifier|static
name|byte
index|[]
name|longToBytes
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|result
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
operator|&&
name|value
operator|!=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|value
operator|>>>=
literal|8
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|//--------------------------------------------------------< RevisionStore>
specifier|public
name|Id
name|putNode
parameter_list|(
name|MutableNode
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|node
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|Id
name|id
init|=
name|pm
operator|.
name|writeNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
name|node
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|public
name|Id
name|putCNEMap
parameter_list|(
name|ChildNodeEntriesMap
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|map
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|Id
name|id
init|=
name|pm
operator|.
name|writeCNEMap
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|public
name|String
name|putCommit
parameter_list|(
name|MutableCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commit
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|commit
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|String
name|id
init|=
name|commit
operator|.
name|getId
argument_list|()
decl_stmt|;
name|byte
index|[]
name|rawId
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|rawId
operator|=
name|longToBytes
argument_list|(
operator|++
name|headCounter
argument_list|)
expr_stmt|;
name|id
operator|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|rawId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rawId
operator|=
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|writeCommit
argument_list|(
name|rawId
argument_list|,
name|commit
argument_list|)
expr_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// TODO fixme, String -> Id
name|cache
operator|.
name|put
argument_list|(
name|Id
operator|.
name|fromString
argument_list|(
name|id
argument_list|)
argument_list|,
operator|new
name|StoredCommit
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setHeadCommitId
parameter_list|(
name|String
name|commitId
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|pm
operator|.
name|writeHead
argument_list|(
name|commitId
argument_list|)
expr_stmt|;
name|headId
operator|=
name|commitId
expr_stmt|;
name|long
name|headCounter
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|headId
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|headCounter
operator|>
name|this
operator|.
name|headCounter
condition|)
block|{
name|this
operator|.
name|headCounter
operator|=
name|headCounter
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|lockHead
parameter_list|()
block|{
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|unlockHead
parameter_list|()
block|{
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|putBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
return|return
name|blobStore
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------< RevisionProvider>
specifier|public
name|NodeState
name|getNodeState
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
return|return
operator|new
name|StoredNodeAsState
argument_list|(
name|node
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|StoredNode
name|getNode
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
return|;
block|}
name|Binding
name|nodeBinding
init|=
name|pm
operator|.
name|readNodeBinding
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|node
operator|=
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|id
argument_list|,
name|this
argument_list|,
name|nodeBinding
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|ChildNodeEntriesMap
name|getCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|ChildNodeEntriesMap
name|map
init|=
operator|(
name|ChildNodeEntriesMap
operator|)
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
return|return
name|map
return|;
block|}
name|map
operator|=
name|pm
operator|.
name|readCNEMap
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|map
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
name|verifyInitialized
argument_list|()
expr_stmt|;
comment|// TODO fixme, String -> Id
name|StoredCommit
name|commit
init|=
operator|(
name|StoredCommit
operator|)
name|cache
operator|.
name|get
argument_list|(
name|Id
operator|.
name|fromString
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
return|return
name|commit
return|;
block|}
name|commit
operator|=
name|pm
operator|.
name|readCommit
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|Id
operator|.
name|fromString
argument_list|(
name|id
argument_list|)
argument_list|,
name|commit
argument_list|)
expr_stmt|;
return|return
name|commit
return|;
block|}
specifier|public
name|StoredNode
name|getRootNode
parameter_list|(
name|String
name|commitId
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|getNode
argument_list|(
name|getCommit
argument_list|(
name|commitId
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|StoredCommit
name|getHeadCommit
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getCommit
argument_list|(
name|getHeadCommitId
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getHeadCommitId
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|headLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|headId
return|;
block|}
finally|finally
block|{
name|headLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
return|return
name|blobStore
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|pos
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
return|return
name|blobStore
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

